package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import pulse.back.entity.s3.MemberDocument;
import reactor.core.publisher.Mono;

@Repository
public interface MemberDocumentRepositoryCustom {
    Mono<MemberDocument> findByMemberId(ObjectId memberId);
}
