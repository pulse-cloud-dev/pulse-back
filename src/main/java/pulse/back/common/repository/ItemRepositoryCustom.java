package pulse.back.common.repository;

import org.springframework.stereotype.Repository;
import pulse.back.domain.member.dto.JobInfoResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface ItemRepositoryCustom {
    Mono<List<JobInfoResponseDto>> findJobInfo();
}
