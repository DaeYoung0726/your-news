package project.yourNews.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.auth.dto.LoginDto;
import project.yourNews.auth.dto.TokenDto;
import project.yourNews.auth.dto.UserRoleDto;
import project.yourNews.auth.service.AccountService;
import project.yourNews.auth.service.AuthService;
import project.yourNews.domains.bannedEmail.service.BannedEmailService;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.utils.api.ApiUtil;
import project.yourNews.utils.cookie.CookieUtil;

import java.util.HashMap;
import java.util.Map;

import static project.yourNews.utils.jwt.JwtProperties.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final CookieUtil cookieUtil;
    private final AuthService authService;
    private final AccountService accountService;
    private final BannedEmailService bannedEmailService;

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {

        TokenDto tokenDto = authService.login(loginDto);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", tokenDto.getAccessToken());

        // 쿠키에 refresh Token값 저장.
        response.addHeader("Set-Cookie",
                cookieUtil.createCookie(REFRESH_COOKIE_VALUE, tokenDto.getRefreshToken()).toString());
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok(responseData);
    }

    /* 로그아웃 */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieUtil.getCookie(REFRESH_COOKIE_VALUE, request);
        String accessToken = request.getHeader(ACCESS_HEADER_VALUE);

        if (accessToken == null || refreshToken == null) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        authService.logout(accessToken, refreshToken, response);

        return ResponseEntity.ok(ApiUtil.from("로그아웃 되었습니다."));
    }

    /* accessToken 재발급 */
    @PostMapping("/reissue")
    public ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshTokenInCookie = cookieUtil.getCookie(REFRESH_COOKIE_VALUE, request);

        if (refreshTokenInCookie == null) {     // 쿠키에 Refresh Token이 없다면
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        TokenDto tokenDto = authService.reissueAccessToken(refreshTokenInCookie);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("newAccessToken", tokenDto.getAccessToken());

        // 쿠키에 refresh Token값 저장.
        response.addHeader("Set-Cookie",
                cookieUtil.createCookie(REFRESH_COOKIE_VALUE, tokenDto.getRefreshToken()).toString());
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok(responseData);
    }

    /* 아이디 찾기 */
    @GetMapping("/find-username")
    public ResponseEntity<?> findingId(@RequestParam("email") @Valid @Email String email) {

        String username = accountService.findingUsername(email);
        return ResponseEntity.ok(ApiUtil.from(username));
    }

    /* 비밀번호 찾기 */
    @PostMapping("/find-pass")
    public ResponseEntity<?> findPass(@RequestParam("email") @Valid @Email String email,
                                           @RequestParam("username") String username) {

        if (bannedEmailService.checkBannedEmail(email))
            throw new CustomException(ErrorCode.BANNED_EMAIL);

        accountService.reissueTempPassword(username, email);
        return ResponseEntity.ok(ApiUtil.from("잠시 후 등록하신 메일로 임시 비밀번호가 도착합니다."));
    }

    /* 사용자 권한 확인 */
    @GetMapping("/user-role")
    public ResponseEntity<UserRoleDto> getUserRole(@RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(authService.findUserRoleByToken(token));
    }

    /* 계정 확인 */
    @PostMapping("/check-account")
    public ResponseEntity<?> checkAccount(@RequestBody LoginDto loginDto) {

        boolean check = authService.login(loginDto) != null;
        return ResponseEntity.ok(ApiUtil.from(check));
    }
}
