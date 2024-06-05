package project.yourNews.token.tokenBlackList;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.yourNews.utils.redis.RedisUtil;

import static project.yourNews.utils.redis.RedisProperties.BLACKLIST_EXPIRATION_TIME;
import static project.yourNews.utils.redis.RedisProperties.BLACKLIST_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final RedisUtil redisUtil;

    /* redis에 저장 */
    public void saveBlackList(String accessToken) {

        String key = BLACKLIST_KEY_PREFIX + accessToken;
        redisUtil.set(key, accessToken);
        redisUtil.expire(key, BLACKLIST_EXPIRATION_TIME);
    }

    /* 블랙리스트 확인. */
    public boolean existsBlackListCheck(String accessToken) {
        return redisUtil.existed(BLACKLIST_KEY_PREFIX + accessToken);
    }

}
