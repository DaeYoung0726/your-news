package project.yourNews.domains.member.dto;

import lombok.Getter;
import lombok.Setter;
import project.yourNews.domains.member.domain.BannedEmail;

import java.time.LocalDateTime;

@Getter @Setter
public class BannedEmailRequestDto {

    private Long id;
    private String email;
    private String reason;
    private LocalDateTime bannedAt;

    public BannedEmail toBannedEntity() {

        return BannedEmail.builder()
                .email(email)
                .reason(reason)
                .bannedAt(bannedAt)
                .build();
    }
}
