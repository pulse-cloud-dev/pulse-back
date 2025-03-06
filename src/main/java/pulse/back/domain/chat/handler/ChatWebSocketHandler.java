package pulse.back.domain.chat.handler;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@NonNullApi
@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    private final List<WebSocketMessage.Type> types
            = List.of(WebSocketMessage.Type.TEXT, WebSocketMessage.Type.BINARY, WebSocketMessage.Type.PING);

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                session.receive()
                        .timeout(Duration.ofSeconds(30))
                        .filter(this::validateMessageType)
                        .map(webSocketMessage -> {
                            if (webSocketMessage.getType() == WebSocketMessage.Type.TEXT) {
                                return session.textMessage(handleTextMessage(webSocketMessage.getPayloadAsText()));
                            }
                            if (webSocketMessage.getType() == WebSocketMessage.Type.BINARY) {
                                return session.binaryMessage(this::handleBinaryMessage);
                            }
                            return session.pongMessage(this::createPongMessage);
                        })
        );
    }

    private Boolean validateMessageType(WebSocketMessage webSocketMessage) {
        return types.contains(webSocketMessage.getType());
    }

    private String handleTextMessage(String message) {
        return message;
    }

    private DataBuffer handleBinaryMessage(DataBufferFactory dataBufferFactory) {
        return dataBufferFactory.wrap("PONG".getBytes());
    }

    private DataBuffer createPongMessage(DataBufferFactory dataBufferFactory) {
        return dataBufferFactory.wrap("PONG".getBytes());
    }
}
