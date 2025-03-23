package pulse.back.entity.mentoring;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record MentoringLocation(
        String x,
        String y,
        Double distance
) {
}
