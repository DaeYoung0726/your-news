package project.yourNews.domains.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.yourNews.domains.category.domain.Category;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.post.domain.Post;

@Getter @Setter
@NoArgsConstructor
public class PostRequestDto {

    @NotBlank(message = "제목은 필수 입력입니다.")
    @Size(max = 30, message = "제목은 최대 30자까지 작성할 수 있습니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력입니다.")
    private String content;

    public Post toPostEntity(Member writer, Category category) {
        return Post.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .category(category)
                .build();
    }

}
