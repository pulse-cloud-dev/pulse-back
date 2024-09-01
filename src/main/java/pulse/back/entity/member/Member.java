package pulse.back.entity.member;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pulse.back.common.enums.MemberRole;

import java.time.LocalDateTime;

@Document
public record Member(
    //pk
    @Id
    ObjectId id,

    //이메일
    String email,

    //비밀번호
    String password,

    //휴대폰번호
    String phoneNumber,

    //생년월일
    String birth,

    //이름
    String name,

    //닉네임
    String nickName,
    //생성일
    @Indexed
    LocalDateTime createdAt,

    //수정일
    LocalDateTime updatedAt,

    //삭제일
    LocalDateTime deletedAt,

    //생성자
    ObjectId createdMemberId,

    //수정자
    ObjectId updatedMemberId,

    //삭제자
    ObjectId deletedMemberId,

    //유저권한
    MemberRole memberRole
) {
}
