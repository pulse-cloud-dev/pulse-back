package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import pulse.back.common.enums.LectureType;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MentoringDetailResponseDto(
        //////////////////////////  멘토 정보 ////////////////////////
        //멘토 닉네임
        String mentorNickname,


        ////////////////////////  멘토링 정보 ////////////////////////
        //멘토링 제목
        String title,

        //멘토링 내용
        String content,

        //멘토링 모집마감 기한 (yyyyMMdd)
        String deadlineDate,

        //멘토링 모집마감 시간 (HHmm)
        String deadlineTime,

        //멘토링 시작일 (yyyyMMdd)
        String startDate,

        //멘토링 마감일 (yyyyMMdd)
        String endDate,

        //강의형식
        LectureType lectureType,

        //온라인 플랫폼
        String onlinePlatform,

        //주소
        String address,

        //상세주소
        String detailAddress,

        //모집인원
        int recruitNumber,

        //신청한 인원
        int applyNumber,

        //멘토링 비용
        BigDecimal cost
) {

}
