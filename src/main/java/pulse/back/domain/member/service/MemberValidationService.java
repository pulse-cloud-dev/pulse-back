package pulse.back.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.SocialRule;
import pulse.back.common.exception.CustomException;
import pulse.back.domain.member.dto.MemberJoinRequestDto;
import pulse.back.domain.member.dto.MemberLoginRequestDto;
import pulse.back.domain.member.repository.MemberRepository;
import pulse.back.entity.member.Member;
import pulse.back.common.enums.ErrorCodes;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberValidationService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    // 로그인 검증
    public Mono<Member> validateToLogin(
            MemberLoginRequestDto requestDto
    ) {
        return memberRepository.findByEmail(requestDto.email())
                .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.MEMBER_NOT_FOUND)))
                .doOnNext(member -> log.debug("member: {}", member));
    }

    //회원가입
    public Mono<Boolean> validateToJoin(MemberJoinRequestDto requestDto) {
//        return memberRepository.findByEmail(requestDto.email())
//                .map(existingMember -> false) // 이메일이 존재할 경우 false 반환
//                .defaultIfEmpty(true); // 이메일이 존재하지 않을 경우 true 반환
        return Mono.just(true);
    }


    //토큰 유효성 검사
    public Mono<Boolean> validateTokenExpired(
            String refreshToken
    ) {
        return Mono.just(tokenProvider.validateToken(refreshToken));
    }

    // 소셜 로그인 인증 검증
    public Mono<Boolean> validateToSocialLoginPath(
            SocialRule social,
            ServerWebExchange exchange
    ) {
        return Mono.just(true);
    }
}
