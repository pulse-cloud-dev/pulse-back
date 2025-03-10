package pulse.back.domain.chat.dto;

import reactor.core.Disposable;

public record RoomSubscription(
        String roomId,
        Disposable disposable
) {
    public static RoomSubscription of(String roomId, Disposable disposable) {
        return new RoomSubscription(roomId, disposable);
    }
}
