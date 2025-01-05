package pulse.back.domain.admin.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.bson.Document; // 여기가 중요합니다!
import pulse.back.domain.member.dto.JobInfoResponseDto;
import pulse.back.entity.common.Item;
import reactor.core.publisher.Mono;

import java.util.List;

@Primary
@Slf4j
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

}
