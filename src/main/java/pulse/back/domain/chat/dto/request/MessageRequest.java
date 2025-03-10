package pulse.back.domain.chat.dto.request;

import org.bson.types.ObjectId;
import pulse.back.domain.chat.dto.Message;
import pulse.back.domain.chat.dto.MessageType;
import pulse.back.domain.chat.dto.entity.MessageDto;
import pulse.back.domain.chat.dto.entity.RoomDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public record MessageRequest(
        MessageType messageType,
        String roomId,
        Object payload
) implements Message {
    public MessageDto toMessageDto(ObjectId memberId) {
        HashMap<ObjectId, LocalDateTime> seenBy = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        seenBy.put(memberId, now);
        return MessageDto.of(new ObjectId(), memberId, roomId, messageType, payload.toString(), now, null, seenBy);
    }

    public RoomDto toRoomDto() {
        return RoomDto.of(UUID.randomUUID().toString(), payload.toString());
    }
}
