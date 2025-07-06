package pulse.back.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import pulse.back.common.config.GlobalPatterns;
import pulse.back.common.enums.PassStatus;
import pulse.back.entity.mento.CertificateInfo;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CertificateInfoRequestDto(
        //자격증명
        @Schema(description = "자격증명", example = "정보처리기사")
        String certificateName,

        //발급기관
        @Schema(description = "발급기관", example = "한국산업인력공단")
        String issuer,

        //합격구분
        @Schema(description = "합격구분", example = "PassStatus 으로 입력 : WRITTEN_PASS, FINAL_PASS")
        PassStatus passStatus,

        //합격년월
        @Pattern(regexp = GlobalPatterns.YYYYMM)
        @Schema(description = "합격년월")
        String passDate
) {
    public static CertificateInfo of(CertificateInfoRequestDto requestDto) {
        return new CertificateInfo(
                requestDto.certificateName(),
                requestDto.issuer(),
                requestDto.passStatus(),
                requestDto.passDate()
        );
    }

    public static List<CertificateInfo> of(List<CertificateInfoRequestDto> requestDtos) {
        return requestDtos.stream()
                .map(CertificateInfoRequestDto::of)
                .toList();
    }
}
