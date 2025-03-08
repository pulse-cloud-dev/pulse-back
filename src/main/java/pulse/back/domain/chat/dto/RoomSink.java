package pulse.back.domain.chat.dto;

import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

public record RoomSink(
        Sinks.Many<Message> roomSink,
        Disposable disposable
) {
}
