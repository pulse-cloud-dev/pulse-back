package pulse.back.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pulse.back.domain.member.dto.MemberLoginRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberValidationService {
    public Member validateToLogin(MemberLoginRequestDto requestDto) {
}
