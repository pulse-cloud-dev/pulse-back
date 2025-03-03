package pulse.back.domain.mentoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.enums.LectureType;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.enums.SortType;
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
    public Mono<ResultData<PaginationDto<MentoringListResponseDto>>> getMentoringList(
            String field, LectureType lectureType, String region, SortType sortType, String searchText, int page, int size, ServerWebExchange exchange
    ) {
        log.debug("[validation] field : {}, lectureType : {}, region : {}, sortType : {}, searchText : {}, page : {}, size : {}",
                field, lectureType, region, sortType, searchText, page, size);
        return mentoringValidationService.validateMentoringListRequestDto(field, lectureType, region, sortType, searchText, page, size, exchange)
                .flatMap(isValid -> {
                    if (isValid) {
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
                    if (isValid) {
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
                    if (isValid) {
                        return mentoringBusinessService.registerMentoring(requestDto, exchange)
                                .flatMap(resultCodes -> Mono.just(new ResultData<>(resultCodes, "멘토링 등록에 성공하였습니다.")));
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.MENTORING_REGISTER_FAILED));
                    }
                });
    }

    //멘토 정보 등록
    public Mono<ResultData<ResultCodes>> postMentorInfo(MentoInfoRequestDto requestDto, ServerWebExchange exchange){
        log.debug("[validation] request : {}" , requestDto);
        return mentoringValidationService.validateMentorInfoRequestDto(requestDto, exchange)
                .flatMap(isValid -> {
                    if (isValid) {
                        return mentoringBusinessService.postMentorInfo(requestDto, exchange)
                                .flatMap(resultCodes -> Mono.just(new ResultData<>(resultCodes, "멘토 정보 등록에 성공하였습니다.")));
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.MENTO_INFO_REGISTER_FAILED));
                    }
                });
    }
}
