package pulse.back.domain.mentoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.*;
import pulse.back.common.exception.CustomException;
import pulse.back.common.response.PaginationDto;
import pulse.back.common.response.ResultData;
import pulse.back.domain.mentoring.dto.*;
import pulse.back.domain.mentoring.service.MentoringBusinessService;
import pulse.back.domain.mentoring.service.MentoringValidationService;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentoringProcessor {
    private final MentoringBusinessService mentoringBusinessService;
    private final MentoringValidationService mentoringValidationService;
    
    //직업 정보 제공
    public Mono<ResultData<List<JobInfoList>>> getFieldList(ServerWebExchange exchange) {
        return mentoringBusinessService.getFieldList(exchange)
                .flatMap(jobInfoList -> Mono.just(new ResultData<>(jobInfoList, "직업 정보 제공에 성공하였습니다.")));
    }

    //멘토링 목록조회
    public Mono<ResultData<PaginationDto<GetMentoringListResponseDto>>> getMentoringList(
            String field, LectureType lectureType, String region, SortType sortType, String searchText, int page, int size, ServerWebExchange exchange
    ) {
        log.debug("[validation] field : {}, lectureType : {}, region : {}, sortType : {}, searchText : {}, page : {}, size : {}",
                field, lectureType, region, sortType, searchText, page, size);
        return mentoringValidationService.validateMentoringListRequestDto(field, lectureType, region, sortType, searchText, page, size, exchange)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return mentoringBusinessService.getMentoringList(field, lectureType, region, sortType, searchText, page, size, exchange);
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.MENTORING_LIST_FAILED));
                    }
                });
    }

    //멘토링 상세조회
    public Mono<ResultData<MentoringDetailResponseDto>> getMentoringDetail(String mentoringId, ServerWebExchange exchange) {
        log.debug("[validation] mentoringId : {}" , mentoringId);
        return mentoringValidationService.validateMentoringId(mentoringId, exchange)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return mentoringBusinessService.getMentoringDetail(mentoringId, exchange)
                                .flatMap(mentoringDetailResponseDto -> Mono.just(new ResultData<>(mentoringDetailResponseDto, "멘토링 상세조회에 성공하였습니다.")));
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.MENTORING_DETAIL_FAILED));
                    }
                });
   }

    //멘토링 등록
    public Mono<ResultData<ResultCodes>> postMentoring(MentoringPostRequestDto requestDto, ServerWebExchange exchange) {
        log.debug("[validation] request : {}" , requestDto);
        return mentoringValidationService.validateMentoringPostRequestDto(requestDto, exchange)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return mentoringBusinessService.registerMentoring(requestDto, exchange)
                                .flatMap(resultCodes -> Mono.just(new ResultData<>(resultCodes, "멘토링 등록에 성공하였습니다.")));
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.MENTORING_REGISTER_FAILED));
                    }
                });
    }

    public ResultData<ResultCodes> uploadMentoringBookmark(UploadMentoringBookmarkRequestDto requestDto, ServerWebExchange exchange) {
        log.debug("[validation] request : {}", requestDto);
        return mentoringValidationService.validateUploadMentoringBookmark(requestDto, exchange)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return mentoringBusinessService.uploadMentoringBookmark(requestDto, exchange)
                                .map(resultCodes -> new ResultData<>(resultCodes, "멘토링 북마크 업로드에 성공하였습니다."));
                    } else {
                        throw new CustomException(ErrorCodes.BAD_REQUEST);
                    }
                })
                .block();
    }

    public Mono<ResultData<List<GetMentoringListResponseDto>>> getMentoringByLocation(Double latitude, Double longitude, int distance, ServerWebExchange exchange) {
        log.debug("[validation] latitude : {}, longitude : {}, distance : {}", latitude, longitude, distance);
        return mentoringValidationService.validateMentoringByLocation(latitude, longitude, distance, exchange)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return mentoringBusinessService.getMentoringByLocation(latitude, longitude, distance, exchange)
                                .map(mentoringList -> new ResultData<>(mentoringList, "좌표값에 따른 멘토링 글 조회에 성공하였습니다."));
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.BAD_REQUEST));
                    }
                });
    }

    public Mono<ResultData<List<GetMentoringListResponseDto>>> getPopularMentoringList(int size, ServerWebExchange exchange) {
        return mentoringBusinessService.getPopularMentoringList(size, exchange)
                .map(mentoringList -> new ResultData<>(mentoringList, "인기 멘토링 목록 조회에 성공하였습니다."));
    }

    public Mono<ResultData<GetMentoInfoDetailResponseDto>> getMentoInfoDetail(
            String mentoId, ServerWebExchange exchange
    ) {
        log.debug("[validation] mentoId : {}", mentoId);
        return mentoringValidationService.validateMentoInfoDetail(mentoId, exchange)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return mentoringBusinessService.getMentoInfoDetail(mentoId, exchange)
                                .map(mentoInfoDetail -> new ResultData<>(mentoInfoDetail, "멘토 정보 상세 조회에 성공하였습니다."));
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.BAD_REQUEST));
                    }
                });
    }
}
