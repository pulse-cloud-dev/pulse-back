package pulse.back.domain.chat.dto.response;

import pulse.back.domain.chat.dto.entity.RoomDto;

public record RoomResponse(
        String roomId,
        String title,
        int memberCount,
        String mentor
) {
    public static RoomResponse of(String roomId, String title, int memberCount, String mentorId) {
        return new RoomResponse(roomId, title, memberCount, mentorId);
    }

    public static RoomResponse from(RoomDto roomDto) {
        return RoomResponse.of(roomDto.roomId(), roomDto.title(), roomDto.memberCount(), roomDto.mentor().toHexString());
    }
}
