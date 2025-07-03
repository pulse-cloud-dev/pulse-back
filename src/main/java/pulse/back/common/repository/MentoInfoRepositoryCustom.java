package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.entity.mento.MentoInfo;
import reactor.core.publisher.Mono;

@Repository
public interface MentoInfoRepositoryCustom {
    Mono<MentoInfo> findByMemberId(ObjectId memberId);
    Mono<Void> insertMentorInfo(ObjectId mentorId, MentoInfoRequestDto requestDto);
}
