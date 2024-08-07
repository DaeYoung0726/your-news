package project.yourNews.common.crawling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.common.crawling.dto.EmailRequest;
import project.yourNews.domains.member.service.MemberService;
import project.yourNews.domains.news.dto.NewsInfoDto;
import project.yourNews.domains.news.service.NewsService;
import project.yourNews.domains.urlHistory.service.URLHistoryService;
import project.yourNews.common.mail.mail.util.MailProperties;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
@EnableScheduling
@Slf4j
public class CrawlingService {

    private final NewsService newsService;
    private final MemberService memberService;
    private final URLHistoryService urlHistoryService;
    private final RabbitTemplate rabbitTemplate;


    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    private static final String SCHEDULED_TIME = "0 0 8-19 * * MON-FRI";  // 주말 제외, 평일에 1시간마다 크롤링

    @Scheduled(cron = SCHEDULED_TIME, zone = "Asia/Seoul") // 오전 8시부터 오후 7시까지
    @Async
    @Transactional
    public void startCrawling() throws IOException {

        List<NewsInfoDto> news = newsService.readAllNews();
        for (NewsInfoDto readNews : news) {
            analyzeWeb(readNews.getNewsName(), readNews.getNewsURL());
        }
    }

    /* 페이지 크롤링 */
    private void analyzeWeb(String newsName, String newsURL) throws IOException {
        try {
            // 해당 소식 구독한 회원의 이메일 불러오기
            List<String> memberEmails = memberService.getMembersSubscribedToNews(newsURL);

            // 웹 페이지 가져오기
            Document doc = Jsoup.connect(newsURL).get();

            // 게시글 요소 찾기
            Elements postElements = doc.select("tr[class='']"); // class가 없는 경우
            postElements.addAll(doc.select("tr.b-top-box")); // b-top-box 클래스를 포함하는 경우

            // 게시글 제목과 URL 출력
            for (Element postElement : postElements) {
                Element newPostElement = postElement.selectFirst("p.b-new");
                if (newPostElement != null) { // 새로운 게시글인 경우에만 처리
                    Element titleElement = postElement.selectFirst("div.b-title-box > a");
                    String postTitle = titleElement.text();
                    String postURL = titleElement.absUrl("href");

                    // 새로운 게시글인 경우에만 출력
                    if (!urlHistoryService.existsURLCheck(postURL)) {

                        sendNewsToMember(memberEmails, newsName, postTitle, postURL);
                        // 새로운 게시글 URL을 목록에 추가
                        urlHistoryService.saveURL(postURL);
                    }
                }
            }
        } catch (Exception e) {
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
