package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import org.bson.types.ObjectId;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MentoInfoDetailResponseDto(
        @Schema(description = "멘토 ID")
        ObjectId mentoId

) {
}
