package project.yourNews.common.mail.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class StibeeRequest implements Serializable {

    private String subscriber;
    private String title;
    private String content;
}

