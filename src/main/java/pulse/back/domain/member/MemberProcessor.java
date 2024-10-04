package pulse.back.domain.member;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.config.auth.TokenResponseDto;
import pulse.back.common.response.ResultData;
import pulse.back.domain.member.dto.MemberLoginRequestDto;
import pulse.back.domain.member.service.MemberBusinessService;
import pulse.back.domain.member.service.MemberValidationService;
import pulse.back.entity.member.Member;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberProcessor {
    private final MemberValidationService memberValidationService;
    private final MemberBusinessService memberBusinessService;
    private final TokenProvider tokenProvider;


    public Mono<ResultData<TokenResponseDto>> login(
            MemberLoginRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        log.debug("[validation] request : {}" , requestDto);
        // 로그인 테스트
        return memberValidationService.validateToLogin(requestDto)
                .flatMap(member ->
                        memberBusinessService.login(member, exchange)
                                .flatMap(tokenResponseDto ->
                                        Mono.just(new ResultData<>(tokenResponseDto, "로그인에 성공하였습니다."))
                                )
                )
                .defaultIfEmpty(new ResultData<>(null, "로그인에 실패하였습니다.")); // member가 없는 경우 처리
    }

}

