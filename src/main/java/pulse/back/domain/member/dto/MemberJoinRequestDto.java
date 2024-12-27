package pulse.back.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.bson.types.ObjectId;
import pulse.back.common.config.GlobalPatterns;
import pulse.back.common.enums.MemberRole;
import pulse.back.entity.member.*;

import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MemberJoinRequestDto(
        @NotNull
        @Pattern(regexp = GlobalPatterns.EMAIL, message = "이메일 형식에 맞지 않습니다.")
        @Schema(description = "회원 이메일", example = "test@test.com")
        String email,

        @NotNull
        @Schema(description = "회원 비밀번호", example = "1111")
        String password,

        @NotNull
        @Pattern(regexp = GlobalPatterns.PHONE_NUMBER, message = "전화번호 형식에 맞지 않습니다.")
        @Schema(description = "회원 휴대폰번호", example = "01012345678")
        String phoneNumber,

        @NotNull
        @Pattern(regexp = GlobalPatterns.YYYYMMDD, message = "생년월일 형식에 맞지 않습니다.")
        @Schema(description = "회원 생년월일", example = "19900101")
        String birth,

        @NotNull
        @Schema(description = "회원 이름", example = "홍길동")
        String name,

        @NotNull
        @Schema(description = "회원 닉네임", example = "홍당무")
        String nickName,

        @Schema(description = "회원 학력사항", type = "array", example = "대학교 졸업")
        List<AcademicInfoRequestDto> academicInfoList,

        @Schema(description = "회원 자격증사항", type = "array", example = "정보처리기사")
        List<CertificateInfoRequestDto> certificateInfoList,

        @Schema(description = "회원 직업정보", type = "array", example = "개발자")
        List<JobInfoRequestDto> jobInfoList,

        @Schema(description = "회원 경력사항", type = "array", example = "네이버백엔드2년")
        List<CareerInfoRequestDto> careerInfoList
) {
    public static Member of(
            MemberJoinRequestDto requestDto, String password
    ) {
        return new Member(
                new ObjectId(),
                requestDto.email(),
                password,
                requestDto.phoneNumber(),
                requestDto.birth(),
                requestDto.name(),
                requestDto.nickName(),
                MemberRole.USER,
                null,
                null,
                LocalDateTime.now(),
                null,
                null,
                null,
                null,
                null,
                requestDto.academicInfoList() != null ? AcademicInfoRequestDto.of(requestDto.academicInfoList()) : null,
                requestDto.certificateInfoList() != null ? CertificateInfoRequestDto.of(requestDto.certificateInfoList()) : null,
                requestDto.jobInfoList() != null ? JobInfoRequestDto.of(requestDto.jobInfoList()) : null,
                requestDto.careerInfoList() != null ? CareerInfoRequestDto.of(requestDto.careerInfoList()) : null
        );
    }
}
