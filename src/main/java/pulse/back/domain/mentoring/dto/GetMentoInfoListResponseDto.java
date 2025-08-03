package pulse.back.domain.mentoring.dto;

public record GetMentoInfoListResponseDto(
        String memberId,
        String profileImage,
        String nickname,
        String field,
        String career,
        int mentoringCount
) {
}
