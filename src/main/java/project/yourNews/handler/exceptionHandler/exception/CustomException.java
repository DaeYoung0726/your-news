package project.yourNews.handler.exceptionHandler.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
}
