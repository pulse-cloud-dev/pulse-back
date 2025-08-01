package pulse.back.common.util;

import org.springframework.stereotype.Component;
import pulse.back.common.config.GlobalPatterns;

import java.time.*;
import java.time.format.DateTimeFormatter;
import io.micrometer.common.util.StringUtils;
import java.util.regex.Pattern;

@Component
public class MyDateUtils {

    private static final DateTimeFormatter FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter FORMATTER_HHMM = DateTimeFormatter.ofPattern("HHmm");

    // 날짜
    public static LocalDateTime fromStringAsSTART(String yyyyMMdd){
        return fromString(yyyyMMdd);
    }

    public static LocalDateTime fromStringAsEND(String yyyyMMdd) {
        LocalDateTime localDate = fromString(yyyyMMdd);
        return localDate == null ? null : localDate.plusDays(1);
    }

    public static LocalDateTime fromDateAndTime(String yyyyMMdd, String hhmm) {
        if (StringUtils.isEmpty(yyyyMMdd) || StringUtils.isEmpty(hhmm)) return null;
        if (!Pattern.matches(GlobalPatterns.YYYYMMDD, yyyyMMdd)) return null;
        if (!Pattern.matches(GlobalPatterns.HHMM, hhmm)) return null;

        LocalDate date = LocalDate.parse(yyyyMMdd, FORMATTER_YYYYMMDD);
        LocalTime time = LocalTime.parse(hhmm, FORMATTER_HHMM);

        return LocalDateTime.of(date, time); // 반환 타입: LocalDateTime (KST 기준)
    }

    public static LocalDateTime fromString(String yyyyMMdd) {
        if (StringUtils.isEmpty(yyyyMMdd)) return null;
        if (!Pattern.matches(GlobalPatterns.YYYYMMDD, yyyyMMdd)) return null;

        return LocalDate.parse(yyyyMMdd, FORMATTER_YYYYMMDD).atStartOfDay();
    }

    public static LocalTime timeFromString(String HHmm) {
        if (StringUtils.isEmpty(HHmm)) return null;
        if (!Pattern.matches("^([0-1][0-9]|2[0-3])[0-5][0-9]$", HHmm)) return null;

        return LocalTime.parse(HHmm, FORMATTER_HHMM);
    }

    // LocalTime을 HHMM 문자열로 변환
    public static String timeToString(LocalTime time) {
        if (time == null) return null;
        return time.format(FORMATTER_HHMM);
    }
}