package pulse.back.entity.mentoring;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Mentoring(
        String id,
        String title,
        String description,
        String mentorId,
        String menteeId,
        String status
) {
}
