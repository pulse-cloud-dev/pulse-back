package pulse.back.common.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pulse.back.entity.mentoring.MentoringViewLog;
import reactor.core.publisher.Mono;

@Primary
@Slf4j
@RequiredArgsConstructor
public class MentoringViewLogRepositoryImpl implements MentoringViewLogRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<Boolean> checkViewLogByIp(ObjectId mentoringId, String ipAddress) {
        Query query = new Query(
                Criteria.where("mentoringId").is(mentoringId)
                        .and("ipAddress").is(ipAddress)
                        .and("memberId").is(null)
        );

        return mongoOperations.exists(query, MentoringViewLog.class);
    }

    @Override
    public Mono<Boolean> checkViewLogByMemberId(ObjectId mentoringId, ObjectId memberId) {
        Query query = new Query(
                Criteria.where("mentoringId").is(mentoringId)
                        .and("memberId").is(memberId)
        );

        return mongoOperations.exists(query, MentoringViewLog.class);
    }
}
