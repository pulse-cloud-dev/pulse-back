package pulse.back.domain.social.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NaverLoginResponseDto(
        @Schema(description = "이메일")
        String email,

        @Schema(description = "이름")
        String name,

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "프로필 이미지")
        String profileImage,

        @Schema(description = "성별")
        String gender,

        @Schema(description = "생일")
        String birthday,

        @Schema(description = "나이")
        String age,

        @Schema(description = "출생년도")
        String birthyear,

        @Schema(description = "휴대폰 번호")
        String mobile
) {
}
