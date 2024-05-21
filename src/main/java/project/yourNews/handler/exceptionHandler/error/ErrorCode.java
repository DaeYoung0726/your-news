package project.yourNews.handler.exceptionHandler.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 올바르지 않은 파라미터 값을 보낼 때.
    INVALID_USERNAME_PATTERN(400, "아이디는 특수문자를 제외한 4~20자리여야 합니다."),
    INVALID_NICKNAME_PATTERN(400, "닉네임은 특수문자를 제외한 2~10자리여야 합니다."),

    // 존재하지 않는 값을 보낼 때.
    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    POST_NOT_FOUND(404, "존재하지 않는 글입니다."),
    CATEGORY_NOT_FOUND(404, "존재하지 않는 카테고리입니다."),
    NEWS_NOT_FOUND(404, "해당 소식은 존재하지 않습니다."),
    URL_NOT_FOUND(404, "해당하는 URL이 없습니다"),
    LIKE_NOT_FOUND(404, "해당 게시글을 좋아요 누른 적이 없습니다."),

    // 이미 존재하는 값을 보냈을 때.
    ALREADY_LIKE_POST(409, "이미 해당 게시글에 좋아요를 눌렀습니다.");

    private final int status;
    private final String message;
}
