package pulse.back.domain.chat.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {
    private final TokenProvider tokenProvider;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                session.receive()
//                        .timeout(Duration.ofSeconds(30))
                        .handle((webSocketMessage, sink) -> {
                            if (webSocketMessage.getType() == WebSocketMessage.Type.PING) {
                                sink.next(session.pongMessage(dataBufferFactory ->
                                        handlePingMessage(dataBufferFactory, webSocketMessage.getPayloadAsText())));
                                return;
                            }
                            if (webSocketMessage.getType() == WebSocketMessage.Type.TEXT) {
                                sink.next(session.textMessage(handleTextMessage(webSocketMessage.getPayloadAsText())));
                                return;
                            }
                            if (webSocketMessage.getType() == WebSocketMessage.Type.BINARY) {
                                sink.next(session.binaryMessage(dataBufferFactory ->
                                        handleBinaryMessage(dataBufferFactory, webSocketMessage.getPayload())));
                                return;
                            }
                            sink.error(new CustomException(ErrorCodes.BAD_REQUEST));
                        })
        );
    }

    private String handleTextMessage(String message) {
        // TODO: Token 재발급 로직 추가
        log.info("MESSAGE : {}", message);
        return message;
    }

    private DataBuffer handleBinaryMessage(DataBufferFactory dataBufferFactory, DataBuffer dataBuffer) {
        return dataBufferFactory.wrap(dataBuffer.toByteBuffer());
    }

    private DataBuffer handlePingMessage(DataBufferFactory dataBufferFactory, String token) {
        long remainingExpiredTime = tokenProvider.getRemainingExpiredTime(token);
        return dataBufferFactory.wrap(String.valueOf(remainingExpiredTime).getBytes());
    }
}
