package pulse.back.domain.member.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.QueryPathResolver;
import pulse.back.entity.member.Member;
import pulse.back.entity.member.QMember;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Primary
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;
    private final QMember MEMBER = QMember.member;


    @Override
    public Mono<Boolean> updateMemberPassword(String email, String newPassword, ServerWebExchange exchange) {
        Query query = new Query(Criteria.where(QueryPathResolver.get(MEMBER.email)).is(email));
        Update update = new Update()
                .set(QueryPathResolver.get(MEMBER.password), newPassword)
                .set(QueryPathResolver.get(MEMBER.updatedAt), LocalDateTime.now());

        return mongoOperations.updateFirst(query, update, Member.class)
                .map(updateResult -> updateResult.getModifiedCount() > 0)
                .defaultIfEmpty(false);
    }
}

