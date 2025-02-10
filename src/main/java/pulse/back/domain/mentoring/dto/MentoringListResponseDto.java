package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;
import pulse.back.common.config.GlobalPatterns;
import pulse.back.common.enums.LectureType;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MentoringListResponseDto(
        // 멘토링 ID
        String mentoringId,

        // 강의형식 (ONLINE, OFFLINE)
        LectureType lectureType,

        // 멘토링 제목
        String title,

        // 멘토 프로필 사진
        MultipartFile mentorProfileImage,

        // 멘토 직업
        String deadlineDate,

        String deadlineTime
) {
}
