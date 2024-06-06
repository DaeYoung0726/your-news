package project.yourNews.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.crawling.dto.EmailRequest;

import java.util.List;

import static project.yourNews.mail.util.MailProperties.NEWS_SUBJECT;

@RequiredArgsConstructor
@Transactional
@Service
public class NewsMailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailUsername;

    /* 메일 보내기 */
    public void sendMail(EmailRequest emailRequest) {

        SimpleMailMessage message = getNewsMessage(emailRequest.getEmails(), emailRequest.getContent());

        try {
            javaMailSender.send(message);
        } catch (MailException ignored) {
        }
    }

    /* 소식 보내기 */
    private SimpleMailMessage getNewsMessage(List<String> emails, String news) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setBcc(emails.toArray(new String[0]));
        message.setSubject(NEWS_SUBJECT);
        message.setText(news);
        message.setFrom(emailUsername);

        return message;
    }

}
