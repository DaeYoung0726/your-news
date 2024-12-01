package project.yourNews.crawling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class EmailRequest implements Serializable {

    private String  subscriber;
    private String title;
    private String content;
}
