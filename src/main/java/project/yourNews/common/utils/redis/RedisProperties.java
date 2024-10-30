package project.yourNews.common.utils.redis;

public interface RedisProperties {

    String BLACKLIST_KEY_PREFIX = "BlackListToken::";
    String REFRESH_TOKEN_KEY_PREFIX = "RefreshToken::";

    String CODE_KEY_PREFIX = "code::";
    long CODE_EXPIRATION_TIME = 3*60;

    String URL_HISTORY_KEY_PREFIX = "URL::";
    long URL_EXPIRATION_TIME = 7*24*60*60;
    long YU_URL_EXPIRATION_TIME = 30*24*60*60;

    String NEWS_INFO_KEY_PREFIX = "news::";
    long NEWS_INFO_EXPIRATION_TIME = 13*60*60;
}
