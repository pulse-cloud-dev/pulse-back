package pulse.back.domain.chat.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pulse.back.entity.chat.Chat;
import reactor.core.publisher.Mono;

public interface ChatRepository extends ReactiveMongoRepository<Chat, ObjectId> {
    Mono<Chat> findByRoomIdAndMemberId(String roomId, ObjectId memberId);
}
