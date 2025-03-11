package pulse.back.domain.chat.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pulse.back.entity.chat.Room;
import reactor.core.publisher.Flux;

import java.util.List;

public interface RoomRepository extends ReactiveMongoRepository<Room, String>, RoomRepositoryCustom {
    Flux<Room> findByRoomIdIn(List<String> roomIds);
}
