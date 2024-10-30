package project.yourNews.domains.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.yourNews.domains.post.domain.Post;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class PostInfoDto {

    private Long id;
    private String title;
    private String writer;
    private int likeCount;
    private String createdDate;

    public PostInfoDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.writer = post.getWriter().getNickname();
        this.likeCount = post.getLikes().size();
        this.createdDate = post.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
