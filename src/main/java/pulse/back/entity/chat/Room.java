package pulse.back.entity.chat;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Rooms(

        @Id
        String roomId,
        ObjectId memberId
) {
}
