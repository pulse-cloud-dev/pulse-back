package pulse.back.domain.chat.dto.response;

import pulse.back.common.enums.ErrorCodes;
import pulse.back.domain.chat.dto.Message;
import pulse.back.domain.chat.dto.MessageType;
import pulse.back.domain.chat.dto.entity.MessageDto;

public record MessageResponse(
        MessageType messageType,
        MessageDto messageDto,
        Object payload
) implements Message {
    public static MessageResponse of(MessageType messageType, MessageDto messageDto, Object payload) {
        return new MessageResponse(messageType, messageDto, payload);
    }

    public static MessageResponse createPong(long payload) {
        return MessageResponse.of(MessageType.PONG, null, payload);
    }

//    public static MessageResponse createReissue(TokenResponseDto payload) {
//        return MessageResponse.of(MessageType.REISSUE, null, payload);
//    }

    public static MessageResponse createError(ErrorCodes e) {
        return MessageResponse.of(MessageType.ERROR, null, ErrorMessage.fromErrorCodes(e));
    }

    public static MessageResponse createRoom(String roomId) {
        return MessageResponse.of(MessageType.CREATE, null, roomId);
    }

    public static MessageResponse createAck(String roomId) {
        return new MessageResponse(MessageType.ACK, null, roomId);
    }

    public static MessageResponse from(MessageDto messageDto) {
        return MessageResponse.of(messageDto.messageType(), messageDto, null);
    }
}
