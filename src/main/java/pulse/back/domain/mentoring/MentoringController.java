package pulse.back.domain.mentoring;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.response.ResultData;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentoring")
public class MentoringController {
    private final MentoringProcessor mentoringProcessor;

    //멘토링 등록
    @PostMapping("/post")
    @Operation(operationId = "PULSE-112", summary = "멘토링 등록", description = """
            ### [ 설명 ]
            - 멘토링을 등록합니다.
            <br>
            ### [ 주의사항 ]
            - 
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [MentoringPostRequestDto]
            - Response : [ResultData<ResultCodes>]
            ```
            """)
    public Mono<ResultData<ResultCodes>> postMentoring(
            @RequestBody MentoringPostRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        return mentoringProcessor.postMentoring(requestDto, exchange);
    }
}
