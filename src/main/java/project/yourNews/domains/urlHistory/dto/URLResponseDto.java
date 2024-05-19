package project.yourNews.domains.urlHistory.dto;

import lombok.Getter;
import project.yourNews.domains.urlHistory.domain.URLHistory;

import java.time.LocalDateTime;

@Getter
public class URLResponseDto {

    private final Long id;
    private final String dispatchedURL;
    private final LocalDateTime createdTime;

    public URLResponseDto(URLHistory url) {
        this.id = url.getId();
        this.dispatchedURL = url.getDispatchedURL();
        this.createdTime = url.getCreatedDate();
    }
}
