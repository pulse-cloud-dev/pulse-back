package pulse.back.common.repository;

import io.netty.util.AsyncMapping;
import org.springframework.stereotype.Repository;
import pulse.back.domain.category.dto.GetItemCodeListResponseDto;
import pulse.back.domain.member.dto.JobInfoResponseDto;
import pulse.back.entity.common.Item;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepositoryCustom {
    Mono<List<JobInfoResponseDto>> findJobInfo();

    Mono<List<GetItemCodeListResponseDto>> findByItemCode(String itemCode);

    Mono<Boolean> existsByItemCode(String itemCode);

    Mono<List<String>> findAllByCategoryCode(String categoryCode);

    Mono<Item> findByCode(String code);
}
