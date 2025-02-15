package pulse.back.domain.mentoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ResultCodes;
import pulse.back.domain.member.repository.MemberRepository;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.domain.mentoring.dto.MentoringDetailResponseDto;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;
import pulse.back.domain.mentoring.repository.MentoringRepository;
import pulse.back.entity.mentoring.Mentoring;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoringBusinessService {
    private final MentoringRepository mentoringRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    //멘토링 상세조회
    public Mono<MentoringDetailResponseDto> getMentoringDetail(String mentoringId, ServerWebExchange exchange) {
        return mentoringRepository.findById(new ObjectId(mentoringId))
                .flatMap(mentoring -> memberRepository.findById(mentoring.createdMemberId())
                        .map(member -> MentoringDetailResponseDto.of(mentoring, member)));
    }

    //멘토링 등록
    public Mono<ResultCodes> registerMentoring(MentoringPostRequestDto requestDto, ServerWebExchange exchange) {
        ObjectId memberId = tokenProvider.getMemberId(exchange);
        return mentoringRepository.insert(Mentoring.from(requestDto, memberId))
                .then(Mono.just(ResultCodes.SUCCESS));
    }

    //멘토 정보 등록
    public Mono<ResultCodes> postMentorInfo(MentoInfoRequestDto requestDto, ServerWebExchange exchange) {
        ObjectId memberId = tokenProvider.getMemberId(exchange);
        return mentoringRepository.insertMentorInfo(memberId, requestDto)
                .then(Mono.just(ResultCodes.SUCCESS));
    }

}
