package project.yourNews.common.mail.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.mail.mail.service.strategy.MailStrategy;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class ReissueTempPassService {

    private final MemberRepository memberRepository;
    private final OtherMailService otherMailService;
    private final PasswordEncoder passwordEncoder;
    @Qualifier("passMailStrategy")
    private final MailStrategy passMailStrategy;

    private static final String PASS_CONTAINS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /* 임시 비밀번호 설정 후 메일로 보내기 */
    @Transactional
    public void sendPassToMail(String username, String email) {

        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String tempPass = createPass();

        member.updatePass(passwordEncoder.encode(tempPass));

        passMailStrategy.setReceiver(email);
        otherMailService.sendMail(email, tempPass, passMailStrategy);
    }

    /* 임시 비밀번호 발급 */
    private String createPass() {
        try {
            StringBuilder sb = new StringBuilder();
            Random random = SecureRandom.getInstanceStrong();

            for (int i = 0; i < 20; i++) {
                char randPass = PASS_CONTAINS.charAt(random.nextInt(PASS_CONTAINS.length()));
                sb.append(randPass);
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate secure random number", e);
        }
    }
}
