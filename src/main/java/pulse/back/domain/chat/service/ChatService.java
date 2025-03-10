package pulse.back.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.domain.chat.dto.entity.MessageDto;
import pulse.back.domain.chat.dto.entity.RoomDto;
import pulse.back.domain.chat.repository.ChatRepository;
import pulse.back.domain.chat.repository.MessageRepository;
import pulse.back.domain.chat.repository.RoomRepository;
import pulse.back.entity.chat.Chat;
import pulse.back.entity.chat.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public Mono<String> createRoom(RoomDto roomDto) {
        return getObjectIdFromContext()
                .flatMap(memberId -> roomRepository.insert(roomDto.toEntity())
                        .flatMap(room -> chatRepository.insert(Chat.of(room.roomId(), memberId)))
                        .flatMap(chat -> messageRepository.insert(Message.createInfo(chat.memberId(), chat.roomId())))
                        .map(Message::roomId)
                        .onErrorMap(e -> new CustomException(ErrorCodes.BAD_REQUEST))
                );
    }

    // TODO: 결제가 완료되면 채팅방 참가 목록에 memberId 추가 및 info 메세지 등록 (Http)
    @Transactional
    public Mono<RoomDto> checkRoom(String roomId) {
        return getObjectIdFromContext()
                .flatMap(memberId -> roomRepository.findById(roomId)
                        .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.ROOM_NOT_FOUND)))
                        .flatMap(room -> chatRepository.findByRoomIdAndMemberId(roomId, memberId)
                                .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.NOT_JOINED_ROOM)))
                                .thenReturn(RoomDto.from(room))
                        )
                );
    }

    @Transactional
    public Flux<MessageDto> updateAndGetAllMessages(String roomId) {
        return getObjectIdFromContext().flux()
                .flatMap(memberId -> messageRepository.findAllByRoomId(roomId)
                        .switchIfEmpty(Flux.error(new CustomException(ErrorCodes.ROOM_NOT_FOUND)))
                        .flatMap(message -> messageRepository.insert(message.updateSeenBy(memberId, message))
                                .map(MessageDto::from))
                );
    }

    public Mono<ObjectId> getObjectIdFromContext() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .map(ObjectId::new)
                .cache();
    }
}
