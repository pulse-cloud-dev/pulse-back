package pulse.back.common.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.LectureType;
import pulse.back.common.enums.SortType;
import pulse.back.domain.mentoring.dto.JobInfoList;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.domain.mentoring.dto.MentoringDetailResponseDto;
import pulse.back.domain.mentoring.dto.MentoringListResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface MentoringRepositoryCustom{
    Mono<List<JobInfoList>> findJobInfo();

    Mono<List<MentoringListResponseDto>> getMentoringList(
            String field, LectureType lectureType, String region, SortType sortType, String searchText, int page, int size, ObjectId requesterId
    );

    Mono<Long> getMentoringListTotalCount(
            String field, LectureType lectureType, String region, SortType sortType, String searchText
    );

    Mono<Void> incrementViewCount(ObjectId mentoringId);
    Mono<Integer> countByCreatedMemberId(ObjectId memberId);
    Mono<List<MentoringListResponseDto>> findMentoringByLocation(Double latitude, Double longitude, int distance, ObjectId currentMemberId);
    Mono<List<MentoringListResponseDto>> getPopularMentoringList(int size);
}
