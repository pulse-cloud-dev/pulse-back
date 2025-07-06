package pulse.back.entity.mentoring;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Document
public record MentoringViewLog(
        // pk
        @Id
        ObjectId id,

        // mentoring pk
        ObjectId mentoringId,

        // memberId
        ObjectId memberId,

        //ip address
        String ipAddress,

        // 생성일
        @Indexed
        OffsetDateTime createdAt,

        // 생성자
        ObjectId createdMemberId
) {
}
