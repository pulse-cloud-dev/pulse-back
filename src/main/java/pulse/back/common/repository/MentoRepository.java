package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pulse.back.entity.mento.Mento;

@Repository
public interface MentoRepository extends ReactiveMongoRepository<Mento, ObjectId>, MentoRepositoryCustom {
}
