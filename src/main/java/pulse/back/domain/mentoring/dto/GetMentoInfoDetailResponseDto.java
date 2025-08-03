package pulse.back.domain.mentoring.dto;

import pulse.back.common.util.MyDateUtils;
import pulse.back.entity.member.Member;
import pulse.back.entity.mento.AcademicInfo;
import pulse.back.entity.mento.CareerInfo;
import pulse.back.entity.mento.CertificateInfo;
import pulse.back.entity.mento.MentoInfo;
import pulse.back.entity.mentoring.Mentoring;
import pulse.back.entity.s3.MemberDocument;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record GetMentoInfoDetailResponseDto(
        // 멘토링 횟수
        int mentoringCount,

        // 멘토 소개글
        String introduction,

        // 자격증 정보
        List<CertificateInfo> certificateInfoList,

        //학력정보
        List<AcademicInfo> academicInfoList,

        //선호지역
        List<String> preferredLocations,

        // 멘토링 리스트
        List<GetMentoringListResponseDto> mentoringList
) {
    public static GetMentoInfoDetailResponseDto of(
            MentoInfo mentoInfo,
            List<GetMentoringListResponseDto> mentoringList
    ) {
        int mentorCareerTotalYear = 0;

        if (mentoInfo.careerInfo() != null && !mentoInfo.careerInfo().isEmpty()) {
            // 가장 최근 입사일자를 가진 회사 찾기
            CareerInfo latestCareer = mentoInfo.careerInfo().stream()
                    .max(Comparator.comparing(CareerInfo::joinDate))
                    .orElse(null);

            // 총 근무 개월 수 계산
            long totalMonths = 0;
            for (CareerInfo careerInfo : mentoInfo.careerInfo()) {
                if (careerInfo.joinDate() != null) {
                    LocalDateTime joinDate = MyDateUtils.fromString(careerInfo.joinDate());
                    LocalDateTime retireDate;

                    if (careerInfo.isWorking()) {
                        retireDate = LocalDateTime.now();
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

        return new GetMentoInfoDetailResponseDto(
                mentoringList != null ? mentoringList.size() : 0,
                mentoInfo.mentorIntroduction() != null ? mentoInfo.mentorIntroduction() : null,
                mentoInfo.certificateInfo() != null && !mentoInfo.certificateInfo().isEmpty() ? mentoInfo.certificateInfo() : null,
                mentoInfo.academicInfo() != null && !mentoInfo.academicInfo().isEmpty() ? mentoInfo.academicInfo() : null,
                null,
                mentoringList != null ? mentoringList : List.of()
        );
    }
}
