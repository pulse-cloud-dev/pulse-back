package pulse.back.domain.mentoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.enums.LectureType;
import pulse.back.common.enums.SortType;
import pulse.back.common.exception.CustomException;
import pulse.back.common.util.CheckDateUtils;
import pulse.back.common.repository.MemberRepository;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;
import pulse.back.common.repository.MentoringRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoringValidationService {
    private final MentoringRepository mentoringRepository;
    private final MemberRepository memberRepository;

    private final TokenProvider tokenProvider;
    private final CheckDateUtils checkDateUtils;

    //멘토링 목록조회
    public Mono<Boolean> validateMentoringListRequestDto(String field, LectureType lectureType, String region, SortType sortType, String searchText, int page, int size, ServerWebExchange exchange) {
        return Mono.just(true);
    }

    //멘토링 상세조회
    public Mono<Boolean> validateMentoringId(String mentoringId, ServerWebExchange exchange) {
        return mentoringRepository.findById(new ObjectId(mentoringId))
                .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.MENTORING_NOT_FOUND)))
                .map(mentoring -> true);
    }

    //멘토링 등록시 유효성 검사
    public Mono<Boolean> validateMentoringPostRequestDto(MentoringPostRequestDto requestDto, ServerWebExchange exchange) {
        return memberRepository.checkMentorInfoExists(tokenProvider.getMemberId(exchange))
                .flatMap(isExists -> {
                    if (Boolean.TRUE.equals(isExists)) {
                        // CheckDateUtils를 사용한 날짜 검증
                        return checkDateUtils.validateMentoringDates(requestDto)
                                .then(Mono.fromCallable(() -> {
                                    // 강의 형식과 주소 정보 검증
                                    if (requestDto.lectureType() == LectureType.ONLINE) {
                                        // ONLINE인 경우 address와 detailAddress가 존재하면 안됨
                                        if (hasValue(requestDto.address()) || hasValue(requestDto.detailAddress())) {
                                            throw new CustomException(ErrorCodes.INVALID_ONLINE_ADDRESS);
                                        }
                                    } else if (requestDto.lectureType() == LectureType.OFFLINE) {
                                        // OFFLINE인 경우 address가 반드시 존재해야 함
                                        if (!hasValue(requestDto.address())) {
                                            throw new CustomException(ErrorCodes.MISSING_OFFLINE_ADDRESS);
                                        }
                                    }
                                    return true;
                                }));
                    } else {
                        return Mono.error(new CustomException(ErrorCodes.MENTO_NOT_REGISTERED));
                    }
                });
    }

    private boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
