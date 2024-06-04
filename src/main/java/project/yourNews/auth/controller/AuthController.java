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
import project.yourNews.auth.dto.UserRoleDto;
import project.yourNews.auth.service.AuthService;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.token.refresh.RefreshTokenService;
import project.yourNews.utils.cookie.CookieUtil;
import project.yourNews.utils.jwt.JwtUtil;

import java.util.HashMap;
import java.util.Map;

import static project.yourNews.utils.jwt.JwtProperties.REFRESH_COOKIE_VALUE;
import static project.yourNews.utils.jwt.JwtProperties.TOKEN_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {

        Member member = authService.login(loginDto);

        String role = String.valueOf(member.getRole());

        String accessToken = TOKEN_PREFIX + jwtUtil.generateAccessToken(role, member.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(role, member.getUsername());

        refreshTokenService.saveRefreshToken(member.getUsername(), refreshToken);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", accessToken);

        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshToken).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok(responseData);
    }

    /* accessToken 재발급 */
    @PostMapping("/reissue")
    public ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshTokenInCookie = cookieUtil.getCookie(REFRESH_COOKIE_VALUE, request);

        if (refreshTokenInCookie == null) {     // 쿠키에 Refresh Token이 없다면
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String refreshToken = refreshTokenService.findRefreshToken(refreshTokenInCookie);

        if (jwtUtil.isExpired(refreshToken)) {    // refresh token 만료
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String accessTokenReIssue = TOKEN_PREFIX + refreshTokenService.accessTokenReIssue(refreshToken);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("newAccessToken", accessTokenReIssue);

        // Refresh token rotation(RTR) 사용
        String refreshTokenReIssue = refreshTokenService.refreshTokenReIssue(refreshToken);

        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshTokenReIssue).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok(responseData);
    }

    /* 아이디 찾기 */
    @GetMapping("/find-username")
    public ResponseEntity<String> findingId(@RequestParam("email") @Valid @Email String email) {

        String username = authService.findingUsername(email);
        return ResponseEntity.ok(username);
    }

    /* 비밀번호 찾기 */
    @PostMapping("/find-pass")
    public ResponseEntity<String> findPass(@RequestParam("email") @Valid @Email String email,
                                           @RequestParam("username") String username) {

        authService.reissueTempPassword(username, email);
        return ResponseEntity.ok("잠시 후 등록하신 메일로 임시 비밀번호가 도착합니다.");
    }

    /* 사용자 권한 확인 */
    @GetMapping("/user-role")
    public ResponseEntity<UserRoleDto> getUserRole(@RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(authService.findUserRoleByToken(token));
    }
}
