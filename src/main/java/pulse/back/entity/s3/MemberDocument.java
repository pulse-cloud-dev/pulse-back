package pulse.back.entity.s3;

import nonapi.io.github.classgraph.json.Id;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public record MemberDocument(
        @Id
        ObjectId id,

        // 파일경로
        String filePath,

        // 생성일
        @Indexed
        LocalDateTime createdAt,

        // 생성자
        ObjectId createdMemberId
){
}