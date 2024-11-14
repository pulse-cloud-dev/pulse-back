package pulse.back.domain.member.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.entity.member.Member;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Primary
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<Boolean> updateMemberPassword(String email, String newPassword, ServerWebExchange exchange) {
        Query query = new Query(Criteria.where("email").is(email));
        Update update = new Update()
                .set("password", newPassword)
                .set("updatedAt", LocalDateTime.now());

        return mongoOperations.updateFirst(query, update, Member.class)
                .map(updateResult -> updateResult.getModifiedCount() > 0)  // 수정된 문서가 있는지 확인
                .defaultIfEmpty(false);  // 아무것도 수정되지 않은 경우 false 반환
    }
}
