package pulse.back.domain.chat.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pulse.back.entity.chat.Room;

public interface RoomRepository extends ReactiveMongoRepository<Room, String> {
}
