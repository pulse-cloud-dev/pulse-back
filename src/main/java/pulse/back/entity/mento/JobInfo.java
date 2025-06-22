package pulse.back.entity.mento;

import org.springframework.data.mongodb.core.mapping.Document;

//직업정보
@Document
public record JobInfo(
    String jobName
) {
}
