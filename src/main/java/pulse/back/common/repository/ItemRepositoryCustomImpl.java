package pulse.back.common.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pulse.back.domain.category.dto.GetItemCodeListResponseDto;
import pulse.back.domain.member.dto.JobInfoResponseDto;
import pulse.back.entity.common.Item;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Primary
@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<List<JobInfoResponseDto>> findJobInfo() {
        Query query = new Query(Criteria.where("categoryCode").is("JOB"));

        return mongoOperations.find(query, Item.class)
                .map(item -> new JobInfoResponseDto(
                        item.name(),
                        item.code()
                ))
                .collectList();
    }

    @Override
    public Mono<List<GetItemCodeListResponseDto>> findByItemCode(String categoryCode) {
        Query query = new Query(Criteria.where("categoryCode").is(categoryCode));

        return mongoOperations.find(query, Item.class)
                .map(GetItemCodeListResponseDto::of)
                .collectList();
    }

    @Override
    public Mono<Boolean> existsByItemCode(String itemCode) {
        Query query = new Query(Criteria.where("code").is(itemCode));

        return mongoOperations.exists(query, Item.class);
    }
}
