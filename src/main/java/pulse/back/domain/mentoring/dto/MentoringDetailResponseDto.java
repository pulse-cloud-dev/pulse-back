package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;
import pulse.back.common.enums.LectureType;
import pulse.back.entity.member.CareerInfo;
import pulse.back.entity.member.Member;
import pulse.back.entity.mentoring.Mentoring;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MentoringDetailResponseDto(
        //////////////////////////  멘토 정보 ////////////////////////
        //멘토 닉네임
        @Schema(description = "멘토 닉네임", example = "멘토 닉네임")
        String mentorNickname,

        //멘토 프로필 사진
        @Schema(description = "멘토 프로필 사진", example = "멘토 프로필 사진")
        MultipartFile mentorProfileImage,

        //멘토 직업
        @Schema(description = "멘토 직업(직무,직업)", example = "멘토 직업")
        String mentorJob,

        //멘토 연차
        @Schema(description = "멘토 연차(총 경력)", example = "N년차")
        int mentorCareerTotalYear,

        //멘토 경력 (마지막 회사)
        @Schema(description = "멘토 경력(마지막 회사)", example = "멘토 경력")
        String mentorLastCompany,


        ////////////////////////  멘토링 정보 ////////////////////////
        //멘토링 제목
        @Schema(description = "멘토링 제목", example = "멘토링 제목")
        String title,

        //멘토링 내용
        @Schema(description = "멘토링 내용", example = "멘토링 내용")
        String content,

        //멘토링 모집마감 기한 (yyyyMMdd)
        @Schema(description = "멘토링 모집마감 기한", example = "yyyyMMdd")
        LocalDate deadlineDate,

        //멘토링 모집마감 시간 (HHmm)
        @Schema(description = "멘토링 모집마감 시간", example = "HHmm")
        LocalTime deadlineTime,

        //멘토링 시작일 (yyyyMMdd)
        @Schema(description = "멘토링 시작일", example = "yyyyMMdd")
        LocalDate startDate,

        //멘토링 마감일 (yyyyMMdd)
        @Schema(description = "멘토링 마감일", example = "yyyyMMdd")
        LocalDate endDate,

        //강의형식
        @Schema(description = "강의형식", example = "LectureType : ONLINE, OFFLINE")
        LectureType lectureType,

        //온라인 플랫폼
        @Schema(description = "온라인 플랫폼", example = "zoom, google_meet (미입력시 '미정'으로 등록됩니다.)")
        String onlinePlatform,

        //주소
        @Schema(description = "주소", example = "서울시 강남구")
        String address,

        //상세주소
        @Schema(description = "상세주소", example = "길바닥 어딘가")
        String detailAddress,

        //모집인원
        @Schema(description = "모집인원", example = "50 (최대 254)")
        int recruitNumber,

        //현재까지 신청한 인원
        @Schema(description = "현재까지 신청한 인원", example = "50")
        int applyNumber,

        //멘토링 비용
        @Schema(description = "멘토링 비용", example = "25000")
        BigDecimal cost
) {
        public static MentoringDetailResponseDto of(Mentoring mentoring, Member member) {
                int mentorCareerTotalYear = 0;
                String mentorLastCompany = "";

                if (member.careerInfo() != null) {
                        //carrerInfo의 joinDate가 가장 최신 날짜인 것에서 오래된 날짜를 빼서 년도만 구하기
                        for (CareerInfo careerInfo : member.careerInfo()) {
                                if(careerInfo.joinDate() != null){
                                        int joinYear = Integer.parseInt(careerInfo.joinDate().substring(0, 4));
                                        int retireYear = careerInfo.retireDate() != null ? Integer.parseInt(careerInfo.retireDate().substring(0, 4)) : 2022;
                                        mentorCareerTotalYear += retireYear - joinYear;
                                }
                        }

                }

                int applyNumber = 0;
                if(mentoring.menteeInfoList() != null) {
                        applyNumber = mentoring.menteeInfoList().size();
                }


                return new MentoringDetailResponseDto(
                        member.nickName(),
                        member.profileImage(),
                        member.jobInfo(),
                        mentorCareerTotalYear,
                        mentorLastCompany,
                        mentoring.title(),
                        mentoring.content(),
                        mentoring.deadlineDate(),
                        mentoring.deadlineTime(),
                        mentoring.startDate(),
                        mentoring.endDate(),
                        mentoring.lectureType(),
                        mentoring.onlinePlatform(),
                        mentoring.address(),
                        mentoring.detailAddress(),
                        mentoring.recruitNumber(),
                        applyNumber,
                        mentoring.cost()
                );
        }
}
