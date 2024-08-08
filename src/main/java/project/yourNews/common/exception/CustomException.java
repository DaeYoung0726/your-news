package project.yourNews.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.yourNews.common.exception.error.ErrorCode;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
}
