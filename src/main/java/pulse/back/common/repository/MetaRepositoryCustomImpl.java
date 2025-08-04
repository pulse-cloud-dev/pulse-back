package pulse.back.common.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pulse.back.domain.category.dto.GetItemCodeListResponseDto;
import pulse.back.domain.category.dto.GetMetaCodeListResponseDto;
import pulse.back.entity.common.Item;
import pulse.back.entity.common.Meta;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Slf4j
@Primary
@RequiredArgsConstructor
public class MetaRepositoryCustomImpl implements MetaRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<List<GetMetaCodeListResponseDto>> findByMetaCode(String itemCode) {
        Query query = new Query(Criteria.where("itemCode").is(itemCode));

        return mongoOperations.find(query, Meta.class)
                .map(GetMetaCodeListResponseDto::of)
                .collectList();
    }

    @Override
    public Mono<Boolean> existsByMetaCode(String metaCode) {
        Query query = new Query(Criteria.where("code").is(metaCode));
        return mongoOperations.exists(query, Meta.class);
    }

    @Override
    public Mono<List<String>> findAllByItemCode(Set<String> regionCodes) {
        Query query = new Query(Criteria.where("regionCode").in(regionCodes));
        return mongoOperations.find(query, Meta.class)
                .map(Meta::code)
                .collectList();
    }

    @Override
    public Mono<Meta> findByCode(String code) {
        Query query = new Query(Criteria.where("code").is(code));
        return mongoOperations.findOne(query, Meta.class);
    }
}
