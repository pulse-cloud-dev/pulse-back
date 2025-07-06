package pulse.back.entity.mento;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public record MentoInfo(
        // pk
        @Id
        ObjectId id,

        // 회원 아이디
        ObjectId memberId,

        // 멘토소개말 (멘토 등록했을 경우)
        String mentorIntroduction,

        // 학력정보
        List<AcademicInfo> academicInfo,

        // 자격증정보
        List<CertificateInfo> certificateInfo,

        // 직업정보
        String jobInfo,

        // 경력정보
        List<CareerInfo> careerInfo,

        // 생성일
        @Indexed
        OffsetDateTime createdAt,

        // 수정일
        OffsetDateTime updatedAt,

        // 삭제일
        OffsetDateTime deletedAt,

        // 생성자
        ObjectId createdMemberId,

        // 수정자
        ObjectId updatedMemberId,

        // 삭제자
        ObjectId deletedMemberId
) {
}
