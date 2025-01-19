package pulse.back.common.util;

import org.springframework.stereotype.Component;
import pulse.back.common.config.GlobalPatterns;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import io.micrometer.common.util.StringUtils;
import java.util.regex.Pattern;

@Component
public class MyDateUtils {

    private static final DateTimeFormatter FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter FORMATTER_HHMM = DateTimeFormatter.ofPattern("HHmm");

    // 날짜
    public static LocalDate fromStringAsSTART(String yyyyMMdd){
        return fromString(yyyyMMdd);
    }

    public static LocalDate fromStringAsEND(String yyyyMMdd){
        LocalDate localDate = fromString(yyyyMMdd);
        return localDate==null ? null : localDate.plusDays(1);
    }

    public static LocalDate fromString(String yyyyMMdd){
        if(StringUtils.isEmpty(yyyyMMdd)) return null;
        if(!Pattern.matches(GlobalPatterns.YYYYMMDD, yyyyMMdd)) return null;

        return LocalDate.parse(yyyyMMdd, FORMATTER_YYYYMMDD);
    }

    //시간
    public static LocalTime timeFromString(String HHmm) {
        if (StringUtils.isEmpty(HHmm)) return null;
        // 정규식으로 HHMM 형식 검증 (00-23시, 00-59분)
        if (!Pattern.matches("^([0-1][0-9]|2[0-3])[0-5][0-9]$", HHmm)) return null;

        return LocalTime.parse(HHmm, FORMATTER_HHMM);
    }

    // LocalTime을 HHMM 문자열로 변환
    public static String timeToString(LocalTime time) {
        if (time == null) return null;
        return time.format(FORMATTER_HHMM);
    }
}