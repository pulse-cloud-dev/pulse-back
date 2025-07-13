package pulse.back.common.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import pulse.back.domain.member.dto.AcademicInfoRequestDto;
import pulse.back.domain.member.dto.CareerInfoRequestDto;
import pulse.back.domain.member.dto.CertificateInfoRequestDto;
import pulse.back.domain.mentoring.dto.MentoInfoRequestDto;
import pulse.back.entity.member.Member;
import pulse.back.entity.mento.MentoInfo;
import pulse.back.entity.mentoring.Mentoring;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Slf4j
@Primary
@RequiredArgsConstructor
public class MentoInfoRepositoryCustomImpl implements MentoInfoRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<Boolean> existsByMemberId(ObjectId memberId) {
        return mongoOperations.exists(
                Query.query(Criteria.where("memberId").is(memberId)),
                MentoInfo.class);
    }

    @Override
    public Mono<MentoInfo> findByMemberId(ObjectId memberId) {
        // memberId로 MentoInfo 조회
        return mongoOperations.findOne(
                Query.query(Criteria.where("memberId").is(memberId)),
                MentoInfo.class);
    }

    @Override
    public Mono<Void> insertMentorInfo(ObjectId mentorId, MentoInfoRequestDto requestDto) {
        MentoInfo mentoInfo = new MentoInfo(
                new ObjectId(), // 새로운 ID 생성
                mentorId, // 회원 아이디
                requestDto.mentorIntroduction(), // 멘토소개말
                requestDto.academicInfoList() != null && !requestDto.academicInfoList().isEmpty() ?
                        AcademicInfoRequestDto.of(requestDto.academicInfoList()) : null, // 학력정보
                requestDto.certificateInfoList() != null && !requestDto.certificateInfoList().isEmpty() ?
                        CertificateInfoRequestDto.of(requestDto.certificateInfoList()) : null, // 자격증정보
                requestDto.jobInfo() != null ?
                        requestDto.jobInfo().jobCode() : null, // 직업정보 (JobInfoRequestDto -> String)
                requestDto.careerInfoList() != null && !requestDto.careerInfoList().isEmpty() ?
                        CareerInfoRequestDto.of(requestDto.careerInfoList()) : null, // 경력정보
                LocalDateTime.now(), // 생성일
                null, // 수정일
                null, // 삭제일
                mentorId, // 생성자
                null, // 수정자
                null // 삭제자
        );

        return mongoOperations.insert(mentoInfo).then();
    }
}
