package pulse.back.domain.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.common.repository.CategoryRepository;
import pulse.back.common.repository.ItemRepository;
import pulse.back.common.repository.MetaRepository;
import pulse.back.common.response.ResultData;
import pulse.back.domain.category.dto.GetCategoryCodeListResponseDto;
import pulse.back.domain.category.dto.GetItemCodeListResponseDto;
import pulse.back.domain.category.dto.GetMetaCodeListResponseDto;
import pulse.back.domain.category.service.CategoryBusinessService;
import pulse.back.domain.category.service.CategoryValidationService;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryProcessor {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final MetaRepository metaRepository;

    private final CategoryBusinessService categoryBusinessService;
    private final CategoryValidationService categoryValidationService;

    public Mono<ResultData<List<GetCategoryCodeListResponseDto>>> getCategoryCodeList() {
        return categoryRepository.findAll()
                .map(GetCategoryCodeListResponseDto::of)
                .collectList()
                .map(categoryList -> new ResultData<>(categoryList, "카테고리 코드 목록을 조회했습니다."));
    }

    public Mono<ResultData<List<GetItemCodeListResponseDto>>> getItemCodeList(String categoryCode) {
        return categoryValidationService.validateToCategoryCode(categoryCode)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.error(new CustomException(ErrorCodes.INVALID_CODE));
                    }
                    return itemRepository.findByItemCode(categoryCode)
                            .map(itemList -> new ResultData<>(itemList, "아이템 코드 목록을 조회했습니다."));
                });
    }

    public Mono<ResultData<List<GetMetaCodeListResponseDto>>> getMetaCodeList(String itemCode) {
        return categoryValidationService.validateToItemCode(itemCode)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.error(new CustomException(ErrorCodes.INVALID_CODE));
                    }
                    return metaRepository.findByMetaCode(itemCode)
                            .map(metaList -> new ResultData<>(metaList, "메타 코드 목록을 조회했습니다."));
                });
    }
}
