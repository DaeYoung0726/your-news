package project.yourNews.handler.logoutHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.token.refresh.RefreshTokenService;
import project.yourNews.token.tokenBlackList.TokenBlackListService;
import project.yourNews.utils.cookie.CookieUtil;

import static project.yourNews.utils.jwt.JwtProperties.*;

@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;
    private final TokenBlackListService tokenBlackListService;
    private final CookieUtil cookieUtil;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshToken = cookieUtil.getCookie(REFRESH_COOKIE_VALUE, request);
        String accessToken = request.getHeader(ACCESS_HEADER_VALUE).substring(TOKEN_PREFIX.length()).trim();

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        cookieUtil.deleteCookie(REFRESH_COOKIE_VALUE, response);    // 쿠키값 삭제

        tokenBlackListService.saveBlackList(accessToken);
        refreshTokenService.deleteRefreshToken(refreshToken);       // 로그아웃 시 redis에서 refreshToken 삭제
    }

}