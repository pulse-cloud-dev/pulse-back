package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MentoringViewLogRepositoryCustom {
    Mono<Boolean> checkViewLogByIp(ObjectId mentoringId, String ipAddress);
    Mono<Boolean> checkViewLogByMemberId(ObjectId mentoringId, ObjectId memberId);
}
