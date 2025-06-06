package pulse.back.domain.mentoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.enums.LectureType;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.enums.SortType;
import pulse.back.common.exception.CustomException;
import pulse.back.common.response.PaginationDto;
import pulse.back.common.response.ResultData;
import pulse.back.common.util.MyNumberUtils;
import pulse.back.domain.api.geocoding.GeocodingService;
import pulse.back.domain.member.repository.MemberRepository;
import pulse.back.domain.mentoring.dto.*;
import pulse.back.domain.mentoring.repository.MentoringRepository;
import pulse.back.entity.mentoring.Mentoring;
import pulse.back.entity.mentoring.MentoringLocation;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentoringBusinessService {
    private final MentoringRepository mentoringRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final GeocodingService geocodingService;

    //멘토링 검색필터 전용] 직업 정보 제공 (분야)
    public Mono<List<JobInfoList>> getFieldList(ServerWebExchange exchange) {
        return mentoringRepository.findJobInfo();
    }

    //멘토링 목록조회
    public Mono<ResultData<PaginationDto<MentoringListResponseDto>>> getMentoringList(
            String field, LectureType lectureType, String region, SortType sortType,
            String searchText, int page, int size, ServerWebExchange exchange) {

        Sort.Direction direction = convertSortTypeToDirection(sortType);

        return mentoringRepository.getMentoringListTotalCount(field, lectureType, region, sortType, searchText)
                .flatMap(totalCount -> {
                    int totalPages = MyNumberUtils.getTotalPages(totalCount, size);
                    final int adjustedPage = Math.min(totalPages, page);

                    return mentoringRepository.getMentoringList(field, lectureType, region, sortType, searchText, adjustedPage, size, exchange)
                            .next()  // Flux<List>를 Mono<List>로 변환
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
        return mentoringRepository.findById(new ObjectId(mentoringId))
                .flatMap(mentoring -> memberRepository.findById(mentoring.createdMemberId())
                        .map(member -> MentoringDetailResponseDto.of(mentoring, member)));
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

    //멘토 정보 등록
    public Mono<ResultCodes> postMentorInfo(MentoInfoRequestDto requestDto, ServerWebExchange exchange) {
        ObjectId memberId = tokenProvider.getMemberId(exchange);
        return mentoringRepository.insertMentorInfo(memberId, requestDto)
                .then(Mono.just(ResultCodes.SUCCESS));
    }

}
