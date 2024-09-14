package project.yourNews.common.mail.mail.service.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static project.yourNews.common.mail.mail.util.MailProperties.ASK_SUBJECT;
import static project.yourNews.common.mail.mail.util.MailProperties.ASK_TEXT;

@Component
public class AskMailStrategy implements MailStrategy {

    @Value("${mail.admin.email}")
    private String adminEmail;

    @Override
    public String getReceiver() {
        return adminEmail;
    }

    @Override
    public void setReceiver(String receiver) {}

    @Override
    public String getSubject() {
        return ASK_SUBJECT;
    }

    @Override
    public String generateContent(String email, String askContent) {
        String emailUsername = email.split("@")[0];
        return "사용자 : " + emailUsername + "<br>" + ASK_TEXT + email + "<br>" + askContent;
    }
}
