package pulse.back.entity.member;

import org.springframework.data.mongodb.core.mapping.Document;
import pulse.back.common.enums.EducationLevel;
import pulse.back.common.enums.EducationStatus;

import java.time.LocalDateTime;

//학력정보
@Document
public record AcademicInfo(
        //학력구분
        EducationLevel educationLevel,

        //학교명
        String schoolName,

        //전공
        String major,

        //졸업여부
        EducationStatus educationStatus,

        //입학년월
        LocalDateTime admissionDate,

        //졸업년월
        LocalDateTime graduationDate
) {
}
