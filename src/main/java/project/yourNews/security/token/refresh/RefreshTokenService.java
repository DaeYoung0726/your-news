package project.yourNews.security.token.refresh;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.utils.jwt.JwtUtil;
import project.yourNews.common.utils.redis.RedisUtil;

import static project.yourNews.common.utils.redis.RedisProperties.REFRESH_TOKEN_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Value("${token.refresh.in-redis}")
    private long REDIS_REFRESH_EXPIRATION;

    /* redis에 저장 */
    public void saveRefreshToken(String username, String refreshToken) {

        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        redisUtil.set(key, refreshToken);
        redisUtil.expire(key, REDIS_REFRESH_EXPIRATION);
    }

    /* refreshToken으로 redis에서 불러오기 */
    public String findRefreshToken(String refreshToken) {

        String username = jwtUtil.getUsername(refreshToken);
        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        String findRefreshToken = (String) redisUtil.get(key);
        if(findRefreshToken == null)
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);

        return findRefreshToken;
    }

    /* redis에서 삭제 */
    public void deleteRefreshToken(String refreshToken) {

        String username = jwtUtil.getUsername(refreshToken);
        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        redisUtil.del(key);
    }

    /* accessToken 재발급 */
    public String accessTokenReIssue(String refreshToken) {

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        return jwtUtil.generateAccessToken(role, username);      // 재발급
    }

    /* Refresh token rotation(RTR) 사용 */
    public String refreshTokenReIssue(String refreshToken) {

        this.deleteRefreshToken(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String refreshTokenReIssue = jwtUtil.generateRefreshToken(role, username);

        this.saveRefreshToken(username, refreshTokenReIssue);

        return refreshTokenReIssue;
    }

}
