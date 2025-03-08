package pulse.back.domain.chat.dto;

import pulse.back.common.config.auth.TokenResponseDto;
import pulse.back.common.enums.ErrorCodes;

public record MessageResponse(
        MessageType messageType,
        MessageDto messageDto,
        Object payload
) implements Message{
    public static MessageResponse createPong(long payload) {
        return new MessageResponse(MessageType.PONG, null, payload);
    }

    public static MessageResponse createReissue(TokenResponseDto payload) {
        return new MessageResponse(MessageType.REISSUE, null, payload);
    }

    public static MessageResponse createError(ErrorCodes e) {
        return new MessageResponse(MessageType.ERROR, null, ErrorMessage.fromErrorCodes(e));
    }

    public static MessageResponse createAck(String roomId) {
        return new MessageResponse(MessageType.ACK, null, roomId);
    }
}
