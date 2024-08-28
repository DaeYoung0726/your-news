package project.yourNews.common.crawling.strategy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface CrawlingStrategy {
    boolean canHandle(String newsName);
    Elements getPostElements(Document doc);
    boolean shouldProcessElement(Element postElement);
    String extractPostTitle(Element postElement);
    String extractPostURL(Element postElement);
    String getScheduledTime();
    void saveURL(String postURL);
    boolean isExisted(String postURL);
}
