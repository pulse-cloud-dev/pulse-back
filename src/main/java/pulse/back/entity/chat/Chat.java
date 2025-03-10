package pulse.back.entity.chat;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Chat(
        String roomId,
        ObjectId memberId
) {
    public static Chat of(String roomId, ObjectId memberId) {
        return new Chat(roomId, memberId);
    }
}
