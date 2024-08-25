package pulse.back.domain.member;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenResponseDto;
import pulse.back.common.response.ResultData;
import pulse.back.domain.member.dto.MemberLoginRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberProcessor memberProcessor;

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(operationId = "2329", summary = "로그인", description = "로그인을 진행합니다. ")
    public ResultData<TokenResponseDto> login(
            @RequestBody @Valid MemberLoginRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        return memberProcessor.login(requestDto, exchange);
    }
}
