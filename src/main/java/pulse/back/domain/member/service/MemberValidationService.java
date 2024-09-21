package pulse.back.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pulse.back.common.exception.CustomException;
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
    public Mono<Member> validateToLogin(MemberLoginRequestDto requestDto) {
        return memberRepository.findByEmail(requestDto.email())
                .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.MEMBER_NOT_FOUND)))
                .doOnNext(member -> log.debug("member: {}", member));
    }
}
