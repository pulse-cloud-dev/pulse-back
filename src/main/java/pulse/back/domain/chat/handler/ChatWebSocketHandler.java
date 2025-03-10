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
import pulse.back.domain.chat.dto.Message;
import pulse.back.domain.chat.dto.RoomSubscription;
import pulse.back.domain.chat.dto.request.MessageRequest;
import pulse.back.domain.chat.dto.response.ErrorMessage;
import pulse.back.domain.chat.dto.response.MessageResponse;
import pulse.back.domain.chat.service.ChatService;
import pulse.back.domain.member.repository.MemberRepository;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

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
    private final MemberRepository memberRepository;
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

    private void cleanUp(SignalType signalType) {
        chatService.getObjectIdFromContext()
                .doOnSuccess(memberId -> {
                    // 구독 해제
                    RoomSubscription roomSubscription = subscriptionMap.remove(memberId);
                    roomSubscription.disposable().dispose();
                    // active 해제
                    activeUserInfo.get(roomSubscription.roomId()).remove(memberId);
                    log.info("User[{}] clean up", memberId);
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

            case REISSUE -> {
                String refreshToken = message.payload().toString();
                if (tokenProvider.validateToken(refreshToken)) {
                    yield memberRepository.findById(tokenProvider.getMemberId(refreshToken))
                            .flatMap(member -> Mono.just(tokenProvider.reissueAccessToken(member.id().toString(), member.memberRole())))
                            .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.MEMBER_NOT_FOUND)))
                            .map(tokenResponseDto -> objectToString(MessageResponse.createReissue(tokenResponseDto)));
                } else {
                    yield Mono.error(new CustomException(ErrorCodes.INVALID_TOKEN));
                }
            }

            case CREATE -> create((MessageRequest) message);

            case JOIN -> joinChatRoom((MessageRequest) message, session);

            case TEXT -> handleTextMessage((MessageRequest) message);

            case BINARY -> handleBinaryMessage((MessageRequest) message);

            case ERROR -> Mono.just(objectToString(message.payload()));

            default -> Mono.error(new CustomException(ErrorCodes.WRONG_MESSAGE_TYPE));
        };
    }

    private Mono<String> create(MessageRequest messageRequest) {
        // TODO: 멘토인지 권한 확인
        return chatService.createRoom(messageRequest.toRoomDto())
                .doOnSuccess(roomId -> {
                    activeUserInfo.put(roomId, new ConcurrentSkipListSet<>());
                    rabbitListener.addRoomSink(roomId);
                    log.info("Room[{}] created", roomId);
                })
                .map(roomId -> objectToString(MessageResponse.createRoom(roomId)));
    }

    private Mono<String> joinChatRoom(MessageRequest messageRequest, WebSocketSession session) {
        // 0. payload(optional): 이전 roomId
        // 1. DB에서 방 존재 확인
        // 2. DB에서 방의 메세지 기록 로드 및 seenBy 업데이트
        // 3. RoomSink 구독
        return chatService.getObjectIdFromContext()
                .flatMap(memberId -> chatService.checkRoom(messageRequest.roomId())
                        .flatMap(roomDto -> chatService.updateAndGetAllMessages(roomDto.roomId())
                                .map(messageDto -> MessageResponse.from(messageDto, roomDto.title()))
                                .collectList()
                                .map(this::objectToString)
                        )
                        .doOnSuccess(ignored -> {
                            // 기존 방 비활성화 및 새 방 활성화
                            String beforeRoomId = messageRequest.payload().toString();
                            if (beforeRoomId != null && activeUserInfo.containsKey(beforeRoomId)) {
                                activeUserInfo.get(beforeRoomId).remove(memberId);
                            }
                            activeUserInfo.get(messageRequest.roomId()).add(memberId);

                            // 기존 방 구독 해제 및 새 방 구독
                            Disposable disposable = session.send(
                                    rabbitListener.getRoomSinkAsFlux(messageRequest.roomId())
                                            .map(this::objectToString)
                                            .map(session::textMessage)
                            ).subscribe();
                            subscriptionMap.compute(memberId, (key, beforeSubscription) -> {
                                if (beforeSubscription != null) {
                                    beforeSubscription.disposable().dispose();
                                }
                                return RoomSubscription.of(messageRequest.roomId(), disposable);
                            });
                            log.info("User[{}] join room[{}]", memberId, messageRequest.roomId());
                        })
                );
    }

    private Mono<String> handleTextMessage(MessageRequest messageRequest) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(messageRequest.roomId()) ||
                    !activeUserInfo.get("memberId").contains(messageRequest.roomId())) { // TODO: 수정
                return Mono.error(new CustomException(ErrorCodes.INVALID_ROOM_ID));
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

    private Mono<String> createErrorMessage(CustomException exception) {
        log.error("ERROR : {} [{}]", exception.body(), exception.message());
        return Mono.just(objectToString(ErrorMessage.fromException(exception)));
    }
}
