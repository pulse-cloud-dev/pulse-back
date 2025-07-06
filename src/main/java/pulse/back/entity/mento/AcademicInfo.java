package pulse.back.entity.mento;

import org.springframework.data.mongodb.core.mapping.Document;
import pulse.back.common.enums.EducationLevel;
import pulse.back.common.enums.EducationStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
        String admissionDate,

        //졸업년월
        String graduationDate
) {
}
