package project.yourNews.token.refresh;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.utils.jwt.JwtUtil;
import project.yourNews.utils.redis.RedisUtil;

import static project.yourNews.utils.redis.RedisProperties.REFRESH_EXPIRATION_TIME_IN_REDIS;
import static project.yourNews.utils.redis.RedisProperties.REFRESH_TOKEN_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    /* redis에 저장 */
    public void saveRefreshToken(String username, String refreshToken) {

        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        redisUtil.set(key, refreshToken);
        redisUtil.expire(key, REFRESH_EXPIRATION_TIME_IN_REDIS);
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
