package pulse.back.domain.chat.config;

import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pulse.back.domain.chat.dto.RabbitMQProperties;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {
    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQProperties.host());
        factory.setPort(rabbitMQProperties.port());
        factory.setUsername(rabbitMQProperties.username());
        factory.setPassword(rabbitMQProperties.password());
        factory.useNio();
        return factory;
    }

    @Bean
    public Sender sender(ConnectionFactory connectionFactory) {
        SenderOptions senderOptions = new SenderOptions()
                .connectionFactory(connectionFactory)
                .resourceManagementScheduler(Schedulers.boundedElastic());
        return RabbitFlux.createSender(senderOptions);
    }

    @Bean
    public Receiver receiver(ConnectionFactory connectionFactory) {
        ReceiverOptions receiverOptions = new ReceiverOptions()
                .connectionFactory(connectionFactory)
                .connectionSubscriptionScheduler(Schedulers.boundedElastic());
        return RabbitFlux.createReceiver(receiverOptions);
    }

}
