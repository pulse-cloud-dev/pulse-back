package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pulse.back.entity.common.Meta;

@Repository
public interface MetaRepository extends ReactiveMongoRepository<Meta, ObjectId> {
}
