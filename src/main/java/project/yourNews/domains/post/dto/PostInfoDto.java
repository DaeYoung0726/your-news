package project.yourNews.domains.post.dto;

import lombok.Getter;
import project.yourNews.domains.post.domain.Post;

@Getter
public class PostInfoDto {

    private final Long id;
    private final String title;
    private final String writer;
    private final int likeCount;

    public PostInfoDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.writer = post.getWriter().getNickname();
        this.likeCount = post.getLikes().size();
    }
}
