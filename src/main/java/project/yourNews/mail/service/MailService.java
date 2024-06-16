package project.yourNews.mail.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.MessageRejectedException;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.mail.MailType;

import static project.yourNews.mail.util.MailProperties.*;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class MailService {

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    @Value("${mail.admin.email}")
    private String adminEmail;

    @Value("${mail.from-email}")
    private String fromEmail;

    @Value("${mail.name}")
    private String mailName;

    /* 메일 보내기 */
    @Async
    public void sendMail(String email, String content, MailType type) {
        try {
            SendEmailRequest request = null;

            switch (type) {
                case CODE -> request = getCodeMessage(email, content);
                case PASS -> request = getPassMessage(email, content);
                case ASK -> request = getAskMessage(email, content);
            }
            amazonSimpleEmailService.sendEmail(request);
        } catch (MessageRejectedException e) {
            log.error("발신 제한 초과 or 불법 콘텐츠 포함");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 인증메일 보내기 */
    private SendEmailRequest getCodeMessage(String email, String code) {

        String text = CODE_TEXT + code;
        return getSendEmailRequest(email, CODE_SUBJECT, text);
    }

    /* 임시 비밀번호 보내기 */
    private SendEmailRequest getPassMessage(String email, String pass) {

        String text = PASS_TEXT + pass;
        return getSendEmailRequest(email, PASS_SUBJECT, text);
    }

    /* 관리자에게 문의하기 */
    private SendEmailRequest getAskMessage(String email, String askContent) {

        String text = ASK_TEXT + email + "<br>" + askContent;
        return getSendEmailRequest(adminEmail, ASK_SUBJECT, text);
    }

    /* 메일 구성 */
    private SendEmailRequest getSendEmailRequest(String email, String subject, String text) {

        String source = String.format("%s <%s>", mailName, fromEmail);

        return new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withSubject(new Content().withCharset("UTF-8").withData(subject))
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(text))))
                .withSource(source);
    }
}
