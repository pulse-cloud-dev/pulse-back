package pulse.back.common.repository;

import io.netty.util.AsyncMapping;
import org.springframework.stereotype.Repository;
import pulse.back.domain.category.dto.GetItemCodeListResponseDto;
import pulse.back.domain.member.dto.JobInfoResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface ItemRepositoryCustom {
    Mono<List<JobInfoResponseDto>> findJobInfo();

    Mono<List<GetItemCodeListResponseDto>> findByItemCode(String itemCode);

    Mono<Boolean> existsByItemCode(String itemCode);
}
