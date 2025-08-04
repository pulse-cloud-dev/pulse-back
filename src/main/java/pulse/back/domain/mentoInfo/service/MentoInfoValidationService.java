package pulse.back.domain.mentoInfo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.common.repository.ItemRepository;
import pulse.back.common.repository.MentoInfoRepository;
import pulse.back.common.repository.MetaRepository;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import reactor.core.publisher.Mono;
import pulse.back.entity.common.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoInfoValidationService {
    private final MentoInfoRepository mentoInfoRepository;
    private final ItemRepository itemRepository;
    private final MetaRepository metaRepository;
    private final TokenProvider tokenProvider;

    public Mono<Boolean> validateMentorInfoRequestDto(MentoInfoRequestDto requestDto, ServerWebExchange exchange) {
        ObjectId memberId = tokenProvider.getMemberId(exchange);

        return mentoInfoRepository.existsByMemberId(memberId)
                .flatMap(isExist -> {
                    if (isExist) {
                        return Mono.error(new CustomException(ErrorCodes.MENTO_ALREADY_REGISTERED));
                    }
                    return Mono.just(true);
                })
                .flatMap(result -> {
                    if (requestDto.preferredLocations() != null && !requestDto.preferredLocations().isEmpty()) {
                        return validatePreferredLocations(requestDto.preferredLocations())
                                .thenReturn(result);
                    }
                    return Mono.just(result);
                });
    }

    private Mono<Void> validatePreferredLocations(List<String> preferredLocations) {
        return itemRepository.findAllByCategoryCode("REGION")
                .flatMap(regionItems -> {
                    Set<String> regionCodes = new HashSet<>(regionItems);

                    return metaRepository.findAllByItemCode(regionCodes)
                            .flatMap(metaCodes -> {
                                Set<String> validCodes = new HashSet<>(regionCodes);
                                validCodes.addAll(metaCodes);

                                List<String> invalidCodes = preferredLocations.stream()
                                        .filter(loc -> !validCodes.contains(loc))
                                        .toList();

                                if (!invalidCodes.isEmpty()) {
                                    return Mono.error(new CustomException(ErrorCodes.INVALID_PREFERRED_LOCATIONS));
                                }
                                return Mono.empty();
                            });
                });
    }
}
