package project.yourNews.domains.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import project.yourNews.domains.category.domain.Category;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.post.domain.Post;

@Getter @Setter
public class PostRequestDto {

    @NotBlank(message = "제목은 필수 입력입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력입니다.")
    private String content;

    private Member writer;
    private Category category;

    public Post toPostEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .category(category)
                .build();
    }

}
