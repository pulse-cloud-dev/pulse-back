package pulse.back.entity.chat;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Rooms(
        ObjectId memberId,
        String roomId
) {
}
