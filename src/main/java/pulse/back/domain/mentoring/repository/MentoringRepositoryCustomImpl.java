package pulse.back.domain.mentoring.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.entity.member.Member;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Primary
@Slf4j
@RequiredArgsConstructor
public class MentoringRepositoryCustomImpl implements MentoringRepositoryCustom{
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<Void> insertMentorInfo(ObjectId mentorId, MentoInfoRequestDto requestDto) {
        Update update = new Update();

        // 학력 정보가 존재하는 경우에만 업데이트
        if (requestDto.academicInfoList() != null && !requestDto.academicInfoList().isEmpty()) {
            update.set("academicInfo", requestDto.academicInfoList());
        }

        // 자격증 정보가 존재하는 경우에만 업데이트
        if (requestDto.certificateInfoList() != null && !requestDto.certificateInfoList().isEmpty()) {
            update.set("certificateInfo", requestDto.certificateInfoList());
        }

        // 직업 정보가 존재하는 경우에만 업데이트
        if (requestDto.jobInfo() != null) {
            update.set("jobInfo", requestDto.jobInfo());
        }

        // 경력 정보가 존재하는 경우에만 업데이트
        if (requestDto.careerInfoList() != null && !requestDto.careerInfoList().isEmpty()) {
            update.set("careerInfo", requestDto.careerInfoList());
        }

        update.set("updatedAt", LocalDateTime.now());
        update.set("updatedMemberId", mentorId);

        Query query = Query.query(Criteria.where("_id").is(mentorId));
        return mongoOperations.updateFirst(query, update, Member.class).then();
    }
}
