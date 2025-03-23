package pulse.back.entity.mentoring;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pulse.back.common.enums.LectureType;
import pulse.back.common.util.MyDateUtils;
import pulse.back.domain.mentoring.dto.MentoringPostRequestDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        LocalDate deadlineDate,

        // 멘토링 모집마감 시간
        LocalTime deadlineTime,

        // 멘토링 시작일
        LocalDate startDate,

        // 멘토링 마감일
        LocalDate endDate,

        // 강의형식
        LectureType lectureType,

        // 온라인 플랫폼
        String onlinePlatform,

        // 주소
        String address,

        // 상세주소
        String detailAddress,

        //좌표
        MentoringLocation location,

        // 모집인원
        int recruitNumber,

        // 멘토링 비용
        BigDecimal cost,

        //멘토링에 참여한 멘티 pk
        List<MenteeInfo> menteeInfoList,

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
    public static Mentoring from (MentoringPostRequestDto requestDto, MentoringLocation location, ObjectId mentorId){
        return new Mentoring(
                new ObjectId(),
                requestDto.title(),
                requestDto.content(),
                MyDateUtils.fromString(requestDto.deadlineDate()),
                MyDateUtils.timeFromString(requestDto.deadlineTime()),
                MyDateUtils.fromString(requestDto.startDate()),
                MyDateUtils.fromString(requestDto.endDate()),
                requestDto.lectureType(),
                requestDto.onlinePlatform(),
                requestDto.address(),
                requestDto.detailAddress(),
                location,
                requestDto.recruitNumber(),
                requestDto.cost(),
                null,
                LocalDateTime.now(),
                null,
                null,
                mentorId,
                null,
                null
        );
    }
}
