package pulse.back.domain.chat.dto;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public record MessageRequest(
        MessageType messageType,
        String roomId,
        Object payload
) implements Message{
    public MessageDto toMessageDto(ObjectId memberId) {
        return MessageDto.of(new ObjectId(), memberId, roomId, messageType, payload.toString(), LocalDateTime.now(), null, null);
    }
}
