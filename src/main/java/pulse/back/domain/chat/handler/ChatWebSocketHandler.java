package pulse.back.domain.chat.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                session.receive()
//                        .timeout(Duration.ofSeconds(30))      // TODO: 프론트에서 PING이 구현되면 주석 해제
                        .map(this::messageToMessageDto)
                        .flatMap(this::handleChatMessage)
                        .map(session::textMessage)
        );
    }

    private Mono<String> handleChatMessage(final Message message) {
        log.info("MESSAGE : {}", message);
        if (message.messageType() == null) return createErrorMessage(ErrorCodes.INVALID_MESSAGE_TYPE);

        return switch (message.messageType()) {
            case PING -> {
                long remainingExpiredTime = tokenProvider.getRemainingExpiredTime(message.payload().toString());
                if (remainingExpiredTime > 0) {
                    yield Mono.just(objectToString(Message.createPong(remainingExpiredTime)));
                } else {
                    yield createErrorMessage(ErrorCodes.INVALID_TOKEN);
                }
            }

            case REISSUE -> {
                if (tokenProvider.validateToken(message.payload().toString())) {
                    // TODO: member id, role을 토큰 Claim에 넣어두지 않고 DB에서 조회하는지 물어보기
                    yield Mono.just(objectToString(Message.createReissue(tokenProvider.reissueAccessToken(null, null))));
                } else {
                    yield createErrorMessage(ErrorCodes.INVALID_TOKEN);
                }
            }

            case TEXT -> handleTextMessage(message);

            case BINARY -> handleBinaryMessage(message);

            case ERROR -> Mono.just(objectToString(message.payload()));

            default -> createErrorMessage(ErrorCodes.WRONG_MESSAGE_TYPE);
        };
    }

    private Mono<String> handleTextMessage(Message message) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(message.roomId())) return createErrorMessage(ErrorCodes.INVALID_ROOM_ID);

            // TODO: 멘토링 사이트에서 채팅방 활성화를 기본값으로 할 것인지?
            // 만약 채팅방을 기본적으로 사용하지 않는다면 채팅 기능을 사용할 때 웹소켓을 연결할텐데
            // 그러면 채팅방을 생성하면 자동으로 채팅방이 활성화되게 할 것인지, 
            // 채팅방이 생성되어도 채팅방을 활성화하기 위해 별도의 요청을 보내야 하는지 확인 (요청을 분리한다면 방 생성은 http로 할 생각)

//            rabbitTemplate.convertAndSend("roomId", message);

            return Mono.just(objectToString(Message.createAck(message.roomId())));
        });
    }

    private Mono<String> handleBinaryMessage(Message message) {
        return Mono.just("BINARY");
    }

    private Mono<String> createErrorMessage(ErrorCodes errorCodes) {
        log.error("ERROR : {}", errorCodes);
        return Mono.just(objectToString(ErrorMessage.fromErrorCodes(errorCodes)));
    }

    private Message messageToMessageDto(WebSocketMessage message) {
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
