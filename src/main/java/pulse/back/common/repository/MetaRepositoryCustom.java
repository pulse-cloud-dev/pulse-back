package pulse.back.common.repository;

import org.springframework.stereotype.Repository;
import pulse.back.domain.category.dto.GetMetaCodeListResponseDto;
import pulse.back.entity.common.Meta;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MetaRepositoryCustom {
    Mono<List<GetMetaCodeListResponseDto>> findByMetaCode(String itemCode);
    Mono<Boolean> existsByMetaCode(String metaCode);

    Mono<List<String>> findAllByItemCode(Set<String> regionCodes);

    Mono<Meta> findByCode(String code);
}
