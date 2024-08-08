package project.yourNews.common.mail.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.yourNews.common.mail.mail.MailType;
import project.yourNews.common.mail.mail.dto.AskDto;

@RequiredArgsConstructor
@Service
public class AskService {

    private final OtherMailService otherMailService;

    /* 문의하기 */
    public void askToAdmin(AskDto askDto) {

        otherMailService.sendMail(askDto.getAsker(), askDto.getContent(), MailType.ASK);
    }
}
