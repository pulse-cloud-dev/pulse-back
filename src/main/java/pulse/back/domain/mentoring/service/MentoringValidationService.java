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
import pulse.back.common.repository.MentoInfoRepository;
import pulse.back.common.repository.MentoringBookmarksRepository;
import pulse.back.common.util.CheckDateUtils;
import pulse.back.common.repository.MemberRepository;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;
import pulse.back.common.repository.MentoringRepository;
import pulse.back.domain.mentoring.dto.UploadMentoringBookmarkRequestDto;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoringValidationService {
    private final MentoringRepository mentoringRepository;
    private final MentoInfoRepository mentoInfoRepository;
    private final MemberRepository memberRepository;
    private final MentoringBookmarksRepository mentoringBookmarksRepository;

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
        ObjectId memberId = tokenProvider.getMemberId(exchange);

        // 멘토 등록 여부 추가 확인
        return mentoInfoRepository.existsByMemberId(memberId)
                .flatMap(isRegistered -> {
                    if (!isRegistered) {
                        return Mono.error(new CustomException(ErrorCodes.MENTO_NOT_REGISTERED_USER));
                    }

                    // 멘토링 등록 개수 확인 (5개 초과 시 에러)
                    return mentoringRepository.countByCreatedMemberId(memberId)
                            .flatMap(count -> {
                                if (count > 5) {
                                    return Mono.error(new CustomException(ErrorCodes.MENTORING_ALREADY_EXIST));
                                }

                                // 날짜 검증
                                return checkDateUtils.validateMentoringDates(requestDto)
                                        .then(Mono.fromCallable(() -> {
                                            // 강의 형식과 주소 정보 검증
                                            if (requestDto.lectureType() == LectureType.ONLINE) {
                                                if (hasValue(requestDto.address()) || hasValue(requestDto.detailAddress())) {
                                                    throw new CustomException(ErrorCodes.INVALID_ONLINE_ADDRESS);
                                                }
                                            } else if (requestDto.lectureType() == LectureType.OFFLINE) {
                                                if (!hasValue(requestDto.address())) {
                                                    throw new CustomException(ErrorCodes.MISSING_OFFLINE_ADDRESS);
                                                }
                                            }
                                            return true;
                                        }));
                            });
                });
    }

    public Mono<Boolean> validateUploadMentoringBookmark(UploadMentoringBookmarkRequestDto requestDto, ServerWebExchange exchange) {
        ObjectId memberId = tokenProvider.getMemberId(exchange);

        if (requestDto.isBookmark()) {
            return mentoringBookmarksRepository.countByMemberId(memberId)
                    .flatMap(count -> {
                        if (count > 500) {
                            return Mono.error(new CustomException(ErrorCodes.BOOKMARK_REGISTRATION_LIMIT_EXCEEDED));
                        }
                        return Mono.just(true);
                    });
        } else {
            // 북마크 삭제 요청인 경우에는 개수 제한 검증 불필요
            return Mono.just(true);
        }
    }

    private boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
