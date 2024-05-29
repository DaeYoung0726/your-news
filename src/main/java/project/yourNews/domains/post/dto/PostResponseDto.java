package project.yourNews.domains.post.dto;

import lombok.Getter;
import project.yourNews.domains.category.domain.Category;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.post.domain.Post;

@Getter
public class PostResponseDto {

    private final String title;
    private final String content;
    private final Member writer;
    private final Category category;
    private final int likeCount;

    public PostResponseDto(Post post) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.writer = post.getWriter();
        this.category = post.getCategory();
        this.likeCount = post.getLikes().size();
    }
}
