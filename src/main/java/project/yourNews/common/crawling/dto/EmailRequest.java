package project.yourNews.common.crawling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class EmailRequest implements Serializable {

    private List<String> subscriber;
    private String title;
    private String content;
}
