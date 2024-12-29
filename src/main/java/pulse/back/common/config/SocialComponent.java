package pulse.back.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialComponent {

    //이메일 주소 마스킹
    public String maskEmail(String email) {
        //이메일을 로컬 파트와 도메인 파트로 분리
        String[] emailParts = email.split("@");
        String localPart = emailParts[0];
        String domainPart = emailParts[1];

        //로컬 파트 마스킹: 첫 3글자만 남기고 나머지를 *로 변경
        String maskedLocalPart = localPart.substring(0, Math.min(3, localPart.length()))
                + "*".repeat(Math.max(0, localPart.length() - 3));

        //도메인 파트를 도메인 이름과 확장자로 분리
        String[] domainParts = domainPart.split("\\.");
        String domainName = domainParts[0];
        String domainExtension = domainParts[1];

        //도메인 이름 마스킹: 첫 2글자만 남기고 나머지를 *로 변경
        String maskedDomainName = domainName.substring(0, Math.min(2, domainName.length()))
                + "*".repeat(Math.max(0, domainName.length() - 2));

        //마스킹된 이메일 주소 반환
        return maskedLocalPart + "@" + maskedDomainName + "." + domainExtension;
    }
}
