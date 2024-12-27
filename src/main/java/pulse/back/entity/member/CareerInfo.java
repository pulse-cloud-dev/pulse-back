package pulse.back.entity.member;

import org.springframework.data.mongodb.core.mapping.Document;
import pulse.back.common.enums.RoleLevel;

//경력정보
@Document
public record CareerInfo(
    //회사명
    String companyName,

    //부서
    String department,

    //직급
    RoleLevel position,

    //입사년월
    String joinDate,

    //퇴사년월
    String retireDate,

    //근무여부
    boolean isWorking
) {
}
