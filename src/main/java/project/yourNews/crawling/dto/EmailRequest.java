package project.yourNews.crawling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EmailRequest implements Serializable {

    private List<String> emails;
    private String content;
}
