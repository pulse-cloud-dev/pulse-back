package pulse.back.domain.mentoring.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import reactor.core.publisher.Mono;

@Repository
public interface MentoringRepositoryCustom{
    Mono<Void> insertMentorInfo(ObjectId mentorId, MentoInfoRequestDto requestDto);
}
