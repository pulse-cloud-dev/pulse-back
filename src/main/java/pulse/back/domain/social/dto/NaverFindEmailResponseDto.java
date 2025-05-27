package pulse.back.domain.social.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NaverFindEmailResponseDto(
        @Schema(description = "이메일")
        String email,

        @Schema(description = "이름")
        String name
) {
}
