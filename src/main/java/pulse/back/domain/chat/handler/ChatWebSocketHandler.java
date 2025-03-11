package pulse.back.domain.chat.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
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
import pulse.back.domain.chat.dto.Message;
import pulse.back.domain.chat.dto.RoomSubscription;
import pulse.back.domain.chat.dto.entity.RoomDto;
import pulse.back.domain.chat.dto.request.MessageRequest;
import pulse.back.domain.chat.dto.response.ErrorMessage;
import pulse.back.domain.chat.dto.response.MessageResponse;
import pulse.back.domain.chat.service.ChatService;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static reactor.rabbitmq.ResourcesSpecification.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {
    private final ConcurrentHashMap<String, Set<ObjectId>> activeUserInfo = new ConcurrentHashMap<>(); //active 된 사용자 정보
    private final ConcurrentHashMap<ObjectId, RoomSubscription> subscriptionMap = new ConcurrentHashMap<>(); //subscription 정보
    private final String EXCHANGE_NAME = "pulse.direct";
    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;
    private final RabbitListener rabbitListener;
    private final Sender sender;
    private final ChatService chatService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                session.receive()
//                        .timeout(Duration.ofSeconds(30))      // TODO: 프론트에서 PING이 구현되면 주석 해제
                        .map(this::messageToMessageDto)
                        .flatMap(message -> handleChatMessage(message, session)
                                .onErrorResume(e -> createErrorMessage((CustomException) e)))
                        .map(session::textMessage)
                        .doFinally(this::cleanUp)
        );
    }

    public void initialize(String roomId) {
        initializeRooms(roomId);
        subscribeQueue(roomId);
    }

    /**
     * 클라이언트 종료 시 해당 클라이언트의 정보를 제거한다.
     */
    private void cleanUp(SignalType signalType) {
        chatService.getMemberIdFromContext()
                .doOnSuccess(memberId -> {
                    // 구독 해제
                    RoomSubscription roomSubscription = subscriptionMap.remove(memberId);
                    if (roomSubscription != null) {
                        roomSubscription.disposable().dispose();

                        // active 해제
                        activeUserInfo.get(roomSubscription.roomId()).remove(memberId);
                        log.info("User[{}] clean up", memberId);
                    }
                }).subscribe();
    }

    private Mono<String> handleChatMessage(final Message message, WebSocketSession session) {
        log.info("MESSAGE : {}", message);
        if (message.messageType() == null) return Mono.error(new CustomException(ErrorCodes.INVALID_MESSAGE_TYPE));

        return switch (message.messageType()) {
            case PING -> {
                long remainingExpiredTime = tokenProvider.getRemainingExpiredTime(message.payload().toString());
                if (remainingExpiredTime > 0) {
                    yield Mono.just(objectToString(MessageResponse.createPong(remainingExpiredTime)));
                } else {
                    yield Mono.error(new CustomException(ErrorCodes.INVALID_TOKEN));
                }
            }

            // 토큰 재발급은 HTTP로 요청 (Cookie)
//            case REISSUE -> {
//                String refreshToken = message.payload().toString();
//                if (tokenProvider.validateToken(refreshToken)) {
//                    yield memberRepository.findById(tokenProvider.getMemberId(refreshToken))
//                            .flatMap(member -> Mono.just(tokenProvider.reissueAccessToken(member.id().toString(), member.memberRole())))
//                            .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.MEMBER_NOT_FOUND)))
//                            .map(tokenResponseDto -> objectToString(MessageResponse.createReissue(tokenResponseDto)));
//                } else {
//                    yield Mono.error(new CustomException(ErrorCodes.INVALID_TOKEN));
//                }
//            }

            case CREATE -> create((MessageRequest) message);

            case JOIN -> joinChatRoom((MessageRequest) message, session);

            case TEXT -> handleTextMessage((MessageRequest) message);

            case BINARY -> handleBinaryMessage((MessageRequest) message);

            case ERROR -> Mono.just(objectToString(message.payload()));

            default -> Mono.error(new CustomException(ErrorCodes.WRONG_MESSAGE_TYPE));
        };
    }

    /**
     * 0. payload(채팅방 이름) 수신
     * 1. 멘토가 채팅방 생성
     * 2. room 생성, chat 추가, Info message 등록
     * 3. activeUserInfo에 채팅방 생성, Hot Pub 생성
     * 4. 클라이언트에게 roomId 전달
     * 5. 메세지큐에서 가져온 메세지에 현재 활성화된 유저들 읽음 처리한 후 DB 저장
     * 6. 클라이언트에게 업데이트 된 메세지 전달
     */
    private Mono<String> create(MessageRequest messageRequest) {
        RoomDto roomDto = messageRequest.toRoomDto();
        return chatService.createRoom(roomDto)
                .doOnSuccess(this::initializeRooms)
                .thenReturn(objectToString(MessageResponse.createRoom(roomDto.roomId())))
                .doOnTerminate(() -> subscribeQueue(roomDto.roomId()));
    }

    private void initializeRooms(String roomId) {
        activeUserInfo.put(roomId, new ConcurrentSkipListSet<>());
        rabbitListener.addRoomSink(roomId);
        log.info("Room[{}] created", roomId);
    }

    private Disposable subscribeQueue(String roomId) {
        return declareQueueAndBind(roomId)
                .thenMany(rabbitListener.addListener(roomId)
                        .flatMap(messageDto -> chatService.updateAndSaveAllMessages(messageDto, activeUserInfo.get(roomId))
                                .doOnNext(updatedMessageDto -> rabbitListener.tryEmitNext(roomId, updatedMessageDto)))).subscribe();
    }

    // TODO: Queue 삭제 처리
    private Mono<AMQP.Queue.BindOk> declareQueueAndBind(String roomId) {
        return sender.declareExchange(exchange(EXCHANGE_NAME))
                .flatMap(exchangeDeclare -> sender.declareQueue(queue(roomId).durable(true).autoDelete(false)))
                .flatMap(queueDeclare -> sender.bind(binding(EXCHANGE_NAME, queueDeclare.getQueue(), roomId)))
                .doOnSuccess(bindOk -> log.info("Queue[{}] bind success", roomId));
    }

    /**
     * 0. roomId와 payload(이전 roomId: optional) 수신
     * 1. DB에서 room 확인 & chat 확인
     * 2. 해당 채팅방의 최근 100개의 채팅 내역을 조회 후 읽음 처리
     * 3. 클라이언트에게 채팅 내역 전달
     * 4. 기존 방 비활성화 및 새 방 활성화
     * 5. 기존 방 구독 해제 및 새 방 구독
     */
    private Mono<String> joinChatRoom(MessageRequest messageRequest, WebSocketSession session) {
        return chatService.getMemberIdFromContext()
                .flatMap(memberId -> chatService.checkRoom(messageRequest.roomId())
                        .flatMap(roomDto -> chatService.updateAndGetAllMessages(roomDto.roomId())
                                .map(MessageResponse::from)
                                .collectList()
                                .map(this::objectToString)
                        )
                        .doOnSuccess(ignored -> {
                            log.info("Inactive beforeRoom if exists and active new room");
                            Object beforeRoomId = messageRequest.payload();
                            if (beforeRoomId != null && activeUserInfo.containsKey(beforeRoomId.toString())) {
                                activeUserInfo.get(beforeRoomId.toString()).remove(memberId);
                            }
                            activeUserInfo.get(messageRequest.roomId()).add(memberId);

                            log.info("Dispose beforeRoom if exists and subscribe new room");
                            Disposable disposable = session.send(
                                    rabbitListener.getRoomSinkAsFlux(messageRequest.roomId())
                                            .map(MessageResponse::from)
                                            .map(this::objectToString)
                                            .doOnNext(message -> log.info("message : {}", message))
                                            .map(session::textMessage)
                                            .doOnError(e -> log.error("Send failed", e))
                            ).subscribe();
                            subscriptionMap.compute(memberId, (key, beforeSubscription) -> {
                                if (beforeSubscription != null) {
                                    beforeSubscription.disposable().dispose();
                                }
                                return RoomSubscription.of(messageRequest.roomId(), disposable);
                            });
                            log.info("User[{}] join room[{}]", memberId, messageRequest.roomId());
                        }).doOnError(e -> log.error("Join failed", e))
                );
    }

    /**
     * 0. roomId와 payload(content) 수신
     * 1. 메세지큐에 넣기
     */
    private Mono<String> handleTextMessage(MessageRequest messageRequest) {
        return chatService.getMemberIdFromContext()
                .flatMap(memberId -> {
                    if (!StringUtils.hasText(messageRequest.roomId())) {
                        return Mono.error(new CustomException(ErrorCodes.INVALID_ROOM_ID));
                    }

                    if (!activeUserInfo.get(messageRequest.roomId()).contains(memberId)) {
                        return Mono.error(new CustomException(ErrorCodes.NOT_JOINED_ROOM));
                    }

                    return sender.send(messageToOutboundFlux(messageRequest, memberId))
                            .doOnError(e -> log.error("Send failed, e"))
                            .then(Mono.just(objectToString(MessageResponse.createAck(messageRequest.roomId()))));
                });
    }

    /**
     * 0. roomId와 payload(base64로 인코딩된 binary data) 수신
     */
    private Mono<String> handleBinaryMessage(MessageRequest messageRequest) {
        return Mono.just("BINARY");
    }

    private Flux<OutboundMessage> messageToOutboundFlux(MessageRequest messageRequest, ObjectId memberId) {
        return Flux.just(new OutboundMessage(EXCHANGE_NAME, messageRequest.roomId(),
                objectToString(messageRequest.toMessageDto(memberId)).getBytes()));
    }

    private Message messageToMessageDto(WebSocketMessage message) {
        try {
            return objectMapper.readValue(message.getPayloadAsText(), MessageRequest.class);
        } catch (Exception e) {
            log.error("messageToMessageDto failed", e);
            return MessageResponse.createError(ErrorCodes.INVALID_JSON);
        }
    }

    private String objectToString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("objectToString failed", e);
            throw new CustomException(ErrorCodes.INVALID_JSON, object.toString());
        }
    }

    private Mono<String> createErrorMessage(CustomException exception) {
        log.error("ERROR : {} [{}]", exception.body(), exception.message());
        return Mono.just(objectToString(ErrorMessage.fromException(exception)));
    }
}
