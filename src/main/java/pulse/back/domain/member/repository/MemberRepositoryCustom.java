package pulse.back.domain.member.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.domain.member.dto.PasswordResetRequestDto;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepositoryCustom {
    Mono<Boolean> updateMemberPassword(String email, String newPassword, ServerWebExchange exchange);
    Mono<Boolean> checkMentorInfoExists(ObjectId memberId);
}
