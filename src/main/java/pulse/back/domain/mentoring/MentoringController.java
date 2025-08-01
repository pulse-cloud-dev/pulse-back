package pulse.back.domain.mentoring;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.LectureType;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.enums.SortType;
import pulse.back.common.response.PaginationDto;
import pulse.back.common.response.ResultData;
import pulse.back.domain.mentoring.dto.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentoring")
public class MentoringController {
    private final MentoringProcessor mentoringProcessor;

    /*
    * [멘토링 검색필터 전용] 직업 정보 제공 (분야)
    * */
    @Deprecated
    @GetMapping("/field")
    @Operation(operationId = "PULSE-152", summary = "[멘토링 검색필터 전용] 직업 정보 제공 (분야)", description = """
            ### [ 설명 ]
            - 멘토링 검색필터 전용으로 직업 정보를 제공합니다. (분야)
            <br>
            ### [ 주의사항 ]
            - 
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : []
            - Response : [ResultData<List<JobInfoList>>]
            ```
            """)
    public Mono<ResultData<List<JobInfoList>>> getFieldList(
            ServerWebExchange exchange
    ) {
        return mentoringProcessor.getFieldList(exchange);
    }

    /*
     * [멘토링 검색필터 전용] 온오프라인 구분
     * */
    @GetMapping("/lecture-type")
    @Operation(operationId = "PULSE-151", summary = "[멘토링 검색필터 전용] 강의 형식 (온오프라인) 정보 제공", description = """
            ### [ 설명 ]
            - 멘토링 검색필터 전용으로 온라인, 오프라인 구분을 제공합니다.
            <br>
            ### [ 주의사항 ]
            - 
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : []
            - Response : [ResultData<List<LectureTypeResponseDto>>]
            ```
            """)
    public Mono<ResultData<List<LectureType>>> getLectureTypeList(
            ServerWebExchange exchange
    ) {
        return Mono.just(new ResultData<>(List.of(LectureType.values()), "강의 형식 정보를 제공합니다."));
    }

    //멘토링 목록조회
    @GetMapping("/list")
    @Operation(operationId = "PULSE-111", summary = "멘토링 목록조회", description = """
            ### [ 설명 ]
            - 멘토링 목록을 조회합니다.
            <br>
            ### [ 주의사항 ]
            - 온오프라인 입력 필드 lecture_type 는 ONLINE, OFFLINE 만 가능합니다.
            - 정렬 입력 필드 sort_type 는 DEFAULT, POPULAR, LATEST 만 가능합니다.
            - 분야는 다음과 같은 값만 가능합니다.
            - 지역은 다음과 같은 값만 가능합니다. 
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [아래 필드 확인]
            - Response : [ResultData<PaginationDto<MentoringListResponseDto>>]
            ```
            """)
    public Mono<ResultData<PaginationDto<MentoringListResponseDto>>> getMentoringList(
            @RequestParam(required = false, value = "field")
            @Schema(description = "분야") String field,

            @RequestParam(required = false, value = "lecture_type")
            @Schema(description = "온오프라인 : ONLINE, OFFLINE") LectureType lectureType,

            @RequestParam(required = false, value = "region")
            @Schema(description = "지역") String region,

            @RequestParam(required = false, value = "sort_type")
            @Schema(description = "정렬 : DEFAULT(기본순), POPULAR(인기순), LATEST(최신순)") SortType sortType,

            @RequestParam(required = false, value = "search_text")
            @Schema(description = "검색내용") String searchText,

            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Schema(description = "조회 페이지(default : 1)")
            @Min(1) int page,

            @RequestParam(required = false, value = "size", defaultValue = "40")
            @Schema(description = "조회 개수(default : 40)")
            @Min(1) int size,

            ServerWebExchange exchange
    ){
        return mentoringProcessor.getMentoringList(field, lectureType, region, sortType, searchText, page, size, exchange);
    }


    //멘토링 상세조회
    @GetMapping("/{mentoring_id}")
    @Operation(operationId = "PULSE-114", summary = "멘토링 상세조회", description = """
            ### [ 설명 ]
            - 멘토링 상세정보를 조회합니다.
            <br>
            ### [ 주의사항 ]
            -
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [String] mentoring_id
            - Response : [MentoringDetailResponseDto]
            ```
            """)
    public Mono<ResultData<MentoringDetailResponseDto>> getMentoringDetail(
            @PathVariable(name = "mentoring_id") String mentoringId,
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
            - 멘토로 등록되지 않은 회원은 멘토링을 등록할 수 없습니다.
            - 좌표를 검색할 수 없는 주소로 입력할 경우, 멘토링 등록이 실패할 수 있습니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [MentoringPostRequestDto]
            - Response : [ResultData<ResultCodes>]
            ```
            """)
    public Mono<ResultData<ResultCodes>> postMentoring(
            @RequestBody @Valid MentoringPostRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        return mentoringProcessor.postMentoring(requestDto, exchange);
    }

    //좌표값에 따라 멘토링 글 조회하기
    @GetMapping("/location")
    @Operation(operationId = "PULSE-115", summary = "좌표값에 따라 멘토링 글 조회하기", description = """
            ### [ 설명 ]
            - 좌표값에 따라 멘토링 글을 조회합니다.
            <br>
            ### [ 주의사항 ]
            - 좌표값은 위도와 경도로 입력해야 합니다.
            - 아무값도 입력하지 않을 경우, 전체조회가 됩니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Response : [ResultData<List<MentoringListResponseDto>>]
            ```
            """)
    public Mono<ResultData<List<MentoringListResponseDto>>> getMentoringByLocation(
            @RequestParam(required = false, value = "latitude")
            @Schema(description = "위도") Double latitude,

            @RequestParam(required = false, value = "longitude")
            @Schema(description = "경도") Double longitude,

            @RequestParam(required = false, value = "distance")
            @Schema(description = "거리") int distance,

            ServerWebExchange exchange
    ) {
        return mentoringProcessor.getMentoringByLocation(latitude, longitude, distance, exchange);
    }

    // 마이페이지 > 멘토링 조회수 높은거 n개 노출 (default : 8개)
    @GetMapping("/popular")
    @Operation(summary = "[마이페이지] 멘토링 조회수 높은 순 n개 노출", description = """
            ### [ 설명 ]
            - 마이페이지에서 조회수 높은 멘토링을 n개 노출합니다.
            - 추후 확성을 고려하여 size 파라미터를 추가했습니다.
            - 기본값은 8개입니다.
            <br>
            ### [ 주의사항 ]
            - 기본값이 8개이므로 요청 시 size 파라미터를 생략하면 8개가 조회됩니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [int] size (default : 8)
            - Response : [ResultData<List<MentoringListResponseDto>>]
            ```
            """)
    public Mono<ResultData<List<MentoringListResponseDto>>> getPopularMentoringList(
            @RequestParam(required = false, value = "size", defaultValue = "8")
            @Schema(description = "조회할 멘토링 개수 (default : 8)") @Min(1) int size,
            ServerWebExchange exchange
    ) {
        return mentoringProcessor.getPopularMentoringList(size, exchange);
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




}
