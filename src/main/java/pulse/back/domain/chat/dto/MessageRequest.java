package pulse.back.domain.chat.dto;

import pulse.back.common.config.auth.TokenResponseDto;
import pulse.back.common.enums.ErrorCodes;

public record Message(
        MessageType messageType,
        String roomId,
        Object payload
) {
    public static Message createPong(long payload) {
        return new Message(MessageType.PONG, null, payload);
    }

    public static Message createReissue(TokenResponseDto payload) {
        return new Message(MessageType.REISSUE, null, payload);
    }

    public static Message createError(ErrorCodes e) {
        return new Message(MessageType.ERROR, null, ErrorMessage.fromErrorCodes(e));
    }

    public static Message createAck(String roomId) {
        return new Message(MessageType.ACK, roomId, "SUCCESS");
    }
}
