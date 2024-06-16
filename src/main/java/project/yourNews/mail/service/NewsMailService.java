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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.crawling.dto.EmailRequest;

import java.util.List;

import static project.yourNews.mail.util.MailProperties.NEWS_SUBJECT;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class NewsMailService {

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    @Value("${mail.from-email}")
    private String fromEmail;

    @Value("${mail.name}")
    private String mailName;

    @Value("${mail.unsubscribe.link}")
    private String unsubscribeLink;

    /* 메일 보내기 */
    public void sendMail(EmailRequest emailRequest) {
        try {
            SendEmailRequest request = getNewsMessage(emailRequest.getEmails(), emailRequest.getContent());
            amazonSimpleEmailService.sendEmail(request);
        } catch (MessageRejectedException e) {
            log.error("발신 제한 초과 or 불법 콘텐츠 포함");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 소식 보내기 */
    private SendEmailRequest getNewsMessage(List<String> emails, String news) {

        String emailContent = news +
                "<p>소식을 그만 받고 싶다면 클릭해주세요. <a href=\"" + unsubscribeLink + "\">구독 취소</a></p>";

        Destination destination = new Destination().withBccAddresses(emails);

        Message message = new Message()
                .withSubject(new Content().withCharset("UTF-8").withData(NEWS_SUBJECT))
                .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(emailContent)));

        String source = String.format("%s <%s>", mailName, fromEmail);

        return new SendEmailRequest()
                .withDestination(destination)
                .withMessage(message)
                .withSource(source);
    }

}
