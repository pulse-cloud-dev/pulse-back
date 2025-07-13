package pulse.back.domain.mentoring;

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

    @PostMapping("/upload")
    public ResultData<ResultCodes> uploadMentoringBookmark(
            @RequestBody @Valid UploadMentoringBookmarkRequestDto requestDto, ServerWebExchange exchange
    ) {
        return mentoringProcessor.uploadMentoringBookmark(requestDto, exchange);
    }
}
