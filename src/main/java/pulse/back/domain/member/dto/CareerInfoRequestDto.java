package pulse.back.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import pulse.back.common.enums.RoleLevel;
import pulse.back.entity.member.CareerInfo;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CareerInfoRequestDto(
        //회사명
        @Schema(description = "회사명", example = "네이버")
        String companyName,

        //부서
        @Schema(description = "부서", example = "개발부")
        String department,

        //직급
        @Schema(description = "직급", example = "RoleLevel 으로 입력 : TEAM_MEMBER, TEAM_LEADER, MANAGER, EXECUTIVE,,,,")
        RoleLevel position,

        //입사년월
        String joinDate,

        //퇴사년월
        String retireDate,

        //근무여부
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
