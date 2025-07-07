package pulse.back.domain.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenResponseDto;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.enums.SocialRule;
import pulse.back.common.repository.CategoryRepository;
import pulse.back.common.repository.ItemRepository;
import pulse.back.common.response.ResultData;
import pulse.back.domain.admin.terms.TermsRepository;
import pulse.back.domain.member.dto.*;
import pulse.back.common.repository.MemberRepository;
import pulse.back.domain.social.NaverLoginUrlGenerator;
import pulse.back.entity.common.Category;
import pulse.back.entity.common.Item;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberProcessor memberProcessor;
    private final MemberRepository memberRepository;
    private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;
    private final NaverLoginUrlGenerator naverLoginUrlGenerator;

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final TermsRepository termsRepository;

    /**
     * 소셜 로그인 인증 -> 카카오 주석처리필요함
     */
    @GetMapping("/join/{social}")
    @Operation(operationId = "SVO-17", summary = "소셜_로그인_인증", description = """
            ### [ 설명 ]
            - 소셜 로그인 인증을 진행합니다.
            - 소셜 로그인 인증 후, /api/v1/social/naver/join-info API 를 호출하여 사용자 정보를 가져옵니다.
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
    @Operation(operationId = "", summary = "아이디 찾기 (이메일)", description = """
            ### [ 설명 ]
            - 소셜 로그인을 통한 아이디 찾기를 진행합니다.
            - 소셜 로그인 인증 후, /api/v1/social/naver/find-email API 를 호출하여 사용자 정보를 가져옵니다.
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
            @RequestBody MemberLoginRequestDto requestDto,
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
            - 
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [MemberJoinRequestDto]
            - Response : [ResultCodes]
            ```
            """)
    public Mono<ResultData<ResultCodes>> join(
            @RequestBody MemberJoinRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        return memberProcessor.join(requestDto, exchange);
    }

    /*
    * 직무직업 제공 API
    * */
    @GetMapping("/job")
    @Operation(operationId = "SVO-17", summary = "직무직업 제공 API", description = """
            ### [ 설명 ]
            - 직무직업을 제공합니다.
            <br>
            ### [ 주의사항 ]
            - JobInfoRequestDto 의 jobCode 를 입력할 때 이 API 를 사용하여 조회 후 사용해주세요.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : []
            - Response : [ResultCodes<List<JobInfoResponseDto>>]
            ```
            """)
    public Mono<ResultData<List<JobInfoResponseDto>>> getJobList(
            ServerWebExchange exchange
    ) {
        return memberProcessor.getJobCode(exchange);
    }

    /*
     * 이메일 중복체크
     * */
    @PostMapping("/duplicate/email")
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

    //닉네임 중복체크
    @GetMapping("/duplicate/{nick-name}")
    @Operation(operationId = "", summary = "닉네임 중복체크", description = """
            ### [ 설명 ]
            - 닉네임 중복체크를 진행합니다.
            <br>
            ### [ 주의사항 ]
            - 닉네임은 2~10자 이내로 설정할 수 있습니다.
            - 영문, 한글 혼합 사용 가능합니다.
            - 특수문자는 사용할 수 없습니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [PasswordResetRequestDto]
            - Response : [ResultCodes]
            ```
            """)
    public Mono<ResultData<ResultCodes>> nickNameDuplicateCheck(
            @PathVariable(name = "nick-name") String nickName,
            ServerWebExchange exchange
    ) {
        return memberProcessor.nickNameDuplicateCheck(nickName, exchange);
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

    @Deprecated
    @PostMapping("/category-insert")
    public Mono<String> insertCategory(
            @RequestParam(value = "name", required = false) @Schema(description = "카테고리 이름") String name,
            @RequestParam(value = "description", required = false) @Schema(description = "카테고리 설명") String description,
            @RequestParam(value = "code", required = false) @Schema(description = "카테고리 코드") String code,
            ServerWebExchange exchange
    ) {
        Category category = new Category(
                new ObjectId(),
                name,
                description,
                code,
                LocalDateTime.now(),
                null,
                null,
                new ObjectId(),
                null,
                null
        );
        return categoryRepository.insert(category)
                .then(Mono.just("추가 완료"));
    }

    @Deprecated
    @PostMapping("/item-insert")
    public Mono<String> insertItem(
            @RequestParam(value = "category_code", required = false) @Schema(description = "카테고리 코드") String categoryCode,
            @RequestParam(value = "name", required = false) @Schema(description = "이름") String name,
            @RequestParam(value = "description", required = false) @Schema(description = "설명") String description,
            @RequestParam(value = "code", required = false) @Schema(description = "코드") String code,
            ServerWebExchange exchange
    ) {
        Item item = new Item(
                new ObjectId(),
                categoryCode,
                name,
                description,
                code,
                LocalDateTime.now(),
                null,
                null,
                new ObjectId(),
                null,
                null
        );
        return itemRepository.insert(item)
                .then(Mono.just("추가 완료"));
    }

    @Deprecated
    @PostMapping("/terms-insert")
    public Mono<String> insertTerms(
            @RequestParam(value = "used", required = false) @Schema(description = "사용 여부 (0: 미사용, 1: 사용)") int used,
            @RequestParam(value = "required", required = false) @Schema(description = "필수 약관 여부 (0: 선택, 1: 필수)") int required,
            @RequestParam(value = "title", required = false) @Schema(description = "제목") String title,
            @RequestParam(value = "content", required = false) @Schema(description = "내용") String content,
            @RequestParam(value = "category_list", required = false) @Schema(description = "약관이 사용되는 카테고리 리스트 (예: TERMS_PAYMENT, TERMS_MEMBER)") List<String> categoryList,
            @RequestParam(value = "expired_at", required = false) @Schema(description = "약관 만기일 (null: 무기한)") LocalDateTime expiredAt,
            ServerWebExchange exchange
    ) {
        pulse.back.entity.terms.Terms terms = new pulse.back.entity.terms.Terms(
                new ObjectId(),
                used,
                required,
                title,
                content,
                categoryList,
                expiredAt,
                LocalDateTime.now(),
                null,
                null,
                new ObjectId(),
                null,
                null
        );
        return termsRepository.insert(terms)
                .then(Mono.just("추가 완료"));
    }
}
