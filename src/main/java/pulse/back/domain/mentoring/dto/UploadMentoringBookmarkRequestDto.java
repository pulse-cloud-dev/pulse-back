package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import pulse.back.entity.mentoring.MentoringBookmarks;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UploadMentoringBookmarkRequestDto(
        @NotNull
        @Schema(description = "멘토링 글 ID")
        String mentoringId,

        @NotNull
        @Schema(description = "북마크 여부", example = "true")
        Boolean isBookmark
) {
    public static MentoringBookmarks from(UploadMentoringBookmarkRequestDto requestDto, ObjectId memberId) {
        return new MentoringBookmarks(
                new ObjectId(),
                new ObjectId(requestDto.mentoringId()),
                memberId,
                LocalDateTime.now(),
                memberId
        );
    }
}
