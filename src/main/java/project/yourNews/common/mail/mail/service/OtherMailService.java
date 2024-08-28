package project.yourNews.common.mail.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.mail.mail.util.MailProperties;
import project.yourNews.common.mail.mail.MailType;
import project.yourNews.common.mail.mail.dto.StibeeRequest;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class OtherMailService {

    private final RestTemplate restTemplate;

    @Value("${mail.admin.email}")
    private String adminEmail;


    @Value("${stibee.url}")
    private String stibeeUrl;

    /* 메일 보내기 */
    @Async
    public void sendMail(String email, String content, MailType type) {
        try {
            switch (type) {
                case CODE -> getCodeMessage(email, content);
                case PASS -> getPassMessage(email, content);
                case ASK -> getAskMessage(email, content);
            }
        } catch (Exception e) {
            log.error("Failed to send email", e);
        }
    }

    /* 인증메일 보내기 */
    private void getCodeMessage(String email, String code) {

        String emailUsername = email.split("@")[0];
        String emailContent = createContent(emailUsername, MailProperties.CODE_TEXT, code);

        sendStibeeEmail(StibeeRequest.builder()
                .subscriber(email)
                .title(MailProperties.CODE_SUBJECT)
                .content(emailContent)
                .build()
        );
    }

    /* 임시 비밀번호 보내기 */
    private void getPassMessage(String email, String pass) {

        String emailUsername = email.split("@")[0];
        String emailContent = createContent(emailUsername, MailProperties.PASS_TEXT, pass);

        sendStibeeEmail(StibeeRequest.builder()
                .subscriber(email)
                .title(MailProperties.PASS_SUBJECT)
                .content(emailContent)
                .build()
        );
    }

    /* 관리자에게 문의하기 */
    private void getAskMessage(String email, String askContent) {

        String emailUsername = email.split("@")[0];
        String emailContent = createContent(emailUsername, MailProperties.ASK_TEXT, email) + "<br>" + askContent;

        sendStibeeEmail(StibeeRequest.builder()
                .subscriber(adminEmail)
                .title(MailProperties.ASK_SUBJECT)
                .content(emailContent)
                .build()
        );
    }

    private String createContent(String emailUsername, String mailText, String content) {

        return "사용자 : " + emailUsername + "<br>" + mailText + content;
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
