package pulse.back.domain.mentoInfo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoInfoValidationService {
    //멘토 정보 등록
    public Mono<Boolean> validateMentorInfoRequestDto(MentoInfoRequestDto requestDto, ServerWebExchange exchange) {
        return Mono.just(true);
    }
}
