package pulse.back.domain.mentoring.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.LectureType;
import pulse.back.common.enums.SortType;
import pulse.back.domain.mentoring.dto.JobInfoList;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.domain.mentoring.dto.MentoringListResponseDto;
import pulse.back.entity.mentoring.Mentoring;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MentoringRepositoryCustom{
    Mono<List<JobInfoList>> findJobInfo();

    Mono<Void> insertMentorInfo(ObjectId mentorId, MentoInfoRequestDto requestDto);

    Flux<List<MentoringListResponseDto>> getMentoringList(
            String field, LectureType lectureType, String region, SortType sortType, String searchText, int page, int size, ServerWebExchange exchange
    );

    Mono<Long> getMentoringListTotalCount(
            String field, LectureType lectureType, String region, SortType sortType, String searchText
    );
}
