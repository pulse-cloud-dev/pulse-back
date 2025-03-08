package pulse.back.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pulse.back.domain.chat.dto.MessageDto;
import pulse.back.domain.chat.repository.MessageRepository;
import pulse.back.entity.chat.Message;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public Mono<Message> save(MessageDto messageDto) {
        return messageRepository.save(messageDto.toEntity());
    }
}
