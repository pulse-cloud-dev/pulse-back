package pulse.back.domain.member.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pulse.back.entity.member.Member;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends ReactiveMongoRepository<Member, ObjectId>, MemberRepositoryCustom {
    Mono<Member> findByEmail(String email);
    Mono<Member> findByNickName(String nickName);
}