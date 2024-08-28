package project.yourNews.common.crawling.strategy;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import project.yourNews.domains.urlHistory.service.URLHistoryService;

@Component
@RequiredArgsConstructor
public class DefaultCrawlingStrategy implements CrawlingStrategy {

    private final URLHistoryService urlHistoryService;

    @Override
    public String getScheduledTime() {
        return "0 0 8-19 * * MON-FRI";  // 주말 제외, 평일에 1시간마다 크롤링
    }

    @Override
    public boolean canHandle(String newsName) {
        return true;  // 기본 전략, 모든 뉴스 처리
    }

    @Override
    public Elements getPostElements(Document doc) {
        Elements postElements = doc.select("tr[class='']"); // class가 없는 경우
        postElements.addAll(doc.select("tr.b-top-box")); // b-top-box 클래스를 포함하는 경우
        return postElements;
    }

    @Override
    public boolean shouldProcessElement(Element postElement) {
        Element newPostElement = postElement.selectFirst("p.b-new");
        return newPostElement != null;  // 새로운 게시글인지 확인
    }

    @Override
    public String extractPostTitle(Element postElement) {
        Element titleElement = postElement.selectFirst("div.b-title-box > a");
        return titleElement.text();
    }

    @Override
    public String extractPostURL(Element postElement) {
        Element titleElement = postElement.selectFirst("div.b-title-box > a");
        return titleElement.absUrl("href");
    }

    @Override
    public void saveURL(String postURL) {
        urlHistoryService.saveDefaultURL(postURL);
    }

    @Override
    public boolean isExisted(String postURL) {
        return urlHistoryService.existsURLCheck(postURL);
    }
}
