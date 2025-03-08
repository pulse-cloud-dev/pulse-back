package pulse.back.domain.chat.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.domain.chat.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {
    private final ConcurrentHashMap<ObjectId, Set<String>> joiningRoomInfo = new ConcurrentHashMap<>(); //active 된 채팅방 정보
    private final String EXCHANGE_NAME = "pulse.direct";
    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;
    private final RabbitListener rabbitListener;
    private final Sender sender;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                session.receive()
//                        .timeout(Duration.ofSeconds(30))      // TODO: 프론트에서 PING이 구현되면 주석 해제
                        .map(this::messageToMessageDto)
                        .flatMap(message -> handleChatMessage(message, session))
                        .map(session::textMessage)
        );
    }

    private Mono<String> handleChatMessage(final Message message, WebSocketSession session) {
        log.info("MESSAGE : {}", message);
        if (message.messageType() == null) return createErrorMessage(ErrorCodes.INVALID_MESSAGE_TYPE);

        return switch (message.messageType()) {
            case PING -> {
                long remainingExpiredTime = tokenProvider.getRemainingExpiredTime(message.payload().toString());
                if (remainingExpiredTime > 0) {
                    yield Mono.just(objectToString(MessageResponse.createPong(remainingExpiredTime)));
                } else {
                    yield createErrorMessage(ErrorCodes.INVALID_TOKEN);
                }
            }

            case REISSUE -> {
                if (tokenProvider.validateToken(message.payload().toString())) {
                    // TODO: member id, role을 토큰 Claim에 넣어두지 않고 DB에서 조회하는지 물어보기
                    yield Mono.just(objectToString(MessageResponse.createReissue(tokenProvider.reissueAccessToken(null, null))));
                } else {
                    yield createErrorMessage(ErrorCodes.INVALID_TOKEN);
                }
            }

            case JOIN -> joinChatRoom((MessageRequest) message)
                    .defaultIfEmpty(objectToString(MessageResponse.createAck(((MessageRequest) message).roomId()))) // 채팅 내역이 없을 때
                    .doOnSuccess(ignored -> {
                        session.send(
                                rabbitListener.getRoomSinkAsFlux(((MessageRequest) message).roomId())
                                        .map(this::objectToString)
                                        .map(session::textMessage)
                        ).subscribe(unused -> {
                            // TODO: RoomSink에서 메세지가 방출될 때마다 현재 active된 사용자를 DB의 seenBy에 업데이트
                        });
                    });

            case TEXT -> handleTextMessage((MessageRequest) message);

            case BINARY -> handleBinaryMessage((MessageRequest) message);

            case ERROR -> Mono.just(objectToString(message.payload()));

            default -> createErrorMessage(ErrorCodes.WRONG_MESSAGE_TYPE);
        };
    }

    private Mono<String> joinChatRoom(MessageRequest messageRequest) {
        // 1. DB에서 방 존재 확인
        // 2. DB에서 방의 메세지 기록 로드 및 seenBy 업데이트
        // 3. RoomSink 구독

        Flux<MessageResponse> flux = Flux.just(MessageResponse.createAck(null)); // TODO: DB에서 메세지 기록 조회 결과
        return flux
                .collectList()
                .map(this::objectToString);
    }

    // TODO: 멘토링 사이트에서 채팅방 활성화를 기본값으로 할 것인지?
    // 만약 채팅방을 기본적으로 사용하지 않는다면 채팅 기능을 사용할 때 웹소켓을 연결할텐데
    // 그러면 채팅방을 생성하면 자동으로 채팅방이 활성화되게 할 것인지,
    // 채팅방이 생성되어도 채팅방을 활성화하기 위해 별도의 요청을 보내야 하는지 확인 (요청을 분리한다면 방 생성은 http로 할 생각)
    private Mono<String> handleTextMessage(MessageRequest messageRequest) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(messageRequest.roomId()) ||
                    !joiningRoomInfo.get("memberId").contains(messageRequest.roomId())) {
                return createErrorMessage(ErrorCodes.INVALID_ROOM_ID);
            }
            return sender.send(messageToOutboundFlux(messageRequest))
                    .doOnError(e -> log.error("Send failed, e"))
                    .then(Mono.just(objectToString(MessageResponse.createAck(messageRequest.roomId()))));
        });
    }

    private Mono<String> handleBinaryMessage(MessageRequest messageRequest) {
        return Mono.just("BINARY");
    }

    private Flux<OutboundMessage> messageToOutboundFlux(MessageRequest messageRequest) {
        return Flux.just(new OutboundMessage(EXCHANGE_NAME, messageRequest.roomId(),
                objectToString(messageRequest.toMessageDto(null)).getBytes()));
    }

    private Mono<String> createErrorMessage(ErrorCodes errorCodes) {
        log.error("ERROR : {}", errorCodes);
        return Mono.just(objectToString(ErrorMessage.fromErrorCodes(errorCodes)));
    }

    private Message messageToMessageDto(WebSocketMessage message) {
        try {
            return objectMapper.readValue(message.getPayloadAsText(), MessageRequest.class);
        } catch (Exception e) {
            return MessageResponse.createError(ErrorCodes.INVALID_JSON);
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
