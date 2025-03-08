package pulse.back.domain.chat.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pulse.back.entity.chat.Message;

public interface MessageRepository extends ReactiveMongoRepository<Message, ObjectId> {
}
