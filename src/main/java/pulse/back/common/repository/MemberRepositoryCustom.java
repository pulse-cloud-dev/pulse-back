package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepositoryCustom {
    Mono<Boolean> updateMemberPassword(String email, String newPassword, ServerWebExchange exchange);
    Mono<Boolean> checkMentorInfoExists(ObjectId memberId);
}
