package pulse.back.domain.mentoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoringValidationService {
    //멘토링 등록시 유효성 검사
    public Mono<Boolean> validateMentoringPostRequestDto(MentoringPostRequestDto requestDto, ServerWebExchange exchange) {
        return Mono.just(true);
    }
}
