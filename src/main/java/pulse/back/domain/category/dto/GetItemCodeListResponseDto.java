package pulse.back.domain.category.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import pulse.back.entity.common.Category;
import pulse.back.entity.common.Item;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetItemCodeListResponseDto(
        @Schema(description = "아이템 이름")
        String name,

        @Schema(description = "아이템 코드 설명")
        String description,

        @Schema(description = "아이템 코드")
        String code
) {
    public static GetItemCodeListResponseDto of(Item item) {
        return new GetItemCodeListResponseDto(item.name(), item.description(), item.code());
    }
}
