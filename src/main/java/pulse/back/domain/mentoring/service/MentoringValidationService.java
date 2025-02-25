package pulse.back.domain.mentoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.enums.LectureType;
import pulse.back.common.enums.SortType;
import pulse.back.common.exception.CustomException;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;
import pulse.back.domain.mentoring.repository.MentoringRepository;
import pulse.back.entity.mentoring.Mentoring;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoringValidationService {
    private final MentoringRepository mentoringRepository;

    //멘토링 목록조회
    public Mono<Boolean> validateMentoringListRequestDto(String field, LectureType lectureType, String region, SortType sortType, String searchText, int page, int size, ServerWebExchange exchange) {
        //TODO : 멘토링 목록조회 유효성 검사
        return Mono.just(true);
    }

    //멘토링 상세조회
    public Mono<Boolean> validateMentoringId(String mentoringId, ServerWebExchange exchange) {
        return mentoringRepository.findById(new ObjectId(mentoringId))
                .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.MENTORING_NOT_FOUND)))
                .map(mentoring -> true);
    }

    //멘토링 등록시 유효성 검사
    public Mono<Boolean> validateMentoringPostRequestDto(MentoringPostRequestDto requestDto, ServerWebExchange exchange) {
        //TODO : 멘토링 등록시 유효성 검사
        //온라인, 오프라인 여부 확인
        return Mono.just(true);
    }

    //멘토 정보 등록
    public Mono<Boolean> validateMentorInfoRequestDto(MentoInfoRequestDto requestDto, ServerWebExchange exchange) {
        return Mono.just(true);
    }
}
