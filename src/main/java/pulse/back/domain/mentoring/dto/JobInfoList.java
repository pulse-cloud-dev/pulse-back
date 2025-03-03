package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import pulse.back.domain.member.dto.JobInfoResponseDto;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record JobInfoList(
        @Schema(description = "직업 카테고리 코드")
        String jobCategoryCode,

        @Schema(description = "직업 카테고리 이름")
        String jobCategoryName,

        @Schema(description = "카테고리에 해당하는 직업 정보 리스트")
        List<JobInfoResponseDto> jobInfoList
) {
}
