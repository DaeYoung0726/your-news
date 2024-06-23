package project.yourNews.stibee.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class StibeeSubscribeRequest {

    private String eventOccurredBy;
    private String confirmEmailYN;
    private List<Subscriber> subscribers;
}
