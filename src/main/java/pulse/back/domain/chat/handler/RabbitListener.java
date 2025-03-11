package pulse.back.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.domain.chat.dto.entity.MessageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.rabbitmq.Receiver;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitListener {
    private final ConcurrentHashMap<String, Sinks.Many<MessageDto>> roomSinkMap = new ConcurrentHashMap<>();
    private final Receiver receiver;
    private final ObjectMapper objectMapper;

    // 방 만들때 최초 1회 호출
    public Flux<MessageDto> addListener(String roomId) {
        // 현재 채팅방에 active 된 사용자 확인 후 seenBy 업데이트
        return receiver.consumeAutoAck(roomId)
                .doOnNext(delivery -> log.info("delivery : {}", delivery))
                .map(this::deliveryToMessage);
    }

    public void tryEmitNext(String roomId, MessageDto messageDto) {
        roomSinkMap.get(roomId).tryEmitNext(messageDto);
    }

    public void addRoomSink(String roomId) {
        roomSinkMap.put(roomId, Sinks.many().multicast().onBackpressureBuffer());
    }

    public Flux<MessageDto> getRoomSinkAsFlux(String roomId) {
        return roomSinkMap.get(roomId).asFlux();
    }

    private MessageDto deliveryToMessage(Delivery delivery) {
        try {
            return objectMapper.readValue(delivery.getBody(), MessageDto.class);
        } catch (IOException e) {
            throw new CustomException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
