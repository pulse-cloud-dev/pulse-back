package pulse.back.common.util;

import org.springframework.stereotype.Component;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@Component
public class CheckDateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 날짜 문자열이 오늘 이후인지 검증
     * @param dateStr yyyyMMdd 형식의 날짜 문자열
     * @return 오늘 이후면 true, 아니면 false
     * @throws DateTimeParseException 날짜 형식이 잘못된 경우
     */
    public boolean isAfterToday(String dateStr) {
        OffsetDateTime today = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime targetDate = parseToOffsetDateTime(dateStr);
        return !targetDate.isBefore(today);
    }

    /**
     * 시작일이 종료일보다 이후가 아닌지 검증 (startDate <= endDate)
     * @param startDateStr yyyyMMdd 형식의 시작일
     * @param endDateStr yyyyMMdd 형식의 종료일
     * @return 시작일이 종료일보다 이후가 아니면 true, 아니면 false
     * @throws DateTimeParseException 날짜 형식이 잘못된 경우
     */
    public boolean isValidDateRange(String startDateStr, String endDateStr) {
        OffsetDateTime startDate = parseToOffsetDateTime(startDateStr);
        OffsetDateTime endDate = parseToOffsetDateTime(endDateStr);
        return !startDate.isAfter(endDate);
    }

    /**
     * 멘토링 요청 DTO의 모든 날짜를 검증
     * @param requestDto 멘토링 요청 DTO
     * @return 검증 성공시 empty Mono, 실패시 error Mono
     */
    public Mono<Void> validateMentoringDates(MentoringPostRequestDto requestDto) {
        try {
            // 1. 모든 날짜가 오늘 이후인지 검증
            if (!isAfterToday(requestDto.deadlineDate())) {
                return Mono.error(new CustomException(ErrorCodes.INVALID_DEADLINE_DATE));
            }

            if (!isAfterToday(requestDto.startDate())) {
                return Mono.error(new CustomException(ErrorCodes.INVALID_START_DATE));
            }

            if (!isAfterToday(requestDto.endDate())) {
                return Mono.error(new CustomException(ErrorCodes.INVALID_END_DATE));
            }

            // 2. startDate가 endDate보다 이후가 아닌지 검증
            if (!isValidDateRange(requestDto.startDate(), requestDto.endDate())) {
                return Mono.error(new CustomException(ErrorCodes.INVALID_DATE_RANGE));
            }

            return Mono.empty();

        } catch (DateTimeParseException e) {
            return Mono.error(new CustomException(ErrorCodes.INVALID_DATE_FORMAT));
        }
    }

    /**
     * yyyyMMdd 형식의 문자열을 OffsetDateTime으로 변환
     * @param dateStr yyyyMMdd 형식의 날짜 문자열
     * @return OffsetDateTime 객체 (시간은 00:00:00 UTC)
     * @throws DateTimeParseException 날짜 형식이 잘못된 경우
     */
    private OffsetDateTime parseToOffsetDateTime(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
    }
}