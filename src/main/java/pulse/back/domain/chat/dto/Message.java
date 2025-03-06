package pulse.back.domain.chat.dto;

import pulse.back.common.config.auth.TokenResponseDto;
import pulse.back.common.enums.ErrorCodes;

public record Message(
        Type type,
        String roomId,
        Object payload
) {
    public static Message createPong(String roomId, long payload) {
        return new Message(Type.PONG, roomId, payload);
    }

    public static Message createReissue(String roomId, TokenResponseDto payload) {
        return new Message(Type.REISSUE, roomId, payload);
    }

    public static Message createError(ErrorCodes e) {
        return new Message(Type.ERROR, null, ErrorMessage.fromErrorCodes(e));
    }
}
