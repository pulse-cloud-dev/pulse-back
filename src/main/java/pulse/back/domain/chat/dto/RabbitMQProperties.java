package pulse.back.domain.chat.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbitmq")
public record RabbitMQProperties(
        String host,
        int port,
        String username,
        String password
) {
}
