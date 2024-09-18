package project.yourNews.domains.auth.helper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.utils.cookie.CookieUtil;
import project.yourNews.common.utils.jwt.JwtUtil;
import project.yourNews.domains.auth.dto.TokenDto;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.security.token.refresh.RefreshTokenService;
import project.yourNews.security.token.tokenBlackList.TokenBlackListService;

import java.time.LocalDateTime;

import static project.yourNews.common.utils.jwt.JwtProperties.REFRESH_COOKIE_VALUE;
import static project.yourNews.common.utils.jwt.JwtProperties.TOKEN_PREFIX;

@RequiredArgsConstructor
@Component
public class JwtHelper {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlackListService tokenBlackListService;

    /* access, refresh 토큰 생성 */
    public TokenDto createToken(Member member) {

        Long userId = member.getId();
        String username = member.getUsername();
        String role = String.valueOf(member.getRole());

        String accessToken = TOKEN_PREFIX + jwtUtil.generateAccessToken(role, username, userId);
        String refreshToken = jwtUtil.generateRefreshToken(role, username, userId);

        refreshTokenService.saveRefreshToken(username, refreshToken);
        return new TokenDto(accessToken, refreshToken);
    }

    /* access reissue & Refresh Token Rotation */
    public TokenDto reissueToken(String refreshToken) {

        String username = jwtUtil.getUsername(refreshToken);

        if (!refreshTokenService.existedRefreshToken(username))
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);

        Long userId = jwtUtil.getUserId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = TOKEN_PREFIX + jwtUtil.generateAccessToken(role, username, userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(role, username, userId);

        refreshTokenService.saveRefreshToken(username, newRefreshToken);   // 재발급된 refreshToken redis에 저장.

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    /**
     * 사용자 로그아웃 시,
     * access Token -> BlackList
     * refresh Token -> redis에서 삭제
     */
    public void removeToken(String accessToken, String refreshToken, HttpServletResponse response) {

        deleteAccessToken(accessToken);
        deleteRefreshToken(refreshToken, response);
    }

    private void deleteAccessToken(String accessToken) {

        LocalDateTime accessTokenExpireAt = jwtUtil.getExpiryDate(accessToken);
        tokenBlackListService.saveBlackList(accessToken, accessTokenExpireAt);
    }

    private void deleteRefreshToken(String refreshToken, HttpServletResponse response) {

        String username = jwtUtil.getUsername(refreshToken);
        cookieUtil.deleteCookie(REFRESH_COOKIE_VALUE, response);
        refreshTokenService.deleteRefreshToken(username);
    }

    public String getRole(String accessToken) {
        return jwtUtil.getRole(accessToken);
    }
}
