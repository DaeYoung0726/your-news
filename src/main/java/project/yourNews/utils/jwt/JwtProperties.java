package project.yourNews.util.jwt;

public interface JwtProperties {
    Long ACCESS_EXPIRATION_TIME = 60*60*1000L;  // 60분
    Long REFRESH_EXPIRATION_TIME = 24*60*60*1000L;      // 24시간
    String TOKEN_PREFIX = "Bearer ";
    String ACCESS_HEADER_VALUE = "Authorization";
    String REFRESH_COOKIE_VALUE = "RefreshToken";
}
