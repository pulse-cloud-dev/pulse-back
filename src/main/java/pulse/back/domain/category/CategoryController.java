package pulse.back.domain.category;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pulse.back.common.response.ResultData;
import pulse.back.domain.category.dto.GetCategoryCodeListResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController{
    private final CategoryProcessor categoryProcessor;

    @GetMapping("")
    @Operation(summary = "카테고리 코드 확인", description = """
            __카테고리의 모든 코드를 확인하는 API__
            """)
    public Mono<ResultData<List<GetCategoryCodeListResponseDto>>> getCategoryCodeList() {
        return categoryProcessor.getCategoryCodeList();
    }


    //카테고리의 item 코드로 종류 확인하는 API

    //item의 코드로 mata코드의 종류 확인하는 API


}
