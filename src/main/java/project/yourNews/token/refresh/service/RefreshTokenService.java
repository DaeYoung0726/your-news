package project.yourNews.token.refresh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.token.refresh.entity.RefreshToken;
import project.yourNews.token.refresh.repository.RefreshTokenRepository;
import project.yourNews.util.jwt.JwtUtil;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /* RefreshToken 저장 */
    @Transactional
    public void saveRefreshToken(String refreshToken) {

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .refreshToken(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }

    /* RefreshToken 불러오기 */
    @Transactional(readOnly = true)
    public String findRefreshToken(String refreshToken) {

        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (findRefreshToken.getCreatedDate().plusDays(1).isBefore(LocalDateTime.now()))
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);

        return findRefreshToken.getRefreshToken();
    }

    /* RefreshToen 삭제 */
    @Transactional
    public void deleteRefreshToken(String refreshToken) {

        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        refreshTokenRepository.delete(findRefreshToken);
    }

    /* refreshToken으로 accessToken 재발급 */
    @Transactional(readOnly = true)
    public String accessTokenReIssue(String refreshToken) {

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        return jwtUtil.generateAccessToken(username, role);
    }

    /* Refresh token rotation(RTR) 사용 */
    @Transactional
    public String refreshTokenReIssue(String refreshToken) {

        this.deleteRefreshToken(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String refreshTokenReIssue = jwtUtil.generateRefreshToken(role, username);

        this.saveRefreshToken(refreshTokenReIssue);

        return refreshTokenReIssue;
    }
}
