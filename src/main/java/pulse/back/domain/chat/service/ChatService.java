package pulse.back.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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

import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public Mono<String> createRoom(RoomDto roomDto) {
        return getMemberIdFromContext()
                .flatMap(memberId -> roomRepository.insert(roomDto.toEntity(memberId))
                        .flatMap(room -> chatRepository.insert(Chat.of(room.roomId(), memberId)))
                        .flatMap(chat -> messageRepository.insert(Message.createInfo(chat.memberId(), chat.roomId())))
                        .map(Message::roomId)
                        .onErrorMap(e -> new CustomException(ErrorCodes.BAD_REQUEST))
                );
    }

    @Transactional(readOnly = true)
    public Mono<RoomDto> checkRoom(String roomId) {
        return getMemberIdFromContext()
                .flatMap(memberId -> roomRepository.findById(roomId)
                        .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.ROOM_NOT_FOUND)))
                        .flatMap(room -> chatRepository.findByRoomIdAndMemberId(roomId, memberId)
                                .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.NOT_JOINED_ROOM)))
                                .thenReturn(RoomDto.from(room))
                        )
                );
    }

    public Flux<MessageDto> updateAndGetAllMessages(String roomId) {
        return getMemberIdFromContext()
                .flatMapMany(memberId -> messageRepository.findRecentMessagesByRoomId(roomId)
                        .switchIfEmpty(Flux.error(new CustomException(ErrorCodes.ROOM_NOT_FOUND)))
                        .flatMap(message -> messageRepository.save(message.updateSeenBy(memberId))
                                .map(MessageDto::from)
                                .doOnSuccess(messageDto -> log.info("messageDto : {}", messageDto))
                        )
                );
    }

    public Flux<MessageDto> updateAndSaveAllMessages(MessageDto messageDto, Set<ObjectId> memberIds) {
        return Flux.fromIterable(memberIds)
                .map(memberId -> messageDto.toEntity().updateSeenBy(memberId))
                .flatMap(messageRepository::save)
                .map(MessageDto::from);
    }

    public Mono<Void> addMemberToRoom(String roomId) {
        return getMemberIdFromContext()
                .flatMap(memberId -> roomRepository.updateMemberCount(roomId)
                        .switchIfEmpty(Mono.error(new CustomException(ErrorCodes.ROOM_NOT_FOUND)))
                        .flatMap(ignored -> chatRepository.insert(Chat.of(roomId, memberId)))
                        .flatMap(chat -> messageRepository.insert(Message.createInfo(chat.memberId(), chat.roomId())))
                        .then()
                );
    }

    @Transactional(readOnly = true)
    public Flux<RoomDto> getRoomList() {
        return getMemberIdFromContext()
                .flatMapMany(memberId -> chatRepository.findByMemberId(memberId)
                        .switchIfEmpty(Flux.error(new CustomException(ErrorCodes.MEMBER_NOT_FOUND)))
                        .map(Chat::roomId)
                        .collectList()
                        .flatMapMany(roomIds -> roomRepository.findByRoomIdIn(roomIds)
                                .switchIfEmpty(Flux.error(new CustomException(ErrorCodes.ROOM_NOT_FOUND)))
                                .map(RoomDto::from)
                        )
                );
    }

    public Flux<RoomDto> getAllRoom() {
        return roomRepository.findAll().map(RoomDto::from);
    }

    // TODO: JwtFilter 완성되면 다시 수정
    public Mono<ObjectId> getMemberIdFromContext() {
        return Mono.just(new ObjectId("67c980aead6df4267d37948e"));
//        return ReactiveSecurityContextHolder.getContext()
//                .map(SecurityContext::getAuthentication)
//                .doOnSuccess(authentication -> log.info("authentication : {}", authentication.getName()))
//                .map(Authentication::getName)
//                .map(ObjectId::new)
//                .cache()
//                .onErrorMap(e -> new CustomException(ErrorCodes.INTERNAL_SERVER_ERROR));
    }
}
