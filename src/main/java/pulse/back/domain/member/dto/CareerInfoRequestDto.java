package pulse.back.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import pulse.back.common.config.GlobalPatterns;
import pulse.back.common.enums.RoleLevel;
import pulse.back.entity.mento.CareerInfo;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CareerInfoRequestDto(
        @Schema(description = "회사명", example = "네이버")
        String companyName,

        @Schema(description = "부서", example = "개발부")
        String department,

        @Schema(description = "직급", example = "RoleLevel 으로 입력 : TEAM_MEMBER, TEAM_LEADER, MANAGER, EXECUTIVE,,,,")
        RoleLevel position,

        @Pattern(regexp = GlobalPatterns.YYYYMM, message = "입사년월은 YYYYMM 형식이어야 합니다.")
        @Schema(description = "입사년월", example = "202301")
        String joinDate,

        @Pattern(regexp = GlobalPatterns.YYYYMM, message = "퇴사년월은 YYYYMM 형식이어야 합니다.")
        @Schema(description = "퇴사년월", example = "202312")
        String retireDate,

        @Schema(description = "근무여부", example = "true")
        boolean isWorking
) {
    public static CareerInfo of(CareerInfoRequestDto requestDto){
        return new CareerInfo(
                requestDto.companyName(),
                requestDto.department(),
                requestDto.position(),
                requestDto.joinDate(),
                requestDto.retireDate(),
                requestDto.isWorking()
        );
    }

    public static List<CareerInfo> of(List<CareerInfoRequestDto> requestDtos){
        return requestDtos.stream()
                .map(CareerInfoRequestDto::of)
                .toList();
    }
}
