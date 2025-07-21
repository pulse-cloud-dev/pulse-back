package pulse.back.common.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.enums.LectureType;
import pulse.back.common.enums.SortType;
import pulse.back.domain.member.dto.JobInfoResponseDto;
import pulse.back.domain.mentoring.dto.JobInfoList;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.domain.mentoring.dto.MentoringDetailResponseDto;
import pulse.back.domain.mentoring.dto.MentoringListResponseDto;
import pulse.back.entity.common.Item;
import pulse.back.entity.common.Meta;
import pulse.back.entity.member.Member;
import pulse.back.entity.mento.MentoInfo;
import pulse.back.entity.mentoring.Mentoring;
import pulse.back.entity.mentoring.MentoringBookmarks;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Slf4j
@RequiredArgsConstructor
public class MentoringRepositoryCustomImpl implements MentoringRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<List<JobInfoList>> findJobInfo() {
        // 1. 모든 JOB 카테고리 항목 조회
        return mongoOperations.find(
                        Query.query(Criteria.where("categoryCode").is("JOB")),
                        Item.class
                )
                .collectList()
                .flatMap(items -> {
                    List<Mono<JobInfoList>> jobInfoMonos = items.stream()
                            .map(item -> {
                                String jobCategoryCode = item.code();
                                String jobCategoryName = item.name();

                                // 2. meta 컬렉션에서 관련 데이터 조회
                                return mongoOperations.find(
                                                Query.query(Criteria.where("itemCode").is(jobCategoryCode)),
                                                Meta.class
                                        )
                                        .map(meta -> new JobInfoResponseDto(meta.name(), meta.code()))
                                        .collectList()
                                        .map(jobInfoList -> new JobInfoList(
                                                jobCategoryCode,
                                                jobCategoryName,
                                                jobInfoList
                                        ));
                            })
                            .collect(Collectors.toList());

                    return Flux.merge(jobInfoMonos).collectList();
                });
    }

    @Override
    public Flux<List<MentoringListResponseDto>> getMentoringList(
            String field, LectureType lectureType, String region,
            SortType sortType, String searchText, int page, int size,
            ServerWebExchange exchange) {

        return getMentoringSearchQuery(field, lectureType, region, sortType, searchText)
                .flatMapMany(query -> {
                    query.skip((long) (page - 1) * size);
                    query.limit(size);

                    return mongoOperations.find(query, Mentoring.class)
                            .flatMap(mentoring -> {
                                ObjectId memberId = mentoring.createdMemberId();
                                ObjectId mentoringId = mentoring.id(); // 멘토링 ID

                                Mono<Member> memberMono = mongoOperations.findById(memberId, Member.class);

                                Query mentoQuery = Query.query(Criteria.where("memberId").is(memberId));
                                Mono<MentoInfo> mentoMono = mongoOperations.findOne(mentoQuery, MentoInfo.class);

                                // 북마크 여부 확인 쿼리 추가
                                Query bookmarkQuery = Query.query(
                                        Criteria.where("mentoringId").is(mentoringId)
                                                .and("memberId").is(memberId)
                                );
                                Mono<Boolean> bookmarkMono = mongoOperations.exists(bookmarkQuery, MentoringBookmarks.class);

                                return Mono.zip(memberMono, mentoMono, bookmarkMono)
                                        .map(tuple -> MentoringListResponseDto.of(
                                                mentoring,
                                                tuple.getT1(),
                                                tuple.getT2(),
                                                tuple.getT3()
                                        ));
                            })
                            .collectList();
                });
    }

    @Override
    public Mono<Long> getMentoringListTotalCount(String field, LectureType lectureType, String region, SortType sortType, String searchText) {
        return getMentoringSearchQuery(field, lectureType, region, sortType, searchText)
                .flatMap(query -> mongoOperations.count(query, Mentoring.class));
    }

    @Override
    public Mono<Void> incrementViewCount(ObjectId mentoringId) {
        Query query = new Query(Criteria.where("id").is(mentoringId));
        Update update = new Update().inc("viewCount", 1);

        return mongoOperations.updateFirst(query, update, Mentoring.class)
                .then();
    }

    @Override
    public Mono<Integer> countByCreatedMemberId(ObjectId memberId) {
        return mongoOperations.count(
                        Query.query(Criteria.where("createdMemberId").is(memberId)),
                        Mentoring.class)
                .map(Long::intValue);
    }

    @Override
    public Mono<List<MentoringListResponseDto>> findMentoringByLocation(Double latitude, Double longitude, int distance, ObjectId currentMemberId) {
        Query query = new Query();

        // OFFLINE 타입이면서 좌표값이 존재하는 조건
        query.addCriteria(Criteria.where("lectureType").is(LectureType.OFFLINE)
                .and("location.x").exists(true).ne(null).ne("")
                .and("location.y").exists(true).ne(null).ne(""));

        return mongoOperations.find(query, Mentoring.class)
                .filter(mentoring -> {
                    try {
                        // 거리 계산 (하버사인 공식 사용)
                        double mentoringLat = Double.parseDouble(mentoring.location().y());
                        double mentoringLng = Double.parseDouble(mentoring.location().x());
                        double calculatedDistance = calculateDistance(latitude, longitude, mentoringLat, mentoringLng);
                        return calculatedDistance <= distance;
                    } catch (NumberFormatException e) {
                        // 좌표값 파싱 실패 시 제외
                        return false;
                    }
                })
                .flatMap(mentoring -> {
                    ObjectId memberId = mentoring.createdMemberId();
                    ObjectId mentoringId = mentoring.id();

                    Mono<Member> memberMono = mongoOperations.findById(memberId, Member.class);

                    Query mentoQuery = Query.query(Criteria.where("memberId").is(memberId));
                    Mono<MentoInfo> mentoMono = mongoOperations.findOne(mentoQuery, MentoInfo.class);

                    // 북마크 여부 확인
                    Query bookmarkQuery = Query.query(
                            Criteria.where("mentoringId").is(mentoringId)
                                    .and("memberId").is(currentMemberId)
                    );
                    Mono<Boolean> bookmarkMono = mongoOperations.exists(bookmarkQuery, MentoringBookmarks.class);

                    return Mono.zip(memberMono, mentoMono, bookmarkMono)
                            .map(tuple -> MentoringListResponseDto.of(
                                    mentoring,
                                    tuple.getT1(),
                                    tuple.getT2(),
                                    tuple.getT3()
                            ));
                })
                .collectList();
    }

    @Override
    public Mono<List<MentoringListResponseDto>> getPopularMentoringList(int size) {
        Query query = new Query();

        // 인기순 정렬 (fallback으로 createdAt 추가)
        query.with(Sort.by(
                Sort.Order.desc("viewCount"),
                Sort.Order.desc("createdAt")
        ));

        query.limit(size);

        return mongoOperations.find(query, Mentoring.class)
                .flatMap(mentoring -> {
                    ObjectId memberId = mentoring.createdMemberId();
                    ObjectId mentoringId = mentoring.id();

                    Mono<Member> memberMono = mongoOperations.findById(memberId, Member.class);

                    Query mentoQuery = Query.query(Criteria.where("memberId").is(memberId));
                    Mono<MentoInfo> mentoMono = mongoOperations.findOne(mentoQuery, MentoInfo.class);

                    // 북마크 여부 확인
                    Query bookmarkQuery = Query.query(
                            Criteria.where("mentoringId").is(mentoringId)
                                    .and("memberId").is(memberId)
                    );
                    Mono<Boolean> bookmarkMono = mongoOperations.exists(bookmarkQuery, MentoringBookmarks.class);

                    return Mono.zip(memberMono, mentoMono, bookmarkMono)
                            .map(tuple -> MentoringListResponseDto.of(
                                    mentoring,
                                    tuple.getT1(),
                                    tuple.getT2(),
                                    tuple.getT3()
                            ));
                })
                .collectList();
    }



    private Mono<Query> getMentoringSearchQuery(
            String field, LectureType lectureType, String region, SortType sortType, String searchText
    ) {
        Query query = new Query();

        if (field != null) {
            query.addCriteria(Criteria.where("field").is(field));
        }

        if (lectureType != null) {
            query.addCriteria(Criteria.where("lectureType").is(lectureType));
        }

        if (region != null) {
            query.addCriteria(Criteria.where("region").is(region));
        }

        // 정렬 조건 추가
        if (sortType != null) {
            switch (sortType) {
                case LATEST:
                    query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
                    break;
                case POPULAR:
                    // 인기순 정렬 로직은 추후 구현
                    break;
            }
        }

        if (searchText != null) {
            return getMemberIdsBySearchText(searchText)
                    .collectList()
                    .map(memberIds -> {
                        List<Criteria> searchCriteria = new ArrayList<>();

                        // Mentoring 컬렉션 내 필드 검색
                        searchCriteria.add(Criteria.where("title").regex(searchText, "i"));
                        searchCriteria.add(Criteria.where("content").regex(searchText, "i"));
                        searchCriteria.add(Criteria.where("address").regex(searchText, "i"));
                        searchCriteria.add(Criteria.where("detailAddress").regex(searchText, "i"));

                        // Member 관련 검색 - 찾은 멤버 ID들로 OR 조건 구성
                        if (!memberIds.isEmpty()) {
                            searchCriteria.add(Criteria.where("createdMemberId").in(memberIds));
                        }

                        query.addCriteria(new Criteria().orOperator(searchCriteria.toArray(new Criteria[0])));
                        return query;
                    });
        }

        return Mono.just(query);
    }

    private Flux<String> getMemberIdsBySearchText(String searchText) {
        Query memberQuery = new Query();
        List<Criteria> memberCriteria = new ArrayList<>();

        memberCriteria.add(Criteria.where("nickName").regex(searchText, "i"));
        memberCriteria.add(Criteria.where("jobInfo").regex(searchText, "i"));

        memberQuery.addCriteria(new Criteria().orOperator(memberCriteria.toArray(new Criteria[0])));  // Member 검색도 OR 조건으로 변경

        return mongoOperations.find(memberQuery, Member.class)
                .map(member -> member.id().toString());
    }

    // 하버사인 공식을 사용한 거리 계산 (단위: km)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}