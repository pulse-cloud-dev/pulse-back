package pulse.back.domain.chat.dto.entity;

import org.bson.types.ObjectId;
import pulse.back.domain.chat.dto.MessageType;
import pulse.back.entity.chat.Message;

import java.time.LocalDateTime;
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
        return Message.of(id, memberId, roomId, messageType, content, sentAt, deliveredAt, seenBy);
    }

    public static MessageDto from(Message message) {
        return MessageDto.of(message.id(), message.memberId(), message.roomId(), message.messageType(), message.content(),
                message.sentAt(), message.deliveredAt(), message.seenBy());
    }
}
