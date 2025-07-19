package pulse.back.domain.mentoring;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.response.ResultData;
import pulse.back.domain.mentoring.dto.UploadMentoringBookmarkRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentoring-bookmarks")
public class MentoringBookmarksController {
    private final MentoringProcessor mentoringProcessor;
//
    @PostMapping("/upload")
    @Operation(summary = "멘토링 북마크 업로드", description = """
            ### [ 설명 ]
            - 멘토링 북마크를 업로드합니다.
            - false 로 요청한 경우, 기존에 저장된 북마크를 삭제합니다.
            <br>
            ### [ 주의사항 ]
            - 
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [UploadMentoringBookmarkRequestDto]
            - Response : [ResultData<ResultCodes>]
            ```
            """)
    public ResultData<ResultCodes> uploadMentoringBookmark(
            @RequestBody @Valid UploadMentoringBookmarkRequestDto requestDto, ServerWebExchange exchange
    ) {
        return mentoringProcessor.uploadMentoringBookmark(requestDto, exchange);
    }
}
