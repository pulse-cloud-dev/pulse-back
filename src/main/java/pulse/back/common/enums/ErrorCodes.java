package pulse.back.common.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCodes {
    //400
    BOOKMARK_REGISTRATION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST.value(), "등록 가능한 북마크 개수를 초과하였습니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 북마크입니다."),
    INVALID_MEMBER_NAME(HttpStatus.BAD_REQUEST.value(), "특수문자가 포함되어 사용이 불가능합니다."),
    MENTO_NOT_REGISTERED_USER(HttpStatus.BAD_REQUEST.value(), "멘토로 등록되지 않은 사용자입니다."),
    MENTORING_LIST_FAILED(HttpStatus.BAD_REQUEST.value(), "멘토링 목록조회에 실패하였습니다."),
    MENTORING_DETAIL_FAILED(HttpStatus.BAD_REQUEST.value(), "멘토링 상세조회에 실패하였습니다."),
    MENTO_INFO_REGISTER_FAILED(HttpStatus.BAD_REQUEST.value(), "멘토 정보 등록에 실패하였습니다."),
    MENTORING_REGISTER_FAILED(HttpStatus.BAD_REQUEST.value(), "멘토링 등록에 실패하였습니다."),
    MENTO_NOT_REGISTERED(HttpStatus.BAD_REQUEST.value(), "멘토로 등록되어 있지 않습니다."),
    MENTORING_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 멘토링입니다."),
    MENTORING_ALREADY_EXIST(HttpStatus.BAD_REQUEST.value(), "멘토링은 최대 5개까지 진행이 가능합니다. 진행중인 멘토링을 종료 후 다시 등록해주세요."),
    FILE_DELETE_FAILED(HttpStatus.BAD_REQUEST.value(), "파일 삭제에 실패했습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST.value(), "파일 업로드에 실패했습니다."),
    MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 회원입니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST.value(), "잘못된 JSON 형식입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다. 회원가입 진행해주세요. "),
    SOCIAL_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "소셜 로그인 정보가 올바르지 않습니다."),
    INVALID_CODE(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 코드입니다."),
    INVALID_MEMBER_LOGIN_INFO(HttpStatus.BAD_REQUEST.value(), "아이디 또는 비밀번호가 올바르지 않습니다."),
    INVALID_DEADLINE_DATE(HttpStatus.BAD_REQUEST.value(), "마감일은 오늘 이후여야 합니다"),
    INVALID_START_DATE(HttpStatus.BAD_REQUEST.value(), "시작일은 오늘 이후여야 합니다"),
    INVALID_END_DATE(HttpStatus.BAD_REQUEST.value(), "종료일은 오늘 이후여야 합니다"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST.value(), "시작일은 종료일보다 이후일 수 없습니다"),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST.value(), "날짜 형식이 올바르지 않습니다"),
    INVALID_ONLINE_ADDRESS(HttpStatus.BAD_REQUEST.value(),"온라인 강의는 주소 정보를 입력할 수 없습니다"),
    MISSING_OFFLINE_ADDRESS(HttpStatus.BAD_REQUEST.value(),"오프라인 강의는 주소를 반드시 입력해야 합니다"),
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST.value(), "주소가 잘못되었습니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST.value(),"올바른 값이 아닙니다."),
    REQUIRED_REQUEST_BODY_EMPTY(HttpStatus.BAD_REQUEST.value(),"요청값이 비어있습니다."),
    LENGTH_MISMATCH(HttpStatus.BAD_REQUEST.value(),"자리수가 일치하지 않습니다."),
    VALIDATION_CHECK_FOR_BIND_EXCEPTION(HttpStatus.BAD_REQUEST.value(),"바인드 익셉션을 위한 에러코드 (자동호출로 사용)"),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST.value(),"올바른 형식이 아닙니다."),
    MENTO_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST.value(), "이미 멘토로 등록된 사용자입니다."),

    //401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "토큰이 잘못되었습니다."),
    //403
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "접근할 수 없습니다."),

    //404
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), " 존재하지 않습니다."),
    //419
    TOKEN_EXPIRED(HttpStatus.EXPECTATION_FAILED.value(), "토큰이 만료되었습니다."),

    //500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류입니다.");


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
