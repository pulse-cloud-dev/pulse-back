package pulse.back.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import pulse.back.common.config.GlobalPatterns;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MemberLoginRequestDto(

        @NotEmpty
        @Pattern(regexp = GlobalPatterns.EMAIL)
        @Schema(description = "아이디(이메일)", example = "test@test.com")
        String email,

        @NotEmpty
        @Schema(description = "비밀번호", example = "1111")
        String password
) {
}