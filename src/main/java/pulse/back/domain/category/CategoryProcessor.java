package pulse.back.domain.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pulse.back.common.repository.CategoryRepository;
import pulse.back.common.response.ResultData;
import pulse.back.domain.category.dto.GetCategoryCodeListResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryProcessor {
    private final CategoryRepository categoryRepository;

    public Mono<ResultData<List<GetCategoryCodeListResponseDto>>> getCategoryCodeList() {
        return categoryRepository.findAll()
                .map(GetCategoryCodeListResponseDto::of)
                .collectList()
                .map(categoryList -> new ResultData<>(categoryList, "카테고리 코드 목록을 조회했습니다."));
    }
}
