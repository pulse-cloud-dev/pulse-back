package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import pulse.back.common.config.GlobalPatterns;
import pulse.back.common.enums.LectureType;
import pulse.back.entity.member.Member;
import pulse.back.entity.mentoring.Mentoring;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MentoringPostRequestDto(
        @Pattern(regexp = GlobalPatterns.TITLE_100, message = "제목은 최대 100자까지 입력 가능합니다.")
        @Schema(description = "멘토링 제목", example = "자소서 맨토링입니다.")
        String title,

        @Pattern(regexp = GlobalPatterns.CONTENT_3000, message = "내용은 최대 3000자까지 입력 가능합니다.")
        @Schema(description = "멘토링 내용", example = "자소서 작성법을 알려드립니다.")
        String content,

        @Pattern(regexp = GlobalPatterns.YYYYMMDD, message = "날짜는 yyyyMMdd 형식으로 입력해주세요.")
        @Schema(description = "멘토링 모집마감 기한", example = "yyyyMMdd")
        String deadlineDate,

        @Pattern(regexp = GlobalPatterns.HHMM, message = "시간은 HHMM 형식으로 입력해주세요.")
        @Schema(description = "멘토링 모집마감 시간", example = "HHMM")
        String deadlineTime,

        @Pattern(regexp = GlobalPatterns.YYYYMMDD, message = "날짜는 yyyyMMdd 형식으로 입력해주세요.")
        @Schema(description = "멘토링 시작일", example = "yyyyMMdd")
        String startDate,

        @Pattern(regexp = GlobalPatterns.YYYYMMDD, message = "날짜는 yyyyMMdd 형식으로 입력해주세요.")
        @Schema(description = "멘토링 마감일", example = "yyyyMMdd")
        String endDate,

        @Schema(description = "강의형식", example = "LectureType : ONLINE, OFFLINE")
        LectureType lectureType,

        @Pattern(regexp = GlobalPatterns.TEXT_50, message = "온라인 플랫폼은 50자 이내로 작성 가능합니다.")
        @Schema(description = "온라인 플랫폼", example = "zoom, google_meet (미입력시 '미정'으로 등록됩니다.)")
        String onlinePlatform,

        @Schema(description = "주소", example = "서울시 강남구")
        String address,

        @Pattern(regexp = GlobalPatterns.ADDRESS_DETAIL_255, message = "상세주소는 최대 255자까지 입력 가능합니다.")
        @Schema(description = "상세주소", example = "길바닥 어딘가")
        String detailAddress,

        @Max(254)
        @Schema(description = "모집인원", example = "50 (최대 254)")
        int recruitNumber,

        @Schema(description = "멘토링 비용", example = "25000")
        BigDecimal cost
) {
}
