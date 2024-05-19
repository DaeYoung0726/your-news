package project.yourNews.domains.member.dto;

import lombok.Getter;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.domain.Role;
import project.yourNews.domains.post.domain.Post;
import project.yourNews.domains.subNews.domain.SubNews;

import java.util.List;

@Getter
public class MemberResponseDto {

    private final Long id;
    private final String username;
    private final String nickname;
    private final String email;
    private final Role role;
    private final List<SubNews> subNews;    // SubNewsResponseDto로 바꾸기
    private final List<Post> posts;         // PostResponseDto로 바꾸기

    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.role = member.getRole();
        this.subNews = member.getSubNews();
        this.posts = member.getPosts();
    }
}
