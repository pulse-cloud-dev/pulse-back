package pulse.back.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import pulse.back.entity.member.JobInfo;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record JobInfoRequestDto(
        @Schema(description = "직업코드 (api 조회 후 사용할 것)", example = "JOB_DEV_PM")
        String jobCode
) {
    public static JobInfo of(JobInfoRequestDto requestDto) {
        return new JobInfo(
                requestDto.jobCode()
        );
    }

    public static List<JobInfo> of(List<JobInfoRequestDto> requestDtos) {
        return requestDtos.stream()
                .map(JobInfoRequestDto::of)
                .toList();
    }
}
