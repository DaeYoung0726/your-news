package project.yourNews.domains.subNews.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter @Getter
@NoArgsConstructor
public class SubNewsUpdateDto {

    private List<String> newsNames;

    private List<String> keywords;
}
