package project.yourNews.domains.subNews.dto;

import lombok.Getter;
import project.yourNews.domains.subNews.domain.SubNews;

import java.util.List;

@Getter
public class SubNewsResponseDto {

    private final String news;

    public SubNewsResponseDto(SubNews subNews, List<String> keywords) {
        // 영대소식일 경우에만 키워드를 포함
        if (subNews.getNews().getNewsName().equals("영대소식")) {
            this.news = subNews.getNews().getNewsName() + "\n(" + String.join(", \n", keywords) + ")";
        } else {
            this.news = subNews.getNews().getNewsName() + "\n";
        }
    }
}
