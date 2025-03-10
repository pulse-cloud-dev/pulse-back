package pulse.back.domain.chat.dto.entity;

import pulse.back.entity.chat.Room;

public record RoomDto(
        String roomId,
        String title
) {
    public static RoomDto of(String roomId, String title) {
        return new RoomDto(roomId, title);
    }

    public Room toEntity() {
        return Room.of(roomId, title);
    }

    public static RoomDto from(Room room) {
        return RoomDto.of(room.roomId(), room.title());
    }
}
