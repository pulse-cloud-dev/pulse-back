package pulse.back.common.repository;

import org.springframework.stereotype.Repository;
import pulse.back.domain.category.dto.GetMetaCodeListResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface MetaRepositoryCustom {
    Mono<List<GetMetaCodeListResponseDto>> findByMetaCode(String itemCode);
    Mono<Boolean> existsByMetaCode(String metaCode);
}
