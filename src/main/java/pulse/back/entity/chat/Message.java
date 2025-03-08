package pulse.back.entity.chat;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pulse.back.domain.chat.dto.MessageType;

import java.time.LocalDateTime;

@Document
public record Messages(
        // pk
        @Id
        ObjectId id,

        ObjectId memberId,

        String roomId,

        MessageType messageType,

        String content,

        @Indexed
        LocalDateTime sentAt,

        LocalDateTime deliveredAt,

        LocalDateTime seenAt
) {
}
