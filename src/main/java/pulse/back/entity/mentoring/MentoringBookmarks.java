package pulse.back.entity.mentoring;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public record MentoringBookmarks(
        // pk
        @Id
        ObjectId id,

        // 멘토링 pk
        ObjectId mentoringId,

        // 멘토링 북마크한 멤버 pk
        ObjectId memberId,

        // 멘토링 북마크한 날짜
        LocalDateTime createdAt,

        ObjectId createdMemberId
) {
}

