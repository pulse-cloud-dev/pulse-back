package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pulse.back.entity.mentoring.MentoringViewLog;

@Repository
public interface MentoringViewLogRepository extends ReactiveMongoRepository<MentoringViewLog, ObjectId>, MentoringViewLogRepositoryCustom {
}
