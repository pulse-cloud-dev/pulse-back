package pulse.back.domain.chat.dto.entity;

import org.bson.types.ObjectId;
import pulse.back.entity.chat.Room;

public record RoomDto(
        String roomId,
        String title,
        int memberCount,
        ObjectId mentor
) {
    public static RoomDto of(String roomId, String title, int memberCount, ObjectId mentor) {
        return new RoomDto(roomId, title, memberCount, mentor);
    }

    public Room toEntity(ObjectId id) {
        return Room.of(roomId, title, memberCount, id);
    }

    public static RoomDto from(Room room) {
        return RoomDto.of(room.roomId(), room.title(), room.memberCount(), room.mentor());
    }
}
