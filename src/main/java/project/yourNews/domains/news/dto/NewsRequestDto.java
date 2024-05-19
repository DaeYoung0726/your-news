package project.yourNews.domains.news.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import project.yourNews.domains.news.domain.News;

@Getter @Setter
public class NewsRequestDto {

    @NotBlank(message = "소식 이름은 필수 입력 값입니다.")
    private String newsName;

    @NotBlank(message = "소식 URL은 필수 입력 값입니다.")
    private String newsURL;

    public News toNewsEntity() {
        return News.builder()
                .newsName(newsName)
                .newsURL(newsURL)
                .build();
    }
}
