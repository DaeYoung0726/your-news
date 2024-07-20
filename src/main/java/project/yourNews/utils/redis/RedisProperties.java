package project.yourNews.utils.redis;

public interface RedisProperties {

    String BLACKLIST_KEY_PREFIX = "BlackListToken::";
    String REFRESH_TOKEN_KEY_PREFIX = "RefreshToken::";
    String CODE_KEY_PREFIX = "code::";
    long CODE_EXPIRATION_TIME = 3*60;
    String URL_HISTORY_KEY_PREFIX = "URL::";
    long URL_EXPIRATION_TIME = 7*24*60*60;
}
