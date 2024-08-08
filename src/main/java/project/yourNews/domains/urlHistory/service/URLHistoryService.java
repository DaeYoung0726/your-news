package project.yourNews.domains.urlHistory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.common.utils.redis.RedisUtil;

import static project.yourNews.common.utils.redis.RedisProperties.URL_EXPIRATION_TIME;
import static project.yourNews.common.utils.redis.RedisProperties.URL_HISTORY_KEY_PREFIX;

@Slf4j
@RequiredArgsConstructor
@Service
public class URLHistoryService {

    private final RedisUtil redisUtil;

    /* 보낸 url 저장하기 */
    @Transactional
    public void saveURL(String url) {

        String key = URL_HISTORY_KEY_PREFIX + url;
        redisUtil.set(key, url);
        redisUtil.expire(key, URL_EXPIRATION_TIME);
    }

    /* 저장된 url 삭제하기 */
    @Transactional
    public void deleteURL(String dispatchedURL) {

        String key = URL_HISTORY_KEY_PREFIX + dispatchedURL;

        redisUtil.del(key);
    }

    /* 이미 보낸 소식인지 확인 */
    @Transactional
    public boolean existsURLCheck(String url) {

        return redisUtil.existed(URL_HISTORY_KEY_PREFIX + url);
    }
}
