package pulse.back.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.config.auth.TokenResponseDto;
import pulse.back.entity.member.Member;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberBusinessService {
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    /**
     * 로그인
     */
    public TokenResponseDto login(Member member, ServerWebExchange exchange) {
        return tokenProvider.generateTokenDto(member.id(), member.memberRole());
    }
}
