package pulse.back.common.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCodes {
    //400
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다. 회원가입 진행해주세요. "),

    INVALID_MEMBER_LOGIN_INFO(HttpStatus.BAD_REQUEST.value(), "아이디 또는 비밀번호가 올바르지 않습니다."),
    //401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "토큰이 잘못되었습니다."),
    //403
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "접근할 수 없습니다."),
    //404
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), " 존재하지 않습니다."),
    //419
    TOKEN_EXPIRED(HttpStatus.EXPECTATION_FAILED.value(), "토큰이 만료되었습니다.");

    private final int httpStatusCode;
    private final String message;

    ErrorCodes(int httpStatusCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public int httpStatusCode() {
        return httpStatusCode;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
