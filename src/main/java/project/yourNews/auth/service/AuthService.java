package project.yourNews.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.auth.dto.LoginDto;
import project.yourNews.auth.dto.UserRoleDto;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.mail.service.ReissueTempPassService;
import project.yourNews.utils.jwt.JwtUtil;

import static project.yourNews.utils.jwt.JwtProperties.TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final ReissueTempPassService reissueTempPassService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /* 로그인 메서드 */
    public Member login(LoginDto loginDto) {

        Member findMember = memberRepository.findByUsername(loginDto.getUsername()).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if(!checkPassword(loginDto.getPassword(), findMember.getPassword())) {

            throw new CustomException(ErrorCode.USER_INVALID_PASSWORD);
        }

        return findMember;
    }

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

    /* 비밀번호 확인 */
    private boolean checkPassword(String actual, String expect) {

        return passwordEncoder.matches(actual, expect);
    }

    /* 사용자 권한 찾기 */
    public UserRoleDto findUserRoleByToken(String accessToken) {

        String splitToken = accessToken.substring(TOKEN_PREFIX.length()).trim();

        String role = jwtUtil.getRole(splitToken);
        return new UserRoleDto(role);
    }
}
