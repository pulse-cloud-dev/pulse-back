package pulse.back.entity.chat;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pulse.back.domain.chat.dto.MessageType;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    public static Message of(ObjectId id, ObjectId memberId, String roomId, MessageType messageType, String content,
                             LocalDateTime sentAt, LocalDateTime deliveredAt, Map<ObjectId, LocalDateTime> seenBy) {
        return new Message(id, memberId, roomId, messageType, content, sentAt, deliveredAt, seenBy);
    }

    public static Message createInfo(ObjectId memberId, String roomId) {
        HashMap<ObjectId, LocalDateTime> seenBy = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        seenBy.put(memberId, now);
        return new Message(new ObjectId(), memberId, roomId, MessageType.INFO, "입장하셨습니다.", now, now, seenBy);
    }

    public Message updateSeenBy(ObjectId memberId) {
        this.seenBy.putIfAbsent(memberId, LocalDateTime.now());
        return this;
    }
}
