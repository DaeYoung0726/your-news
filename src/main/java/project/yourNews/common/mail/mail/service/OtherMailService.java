package project.yourNews.common.mail.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.mail.mail.dto.StibeeRequest;
import project.yourNews.common.mail.mail.service.strategy.MailStrategy;

@Slf4j
@Service
public class OtherMailService {

    private final RestTemplate restTemplate;

    @Value("${stibee.url}")
    private String stibeeUrl;

    public OtherMailService(@Qualifier("stibeeRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /* 메일 보내기 */
    @Async
    public void sendMail(String email, String content, MailStrategy strategy) {
        try {
            sendStibeeEmail(StibeeRequest.builder()
                    .subscriber(strategy.getReceiver())
                    .title(strategy.getSubject())
                    .content(strategy.generateContent(email, content))
                    .build()
            );
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage(), e);
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
