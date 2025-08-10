package pulse.back.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pulse.back.entity.common.Item;
import pulse.back.entity.common.Meta;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegionCodeService {
    private final ReactiveMongoOperations mongoOperations;

    /**
     * 지역 코드를 실제 지역명으로 변환
     * @param regionCodes 지역 코드 리스트 (Item code 또는 Meta code)
     * @return 지역명 리스트 (예: ["서울", "인천"])
     */
    public Mono<List<String>> convertRegionCodesToNames(List<String> regionCodes) {
        if (regionCodes == null || regionCodes.isEmpty()) {
            return Mono.just(new ArrayList<>());
        }

        return Flux.fromIterable(regionCodes)
                .flatMap(this::getRegionNameFromCode)
                .filter(Objects::nonNull)
                .distinct()
                .collectList();
    }

    /**
     * 단일 지역 코드를 지역명으로 변환
     */
    private Mono<String> getRegionNameFromCode(String regionCode) {
        // 먼저 Item 테이블에서 확인
        Query itemQuery = Query.query(Criteria.where("code").is(regionCode));

        return mongoOperations.findOne(itemQuery, Item.class)
                .flatMap(item -> {
                    // Item에서 찾은 경우, 해당 Item의 name 반환
                    return Mono.just(item.name());
                })
                .switchIfEmpty(
                        // Item에서 찾지 못한 경우, Meta 테이블에서 확인
                        mongoOperations.findOne(Query.query(Criteria.where("code").is(regionCode)), Meta.class)
                                .flatMap(meta -> {
                                    // Meta에서 찾은 경우, itemCode로 Item을 찾아서 name 반환
                                    Query parentItemQuery = Query.query(Criteria.where("code").is(meta.itemCode()));
                                    return mongoOperations.findOne(parentItemQuery, Item.class)
                                            .map(Item::name);
                                })
                );
    }
}