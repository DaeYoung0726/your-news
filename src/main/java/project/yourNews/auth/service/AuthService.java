package project.yourNews.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.yourNews.auth.dto.LoginDto;
import project.yourNews.auth.dto.TokenDto;
import project.yourNews.auth.dto.UserRoleDto;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.token.refresh.RefreshTokenService;
import project.yourNews.token.tokenBlackList.TokenBlackListService;
import project.yourNews.utils.cookie.CookieUtil;
import project.yourNews.utils.jwt.JwtUtil;

import static project.yourNews.utils.jwt.JwtProperties.REFRESH_COOKIE_VALUE;
import static project.yourNews.utils.jwt.JwtProperties.TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlackListService tokenBlackListService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    /* 로그인 메서드 */
    public TokenDto login(LoginDto loginDto) {

        Member findMember = memberRepository.findByUsername(loginDto.getUsername()).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if(!checkPassword(loginDto.getPassword(), findMember.getPassword())) {

            throw new CustomException(ErrorCode.USER_INVALID_PASSWORD);
        }

        String role = String.valueOf(findMember.getRole());
        String username = findMember.getUsername();
        String accessToken = TOKEN_PREFIX + jwtUtil.generateAccessToken(role, username);
        String refreshToken = jwtUtil.generateRefreshToken(role, username);

        refreshTokenService.saveRefreshToken(username, refreshToken);

        return new TokenDto(accessToken, refreshToken);
    }

    /* 로그아웃 메서드 */
    public void logout(String accessTokenHeader, String refreshToken, HttpServletResponse response) {
        String accessToken = accessTokenHeader.substring(TOKEN_PREFIX.length()).trim();

        cookieUtil.deleteCookie(REFRESH_COOKIE_VALUE, response);    // 쿠키값 삭제

        tokenBlackListService.saveBlackList(accessToken);           // accessToken 블랙리스트에 담기
        refreshTokenService.deleteRefreshToken(refreshToken);       // 로그아웃 시 redis에서 refreshToken 삭제
    }

    /* access 토큰 재발급 */
    public TokenDto reissueAccessToken(String refreshTokenInCookie) {

        String refreshToken = refreshTokenService.findRefreshToken(refreshTokenInCookie);
        String accessTokenReIssue = TOKEN_PREFIX + refreshTokenService.accessTokenReIssue(refreshToken);

        // Refresh token rotation(RTR) 사용
        String refreshTokenReIssue = refreshTokenService.refreshTokenReIssue(refreshToken);

        return new TokenDto(accessTokenReIssue, refreshTokenReIssue);
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
