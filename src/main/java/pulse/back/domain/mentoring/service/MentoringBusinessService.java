package pulse.back.domain.mentoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.enums.LectureType;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.enums.SortType;
import pulse.back.common.exception.CustomException;
import pulse.back.common.repository.*;
import pulse.back.common.response.PaginationDto;
import pulse.back.common.response.ResultData;
import pulse.back.common.util.MyNumberUtils;
import pulse.back.domain.api.geocoding.GeocodingService;
import pulse.back.domain.mentoring.dto.*;
import pulse.back.entity.member.Member;
import pulse.back.entity.mento.MentoInfo;
import pulse.back.entity.mentoring.Mentoring;
import pulse.back.entity.mentoring.MentoringBookmarks;
import pulse.back.entity.mentoring.MentoringViewLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoringBusinessService {
    private final MentoringRepository mentoringRepository;
    private final MemberRepository memberRepository;
    private final MentoInfoRepository mentoInfoRepository;
    private final MentoringViewLogRepository mentoringViewLogRepository;
    private final MentoringBookmarksRepository mentoringBookmarksRepository;
    private final TokenProvider tokenProvider;
    private final GeocodingService geocodingService;

    //멘토링 검색필터 전용] 직업 정보 제공 (분야)
    public Mono<List<JobInfoList>> getFieldList(ServerWebExchange exchange) {
        return mentoringRepository.findJobInfo();
    }

    //멘토링 목록조회
    public Mono<ResultData<PaginationDto<MentoringListResponseDto>>> getMentoringList(
            String field, LectureType lectureType, String region, SortType sortType,
            String searchText, int page, int size, ServerWebExchange exchange
    ) {

        Sort.Direction direction = convertSortTypeToDirection(sortType);

        ObjectId requesterId = null;

        if (exchange != null) {
            try {
                requesterId = tokenProvider.getMemberId(exchange);
            } catch (Exception ignored) {}
        }

        ObjectId finalRequesterId = requesterId;
        return mentoringRepository.getMentoringListTotalCount(field, lectureType, region, sortType, searchText)
                .flatMap(totalCount -> {
                    int totalPages = MyNumberUtils.getTotalPages(totalCount, size);
                    final int adjustedPage = Math.min(totalPages, page);

                    return mentoringRepository.getMentoringList(field, lectureType, region, sortType, searchText, adjustedPage, size, finalRequesterId)
                            .map(mentoringList -> {
                                PaginationDto<MentoringListResponseDto> paginationDto = PaginationDto.<MentoringListResponseDto>builder()
                                        .contents(mentoringList)
                                        .totalCount(totalCount)
                                        .totalPages(totalPages)
                                        .size(size)
                                        .page(adjustedPage)
                                        .sort(direction)
                                        .build();
                                return new ResultData<>(paginationDto, "멘토링 목록 조회에 성공하였습니다.");
                            });
                });
    }

    private Sort.Direction convertSortTypeToDirection(SortType sortType) {
        if (sortType == null) {
            return Sort.Direction.DESC;
        }

        switch (sortType) {
            case LATEST:
            case DEFAULT:
                return Sort.Direction.DESC;
            case POPULAR:
                return Sort.Direction.DESC;
            default:
                return Sort.Direction.DESC;
        }
    }

    //멘토링 상세조회
    public Mono<MentoringDetailResponseDto> getMentoringDetail(String mentoringId, ServerWebExchange exchange) {
        ObjectId mentoringObjectId = new ObjectId(mentoringId);

        return mentoringRepository.findById(mentoringObjectId)
                .flatMap(mentoring -> {
                    log.info("멘토링 상세조회: mentoringId={}, mentoring={}", mentoringId, mentoring);

                    Mono<Member> memberMono = memberRepository.findById(mentoring.createdMemberId());
                    Mono<MentoInfo> mentoMono = mentoInfoRepository.findByMemberId(mentoring.createdMemberId());

                    return Mono.zip(memberMono, mentoMono)
                            .flatMap(tuple -> {
                                Member member = tuple.getT1();
                                MentoInfo mentoInfo = tuple.getT2();
                                MentoringDetailResponseDto response = MentoringDetailResponseDto.of(mentoring, member, mentoInfo);

                                return increaseViewCount(mentoringObjectId, exchange).thenReturn(response);
                            });
                });
    }


    //멘토링 등록
    public Mono<ResultCodes> registerMentoring(MentoringPostRequestDto requestDto, ServerWebExchange exchange) {
        ObjectId memberId = tokenProvider.getMemberId(exchange);

        if (requestDto.lectureType() == LectureType.ONLINE) {
            // 온라인 강의인 경우 geocoding 없이 바로 insert
            return mentoringRepository.insert(Mentoring.from(requestDto, null, memberId))
                    .then(Mono.just(ResultCodes.SUCCESS));
        } else {
            // 오프라인 강의인 경우 geocoding 후 insert
            return geocodingService.getGeocodingResult(requestDto.address(), requestDto.detailAddress())
                    .flatMap(mentoringLocation ->
                            mentoringRepository.insert(Mentoring.from(requestDto, mentoringLocation, memberId))
                                    .then(Mono.just(ResultCodes.SUCCESS))
                    )
                    .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.INVALID_ADDRESS)));
        }
    }

    // 조회수 증가 로직 메서드
    private Mono<Void> increaseViewCount(ObjectId mentoringId, ServerWebExchange exchange) {
        try {
            String ipAddress = getClientIpAddress(exchange);
            ObjectId memberId = null;

            try {
                memberId = tokenProvider.getMemberId(exchange);
            } catch (Exception e) {
                // 토큰 관련 에러는 무시하고 비회원으로 처리
                log.debug("토큰 없음 또는 유효하지 않음, 비회원으로 처리: {}", e.getMessage());
            }

            if (memberId == null) {
                // 비회원: IP 주소로 중복 체크
                return mentoringViewLogRepository.checkViewLogByIp(mentoringId, ipAddress)
                        .flatMap(exists -> {
                            if (!exists) {
                                return insertViewLogAndUpdateCount(mentoringId, ipAddress, null);
                            }
                            return Mono.empty();
                        });
            } else {
                // 회원: memberId로 중복 체크
                ObjectId finalMemberId = memberId;
                return mentoringViewLogRepository.checkViewLogByMemberId(mentoringId, memberId)
                        .flatMap(exists -> {
                            if (!exists) {
                                return insertViewLogAndUpdateCount(mentoringId, ipAddress, finalMemberId);
                            }
                            return Mono.empty();
                        });
            }
        } catch (Exception e) {
            // 전체적인 에러 발생 시 조회수 증가 로직을 건너뛰고 로그만 남김
            log.error("조회수 증가 처리 중 에러 발생: {}", e.getMessage());
            return Mono.empty();
        }
    }

    // 조회 로그 삽입 및 조회수 증가 메서드
    private Mono<Void> insertViewLogAndUpdateCount(ObjectId mentoringId, String ipAddress, ObjectId memberId) {
        // 조회 로그 생성
        MentoringViewLog viewLog = new MentoringViewLog(
                null,
                mentoringId,
                memberId,
                ipAddress,
                LocalDateTime.now(),
                memberId
        );

        // 조회 로그 삽입과 조회수 증가를 병렬로 실행
        return Mono.zip(
                mentoringViewLogRepository.save(viewLog),
                mentoringRepository.incrementViewCount(mentoringId)
        ).then();
    }

    // 클라이언트 IP 주소 추출 메서드
    private String getClientIpAddress(ServerWebExchange exchange) {
        try {
            ServerHttpRequest request = exchange.getRequest();

            String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIp = request.getHeaders().getFirst("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }

            return request.getRemoteAddress() != null ?
                    request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        } catch (Exception e) {
            log.error("IP 주소 추출 중 에러 발생: {}", e.getMessage());
            return "unknown";
        }
    }

    public Mono<ResultCodes> uploadMentoringBookmark(UploadMentoringBookmarkRequestDto requestDto, ServerWebExchange exchange) {
        log.debug("[validation] request : {}", requestDto);
        ObjectId memberId = tokenProvider.getMemberId(exchange);
        ObjectId mentoringId = new ObjectId(requestDto.mentoringId());

        return mentoringRepository.findById(mentoringId)
                .flatMap(mentoring -> {
                    if (mentoring == null) {
                        return Mono.error(new CustomException(ErrorCodes.MENTORING_NOT_FOUND));
                    }

                    if (requestDto.isBookmark()) {
                        // 북마크 추가 - 기존에 있는지 확인 후 없으면 추가
                        return mentoringBookmarksRepository.findByMentoringIdAndMemberId(mentoringId, memberId)
                                .flatMap(existingBookmark -> {
                                    // 이미 북마크가 존재하면 성공 반환 (중복 방지)
                                    return Mono.just(ResultCodes.SUCCESS);
                                })
                                .switchIfEmpty(
                                        // 북마크가 없으면 새로 추가
                                        Mono.defer(() -> {
                                            MentoringBookmarks mentoringBookmarks = UploadMentoringBookmarkRequestDto.from(requestDto, memberId);
                                            return mentoringBookmarksRepository.insert(mentoringBookmarks)
                                                    .then(Mono.just(ResultCodes.SUCCESS));
                                        })
                                );
                    } else {
                        // 북마크 삭제 - 기존에 있는지 확인 후 있으면 삭제
                        return mentoringBookmarksRepository.findByMentoringIdAndMemberId(mentoringId, memberId)
                                .flatMap(existingBookmark -> {
                                    // 북마크가 존재하면 삭제
                                    return mentoringBookmarksRepository.delete(existingBookmark)
                                            .then(Mono.just(ResultCodes.SUCCESS));
                                })
                                .switchIfEmpty(
                                        // 북마크가 없으면 에러 반환
                                        Mono.error(new CustomException(ErrorCodes.BOOKMARK_NOT_FOUND))
                                );
                    }
                })
                .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.MENTORING_NOT_FOUND)));
    }

    public Mono<List<MentoringListResponseDto>> getMentoringByLocation(Double latitude, Double longitude, int distance, ServerWebExchange exchange) {
        ObjectId currentMemberId = tokenProvider.getMemberId(exchange);

        return mentoringRepository.findMentoringByLocation(latitude, longitude, distance, currentMemberId);
    }

    public Mono<List<MentoringListResponseDto>> getPopularMentoringList(int size, ServerWebExchange exchange) {
        return mentoringRepository.getPopularMentoringList(size)
                .flatMap(mentoringList -> {
                    if (mentoringList == null || mentoringList.isEmpty()) {
                        return Mono.just(List.of());
                    }
                    return Mono.just(mentoringList);
                });
    }


}
