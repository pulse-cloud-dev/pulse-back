package pulse.back.domain.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pulse.back.entity.chat.Message;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {
    private final ReactiveMongoTemplate mongoTemplate;

    public Flux<Message> findRecentMessagesByRoomId(String roomId) {
        Query query = Query.query(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Order.desc("sentAt")))
                .limit(100);

        return mongoTemplate.find(query, Message.class);
    }

}
