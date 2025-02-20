package pulse.back.domain.mentoring.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.entity.mentoring.Mentoring;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MentoringRepositoryCustom{
    Mono<Void> insertMentorInfo(ObjectId mentorId, MentoInfoRequestDto requestDto);

//    Flux<List<Mentoring>> getMentoringList(
//            LocalDate startDate,
//            LocalDate endDate,
//            ProjectStatus status,
//            Integer searchType,
//            String searchText,
//            int page,
//            int size,
//            Sort.Direction sort
//    );
//
//    Mono<Long> getMentoringListTotalCount(
//            LocalDate startDate,
//            LocalDate endDate,
//            ProjectStatus status,
//            Integer searchType,
//            String searchText
//    );
}
