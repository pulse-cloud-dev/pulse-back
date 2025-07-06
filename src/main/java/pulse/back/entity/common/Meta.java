package pulse.back.entity.common;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Document
public record Meta(
        // pk
        @Id
        ObjectId id,

        // 카테고리 코드
        String categoryCode,

        // 아이템 코드
        String itemCode,

        // 메타 데이터 이름
        String name,

        // 메타 데이터 설명
        String description,

        // 메타 데이터 코드
        String code,

        // 생성일
        @Indexed
        OffsetDateTime createdAt,

        // 수정일
        OffsetDateTime updatedAt,

        // 삭제일
        OffsetDateTime deletedAt,

        // 생성자
        ObjectId createdMemberId,

        // 수정자
        ObjectId updatedMemberId,

        // 삭제자
        ObjectId deletedMemberId
) {
}
