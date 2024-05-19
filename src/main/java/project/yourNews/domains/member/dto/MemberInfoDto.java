package project.yourNews.domains.member.dto;

import lombok.Getter;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.domain.Role;

@Getter
public class MemberInfoDto {

    private final Long id;
    private final String nickname;
    private final Role role;

    public MemberInfoDto(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.role = member.getRole();
    }
}