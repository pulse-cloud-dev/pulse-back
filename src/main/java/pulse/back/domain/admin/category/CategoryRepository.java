package pulse.back.domain.admin.category;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pulse.back.entity.common.Category;

@Repository
public interface CategoryRepository extends ReactiveMongoRepository<Category, ObjectId>, CategoryRepositoryCustom {
}
