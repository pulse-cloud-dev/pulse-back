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

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberValidationService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public Member validateToLogin(MemberLoginRequestDto requestDto){
        // 아이디가 존재하는지 확인
        Member member = memberRepository.findByEmail(requestDto.email());
        if (member == null) {
            throw new CustomException(ErrorCodes.MEMBER_NOT_FOUND);
        }
        log.debug("member: {}", member);

        // 비밀번호가 일치하는지 확인
//        if (passwordEncoder.matches(requestDto.password(), member.password())) {
            return member;
//        }
//        throw new CustomException(ErrorCodes.INVALID_MEMBER_LOGIN_INFO);
    }
}
