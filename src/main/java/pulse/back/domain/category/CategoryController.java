package pulse.back.domain.category;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pulse.back.common.response.ResultData;
import pulse.back.domain.category.dto.GetCategoryCodeListResponseDto;
import pulse.back.domain.category.dto.GetItemCodeListResponseDto;
import pulse.back.domain.category.dto.GetMetaCodeListResponseDto;
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
    @GetMapping("/item-list/{category_code}")
    @Operation(summary = "아이템 코드 확인", description = """
            __카테고리의 코드로 아이템 리스트를 확인하는 API__
            """)
    public Mono<ResultData<List<GetItemCodeListResponseDto>>> getItemCodeList(
            @PathVariable(name = "category_code") String categoryCode
    ) {
        return categoryProcessor.getItemCodeList(categoryCode);
    }

    //item의 코드로 mata코드의 종류 확인하는 API
    @GetMapping("/meta-list/{item_code}")
    @Operation(summary = "메타 코드 확인", description = """
            __아이템의 코드로 메타 코드를 확인하는 API__
            """)
    public Mono<ResultData<List<GetMetaCodeListResponseDto>>> getMetaCodeList(
            @PathVariable(name = "item_code") String itemCode
    ) {
        return categoryProcessor.getMetaCodeList(itemCode);
    }


}
