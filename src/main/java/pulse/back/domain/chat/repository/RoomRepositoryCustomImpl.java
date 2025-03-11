package pulse.back.domain.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import pulse.back.entity.chat.Room;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class RoomRepositoryCustomImpl implements RoomRepositoryCustom {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Room> updateMemberCount(String roomId) {
        Query query = Query.query(Criteria.where("roomId").is(roomId));
        Update update = new Update().inc("memberCount", 1);
        return mongoTemplate.findAndModify(query, update, Room.class);
    }
}
