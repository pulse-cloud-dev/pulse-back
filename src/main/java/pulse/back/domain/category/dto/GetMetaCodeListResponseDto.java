package pulse.back.domain.category.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import pulse.back.entity.common.Meta;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetMetaCodeListResponseDto(
        @Schema(description = "메타 이름")
        String name,

        @Schema(description = "메타 코드 설명")
        String description,

        @Schema(description = "메타 코드")
        String code
) {
    public static GetMetaCodeListResponseDto of(Meta meta) {
        return new GetMetaCodeListResponseDto(meta.name(), meta.description(), meta.code());
    }
}
