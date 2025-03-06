package pulse.back.domain.chat.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.domain.chat.dto.ErrorMessage;
import pulse.back.domain.chat.dto.Message;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {
    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                session.receive()
//                        .timeout(Duration.ofSeconds(30))      // TODO: 프론트에서 PING이 구현되면 주석 해제
                        .map(this::textToMessageDto)
                        .flatMap(this::handleTextMessage)
                        .map(session::textMessage)
        );
    }

    private Mono<String> handleTextMessage(final Message message) {
        log.info("MESSAGE : {}", message);
        if (message.type() == null) return createErrorMessage(ErrorCodes.INVALID_MESSAGE_TYPE);

        return switch (message.type()) {
            case PING -> {
                long remainingExpiredTime = tokenProvider.getRemainingExpiredTime(message.payload().toString());
                if (remainingExpiredTime > 0) {
                    yield Mono.just(objectToString(Message.createPong(message.roomId(), remainingExpiredTime)));
                } else {
                    yield createErrorMessage(ErrorCodes.INVALID_TOKEN);
                }
            }
            case REISSUE -> {
                if (tokenProvider.validateToken(message.payload().toString())) {
                    // TODO: member id, role을 토큰 Claim에 넣어두지 않고 DB에서 조회하는지 물어보기
                    yield Mono.just(objectToString(Message.createReissue(message.roomId(), tokenProvider.reissueAccessToken(null, null))));
                } else {
                    yield createErrorMessage(ErrorCodes.INVALID_TOKEN);
                }
            }
            case TEXT -> Mono.just(message.payload().toString());
            case BINARY -> Mono.just("BINARY");
            case ERROR -> Mono.just(objectToString(message.payload()));
            default -> createErrorMessage(ErrorCodes.BAD_REQUEST);
        };
    }

    private Mono<String> createErrorMessage(ErrorCodes errorCodes) {
        return Mono.just(objectToString(ErrorMessage.fromErrorCodes(errorCodes)));
    }


    private Message textToMessageDto(WebSocketMessage message) {
        try {
            return objectMapper.readValue(message.getPayloadAsText(), Message.class);
        } catch (Exception e) {
            return Message.createError(ErrorCodes.INVALID_JSON);
        }
    }

    private String objectToString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCodes.INVALID_JSON, object.toString());
        }
    }
}
