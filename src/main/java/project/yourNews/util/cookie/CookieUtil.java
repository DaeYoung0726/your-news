package com.project.tableforyou.utils.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    /* 쿠키 생성 메서드 */
    public ResponseCookie createCookie(String key, String value) {

        return ResponseCookie.from(key, value)
                .path("/")
                .httpOnly(true)
                .maxAge(24*60*60)
                .secure(true)
                .sameSite("None")
                .build();
    }

    /* 쿠키 삭제 메서드 */
    public void deleteCookie(String cookieName, HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

    }

    /* 쿠키 값 가져오기 */
    public String getCookie(String key, HttpServletRequest request) {
        String cookieValue = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals(key)) {

                cookieValue = cookie.getValue();
            }
        }
        return cookieValue;
    }
}
