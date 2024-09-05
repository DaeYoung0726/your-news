package project.yourNews.common.crawling.service;

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
import project.yourNews.common.crawling.dto.EmailRequest;
import project.yourNews.common.crawling.strategy.CrawlingStrategy;
import project.yourNews.common.crawling.strategy.YUNewsCrawlingStrategy;
import project.yourNews.common.crawling.strategy.YutopiaCrawlingStrategy;
import project.yourNews.common.mail.mail.util.MailProperties;
import project.yourNews.domains.news.dto.NewsInfoDto;
import project.yourNews.domains.news.service.NewsService;

import java.io.IOException;
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

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @PostConstruct
    public void scheduleCrawlingTasks() {
        // 각 전략별로 스케줄링 작업을 등록
        for (CrawlingStrategy strategy : strategies) {
            String cronExpression = strategy.getScheduledTime();
            taskScheduler.schedule(() -> startCrawling(strategy),
                    new CronTrigger(cronExpression, TimeZone.getTimeZone("Asia/Seoul")));
        }
    }

    @Async
    public void startCrawling(CrawlingStrategy strategy) {

        List<NewsInfoDto> newsList = newsService.readAllNews();

        for (NewsInfoDto news : newsList) {
            if (strategy.canHandle(news.getNewsName())) {
                if (strategy instanceof YutopiaCrawlingStrategy) {
                    // YutopiaCrawlingStrategy이면 여러 URL을 처리
                    List<String> urlsToCrawl =
                            ((YutopiaCrawlingStrategy) strategy).getUrlsForYuTopiaNews(news);
                    for (String url : urlsToCrawl) {
                        analyzeWeb(news.getNewsName(), url, strategy);
                    }
                } else {
                    // 그 외의 경우 단일 URL 처리
                    analyzeWeb(news.getNewsName(), news.getNewsURL(), strategy);
                }
            }
        }
    }

    /* 페이지 크롤링 */
    private void analyzeWeb(String newsName, String newsURL, CrawlingStrategy strategy) {
        try {
            // 웹 페이지 가져오기
            Document doc = Jsoup.connect(newsURL).get();

            // 게시글 요소 찾기
            Elements postElements = strategy.getPostElements(doc);

            List<String> memberEmails = List.of();
            boolean isYUNews = strategy instanceof YUNewsCrawlingStrategy;

            if (!postElements.isEmpty()) {
                // 해당 소식 구독한 회원의 이메일 불러오기
                if (!isYUNews)
                    memberEmails = strategy.getSubscribedMembers(newsName);

                // 게시글 제목과 URL 출력
                for (Element postElement : postElements) {
                    if (strategy.shouldProcessElement(postElement)) {
                        String postTitle = strategy.extractPostTitle(postElement);
                        String postURL = strategy.extractPostURL(postElement);

                        if (!strategy.isExisted(postURL)) {

                            if (isYUNews) {
                                ((YUNewsCrawlingStrategy) strategy).setCurrentPostElement(postTitle);
                                memberEmails = strategy.getSubscribedMembers(newsName);
                            }
                            sendNewsToMember(memberEmails, newsName, postTitle, postURL);
                            // 새로운 게시글 URL을 목록에 추가
                            strategy.saveURL(postURL);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("An error occurred while processing news for URL: {}", newsURL);
        }
    }

    /* 소식 구독자에게 메일 보내기 */
    private void sendNewsToMember(List<String> memberEmails, String newsName, String postTitle, String postURL) {

        String mailContent = "[" + newsName + "]<br>" +
                postTitle +
                "<p><a href=" + postURL + ">게시글 링크</a></p>";

        EmailRequest emailRequest = new EmailRequest(memberEmails, MailProperties.NEWS_SUBJECT, mailContent);
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, emailRequest);
        } catch (Exception e) {
            log.error("Failed to send email request to RabbitMQ: {}", emailRequest, e);
        }

    }
}
