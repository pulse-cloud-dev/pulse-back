package pulse.back.common.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import pulse.back.entity.mentoring.MentoringBookmarks;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Primary
@RequiredArgsConstructor
public class MentoringBookmarksRepositoryCustomImpl implements MentoringBookmarksRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<Void> insertMentoringBookmark(ObjectId memberId, ObjectId mentoringId, LocalDateTime createdAt) {
        // 멘토링 북마크 엔티티 생성
        MentoringBookmarks mentoringBookmark = new MentoringBookmarks(
                new ObjectId(), // 새로운 ID 생성
                mentoringId, // 멘토링 ID
                memberId, // 회원 ID
                createdAt, // 생성일
                memberId // 생성자 ID
        );

        // MongoDB에 멘토링 북마크 저장
        return mongoOperations.insert(mentoringBookmark)
                .then(); // Mono<Void> 반환
    }
}
