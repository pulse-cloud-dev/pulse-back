package pulse.back.domain.mentoring.dto;

public record GetMentoInfoCodeListResponseDto(
        String name,
        String description
) {
    public static GetMentoInfoCodeListResponseDto from(String name, String description) {
        return new GetMentoInfoCodeListResponseDto(name, description);
    }
}
