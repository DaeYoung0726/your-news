package project.yourNews.domains.news.dto;

import lombok.Getter;
import project.yourNews.domains.news.domain.News;

@Getter
public class NewsInfoDto {

    private final Long id;
    private final String newsName;
    private final String newsURL;

    public NewsInfoDto(News news) {
        this.id = news.getId();
        this.newsName = news.getNewsName();
        this.newsURL = news.getNewsURL();
    }
}
