package project.yourNews.domains.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.domains.auth.dto.LoginDto;
import project.yourNews.domains.auth.dto.TokenDto;
import project.yourNews.domains.auth.dto.UserRoleDto;
import project.yourNews.domains.auth.helper.JwtHelper;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;

import static project.yourNews.common.utils.jwt.JwtProperties.TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;

    /* 로그인 메서드 */
    public TokenDto login(LoginDto loginDto) {

        Member findMember = memberRepository.findByUsername(loginDto.getUsername()).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if(!checkPassword(loginDto.getPassword(), findMember.getPassword())) {

            throw new CustomException(ErrorCode.USER_INVALID_PASSWORD);
        }

        return jwtHelper.createToken(findMember);
    }

    /* 로그아웃 메서드 */
    public void logout(String accessTokenHeader, String refreshToken, HttpServletResponse response) {

        String accessToken = accessTokenHeader.substring(TOKEN_PREFIX.length()).trim();
        jwtHelper.removeToken(accessToken, refreshToken, response);
    }

    /* access 토큰 재발급 */
    public TokenDto reissueAccessToken(String refreshToken) {

        return jwtHelper.reissueToken(refreshToken);
    }

    /* 비밀번호 확인 */
    private boolean checkPassword(String actual, String expect) {

        return passwordEncoder.matches(actual, expect);
    }

    /* 사용자 권한 찾기 */
    public UserRoleDto findUserRoleByToken(String accessToken) {

        String splitToken = accessToken.substring(TOKEN_PREFIX.length()).trim();

        return new UserRoleDto(jwtHelper.getRole(splitToken));
    }
}
