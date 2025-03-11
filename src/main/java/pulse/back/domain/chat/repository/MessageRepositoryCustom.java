package pulse.back.domain.chat.repository;

import pulse.back.entity.chat.Message;
import reactor.core.publisher.Flux;

public interface MessageRepositoryCustom {
    Flux<Message> findRecentMessagesByRoomId(String roomId);
}
