package pulse.back.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import pulse.back.common.config.GlobalPatterns;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PasswordResetRequestDto(
        @NotNull
        @Pattern(regexp = GlobalPatterns.EMAIL, message = "Invalid email format.")
        @Schema(description = "회원 이메일(아이디)", example = "test@test.com")
        String memberId,

        @NotNull
        @Schema(description = "새로운 비밀번호", example = "1234")
        String newPassword
) {
}
