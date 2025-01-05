package pulse.back.domain.admin.terms;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pulse.back.entity.terms.Terms;

public interface TermsRepository extends ReactiveMongoRepository<Terms, ObjectId>, TermsRepositoryCustom {
}
