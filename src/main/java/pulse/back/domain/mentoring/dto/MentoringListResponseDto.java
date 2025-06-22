package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.web.multipart.MultipartFile;
import pulse.back.common.enums.LectureType;
import pulse.back.common.util.MyDateUtils;
import pulse.back.entity.mento.CareerInfo;
import pulse.back.entity.member.Member;
import pulse.back.entity.mentoring.Mentoring;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MentoringListResponseDto(
        // 멘토링 ID
        String mentoringId,

        // 강의형식 (ONLINE, OFFLINE)
        LectureType lectureType,

        // 멘토링 제목
        String title,

        // 멘토 프로필 사진
        MultipartFile mentorProfileImage,

        // 멘토 직업
        String mentorJob,

        // 멘토 연차
        int mentorCareerTotalYear,

        // onlinePlatform (온라인일 경우)
        String onlinePlatform,

        // 멘토링 주소 (오프라인일 경우)

        // 멘토 닉네임
        String mentorNickname,

        // 멘토링 모집마감 기한(mm.dd)
        LocalDate deadlineTime
) {
    public static MentoringListResponseDto of(Mentoring mentoring, Member member) {
        int mentorCareerTotalYear = 0;

        if (member.careerInfo() != null && !member.careerInfo().isEmpty()) {
            // 가장 최근 입사일자를 가진 회사 찾기
            CareerInfo latestCareer = member.careerInfo().stream()
                    .max(Comparator.comparing(CareerInfo::joinDate))
                    .orElse(null);

            // 총 근무 개월 수 계산
            long totalMonths = 0;
            for (CareerInfo careerInfo : member.careerInfo()) {
                if (careerInfo.joinDate() != null) {
                    LocalDate joinDate = MyDateUtils.fromString(careerInfo.joinDate());
                    LocalDate retireDate;

                    if (careerInfo.isWorking()) {
                        retireDate = LocalDate.now();
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
        return new MentoringListResponseDto(
                mentoring.id().toString(),
                mentoring.lectureType(),
                mentoring.title(),
                member.profileImage(),
                member.jobInfo(),
                mentorCareerTotalYear,
                mentoring.onlinePlatform(),
                member.nickName(),
                mentoring.deadlineDate()
        );
    }
}
