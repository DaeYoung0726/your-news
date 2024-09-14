package project.yourNews.common.mail.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import project.yourNews.common.mail.mail.dto.AskDto;
import project.yourNews.common.mail.mail.service.strategy.MailStrategy;

@RequiredArgsConstructor
@Service
public class AskService {

    private final OtherMailService otherMailService;
    @Qualifier("askMailStrategy")
    private final MailStrategy askMailStrategy;

    /* 문의하기 */
    public void askToAdmin(AskDto askDto) {

        otherMailService.sendMail(askDto.getAsker(), askDto.getContent(), askMailStrategy);
    }
}
