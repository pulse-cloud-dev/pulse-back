package pulse.back.domain.member;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;


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
    public Mono<String> handleKakaoLogin(ServerWebExchange exchange) {
        log.info("진입 test 진행");
        return exchange.getPrincipal()
                .cast(OAuth2AuthenticationToken.class)
                .flatMap(auth -> authorizedClientRepository.loadAuthorizedClient(auth.getAuthorizedClientRegistrationId(), auth, exchange)
                        .flatMap(client -> {
                            OAuth2User oAuth2User = auth.getPrincipal();

                            if (oAuth2User == null || oAuth2User.getAttributes().isEmpty()) {
                                return Mono.error(new RuntimeException("OAuth2User 정보가 없습니다."));
                            }

                            // 카카오에서 받은 사용자 정보
                            String kakaoId = oAuth2User.getName();
                            String email = (String) oAuth2User.getAttributes().get("kakao_account.email");
                            String nickname = (String) oAuth2User.getAttributes().get("properties.nickname");

                            // 사용자 정보 로그 출력
                            log.info("카카오 로그인 성공 - 사용자 ID: {}", kakaoId);
                            log.info("카카오 사용자 이메일: {}", email);
                            log.info("카카오 사용자 닉네임: {}", nickname);

                            return Mono.just("카카오 로그인 성공, 사용자 정보가 로그에 기록되었습니다.");
                        }))
                .onErrorResume(e -> {
                    log.error("인증 정보 처리 중 오류 발생: {}", e.getMessage());
                    return Mono.error(new RuntimeException("인증 정보 처리 중 오류 발생"));
                });
    }

    @GetMapping("/test")
    public Flux<Member> test() {
        return memberRepository.findAll();
    }
}
