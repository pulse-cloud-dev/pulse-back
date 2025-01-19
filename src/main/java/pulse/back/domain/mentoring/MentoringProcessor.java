package pulse.back.domain.mentoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.common.response.ResultData;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;
import pulse.back.domain.mentoring.service.MentoringBusinessService;
import pulse.back.domain.mentoring.service.MentoringValidationService;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentoringProcessor {
    private final MentoringBusinessService mentoringBusinessService;
    private final MentoringValidationService mentoringValidationService;

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
}
