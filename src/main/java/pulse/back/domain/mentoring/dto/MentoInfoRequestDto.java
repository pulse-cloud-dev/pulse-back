package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import pulse.back.domain.member.dto.AcademicInfoRequestDto;
import pulse.back.domain.member.dto.CareerInfoRequestDto;
import pulse.back.domain.member.dto.CertificateInfoRequestDto;
import pulse.back.domain.member.dto.JobInfoRequestDto;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MentoInfoRequestDto(
        @Schema(description = "회원 학력사항")
        List<AcademicInfoRequestDto> academicInfoList,

        @Schema(description = "회원 자격증사항")
        List<CertificateInfoRequestDto> certificateInfoList,

        @Schema(description = "회원 직업정보")
        JobInfoRequestDto jobInfo,

        @NotNull
        @Schema(description = "회원 경력사항")
        List<CareerInfoRequestDto> careerInfoList,

        @Schema(description = "선호지역")
        List<String> preferredLocations,

        @Max(5000)
        @Schema(description = "멘토 소개")
        String mentorIntroduction
) {
}
