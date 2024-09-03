package project.yourNews.common.crawling.strategy;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.openAI.dto.ChatResponseDto;
import project.yourNews.common.openAI.dto.Message;
import project.yourNews.common.openAI.service.OpenAIService;
import project.yourNews.domains.member.service.MemberService;
import project.yourNews.domains.urlHistory.service.URLHistoryService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class YUNewsCrawlingStrategy implements CrawlingStrategy{

    private final MemberService memberService;
    private final URLHistoryService urlHistoryService;
    private final OpenAIService openAIService;

    private String postTitle;
    private int requestCount = 0;

    private static final String NEWS_NAME = "영대소식";
    private static final String MESSAGE_PREFIX =
            "키워드는 '장학금 및 학자금 지원', '취업', '교육 및 강좌', '학생복지', '연구 및 프로젝트 참여', '행사 및 설명회'가 있어." +
                    " 다음 제목의 키워드를 정확히 말해줘. 가장 유사한 하나의 키워드만 말해줘. 예를 들자면 '취업' 이렇게 말해. ";

    @Override
    public String getScheduledTime() {
        return "55 18 * * * MON-FRI";  // 주말 제외, 평일에 1시간마다 크롤링
    }

    @Override
    public boolean canHandle(String newsName) {
        return newsName.equals(NEWS_NAME);
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

    public List<String> getSubscribedMembers(String newsName) {
        String messageContent = MESSAGE_PREFIX + postTitle;
        Message message = Message.builder()
                .role("user")
                .content(messageContent)
                .build();
        manageRequestRate();
        ChatResponseDto gptResponse = openAIService.askQuestion(List.of(message));

        String keyword = gptResponse.getChoices().get(0).getMessage().getContent();
        keyword = keyword.replaceAll("^[\"']|[\"']$|\\.$", "");

        return memberService.getMembersSubscribedToNewsWithKeyword(keyword);
    }

    public void setCurrentPostElement(String postTitle) {
        this.postTitle = postTitle;
    }

    private void manageRequestRate() {

        if (requestCount > 0 && requestCount % 3 == 0) {
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(1));
                requestCount = 0;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CustomException(ErrorCode.THREAD_INTERRUPTED);
            }
        }
        requestCount++;
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

