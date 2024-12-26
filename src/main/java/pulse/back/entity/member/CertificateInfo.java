package pulse.back.entity.member;

import org.springframework.data.mongodb.core.mapping.Document;
import pulse.back.common.enums.PassStatus;

import java.time.LocalDateTime;

//자격증정보
@Document
public record CertificateInfo(
    //자격증명
    String certificateName,

    //발급기관
    String issuer,

    //합격구분
    PassStatus passStatus,

    //합격년월
    LocalDateTime passDate
) {
}
