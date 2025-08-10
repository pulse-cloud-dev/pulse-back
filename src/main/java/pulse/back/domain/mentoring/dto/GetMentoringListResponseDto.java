package pulse.back.domain.mentoring.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.web.multipart.MultipartFile;
import pulse.back.common.enums.LectureType;
import pulse.back.common.util.MyDateUtils;
import pulse.back.entity.mento.CareerInfo;
import pulse.back.entity.member.Member;
import pulse.back.entity.mento.MentoInfo;
import pulse.back.entity.mentoring.Mentoring;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetMentoringListResponseDto(
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

        // 멘토링 지역 주소 (오프라인일 경우)
        String region,

        // 멘토 닉네임
        String mentorNickname,

        // 멘토링 모집마감 기한(mm.dd)
        LocalDateTime deadlineTime,

        // 조회수
        int viewCount,

        // 북마크
        boolean isBookmark
) {
    public static GetMentoringListResponseDto of(Mentoring mentoring, Member member, MentoInfo mentoInfo, boolean isBookmark) {
        int mentorCareerTotalYear = 0;

        if (mentoInfo.careerInfo() != null && !mentoInfo.careerInfo().isEmpty()) {
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
                        totalMonths += ChronoUnit.MONTHS.between(joinDate, retireDate);
                    }
                }
            }
            mentorCareerTotalYear = (int)(totalMonths / 12) + 1;
        }

        // 지역 정보 추출
        String region = extractRegionFromAddress(mentoring.address(), mentoring.lectureType());

        return new GetMentoringListResponseDto(
                mentoring.id().toString(),
                mentoring.lectureType(),
                mentoring.title(),
                member.profileImage(),
                mentoInfo.jobInfo(),
                mentorCareerTotalYear,
                region,
                member.nickName(),
                mentoring.deadlineDate(),
                mentoring.viewCount(),
                isBookmark
        );
    }

    /**
     * 주소에서 지역명 추출 (시 단위)
     */
    private static String extractRegionFromAddress(String address, LectureType lectureType) {
        // 온라인 강의의 경우 지역 정보 없음
        if (lectureType == LectureType.ONLINE || address == null || address.trim().isEmpty()) {
            return null;
        }

        String addr = address.trim();

        // contains로 지역명 확인
        if (addr.contains("서울")) return "서울";
        if (addr.contains("부산")) return "부산";
        if (addr.contains("대구")) return "대구";
        if (addr.contains("인천")) return "인천";
        if (addr.contains("광주")) return "광주";
        if (addr.contains("대전")) return "대전";
        if (addr.contains("울산")) return "울산";
        if (addr.contains("세종")) return "세종";
        if (addr.contains("경기")) return "경기";
        if (addr.contains("강원")) return "강원";
        if (addr.contains("충북")) return "충북";
        if (addr.contains("충남")) return "충남";
        if (addr.contains("전북")) return "전북";
        if (addr.contains("전남")) return "전남";
        if (addr.contains("경북")) return "경북";
        if (addr.contains("경남")) return "경남";
        if (addr.contains("제주")) return "제주";

        return "기타";
    }
}
