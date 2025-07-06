package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import pulse.back.common.enums.LectureType;
import pulse.back.common.util.MyDateUtils;
import pulse.back.entity.mento.CareerInfo;
import pulse.back.entity.member.Member;
import pulse.back.entity.mento.MentoInfo;
import pulse.back.entity.mentoring.Mentoring;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

@Slf4j
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

        //멘토링 모집마감 기한
        @Schema(description = "멘토링 모집마감 기한", example = "2025-07-06T13:07:28.090+00:00")
        OffsetDateTime deadlineDate,

        //멘토링 시작일 (yyyyMMdd)
        @Schema(description = "멘토링 시작일", example = "yyyyMMdd")
        OffsetDateTime startDate,

        //멘토링 마감일 (yyyyMMdd)
        @Schema(description = "멘토링 마감일", example = "yyyyMMdd")
        OffsetDateTime endDate,

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
        public static MentoringDetailResponseDto of(Mentoring mentoring, Member member, MentoInfo mentoInfo) {
                int mentorCareerTotalYear = 0;
                String mentorLastCompany = "";

                log.info("33333. MentoringDetailResponseDto.of() - mentoring: {}, member: {}, mentoInfo: {}", mentoring, member, mentoInfo);

                if (mentoInfo.careerInfo() != null && !mentoInfo.careerInfo().isEmpty()) {
                        // 가장 최근 입사일자를 가진 회사 찾기
                        CareerInfo latestCareer = mentoInfo.careerInfo().stream()
                                .max(Comparator.comparing(CareerInfo::joinDate))
                                .orElse(null);

                        if (latestCareer != null) {
                                mentorLastCompany = latestCareer.companyName();
                        }

                        // 총 근무 개월 수 계산
                        long totalMonths = 0;
                        for (CareerInfo careerInfo : mentoInfo.careerInfo()) {
                                if (careerInfo.joinDate() != null) {
                                        OffsetDateTime joinDate = MyDateUtils.fromString(careerInfo.joinDate());
                                        OffsetDateTime retireDate;

                                        if (careerInfo.isWorking()) {
                                                retireDate = OffsetDateTime.now();
                                        } else {
                                                retireDate = MyDateUtils.fromString(careerInfo.retireDate());
                                        }

                                        if (joinDate != null && retireDate != null) {
                                                // 각 회사별 근무 개월 수 계산
                                                totalMonths += ChronoUnit.MONTHS.between(joinDate, retireDate);
                                        }
                                }
                        }

                        // 연차 계산: 총 개월 수를 12로 나누고 1을 더함
                        mentorCareerTotalYear = (int)(totalMonths / 12) + 1;
                }

                int applyNumber = 0;
                if(mentoring.menteeInfoList() != null) {
                        applyNumber = mentoring.menteeInfoList().size();
                }

                log.info("mentoring.lectureType() : {}", mentoring.lectureType());
                return new MentoringDetailResponseDto(
                        member.nickName(),
                        member.profileImage(),
                        mentoInfo.jobInfo(),
                        mentorCareerTotalYear,
                        mentorLastCompany,
                        mentoring.title(),
                        mentoring.content(),
                        mentoring.deadlineDate(),
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
