package pulse.back.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GlobalVariables {
    public static long ACCESS_TOKEN_EXPIRED_TIME;   // ACCESS TOKEN 만료시간
    public static long REFRESH_TOKEN_EXPIRED_TIME;  // REFRESH TOKEN 만료시간


    @Autowired
    public void setTokenExpiredTime(
            @Value("${jwt.access-token.timeout:1}") long accessTokenTimeout,
            @Value("${jwt.access-token.timeunit:HOURS}") TimeUnit accessTokenTimeUnit,
            @Value("${jwt.refresh-token.timeout:7}") long refreshTokenTimeout,
            @Value("${jwt.refresh-token.timeunit:DAYS}") TimeUnit refreshTokenTimeUnit
    ){
        ACCESS_TOKEN_EXPIRED_TIME
                = calculateTokenExpiredTime(accessTokenTimeout,accessTokenTimeUnit);
        REFRESH_TOKEN_EXPIRED_TIME
                = calculateTokenExpiredTime(refreshTokenTimeout,refreshTokenTimeUnit);
    }

//    @Autowired
//    public void setAmazonConfig(
//            @Value("${aws.access-key}") String accessKey,
//            @Value("${aws.secret-key}") String secretKey
//    ){
//        AMAZON.ACCESS_KEY = accessKey;
//        AMAZON.SECRET_KEY = secretKey;
//    }


    // 토큰 만료시간 계산
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static long calculateTokenExpiredTime(long timeout, TimeUnit timeUnit) {
        long myExpiredTime = 0L;
        try{
            if(timeUnit ==TimeUnit.DAYS){
                myExpiredTime = 1000L * 60 * 60 * 24 * timeout;
                return myExpiredTime;
            }
            if(timeUnit ==TimeUnit.HOURS){
                myExpiredTime = 1000L * 60 * 60 * timeout;
                return myExpiredTime;
            }
            if(timeUnit ==TimeUnit.MINUTES){
                myExpiredTime = 1000L * 60 * timeout;
                return myExpiredTime;
            }
            if(timeUnit ==TimeUnit.SECONDS){
                myExpiredTime = 1000L * timeout;
                return myExpiredTime;
            }
        }catch (Exception ignore){}
        return myExpiredTime;
    }

    public static class AMAZON {
        public static String ACCESS_KEY;
        public static String SECRET_KEY;

        public static class CLOUD_WATCH_LOGS {
            public static final String GROUP_REQUEST = "REQUEST";
        }
    }
}
