package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pulse.back.entity.s3.MemberDocument;

@Repository
public interface MemberDocumentRepository extends ReactiveMongoRepository<MemberDocument, ObjectId>, MemberDocumentRepositoryCustom {

}
