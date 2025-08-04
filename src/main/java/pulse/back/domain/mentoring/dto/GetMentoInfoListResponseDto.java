package pulse.back.domain.mentoring.dto;

import pulse.back.common.util.MyDateUtils;
import pulse.back.entity.member.Member;
import pulse.back.entity.mento.CareerInfo;
import pulse.back.entity.mento.MentoInfo;
import pulse.back.entity.s3.MemberDocument;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public record GetMentoInfoListResponseDto(
        String mentoId,
        String profileImagePath,
        String nickname,
        String field,
        int mentorCareerTotalYear,
        int mentoringCount
) {
    public static GetMentoInfoListResponseDto of(
            Member member,
            MentoInfo mentoInfo,
            MemberDocument memberDocument,
            int mentoringCount
    ) {
        //TODO : 리팩토링 예정
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
        return new GetMentoInfoListResponseDto(
                mentoInfo.id().toString(),
                memberDocument != null ? memberDocument.filePath() : null,
                member.nickName(),
                mentoInfo.jobInfo(),
                mentorCareerTotalYear,
                mentoringCount
        );
    }
}
