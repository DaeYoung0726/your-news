package project.yourNews.domains.auth.dto;

import lombok.Getter;

@Getter
public class TokenDto {
    private final String accessToken;
    private final String refreshToken;

    public TokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
