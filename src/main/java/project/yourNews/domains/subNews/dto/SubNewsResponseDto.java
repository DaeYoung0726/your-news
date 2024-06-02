package project.yourNews.domains.subNews.dto;

import lombok.Getter;
import project.yourNews.domains.news.domain.News;
import project.yourNews.domains.subNews.domain.SubNews;

@Getter
public class SubNewsResponseDto {

    private final String news;

    public SubNewsResponseDto(SubNews subNews) {
        this.news = subNews.getNews().getNewsName();
    }
}
