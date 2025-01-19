package pulse.back.entity.mentoring;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pulse.back.common.enums.LectureType;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document
public record Mentoring(
        // pk
        @Id
        ObjectId id,

        // 멘토링 제목
        String title,

        // 멘토링 내용
        String content,

        // 멘토링 모집마감 기한
        LocalDateTime deadlineDate,

        // 멘토링 모집마감 시간
        LocalDateTime deadlineTime,

        // 멘토링 시작일
        LocalDateTime startDate,

        // 멘토링 마감일
        LocalDateTime endDate,

        // 강의형식
        LectureType lectureType,

        // 주소
        String address,

        // 상세주소
        String detailAddress,

        // 모집인원
        int recruitNumber,

        // 멘토링 비용
        BigDecimal cost,

        //멘토링에 참여한 멘티 pk
        List<ObjectId> menteeList,

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
    public static Mentoring from (MentoringPostRequestDto requestDto){
        return null;
    }
}
