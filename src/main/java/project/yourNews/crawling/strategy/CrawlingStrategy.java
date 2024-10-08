package project.yourNews.crawling.strategy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public interface CrawlingStrategy {
    boolean canHandle(String newsName);
    Elements getPostElements(Document doc);
    boolean shouldProcessElement(Element postElement);
    String extractPostTitle(Element postElement);
    String extractPostURL(Element postElement);
    String getScheduledTime();
    List<String> getSubscribedMembers(String newsName);
    void saveURL(String postURL);
    boolean isExisted(String postURL);
}
