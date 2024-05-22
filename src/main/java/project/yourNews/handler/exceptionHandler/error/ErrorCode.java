package project.yourNews.handler.exceptionHandler.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 올바르지 않은 파라미터 값을 보낼 때.
    INVALID_USERNAME_PATTERN(400, "아이디는 특수문자를 제외한 4~20자리여야 합니다."),
    INVALID_NICKNAME_PATTERN(400, "닉네임은 특수문자를 제외한 2~10자리여야 합니다."),

    // 인증이 되어 있지 않을 때.
    UNAUTHORIZED(401, "접근 권한이 없습니다."),
    ACCESS_TOKEN_EXPIRED(401, "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(401, "Access Token이 잘못되었습니다."),

    // AccessToken 관련 오류
    BLACKLIST_ACCESS_TOKEN(401, "접근 불가한 AccessToken입니다."),

    // RefreshToken 인증 중 오류
    REFRESH_TOKEN_NOT_FOUND(404, "RefreshToken이 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(404, "RefreshToken이 만료되었습니다."),

    // 로그인 과정 에러.
    USER_INVALID_PASSWORD(404, "잘못된 비밀번호입니다."),
    INVALID_USER_INFO(404, "정보를 정확히 입력해주세요."),

    // 존재하지 않는 값을 보낼 때.
    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    POST_NOT_FOUND(404, "존재하지 않는 글입니다."),
    CATEGORY_NOT_FOUND(404, "존재하지 않는 카테고리입니다."),
    NEWS_NOT_FOUND(404, "해당 소식은 존재하지 않습니다."),
    URL_NOT_FOUND(404, "해당하는 URL이 없습니다"),
    LIKE_NOT_FOUND(404, "해당 게시글을 좋아요 누른 적이 없습니다."),

    // 인증 메일 관련 오류.
    ALREADY_MAIL_REQUEST(429, "1분 후 재전송 해주세요."),
    INVALID_MAIL_ADDRESS(404, "잘못된 이메일입니다."),
    CODE_EXPIRED(410, "유효시간이 지났습니다."),
    INVALID_CODE(400, "인증번호가 일치하지 않습니다."),

    // 이미 존재하는 값을 보냈을 때.
    ALREADY_LIKE_POST(409, "이미 해당 게시글에 좋아요를 눌렀습니다."),
    ALREADY_EXISTS_MAIL(409, "이미 존재하는 이메일입니다."),

    // 서버 에러
    INTERNAL_SERVER_ERROR(500, "서버 에러입니다. 서버 팀에 연락주세요.");

    private final int status;
    private final String message;
}
