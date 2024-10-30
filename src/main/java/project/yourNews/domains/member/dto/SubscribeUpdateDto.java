package project.yourNews.domains.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubscribeUpdateDto {

    private String username;
    private boolean subStatus;
    private boolean dailySubStatus;
}
