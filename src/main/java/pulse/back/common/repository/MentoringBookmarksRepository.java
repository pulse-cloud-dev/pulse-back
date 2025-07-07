package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pulse.back.entity.mentoring.MentoringBookmarks;

@Repository
public interface MentoringBookmarksRepository extends ReactiveMongoRepository<MentoringBookmarks, ObjectId>, MentoringBookmarksRepositoryCustom {
}
