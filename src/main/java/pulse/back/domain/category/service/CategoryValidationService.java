package pulse.back.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pulse.back.common.repository.CategoryRepository;
import pulse.back.common.repository.ItemRepository;
import pulse.back.common.repository.MetaRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryValidationService {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final MetaRepository metaRepository;

    public Mono<Boolean> validateToCategoryCode(String categoryCode) {
        return categoryRepository.existsByCategoryCode(categoryCode);
    }

    public Mono<Boolean> validateToItemCode(String itemCode) {
        return itemRepository.existsByItemCode(itemCode);
    }

    public Mono<Boolean> validateToMetaCode(String metaCode) {
        return metaRepository.existsByMetaCode(metaCode);
    }
}
