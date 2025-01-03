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
import pulse.back.domain.member.dto.PasswordResetRequestDto;
import pulse.back.domain.member.repository.MemberRepository;
import pulse.back.domain.social.NaverLoginUrlGenerator;
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
    private final NaverLoginUrlGenerator naverLoginUrlGenerator;

    /**
     * 소셜 로그인 인증 -> 카카오 주석처리필요함
     */
    @GetMapping("/join/{social}")
    @Operation(operationId = "SVO-17", summary = "소셜_로그인_인증", description = """
            ### [ 설명 ]
            - 소셜 로그인 인증을 진행합니다.
            <br>
            ### [ 주의사항 ]
            - 현재 가능한 로그인은 네이버 로그인입니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [SocialRule]
            - Response : [ResultCodes<TokenResponseDto>]
            ```
            """)
    public Mono<ResultData<String>> socialLoginPath(
            @PathVariable("social") SocialRule social,
            ServerWebExchange exchange
    ) {
        return memberProcessor.socialLoginPath(social, exchange);
    }

    /*
    * 아이디 찾기 (이메일)
    * */
    @GetMapping("/find-id/{social}")
    @Operation(operationId = "SVO-17", summary = "아이디 찾기 (이메일)", description = """
            ### [ 설명 ]
            - 소셜 로그인을 통한 아이디 찾기를 진행합니다.
            <br>
            ### [ 주의사항 ]
            - 현재 가능한 로그인은 네이버 로그인입니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [SocialRule]
            - Response : [ResultCodes<String>]
            ```
            """)
    public Mono<ResultData<String>> getMemberId(
            @PathVariable("social") SocialRule social,
            ServerWebExchange exchange
    ) {
        return memberProcessor.getMemberId(social, exchange);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(operationId = "SVO-17", summary = "로그인", description = """
            ### [ 설명 ]
            - 로그인을 진행합니다.
            <br>
            ### [ 주의사항 ]
            - access_token 의 유효기간은 1시간입니다.
            - refresh_token 의 유효기간은 1주일입니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [                             ]
            - Response : [ResultCodes<TokenResponseDto>]
            ```
            """)
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
    @Operation(operationId = "SVO-17", summary = "access_token 재발급", description = """
            ### [ 설명 ]
            - refresh_token 을 이용하여 access_token 을 재발급합니다.
            <br>
            ### [ 주의사항 ]
            - access_token 의 유효기간은 1시간입니다.
            - refresh_token 의 유효기간은 1주일입니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [                             ]
            - Response : [ResultCodes<TokenResponseDto>]
            ```
            """)
    public Mono<ResultData<TokenResponseDto>> reissueAccessToken(ServerWebExchange exchange) {
        return memberProcessor.reissueAccessToken(exchange);
    }

    /**
     * 회원가입
     */
    @PostMapping("/join")
    @Operation(operationId = "SVO-17", summary = "회원가입", description = """
            ### [ 설명 ]
            - 회원가입 진행합니다.
            <br>
            ### [ 주의사항 ]
            - 비밀번호는 영문 대문자, 소문자, 숫자, 특수문자 중 3종류 이상 조합, 8~30자리이어야 합니다.
            <br>
            ### [ 추가정보 ]
            - 회원가입 시, Enum 을 사용하여 Request 를 받는 필드들이 있습니다.
            - 아래의 Enum 을 참고하여 RequestDto 를 작성해주세요.
            #### RoleLevel
            - TEAM_MEMBER("팀원")
            - PART_LEADER("파트장")
            - TEAM_LEADER("팀장")
            - DIRECTOR("실장")
            - GROUP_LEADER("그룹장")
            - CENTER_HEAD("센터장")
            - MANAGER("매니저")
            - HEAD_OF_DIVISION("본부장")
            - BUSINESS_UNIT_HEAD("사업부장")
            - DIRECTOR_GENERAL("국장")
            #### EducationStatus
            - GRADUATED("졸업")
            - EXPECTED_GRADUATION("졸업예정")
            - ENROLLED("재학중")
            - DROPPED_OUT("중퇴")
            - ON_LEAVE("휴학")
            #### EducationLevel
            - UNDERGRADUATE_2("대학교(2,3학년)")
            - UNDERGRADUATE_4("대학교(4학년)")
            - MASTER("대학원(석사)")
            - DOCTORATE("대학원(박사)")
            #### PassStatus
            - WRITTEN_PASS("필기합격")
            - FINAL_PASS("최종합격")
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [MemberJoinRequestDto]
            - Response : [ResultCodes]
            ```
            """)
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
    @Operation(operationId = "SVO-17", summary = "이메일 중복체크", description = """
            ### [ 설명 ]
            - 이메일 중복체크를 진행합니다.
            <br>
            ### [ 주의사항 ]
            - 네이버 소셜로그인을 이용하여 회원가입을 진행하므로, 해당 API 는 필요할 경우에 사용합니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [PasswordResetRequestDto]
            - Response : [ResultCodes]
            ```
            """)
    public Mono<ResultData<ResultCodes>> emailDuplicateCheck(
            @RequestParam String email,
            ServerWebExchange exchange
    ) {
        return memberProcessor.emailDuplicateCheck(email, exchange);
    }

    /*
    * 비밀번호 재설정
    * */
    @PostMapping("/password-reset")
    @Operation(operationId = "SVO-17", summary = "비밀번호 재설정", description = """
            ### [ 설명 ]
            - 비밀번호 재설정을 진행합니다.
            <br>
            ### [ 주의사항 ]
            - 비밀번호는 영문 대문자, 소문자, 숫자, 특수문자 중 3종류 이상 조합, 8~30자리이어야 합니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [PasswordResetRequestDto]
            - Response : [ResultCodes]
            ```
            """)
    public Mono<ResultData<ResultCodes>> resetPassword(
            @RequestBody PasswordResetRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        return memberProcessor.resetPassword(requestDto, exchange);
    }

    /**
     * 카카오 redirect
     */
    @Deprecated
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
}
