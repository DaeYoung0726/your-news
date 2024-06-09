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
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${mail.admin.email}")
    private String adminEmail;

    /* 메일 보내기 */
    @Async
    public void sendMail(String email, String content, MailType type) {

        SimpleMailMessage message = null;

        switch (type) {
            case CODE -> message = getCodeMessage(email, content);
            case PASS -> message = getPassMessage(email, content);
            case ASK -> message = getAskMessage(email, content);
        }

        try {
            javaMailSender.send(message);
        } catch (MailException ignored) {
        }
    }

    /* 인증메일 보내기 */
    private SimpleMailMessage getCodeMessage(String email, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(CODE_SUBJECT);
        message.setText(CODE_TEXT + code);
        message.setFrom(emailUsername);

        return message;
    }

    /* 임시 비밀번호 보내기 */
    private SimpleMailMessage getPassMessage(String email, String pass) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(PASS_SUBJECT);
        message.setText(PASS_TEXT + pass);
        message.setFrom(emailUsername);

        return message;
    }

    /* 관리자에게 문의하기 */
    private SimpleMailMessage getAskMessage(String email, String askContent) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject(ASK_SUBJECT);
        message.setText(ASK_TEXT + email + '\n' + askContent);
        message.setFrom(emailUsername);

        return message;
    }
}
