package project.yourNews.common.mail.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import project.yourNews.common.crawling.dto.EmailRequest;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.mail.mail.dto.StibeeRequest;

@Transactional
@Slf4j
@Service
public class NewsMailService {


    private final RestTemplate restTemplate;

    @Value("${stibee.url}")
    private String stibeeUrl;

    @Value("${mail.unsubscribe.link}")
    private String unsubscribeLink;

    public NewsMailService(@Qualifier("stibeeRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /* 메일 보내기 */
    public void sendMail(EmailRequest emailRequest) {
        for (String email : emailRequest.getSubscriber()) {
            try {
                String emailUsername = email.split("@")[0];
                String emailContent = "사용자 : " + emailUsername + "<br>" + emailRequest.getContent() +
                        "<br><p><small>소식을 그만 받고 싶으신가요? <a href=\"" + unsubscribeLink + "\">구독 취소</a></small></p>";
                sendStibeeEmail(
                        StibeeRequest.builder()
                                .subscriber(email)
                                .title(emailRequest.getTitle())
                                .content(emailContent)
                                .build()
                );
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", email, e.getMessage(), e);
            }
        }
    }


    /* 스티비 API를 사용하여 이메일 보내기 */
    private void sendStibeeEmail(StibeeRequest request) {
        HttpEntity<StibeeRequest> entity = new HttpEntity<>(request);
        ResponseEntity<String> response = restTemplate.postForEntity(stibeeUrl, entity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Subscription failed. Status code: {}, Response body: {}", response.getStatusCode(), response.getBody());
            throw new CustomException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

}
