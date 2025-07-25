package pulse.back.entity.mentoring;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.LocalDateTime;

public record MenteeInfo(
        // 멘티 pk
        ObjectId menteeId,

        // 멘토링 신청일
        LocalDateTime applyAt
) {
}
