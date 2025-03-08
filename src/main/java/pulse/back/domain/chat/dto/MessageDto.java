package pulse.back.domain.chat.dto;

import org.bson.types.ObjectId;
import pulse.back.entity.chat.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record MessageDto(
        ObjectId id,
        ObjectId memberId,
        String roomId,
        MessageType messageType,
        String content,
        LocalDateTime sentAt,
        LocalDateTime deliveredAt,
        Map<ObjectId, LocalDateTime> seenBy
) {
    public static MessageDto of(ObjectId id, ObjectId memberId, String roomId, MessageType messageType, String content,
                                LocalDateTime sentAt, LocalDateTime deliveredAt, Map<ObjectId, LocalDateTime> seenBy) {
        return new MessageDto(id, memberId, roomId, messageType, content, sentAt, deliveredAt, seenBy);
    }

    public Message toEntity() {
        return new Message(id, memberId, roomId, messageType, content, sentAt, deliveredAt, seenBy);
    }
}
