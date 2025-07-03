package pulse.back.domain.mentoInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.*;
import pulse.back.common.exception.CustomException;
import pulse.back.common.response.ResultData;
import pulse.back.domain.mentoInfo.service.MentoInfoBusinessService;
import pulse.back.domain.mentoInfo.service.MentoInfoValidationService;
import pulse.back.domain.mentoring.dto.GetMentoInfoCodeListResponseDto;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentoInfoProcessor {
    private final MentoInfoBusinessService mentoInfoBusinessService;
    private final MentoInfoValidationService mentoInfoValidationService;

    //멘토 정보 등록
    public Mono<ResultData<ResultCodes>> postMentorInfo(MentoInfoRequestDto requestDto, ServerWebExchange exchange){
        log.debug("[validation] request : {}" , requestDto);
        return mentoInfoValidationService.validateMentorInfoRequestDto(requestDto, exchange)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return mentoInfoBusinessService.postMentorInfo(requestDto, exchange)
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
