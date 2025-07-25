package pulse.back.entity.member;

import com.querydsl.core.annotations.QueryEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;
import pulse.back.common.enums.MemberRole;
import pulse.back.entity.mento.AcademicInfo;
import pulse.back.entity.mento.CareerInfo;
import pulse.back.entity.mento.CertificateInfo;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;

@QueryEntity
@Document
public record Member(
        // pk
        @Id
        ObjectId id,

        // 이메일
        String email,

        // 비밀번호
        String password,

        // 휴대폰번호
        String phoneNumber,

        // 이름
        String name,

        // 닉네임
        String nickName,

        // 유저권한 (Admin, User 등)
        MemberRole memberRole,

        // 프로필 이미지
        MultipartFile profileImage,

        // 생성일
        @Indexed
        LocalDateTime createdAt,

        // 수정일
        LocalDateTime updatedAt,

        // 삭제일
        LocalDateTime deletedAt,

        // 생성자
        ObjectId createdMemberId,

        // 수정자
        ObjectId updatedMemberId,

        // 삭제자
        ObjectId deletedMemberId
) {
}
