package pulse.back.common.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CategoryRepositoryCustom {
    Mono<Boolean> existsByCategoryCode(String categoryCode);
}
