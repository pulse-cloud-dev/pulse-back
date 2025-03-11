package pulse.back.entity.chat;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Room(
        @Id
        String roomId,
        String title,
        int memberCount,
        ObjectId mentor
) {
    public static Room of(String roomId, String title, int memberCount, ObjectId mentor) {
        return new Room(roomId, title, memberCount, mentor);
    }
}
