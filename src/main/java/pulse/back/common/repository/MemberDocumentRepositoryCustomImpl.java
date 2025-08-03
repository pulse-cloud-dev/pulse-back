package pulse.back.common.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pulse.back.entity.s3.MemberDocument;
import reactor.core.publisher.Mono;

@Slf4j
@Primary
@RequiredArgsConstructor
public class MemberDocumentRepositoryCustomImpl implements MemberDocumentRepositoryCustom{
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<MemberDocument> findByMemberId(ObjectId memberId) {
        Query query = new Query(Criteria.where("memberId").is(memberId));
        return mongoOperations.findOne(query, MemberDocument.class)
                .doOnError(error -> log.error("Error finding MemberDocument by memberId: {}", memberId, error))
                .switchIfEmpty(Mono.empty());
    }
}
