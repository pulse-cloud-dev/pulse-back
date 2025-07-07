package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Repository
public interface MentoringBookmarksRepositoryCustom {
    Mono<Void> insertMentoringBookmark(ObjectId memberId, ObjectId mentoringId, OffsetDateTime createdAt);
}
