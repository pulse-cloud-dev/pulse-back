package pulse.back.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class GlobalVariables {

    private static long accessTokenExpiredTime;
    private static long refreshTokenExpiredTime;
    private static String amazonAccessKey;
    private static String amazonSecretKey;

    public static final String KAKAO_LOGIN_PATH = "https://kauth.kakao.com/oauth/authorize?client_id=0ff15f2cbe3c3db523d374e4be7595dd&redirect_uri=http://localhost:8080/api/v1/members/join/kakao-redirect&response_type=code";
    public static final String GOOGLE_LOGIN_PATH = "";

    public GlobalVariables(
            @Value("${jwt.access-token.timeout}") long accessTokenTimeout,
            @Value("${jwt.access-token.timeunit}") TimeUnit accessTokenTimeUnit,
            @Value("${jwt.refresh-token.timeout}") long refreshTokenTimeout,
            @Value("${jwt.refresh-token.timeunit}") TimeUnit refreshTokenTimeUnit,
            @Value("${aws.access-key}") String accessKey,
            @Value("${aws.secret-key}") String secretKey
    ) {
        accessTokenExpiredTime = calculateTokenExpiredTime(accessTokenTimeout, accessTokenTimeUnit);
        refreshTokenExpiredTime = calculateTokenExpiredTime(refreshTokenTimeout, refreshTokenTimeUnit);
        amazonAccessKey = accessKey;
        amazonSecretKey = secretKey;
    }

    public static long accessTokenExpiredTime() {
        return accessTokenExpiredTime;
    }

    public static long refreshTokenExpiredTime() {
        return refreshTokenExpiredTime;
    }

    public static String amazonAccessKey() {
        return amazonAccessKey;
    }

    public static String amazonSecretKey() {
        return amazonSecretKey;
    }

    private static long calculateTokenExpiredTime(long timeout, TimeUnit timeUnit) {
        try {
            switch (timeUnit) {
                case DAYS:
                    return 1000L * 60 * 60 * 24 * timeout;
                case HOURS:
                    return 1000L * 60 * 60 * timeout;
                case MINUTES:
                    return 1000L * 60 * timeout;
                case SECONDS:
                    return 1000L * timeout;
                default:
                    return 0L;
            }
        } catch (Exception ignore) {
            return 0L;
        }
    }
}