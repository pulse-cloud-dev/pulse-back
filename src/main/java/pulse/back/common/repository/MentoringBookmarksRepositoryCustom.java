package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface MentoringBookmarksRepositoryCustom {
    Mono<Void> insertMentoringBookmark(ObjectId memberId, ObjectId mentoringId, LocalDateTime createdAt);
}
