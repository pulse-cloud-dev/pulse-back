package pulse.back.domain.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.config.auth.TokenResponseDto;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.enums.SocialRule;
import pulse.back.common.exception.CustomException;
import pulse.back.common.response.ResultData;
import pulse.back.domain.member.dto.*;
import pulse.back.domain.member.service.MemberBusinessService;
import pulse.back.domain.member.service.MemberValidationService;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberProcessor {
    private final MemberValidationService memberValidationService;
    private final MemberBusinessService memberBusinessService;
    private final TokenProvider tokenProvider;

    /*
    * 로그인
    * */
    public Mono<ResultData<MemberTokenResponseDto>> login(
            MemberLoginRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        log.debug("[validation] request : {}" , requestDto);
        // 로그인 테스트
        return memberValidationService.validateToLogin(requestDto)
                .flatMap(member ->
                        memberBusinessService.login(member, exchange)
                                .flatMap(tokenResponseDto ->
                                        Mono.just(new ResultData<>(new MemberTokenResponseDto(tokenResponseDto.accessToken()), "로그인에 성공하였습니다."))
                                )
                )
                .defaultIfEmpty(new ResultData<>(null, "로그인에 실패하였습니다.")); // member가 없는 경우 처리
    }

    /*
    * 이메일 중복 체크
    * */
    public Mono<ResultData<ResultCodes>> emailDuplicateCheck(
            String email,
            ServerWebExchange exchange
    ) {
        return memberValidationService.validateToEmailDuplicateCheck(email)
                .flatMap(isValid -> {
                    if (isValid) {
                        return Mono.just(new ResultData<>(ResultCodes.SUCCESS, "사용 가능한 이메일입니다."));
                    } else {
                        throw new CustomException(ErrorCodes.BAD_REQUEST, "이미 사용중인 이메일입니다.");
                    }
                });
    }

    /*
    * 회원가입
    * */
    public Mono<ResultData<ResultCodes>> join(
            MemberJoinRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        log.debug("[validation] request : {}" , requestDto);
        return memberValidationService.validateToJoin(requestDto)
                .filter(valid -> valid)
                .flatMap(valid -> memberBusinessService.join(requestDto, exchange) // 기존의 member를 requestDto로 변경
                        .map(resultCode -> new ResultData<>(resultCode, "회원가입에 성공하였습니다."))
                )
                .switchIfEmpty(Mono.just(new ResultData<>(ResultCodes.FAIL, "회원가입에 실패하였습니다."))); // 검증 실패 처리
    }


    /*
    * accessToken 재발급
    * */
    public Mono<ResultData<TokenResponseDto>> reissueAccessToken(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("refreshToken"))
                .map(HttpCookie::getValue)
                .doOnNext(refreshToken -> log.debug("[ 2. check ] refreshToken : {}", refreshToken))
                .flatMap(refreshToken -> memberValidationService.validateTokenExpired(refreshToken)
                        .flatMap(isValid -> {
                            if (isValid) {
                                log.debug("[ 1. check ] boolean : {}", isValid);
                                return memberBusinessService.reissueAccessToken(exchange)
                                        .map(tokenResponseDto -> new ResultData<>(tokenResponseDto, "토큰이 재발행되었습니다."));
                            } else {
                                return Mono.error(new CustomException(ErrorCodes.TOKEN_EXPIRED));
                            }
                        })
                );
    }

    /*
     * 소셜 로그인 인증
     * */
    public Mono<ResultData<String>> socialLoginPath(
            SocialRule social,
            ServerWebExchange exchange
    ){
        log.info("social : {}", social);
        return memberValidationService.validateToSocialLoginPath(social, exchange)
                .flatMap(isValid -> {
                    if (isValid) {
                        return memberBusinessService.socialLoginPath(social, exchange);
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.SOCIAL_NOT_FOUND));
                    }
                });
    }

    /*
    * 아이디 찾기
    * */
    public Mono<ResultData<String>> getMemberId(
            SocialRule social,
            ServerWebExchange exchange
    ) {
        log.info("social : {}", social);
        return memberValidationService.validateToSocialLoginPath(social, exchange)
                .flatMap(isValid -> {
                    if (isValid) {
                        return memberBusinessService.getMemberId(social, exchange);
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.SOCIAL_NOT_FOUND));
                    }
                });
    }

    /*
    * 비밀번호 재설정
    * */
    public Mono<ResultData<ResultCodes>> resetPassword(
            @RequestBody PasswordResetRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        log.debug("[validation] request : {}" , requestDto);
        return memberValidationService.validateToResetPassword(requestDto, exchange)
                .flatMap(isValid -> {
                    if (isValid) {
                        return memberBusinessService.resetPassword(requestDto, exchange);
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.MEMBER_NOT_FOUND));
                    }
                });
    }

    /*
    * 직무직업 코드 제공
    * */
    public Mono<ResultData<List<JobInfoResponseDto>>> getJobCode(
            ServerWebExchange exchange
    ) {
        return memberBusinessService.getJobCode(exchange);
    }
}

