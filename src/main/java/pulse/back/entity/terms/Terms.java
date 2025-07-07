package pulse.back.entity.terms;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;

@Document
public record Terms(
        // PK
        @Id
        ObjectId id,

        // 사용 여부 (0: 미사용, 1: 사용)
        int used,

        // 필수 약관 여부 (0: 선택, 1: 필수)
        int required,

        // 제목
        String title,

        // 내용
        String content,

        // 약관이 사용되는 카테고리 리스트 (예: TERMS_PAYMENT, TERMS_MEMBER)
        List<String> categoryList,

        // 약관 만기일 (null: 무기한)
        LocalDateTime expiredAt,

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