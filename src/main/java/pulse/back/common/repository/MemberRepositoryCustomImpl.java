package pulse.back.common.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.entity.member.Member;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Slf4j
@Primary
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;


    @Override
    public Mono<Boolean> updateMemberPassword(String email, String newPassword, ServerWebExchange exchange) {
        Query query = new Query(Criteria.where("email").is(email));
        Update update = new Update()
                .set("password", newPassword)
                .set("updatedAt", OffsetDateTime.now());

        return mongoOperations.updateFirst(query, update, Member.class)
                .map(updateResult -> updateResult.getModifiedCount() > 0)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> checkMentorInfoExists(ObjectId memberId) {
        Query query = new Query(Criteria.where("id").is(memberId)
                .and("careerInfo").exists(true));

        return mongoOperations.exists(query, Member.class);
    }
}

