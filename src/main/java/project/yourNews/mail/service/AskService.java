package project.yourNews.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.yourNews.mail.MailType;
import project.yourNews.mail.dto.AskDto;

@RequiredArgsConstructor
@Service
public class AskService {

    private final MailService mailService;

    /* 문의하기 */
    public void askToAdmin(AskDto askDto) {

        mailService.sendMail(askDto.getAsker(), askDto.getContent(), MailType.ASK);
    }
}
