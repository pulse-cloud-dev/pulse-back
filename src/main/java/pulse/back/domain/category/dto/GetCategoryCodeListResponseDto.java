package pulse.back.domain.category.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import pulse.back.entity.common.Category;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetCategoryCodeListResponseDto(
        @Schema(description = "카테고리 이름")
        String name,

        @Schema(description = "카테고리 코드 설명")
        String description,

        @Schema(description = "카테고리 코드")
        String code
) {
    public static GetCategoryCodeListResponseDto of(Category category) {
        return new GetCategoryCodeListResponseDto(category.name(), category.description(), category.code());
    }
}
