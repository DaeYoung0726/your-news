package project.yourNews.stibee.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class Subscribers {

    private String email;
    private String name;
}
