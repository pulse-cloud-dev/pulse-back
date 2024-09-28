package pulse.back.domain.member;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenResponseDto;
import pulse.back.common.response.ResultData;
import pulse.back.domain.member.dto.MemberLoginRequestDto;
import pulse.back.domain.member.repository.MemberRepository;
import pulse.back.entity.member.Member;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberProcessor memberProcessor;
    private final MemberRepository memberRepository;

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(operationId = "SVO-17", summary = "로그인", description = "로그인을 진행합니다. ")
    public Mono<ResultData<TokenResponseDto>> login(
            @RequestBody @Valid MemberLoginRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        log.info("requestDto : {}", requestDto);
        return memberProcessor.login(requestDto, exchange);
    }

    /**
     * 회원가입
     */
    @PostMapping("/join")
    @Operation(operationId = "SVO-17", summary = "회원가입", description = "회원가입 진행합니다. ")
    public Mono<ResultData<String>> join(

    ){
        return null;
    }

    /**
     * 카카오 redirect
     * */
    @GetMapping("/join/kakao-redirect")
    @Operation(operationId = "SVO-17", summary = "카카오 리다이렉트", description = "카카오톡 리다이렉트 엔드포인트 입니다. ")
    public Mono<String> kakaoRedirect(

    ){
        log.info("test");
        return Mono.just("카카오톡 리다이렉트 입니다. ");
    }

    @GetMapping("/test")
    public Flux<Member> test() {
        return memberRepository.findAll();
    }
}
