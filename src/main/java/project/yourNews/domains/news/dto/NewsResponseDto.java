package project.yourNews.domains.news.dto;

import lombok.Getter;
import project.yourNews.domains.news.domain.News;

@Getter
public class NewsResponseDto {

    private final Long id;
    private final String newsName;
    private final String newsURL;

    public NewsResponseDto(News news) {
        this.id = news.getId();
        this.newsName = news.getNewsName();
        this.newsURL = news.getNewsURL();
    }
}
