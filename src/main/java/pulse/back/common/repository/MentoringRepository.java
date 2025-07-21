package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pulse.back.entity.mentoring.Mentoring;
import reactor.core.publisher.Flux;

@Repository
public interface MentoringRepository extends ReactiveMongoRepository<Mentoring, ObjectId>, MentoringRepositoryCustom {
}
