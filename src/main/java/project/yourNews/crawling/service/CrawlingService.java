package project.yourNews.crawling.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import project.yourNews.common.mail.mail.MailContentBuilder;
import project.yourNews.common.mail.mail.service.NewsMailService;
import project.yourNews.common.mail.mail.util.MailProperties;
import project.yourNews.crawling.dto.EmailRequest;
import project.yourNews.crawling.strategy.CrawlingStrategy;
import project.yourNews.crawling.strategy.YUNewsCrawlingStrategy;
import project.yourNews.crawling.strategy.YutopiaCrawlingStrategy;
import project.yourNews.domains.news.dto.NewsInfoDto;
import project.yourNews.domains.news.service.NewsService;
import project.yourNews.domains.notification.service.NotificationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@RequiredArgsConstructor
@Service
@EnableScheduling
@Slf4j
public class CrawlingService {

    private final NewsService newsService;
    private final RabbitTemplate rabbitTemplate;
    private final List<CrawlingStrategy> strategies;
    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;
    private final NewsMailService newsMailService;

    private static final int MAX_RETRIES = 2;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @PostConstruct
    public void scheduleCrawlingTasks() {
        // 각 전략별로 스케줄링 작업을 등록
        strategies.forEach(strategy -> taskScheduler.schedule(
                () -> startCrawling(strategy),
                new CronTrigger(strategy.getScheduledTime(), TimeZone.getTimeZone("Asia/Seoul")))
        );
    }

    @Async
    public void startCrawling(CrawlingStrategy strategy) {
        newsService.readAllNews().stream()
                .filter(news -> strategy.canHandle(news.getNewsName()))
                .forEach(news -> processCrawling(strategy, news));
    }

    private void processCrawling(CrawlingStrategy strategy, NewsInfoDto news) {
        if (strategy instanceof YutopiaCrawlingStrategy) {
            ((YutopiaCrawlingStrategy) strategy).getUrlsForYuTopiaNews(news)
                    .forEach(url -> analyzeWeb(news.getNewsName(), url, strategy));
        } else {
            analyzeWeb(news.getNewsName(), news.getNewsURL(), strategy);
        }
    }

    /* 페이지 크롤링 */
    private void analyzeWeb(String newsName, String newsURL, CrawlingStrategy strategy) {
        int retryCount = 0;

        while (retryCount <= MAX_RETRIES) {
            try {
                // 웹 페이지 가져오기
                Document doc = Jsoup.connect(newsURL).get();

                // 게시글 요소 찾기
                Elements postElements = strategy.getPostElements(doc);

                if (postElements.isEmpty())
                    return;

                // isYUNews 여부에 따라 처리 방식 분리
                if (strategy instanceof YUNewsCrawlingStrategy) {
                    processYUNewsPosts(postElements, (YUNewsCrawlingStrategy) strategy, newsName);
                } else {
                    processOtherNewsPosts(postElements, strategy, newsName);
                }

                break; // 성공적으로 전송되었으면 루프 종료

            } catch (IOException e) {
                log.error("An error occurred while processing news for URL: {}. Retry count: {}", newsURL, ++retryCount);
                if (retryCount > MAX_RETRIES) {
                    log.error("Max retries reached for URL: {}. Skipping this URL.", newsURL);
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }


    /* 크롤링 전략이 YUNews 일 시. */
    private void processYUNewsPosts(Elements postElements, YUNewsCrawlingStrategy strategy, String newsName) {

        for (Element postElement : postElements) {
            if (strategy.shouldProcessElement(postElement)) {
                String postTitle = strategy.extractPostTitle(postElement);
                String postURL = strategy.extractPostURL(postElement);

                if (!strategy.isExisted(postURL)) {
                    notificationService.saveNewsInfo(newsName, postTitle, postURL);
                    strategy.setCurrentPostElement(postTitle);
                    List<String> memberEmails = strategy.getSubscribedMembers(newsName);

                    sendNewsToMember(memberEmails, newsName, postTitle, postURL);
                    strategy.saveURL(postURL);
                }
            }
        }
    }

    /* 크롤링 전략이 YUNews 아닐 시. */
    private void processOtherNewsPosts(Elements postElements, CrawlingStrategy strategy, String newsName) {

        List<String> memberEmails = strategy.getSubscribedMembers(newsName);

        for (Element postElement : postElements) {
            if (strategy.shouldProcessElement(postElement)) {
                String postTitle = strategy.extractPostTitle(postElement);
                String postURL = strategy.extractPostURL(postElement);

                if (!strategy.isExisted(postURL)) {
                    notificationService.saveNewsInfo(newsName, postTitle, postURL);
                    sendNewsToMember(memberEmails, newsName, postTitle, postURL);
                    strategy.saveURL(postURL);
                }
            }
        }
    }

    /* 소식 구독자에게 메일 보내기 */
    private void sendNewsToMember(List<String> memberEmails, String newsName, String postTitle, String postURL) {
        String mailContent = MailContentBuilder.buildNewsMailContent(newsName, postTitle, postURL);

        int batchSize = 100;
        List<List<String>> emailBatches = partitionList(memberEmails, batchSize);

        for (List<String> emailBatch : emailBatches) {
            EmailRequest emailRequest = new EmailRequest(emailBatch, MailProperties.NEWS_SUBJECT, mailContent);

            try {
                rabbitTemplate.convertAndSend(exchangeName, routingKey, emailRequest);
            } catch (Exception e) {
                log.error("Failed to send email request to RabbitMQ : {}", newsName, e);
            }
        }
    }

    private List<List<String>> partitionList(List<String> list, int batchSize) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }
}
