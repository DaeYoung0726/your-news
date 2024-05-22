package project.yourNews.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CodeDto {

    private String code;
    private final LocalDateTime createTime;
}
