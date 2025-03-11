package pulse.back.domain.chat.repository;

import pulse.back.entity.chat.Room;
import reactor.core.publisher.Mono;

public interface RoomRepositoryCustom {
    Mono<Room> updateMemberCount(String roomId);
}
