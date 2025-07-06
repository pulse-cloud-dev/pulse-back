package pulse.back.domain.mentoInfo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.repository.MentoInfoRepository;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoInfoBusinessService {
    private final TokenProvider tokenProvider;
    private final MentoInfoRepository mentoInfoRepository;

    //멘토 정보 등록 여부
    public Mono<Boolean> getMentoInfoExist(ServerWebExchange exchange) {
        ObjectId memberId = tokenProvider.getMemberId(exchange);
        return mentoInfoRepository.existsByMemberId(memberId);
    }

    //멘토 정보 등록
    public Mono<ResultCodes> postMentorInfo(MentoInfoRequestDto requestDto, ServerWebExchange exchange) {
        ObjectId memberId = tokenProvider.getMemberId(exchange);
        return mentoInfoRepository.insertMentorInfo(memberId, requestDto)
                .then(Mono.just(ResultCodes.SUCCESS));
    }
}
