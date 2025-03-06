package pulse.back.domain.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import pulse.back.domain.chat.handler.ChatWebSocketHandler;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class HandlerMappingConfig {
    private final ChatWebSocketHandler chatWebSocketHandler;

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        return new SimpleUrlHandlerMapping(
                Map.of("/ws/v1/chat", chatWebSocketHandler)
                , 1);
    }
}
