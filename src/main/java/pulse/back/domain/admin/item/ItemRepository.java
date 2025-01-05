package pulse.back.domain.admin.item;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pulse.back.entity.common.Item;

public interface ItemRepository extends ReactiveMongoRepository<Item, ObjectId>, ItemRepositoryCustom {
}
