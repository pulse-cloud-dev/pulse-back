package pulse.back.entity.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Room(
        @Id
        String roomId,
        String title
) {
    public static Room of(String roomId, String title) {
        return new Room(roomId, title);
    }
}
