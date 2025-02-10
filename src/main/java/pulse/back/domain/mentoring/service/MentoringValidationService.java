package pulse.back.domain.mentoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;
import pulse.back.entity.mentoring.Mentoring;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoringValidationService {
    //멘토링 등록시 유효성 검사
    public Mono<Boolean> validateMentoringPostRequestDto(MentoringPostRequestDto requestDto, ServerWebExchange exchange) {
        //TODO : 멘토링 등록시 유효성 검사
        //온라인, 오프라인 여부 확인
        return Mono.just(true);
    }

    //멘토링 목록조회
}
