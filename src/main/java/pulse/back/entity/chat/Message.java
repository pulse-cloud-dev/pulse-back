package pulse.back.entity.chat;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pulse.back.domain.chat.dto.MessageType;

import java.time.LocalDateTime;
import java.util.Map;

@Document
public record Message(
        // pk
        @Id
        ObjectId id,

        ObjectId memberId,

        String roomId,

        MessageType messageType,

        String content,

        @Indexed
        LocalDateTime sentAt,   // 서버에서 수신한 시간

        LocalDateTime deliveredAt,      // 클라이언트로 전송한 시간

        Map<ObjectId, LocalDateTime> seenBy    // 클라이언트에서 읽은 시간
) {
}
