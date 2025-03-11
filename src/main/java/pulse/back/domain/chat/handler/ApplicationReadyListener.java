package pulse.back.domain.chat.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pulse.back.domain.chat.dto.entity.RoomDto;
import pulse.back.domain.chat.service.ChatService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {
    private final ChatService chatService;
    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        chatService.getAllRoom()
                .map(RoomDto::roomId)
                .doOnNext(chatWebSocketHandler::initialize)
                .subscribe(s -> log.info("All rooms initialized completely"));
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
