package project.yourNews.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.mail.MailType;

import static project.yourNews.mail.util.MailProperties.*;

@RequiredArgsConstructor
@Transactional
@EnableAsync
@Service
public class MailService {

    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailUsername;

    /* 메일 보내기 */
    @Async
    public void sendMail(String email, String content, MailType type) {

        SimpleMailMessage message = null;

        switch (type) {
            case CODE -> message = getCodeMessage(email, content);
            case NEWS -> message = getNewsMessage(email, content);
            case PASS -> message = getPassMessage(email, content);
        }

        try {
            javaMailSender.send(message);
        } catch (MailException ignored) {
        }
    }

    /* 인증메일 보내기 */
    public SimpleMailMessage getCodeMessage(String email, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(CODE_SUBJECT);
        message.setText(CODE_TEXT + code);
        message.setFrom(emailUsername);

        return message;
    }

    /* 소식 보내기 */
    public SimpleMailMessage getNewsMessage(String email, String news) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(NEWS_SUBJECT);
        message.setText(NEWS_TEXT + news);
        message.setFrom(emailUsername);

        return message;
    }

    /* 임시 비밀번호 보내기 */
    public SimpleMailMessage getPassMessage(String email, String pass) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(PASS_SUBJECT);
        message.setText(PASS_TEXT + pass);
        message.setFrom(emailUsername);

        return message;
    }
}
