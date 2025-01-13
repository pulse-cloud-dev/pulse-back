package pulse.back.common;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class GlobalVariables {

    @Value("${jwt.access-token.timeout:1}")
    private long accessTokenTimeout;

    @Value("${jwt.access-token.timeunit:HOURS}")
    private TimeUnit accessTokenTimeUnit;

    @Value("${jwt.refresh-token.timeout:7}")
    private long refreshTokenTimeout;

    @Value("${jwt.refresh-token.timeunit:DAYS}")
    private TimeUnit refreshTokenTimeUnit;

    @Value("${aws.access-key}")
    private String awsAccessKey;

    @Value("${aws.secret-key}")
    private String awsSecretKey;

    private static long ACCESS_TOKEN_EXPIRED_TIME;
    private static long REFRESH_TOKEN_EXPIRED_TIME;
    private static String AWS_ACCESS_KEY;
    private static String AWS_SECRET_KEY;

    public static final String KAKAO_LOGIN_PATH = "https://kauth.kakao.com/oauth/authorize?client_id=0ff15f2cbe3c3db523d374e4be7595dd&redirect_uri=http://localhost:8080/api/v1/members/join/kakao-redirect&response_type=code";
    public static final String GOOGLE_LOGIN_PATH = "";

    @PostConstruct
    private void init() {
        ACCESS_TOKEN_EXPIRED_TIME = calculateTokenExpiredTime(accessTokenTimeout, accessTokenTimeUnit);
        REFRESH_TOKEN_EXPIRED_TIME = calculateTokenExpiredTime(refreshTokenTimeout, refreshTokenTimeUnit);
        AWS_ACCESS_KEY = awsAccessKey;
        AWS_SECRET_KEY = awsSecretKey;
    }

    public static long getAccessTokenExpiredTime() {
        return ACCESS_TOKEN_EXPIRED_TIME;
    }

    public static long getRefreshTokenExpiredTime() {
        return REFRESH_TOKEN_EXPIRED_TIME;
    }

    public static String getAwsAccessKey() {
        return AWS_ACCESS_KEY;
    }

    public static String getAwsSecretKey() {
        return AWS_SECRET_KEY;
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