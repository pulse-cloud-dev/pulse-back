package pulse.back.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.domain.chat.dto.Message;
import pulse.back.domain.chat.dto.RoomSink;
import pulse.back.domain.chat.service.MessageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitListener {
    private final ConcurrentHashMap<String, RoomSink> roomSinkMap =  new ConcurrentHashMap<>();
    private final Receiver receiver;
    private final ObjectMapper objectMapper;
    private final MessageService messageService;

    // 채팅방 생성할 때 최초 1회 호출하여 Sink와 Rabbitmq 연결
    public Mono<Void> addListener(String roomId) {
        return receiver.consumeAutoAck(roomId)
                .doOnNext(delivery -> {
                    log.info("delivery : {}", delivery);
                    roomSinkMap.get(roomId).roomSink().tryEmitNext(deliveryToMessage(delivery));
                })
                .then();
    }

    public Flux<Message> getRoomSinkAsFlux(String roomId) {
        return roomSinkMap.get(roomId).roomSink().asFlux();
    }

    private Message deliveryToMessage(Delivery delivery) {
        try {
            return objectMapper.readValue(delivery.getBody(), Message.class);
        } catch (IOException e) {
            throw new CustomException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
