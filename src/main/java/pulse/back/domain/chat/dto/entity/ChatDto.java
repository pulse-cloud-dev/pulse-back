package pulse.back.domain.chat.dto.entity;

import org.bson.types.ObjectId;

public record ChatDto(
        String roomId,
        ObjectId memberId
) {
    public static ChatDto of(String roomId, ObjectId memberId) {
        return new ChatDto(roomId, memberId);
    }
}
