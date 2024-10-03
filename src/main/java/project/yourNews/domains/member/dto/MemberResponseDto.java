package project.yourNews.domains.member.dto;

import lombok.Getter;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.domain.Role;
import project.yourNews.domains.subNews.dto.SubNewsResponseDto;

import java.util.List;

@Getter
public class MemberResponseDto {

    private final Long id;
    private final String username;
    private final String nickname;
    private final String email;
    private final boolean subStatus;
    private final Role role;
    private final List<SubNewsResponseDto> subNews;

    public MemberResponseDto(Member member, List<String> keywords) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.subStatus = member.isSubStatus();
        this.role = member.getRole();

        this.subNews = member.getSubNews().stream()
                .map(subNews -> new SubNewsResponseDto(subNews, keywords))
                .toList();
    }
}
