package project.yourNews.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.mail.service.ReissueTempPassService;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final ReissueTempPassService reissueTempPassService;

    /* 아이디 찾기 메서드 */
    @Transactional(readOnly = true)
    public String findingUsername(String email) {

        Member findMember = memberRepository.findByEmail(email).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return findMember.getUsername();
    }

    /* 임시 비밀번호 발급 */
    @Transactional
    public void reissueTempPassword(String username, String email) {

        if (!memberRepository.existsByUsernameAndEmail(username, email))
            throw new CustomException(ErrorCode.INVALID_USER_INFO);

        reissueTempPassService.sendPassToMail(username, email);
    }
}
