package pulse.back.common.config;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class GlobalPatterns {
    // ID 패턴: 알파벳 및 숫자를 포함하고, 6~20자의 길이를 가짐
    public static final String ID = "^(?=.*[a-zA-Z])[a-zA-Z0-9_-]{6,20}$";

    // 비밀번호 패턴: 알파벳, 숫자, 특수문자를 포함하며 최소 8자 이상
    public static final String PASSWORD_ALL_CONFORM = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$\\-_,.<>])[a-zA-Z0-9!@#$\\-_,.<>]{8,}$";

    // 비밀번호 패턴: 알파벳, 숫자 또는 특수문자 중 두 가지 이상 포함하고 6~20자
    public static final String PASSWORD = "^(?:(?=.*[a-zA-Z])(?=.*[0-9])|(?=.*[a-zA-Z])(?=.*[!@#$\\-_,.<>])|(?=.*[0-9])(?=.*[!@#$\\-_,.<>]))[a-zA-Z0-9!@#$\\-_,.<>]{6,20}$";

    // 이름 패턴: 영어, 숫자, 한글 및 특수문자(-, _) 포함, 1~20자
    public static final String NAME = "^[A-Za-z0-9가-힣-_]{1,20}$";

    // 이메일 패턴: 일반적인 이메일 형식
    public static final String EMAIL = "^[a-zA-Z0-9._%+-\\.]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // 휴대폰 번호 패턴: 한국의 휴대폰 번호 형식
    public static final String PHONE_NUMBER = "^01[016789][0-9]{3,4}[0-9]{4}$";

    // 인증 코드 패턴: 6자리 숫자
    public static final String VERIFICATION_CODE = "^[0-9]{6}$";

    // 카드 번호 패턴: 16자리 숫자
    public static final String CARD_NUMBER = "^[0-9]{16}$";

    // HHMMSS 패턴: 시:분:초 형식 (24시간)
    public static final String HHMMSS = "^(0[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9]$";

    // 2자리 숫자 패턴
    public static final String DIGITS_2 = "^\\d{2}$";

    // YYYYMMDDHHMMSS 패턴: 날짜 및 시간 형식
    public static final String YYYYMMDDHHMMSS = "^(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])([01][0-9]|2[0-3])([0-5][0-9]){2}$";

    // YYYYMMDD 패턴: 날짜 형식
    public static final String YYYYMMDD = "^(19|20)\\d\\d(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$";

    // YYMMDD 패턴: 2자리 연도, 월, 일 형식
    public static final String YYMMDD = "^\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$";

    // YYMM 패턴: 2자리 연도, 월 형식
    public static final String YYMM = "^\\d{2}(0[1-9]|1[0-2])$";

    // UUID 패턴: UUID 형식
    public static final String UUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    // 은행 코드 패턴: 3자리 숫자
    public static final String BANK_CODE = "^[0-9]{3}$";

    // 카드 번호 패턴: 13~16자리 숫자
    public static final String CARD_NO = "^\\d{13,16}$";

    // 6~10자리 숫자 패턴
    public static final String DIGIT_6_10 = "^\\d{6}(\\d{4})?$";

    // 2자리 숫자 패턴
    public static final String DIGIT_2 = "^[0-9]{2}$";

    // 회사 등록 번호 패턴: 10자리 숫자
    public static final String COMPANY_REGISTRATION_NUMBER = "^[0-9]{10}$";

    // 주소 1 패턴: 5~6자리 숫자 (우편번호 등)
    public static final String ADDRESS_1 = "^[0-9]{5,6}$";

    // S3 파일 디렉토리 패턴: S3 버킷 경로 형식
    public static final String S3_FILE_DIR = "^[^/](?:[^/]+/)+$";

    // 실수형 패턴: 양수 및 음수 포함
    public static final String ONLY_DOUBLE_TYPE = "^[+-]?\\d+(\\.\\d+)?$";

    // 양수 실수형 패턴
    public static final String ONLY_POSITIVE_DOUBLE_TYPE = "^\\d+(\\.\\d+)?$";

    // 시간대 패턴: +HH:MM 또는 -HH:MM 형식
    public static final String TIMEZONE = "^[+-](?:2[0-3]|[01][0-9]):[0-5][0-9]$";

    // BSON ObjectId 패턴: 24자리 16진수
    public static final String BSON_OBJECT_ID = "^[0-9a-fA-F]{24}$";

    public GlobalPatterns() {
    }

    public static class MATCHES {
        public MATCHES() {
        }

        // 비밀번호 검증 메소드
        public static boolean PASSWORD_CHECK(String password) {
            return Pattern.matches(PASSWORD, password);
        }

        // 이메일 검증 메소드
        public static boolean EMAIL_CHECK(String email) {
            return Pattern.matches(EMAIL, email);
        }

        // 전화번호 검증 메소드
        public static boolean PHONE_NUMBER_CHECK(String phoneNumber) {
            return Pattern.matches(PHONE_NUMBER, phoneNumber);
        }
    }
}
