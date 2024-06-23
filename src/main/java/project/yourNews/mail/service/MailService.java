package project.yourNews.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.mail.MailType;
import project.yourNews.mail.dto.StibeeRequest;

import static project.yourNews.mail.util.MailProperties.*;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class MailService {

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
            e.printStackTrace();
        }
    }

    /* 인증메일 보내기 */
    private void getCodeMessage(String email, String code) {

        String emailUsername = email.split("@")[0];
        String emailContent = "사용자 : " + emailUsername + "<br>" + CODE_TEXT + code;

        sendStibeeEmail(StibeeRequest.builder()
                .subscriber(email)
                .title(CODE_SUBJECT)
                .content(emailContent)
                .build()
        );
    }

    /* 임시 비밀번호 보내기 */
    private void getPassMessage(String email, String pass) {

        String emailUsername = email.split("@")[0];
        String emailContent = "사용자 : " + emailUsername + "<br>" + PASS_TEXT + pass;

        sendStibeeEmail(StibeeRequest.builder()
                .subscriber(email)
                .title(PASS_SUBJECT)
                .content(emailContent)
                .build()
        );
    }

    /* 관리자에게 문의하기 */
    private void getAskMessage(String email, String askContent) {

        String emailUsername = email.split("@")[0];
        String emailContent = "사용자 : " + emailUsername + "<br>" + ASK_TEXT + email + "<br>" + askContent;

        sendStibeeEmail(StibeeRequest.builder()
                .subscriber(adminEmail)
                .title(ASK_SUBJECT)
                .content(emailContent)
                .build()
        );
    }

    /* 스티비 API를 사용하여 이메일 보내기 */
    private void sendStibeeEmail(StibeeRequest request) {
        HttpEntity<StibeeRequest> entity = new HttpEntity<>(request);
        ResponseEntity<String> response = restTemplate.postForEntity(stibeeUrl, entity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new CustomException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }
}
