package pulse.back.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.GlobalVariables;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.config.auth.TokenResponseDto;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.enums.SocialRule;
import pulse.back.common.exception.CustomException;
import pulse.back.common.response.ResultData;
import pulse.back.domain.member.dto.MemberJoinRequestDto;
import pulse.back.domain.member.dto.PasswordResetRequestDto;
import pulse.back.domain.social.NaverLoginUrlGenerator;
import pulse.back.entity.member.Member;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseCookie;
import pulse.back.domain.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberBusinessService {
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;
    private final NaverLoginUrlGenerator naverLoginUrlGenerator;

    //로그인
    public Mono<TokenResponseDto> login(Member member, ServerWebExchange exchange) {
        return Mono.just(tokenProvider.generateTokenDto(member.id(), member.memberRole()))
                .doOnNext(tokenResponseDto -> {
                    log.debug("tokenResponseDto  : {}", tokenResponseDto);
                    setRefreshTokenAtCookie(exchange, tokenResponseDto);
                });
    }
//    public Mono<TokenResponseDto> login(Member member, ServerWebExchange exchange) {
//        return Mono.just(tokenProvider.generateTokenDto(member.id(), member.memberRole()));
//    }

    //회원가입
    public Mono<ResultCodes> join(MemberJoinRequestDto requestDto, ServerWebExchange exchange) {
        log.debug("멤버비즈니스 requestDto : {}", requestDto);
        return memberRepository.insert(MemberJoinRequestDto.of(requestDto, bCryptPasswordEncoder.encode(requestDto.password())))
                .map(member -> ResultCodes.SUCCESS) // 삽입이 성공하면 SUCCESS 반환
                .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.BAD_REQUEST))); // 삽입 실패 시 에러 반환
    }


    //소셜 로그인
    public Mono<ResultData<String>> socialLoginPath(SocialRule social, ServerWebExchange exchange){
        String path = switch (social) {
            case KAKAO -> GlobalVariables.KAKAO_LOGIN_PATH;
            case NAVER -> naverLoginUrlGenerator.generateLoginUrl();
            case GOOGLE -> GlobalVariables.GOOGLE_LOGIN_PATH;
            default -> throw new CustomException(ErrorCodes.SOCIAL_NOT_FOUND);
        };
        return Mono.just(new ResultData<>(path, "회원가입을 위한 소셜 로그인 경로입니다."));
    }

    //아이디 찾기
    public Mono<ResultData<String>> getMemberId(SocialRule social, ServerWebExchange exchange) {
        String path = switch (social) {
            case KAKAO -> GlobalVariables.KAKAO_LOGIN_PATH;
            case NAVER -> naverLoginUrlGenerator.generateFindEmailUrl();
            case GOOGLE -> GlobalVariables.GOOGLE_LOGIN_PATH;
            default -> throw new CustomException(ErrorCodes.SOCIAL_NOT_FOUND);
        };
        return Mono.just(new ResultData<>(path, "아이디 찾기를 위한 소셜 로그인 경로입니다."));
    }

    //리프레쉬 토큰 발급
    public Mono<Boolean> validateTokenExpired(String refreshToken) {
        return Mono.just(tokenProvider.validateToken(refreshToken));
    }

    //accessToken 토큰 발급
    public Mono<TokenResponseDto> reissueAccessToken(ServerWebExchange exchange) {
        ObjectId memberId = tokenProvider.getMemberId(exchange);
        return memberRepository.findById(memberId)
                .flatMap(member -> Mono.just(tokenProvider.reissueAccessToken(member.id().toString(), member.memberRole())))
                .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.MEMBER_NOT_FOUND))
                );
    }


    //refreshToken 토큰 쿠키에 저장
    private void setRefreshTokenAtCookie(ServerWebExchange exchange, TokenResponseDto tokenResponseDto) {
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", tokenResponseDto.refreshToken())
                .maxAge((long) (GlobalVariables.REFRESH_TOKEN_EXPIRED_TIME * 0.001))
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite("None")
                .build();

        exchange.getResponse().addCookie(responseCookie);
    }

    private String maskEmail(String email) {
        //이메일을 로컬 파트와 도메인 파트로 분리
        String[] emailParts = email.split("@");
        String localPart = emailParts[0];
        String domainPart = emailParts[1];

        //로컬 파트 마스킹: 첫 3글자만 남기고 나머지를 *로 변경
        String maskedLocalPart = localPart.substring(0, Math.min(3, localPart.length()))
                + "*".repeat(Math.max(0, localPart.length() - 3));

        //도메인 파트를 도메인 이름과 확장자로 분리
        String[] domainParts = domainPart.split("\\.");
        String domainName = domainParts[0];
        String domainExtension = domainParts[1];

        //도메인 이름 마스킹: 첫 2글자만 남기고 나머지를 *로 변경
        String maskedDomainName = domainName.substring(0, Math.min(2, domainName.length()))
                + "*".repeat(Math.max(0, domainName.length() - 2));

        //마스킹된 이메일 주소 반환
        return maskedLocalPart + "@" + maskedDomainName + "." + domainExtension;
    }

    //비밀번호 재설정
    public Mono<ResultData<ResultCodes>> resetPassword(
            PasswordResetRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        return memberRepository.updateMemberPassword(requestDto.memberId(), bCryptPasswordEncoder.encode(requestDto.newPassword()), exchange)
                .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.MEMBER_NOT_FOUND)))
                .map(result -> new ResultData<>(ResultCodes.SUCCESS, "비밀번호가 재설정되었습니다."));
    }
}
