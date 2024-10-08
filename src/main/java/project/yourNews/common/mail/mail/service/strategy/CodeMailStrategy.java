package project.yourNews.common.mail.mail.service.strategy;

import org.springframework.stereotype.Component;
import project.yourNews.common.mail.mail.MailContentBuilder;

import static project.yourNews.common.mail.mail.util.MailProperties.CODE_SUBJECT;
import static project.yourNews.common.mail.mail.util.MailProperties.CODE_TEXT;

@Component
public class CodeMailStrategy implements MailStrategy {

    private String receiver;

    @Override
    public String getReceiver() {
        return receiver;
    }

    @Override
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public String getSubject() {
        return CODE_SUBJECT;
    }

    @Override
    public String generateContent(String email, String code) {
        String emailUsername = email.split("@")[0];
        return CODE_TEXT + code + "<br>" + MailContentBuilder.buildUserEmailContent(emailUsername);
    }
}
