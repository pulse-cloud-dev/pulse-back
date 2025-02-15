package pulse.back.domain.mentoring;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.response.ResultData;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.domain.mentoring.dto.MentoringDetailResponseDto;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentoring")
public class MentoringController {
    private final MentoringProcessor mentoringProcessor;

    //멘토링 상세조회
    @GetMapping("/{mentoringId}")
    @Operation(operationId = "PULSE-114", summary = "멘토링 상세조회", description = """
            ### [ 설명 ]
            - 멘토링 상세정보를 조회합니다.
            <br>
            ### [ 주의사항 ]
            -
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [String] mentoringId
            - Response : [MentoringDetailResponseDto]
            ```
            """)
    public Mono<ResultData<MentoringDetailResponseDto>> getMentoringDetail(
            @PathVariable String mentoringId,
            ServerWebExchange exchange
    ){
        return mentoringProcessor.getMentoringDetail(mentoringId, exchange);
    }

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

    //멘토링 신청
//    @PostMapping("/apply")
//    @Operation(operationId = "PULSE-113", summary = "멘토링 신청", description = """
//            ### [ 설명 ]
//            - 멘토링을 신청합니다.
//            <br>
//            ### [ 주의사항 ]
//            -
//            <br>
//            ### [ 요청응답 ]
//            ```
//            - Request  : [MentoringApplyRequestDto]
//            - Response : [ResultData<ResultCodes>]
//            ```
//            """)

    //멘토 정보 등록
    @PostMapping("/mento-info")
    @Operation(operationId = "PULSE-123", summary = "멘토 정보 등록", description = """
            ### [ 설명 ]
            - 멘토 정보를 등록합니다.
            <br>
            ### [ 주의사항 ]
            <br>
            ### [ 추가정보 ]
            - 멘토 정보 등록시 시, Enum 을 사용하여 Request 를 받는 필드들이 존재합니다.
            - 아래의 Enum 을 참고하여 RequestDto 를 작성해주세요.
            #### RoleLevel
            - TEAM_MEMBER("팀원")
            - PART_LEADER("파트장")
            - TEAM_LEADER("팀장")
            - DIRECTOR("실장")
            - GROUP_LEADER("그룹장")
            - CENTER_HEAD("센터장")
            - MANAGER("매니저")
            - HEAD_OF_DIVISION("본부장")
            - BUSINESS_UNIT_HEAD("사업부장")
            - DIRECTOR_GENERAL("국장")
            #### EducationStatus
            - GRADUATED("졸업")
            - EXPECTED_GRADUATION("졸업예정")
            - ENROLLED("재학중")
            - DROPPED_OUT("중퇴")
            - ON_LEAVE("휴학")
            #### EducationLevel
            - UNDERGRADUATE_2("대학교(2,3학년)")
            - UNDERGRADUATE_4("대학교(4학년)")
            - MASTER("대학원(석사)")
            - DOCTORATE("대학원(박사)")
            #### PassStatus
            - WRITTEN_PASS("필기합격")
            - FINAL_PASS("최종합격")
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [MentoInfoRequestDto]
            - Response : [ResultCodes]
            ```
            """)
    public Mono<ResultData<ResultCodes>> postMentorInfo(
            @RequestBody MentoInfoRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        return mentoringProcessor.postMentorInfo(requestDto, exchange);
    }
}
