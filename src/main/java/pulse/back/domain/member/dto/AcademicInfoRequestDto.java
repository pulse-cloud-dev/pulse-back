package pulse.back.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import pulse.back.common.config.GlobalPatterns;
import pulse.back.common.enums.EducationLevel;
import pulse.back.common.enums.EducationStatus;
import pulse.back.common.util.MyDateUtils;
import pulse.back.entity.mento.AcademicInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AcademicInfoRequestDto(
        @Schema(description = "학력구분", example = "EducationLevel 으로 입력 : UNDERGRADUATE_2, UNDERGRADUATE_4, MASTER, DOCTORATE,,,")
        EducationLevel educationLevel,

        @Schema(description = "학교명", example = "서울대학교")
        String schoolName,

        @Schema(description = "전공", example = "컴퓨터공학")
        String major,

        @Schema(description = "졸업여부", example = "EducationStatus 으로 입력 : GRADUATED, GRADUATING,,,")
        EducationStatus educationStatus,

        @Pattern(regexp = GlobalPatterns.YYYYMM)
        @Schema(description = "입학년월", example = "yyyyMM")
        String admissionDate,

        @Pattern(regexp = GlobalPatterns.YYYYMM)
        @Schema(description = "졸업년월", example = "yyyyMM")
        String graduationDate
) {
    public static AcademicInfo of(AcademicInfoRequestDto requestDto) {
        return new AcademicInfo(
                requestDto.educationLevel(),
                requestDto.schoolName(),
                requestDto.major(),
                requestDto.educationStatus(),
                requestDto.admissionDate(),
                requestDto.graduationDate()
        );
    }

    public static List<AcademicInfo> of(List<AcademicInfoRequestDto> requestDtoList) {
        return requestDtoList.stream()
                .map(AcademicInfoRequestDto::of)
                .toList();
    }
}
