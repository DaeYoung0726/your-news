package project.yourNews.common.utils.jwt;

public interface JwtProperties {
    String TOKEN_PREFIX = "Bearer ";
    String ACCESS_HEADER_VALUE = "Authorization";
    String REFRESH_COOKIE_VALUE = "RefreshToken";
}
