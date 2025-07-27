package pulse.back.domain.mentoInfo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.common.repository.MentoInfoRepository;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoInfoValidationService {
    private final MentoInfoRepository mentoInfoRepository;
    private final TokenProvider tokenProvider;
    //멘토 정보 등록
    public Mono<Boolean> validateMentorInfoRequestDto(MentoInfoRequestDto requestDto, ServerWebExchange exchange) {
        ObjectId memberId = tokenProvider.getMemberId(exchange);

        return mentoInfoRepository.existsByMemberId(memberId)
                .flatMap(isExist -> {
                    if (isExist) {
                        throw new CustomException(ErrorCodes.MENTO_ALREADY_REGISTERED);
                    }
                    return Mono.just(true);
                });
    }
}
