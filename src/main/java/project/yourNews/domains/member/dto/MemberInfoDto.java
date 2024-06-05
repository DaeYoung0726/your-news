package project.yourNews.domains.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.yourNews.domains.member.domain.Member;

@Getter
@NoArgsConstructor
public class MemberInfoDto {

    private Long id;
    private String nickname;
    private String email;


    public MemberInfoDto(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
    }
}