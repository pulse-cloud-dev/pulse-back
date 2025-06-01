package pulse.back.common.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pulse.back.entity.common.Category;
import reactor.core.publisher.Mono;

@Slf4j
@Primary
@RequiredArgsConstructor
public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<Boolean> existsByCategoryCode(String categoryCode) {
        Query query = new Query(Criteria.where("code").is(categoryCode));

        return mongoOperations.exists(query, Category.class);
    }
}
