package pulse.back.entity.common;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Document
public record Item(
        // pk
        @Id
        ObjectId id,

        // 카테고리 코드
        String categoryCode,

        // 아이템 이름
        String name,

        // 아이템 설명
        String description,

        // 아이템 코드
        String code,

        // 생성일
        @Indexed
        LocalDateTime createdAt,

        // 수정일
        LocalDateTime updatedAt,

        // 삭제일
        LocalDateTime deletedAt,

        // 생성자
        ObjectId createdMemberId,

        // 수정자
        ObjectId updatedMemberId,

        // 삭제자
        ObjectId deletedMemberId
) {
}
