package project.yourNews.common.mail.mail.service.strategy;

import org.springframework.stereotype.Component;
import project.yourNews.common.mail.mail.MailContentBuilder;

import static project.yourNews.common.mail.mail.util.MailProperties.PASS_SUBJECT;
import static project.yourNews.common.mail.mail.util.MailProperties.PASS_TEXT;

@Component
public class PassMailStrategy implements MailStrategy {

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
        return PASS_SUBJECT;
    }

    @Override
    public String generateContent(String email, String pass) {
        String emailUsername = email.split("@")[0];

        return PASS_TEXT + pass + "<br>" + MailContentBuilder.buildUserEmailContent(emailUsername);
    }
}
