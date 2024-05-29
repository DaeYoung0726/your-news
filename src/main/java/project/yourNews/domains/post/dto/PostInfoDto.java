package project.yourNews.domains.post.dto;

import lombok.Getter;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.post.domain.Post;

@Getter
public class PostInfoDto {

    private final Long id;
    private final String title;
    private final Member writer;
    private final int likeCount;

    public PostInfoDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.writer = post.getWriter();
        this.likeCount = post.getLikes().size();
    }
}
