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
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.enums.SocialRule;
import pulse.back.common.response.ResultData;
import pulse.back.domain.member.dto.MemberJoinRequestDto;
import pulse.back.domain.member.dto.MemberLoginRequestDto;
import pulse.back.domain.member.dto.MemberTokenResponseDto;
import pulse.back.domain.member.repository.MemberRepository;
import pulse.back.entity.member.Member;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberProcessor memberProcessor;
    private final MemberRepository memberRepository;
    private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    /**
     * 소셜 로그인 인증
     */
    @GetMapping("/{social}")
    @Operation(operationId = "SVO-17", summary = "소셜_로그인_인증", description = "소셜 로그인 인증을 진행합니다. ")
    public Mono<ResultData<String>> socialLoginPath(
            @PathVariable SocialRule social,
            ServerWebExchange exchange
    ) {
        log.info("social : {}", social);
        return memberProcessor.socialLoginPath(social, exchange);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(operationId = "SVO-17", summary = "로그인", description = "로그인을 진행합니다. ")
    public Mono<ResultData<MemberTokenResponseDto>> login(
            @RequestBody @Valid MemberLoginRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        log.info("requestDto : {}", requestDto);
        return memberProcessor.login(requestDto, exchange);
    }

    /**
     * access_token 재발급
     */
    @PostMapping("/reissue")
    @Operation(operationId = "SVO-17", summary = "access_token 재발급", description = "refresh_token을 이용하여 access_token을 재발급합니다.")
    public Mono<ResultData<TokenResponseDto>> reissueAccessToken(ServerWebExchange exchange) {
        return memberProcessor.reissueAccessToken(exchange);
    }

    /**
     * 회원가입
     */
    @PostMapping("/join")
    @Operation(operationId = "SVO-17", summary = "회원가입", description = "회원가입 진행합니다. ")
    public Mono<ResultData<ResultCodes>> join(
            @RequestBody @Valid MemberJoinRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        return memberProcessor.join(requestDto, exchange);
    }

    /*
    * 이메일 중복체크
    * */
    @PostMapping("/email-duplicate")
    @Operation(operationId = "SVO-17", summary = "이메일 중복체크", description = "이메일 중복체크를 진행합니다. ")
    public Mono<ResultData<ResultCodes>> emailDuplicateCheck(
            @RequestBody String email,
            ServerWebExchange exchange
    ) {
        return memberProcessor.emailDuplicateCheck(email, exchange);
    }

    /**
     * 카카오 redirect
     */
    @GetMapping("/join/kakao-redirect")
    public Mono<ServerResponse> handleKakaoLogin(ServerWebExchange exchange) {
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

                            // 응답을 ServerResponse로 설정
                            String responseMessage = "카카오 로그인 성공, 사용자 정보가 로그에 기록되었습니다.";
                            return ServerResponse.ok()
                                    .contentType(MediaType.TEXT_PLAIN)
                                    .bodyValue(responseMessage);
                        }))
                .onErrorResume(e -> {
                    log.error("인증 정보 처리 중 오류 발생: {}", e.getMessage());
                    return ServerResponse.status(500)
                            .contentType(MediaType.TEXT_PLAIN)
                            .bodyValue("인증 정보 처리 중 오류 발생");
                });
    }


    @Deprecated
    @GetMapping("/test")
    public Flux<Member> test() {
        return memberRepository.findAll();
    }
}
