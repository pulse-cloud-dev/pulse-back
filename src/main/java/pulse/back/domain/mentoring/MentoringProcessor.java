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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    //멘토 정보 등록
    public Mono<ResultData<ResultCodes>> postMentorInfo(MentoInfoRequestDto requestDto, ServerWebExchange exchange){
        log.debug("[validation] request : {}" , requestDto);
        return mentoringValidationService.validateMentorInfoRequestDto(requestDto, exchange)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return mentoringBusinessService.postMentorInfo(requestDto, exchange)
                                .flatMap(resultCodes -> Mono.just(new ResultData<>(resultCodes, "멘토 정보 등록에 성공하였습니다.")));
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.MENTO_INFO_REGISTER_FAILED));
                    }
                });
    }

    public Mono<ResultData<List<GetMentoInfoCodeListResponseDto>>> getRoleLevelList() {
        List<GetMentoInfoCodeListResponseDto> roleLevelList = Arrays.stream(RoleLevel.values())
                .map(roleLevel -> GetMentoInfoCodeListResponseDto.from(
                        roleLevel.name(),
                        roleLevel.getDescription()
                ))
                .collect(Collectors.toList());

        return Mono.just(new ResultData<>(roleLevelList, "직책 목록 조회에 성공하였습니다."));
    }

    public Mono<ResultData<List<GetMentoInfoCodeListResponseDto>>> getEducationStatusList() {
        List<GetMentoInfoCodeListResponseDto> educationStatusList = Arrays.stream(EducationStatus.values())
                .map(educationStatus -> GetMentoInfoCodeListResponseDto.from(
                        educationStatus.name(),
                        educationStatus.getDescription()
                ))
                .collect(Collectors.toList());

        return Mono.just(new ResultData<>(educationStatusList, "졸업 여부 조회에 성공하였습니다."));
    }

    public Mono<ResultData<List<GetMentoInfoCodeListResponseDto>>> getEducationLevelList() {
        List<GetMentoInfoCodeListResponseDto> educationLevelList = Arrays.stream(EducationLevel.values())
                .map(educationLevel -> GetMentoInfoCodeListResponseDto.from(
                        educationLevel.name(),
                        educationLevel.getDescription()
                ))
                .collect(Collectors.toList());

        return Mono.just(new ResultData<>(educationLevelList, "학력 정보 조회에 성공하였습니다."));
    }

    public Mono<ResultData<List<GetMentoInfoCodeListResponseDto>>> getPassStatusList() {
        List<GetMentoInfoCodeListResponseDto> passStatusList = Arrays.stream(PassStatus.values())
                .map(passStatus -> GetMentoInfoCodeListResponseDto.from(
                        passStatus.name(),
                        passStatus.getDescription()
                ))
                .collect(Collectors.toList());

        return Mono.just(new ResultData<>(passStatusList, "합격구분코드 조회에 성공하였습니다."));
    }
}
