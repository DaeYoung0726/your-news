package project.yourNews.utils.redis;

public interface RedisProperties {

    String BLACKLIST_KEY_PREFIX = "BlackListToken:";
    long BLACKLIST_EXPIRATION_TIME = 60*60;
    String REFRESH_TOKEN_KEY_PREFIX = "RefreshToken:";
    long REFRESH_EXPIRATION_TIME_IN_REDIS = 24*60*60;
    String CODE_KEY_PREFIX = "code:";
    long CODE_EXPIRATION_TIME = 3*60;
}
