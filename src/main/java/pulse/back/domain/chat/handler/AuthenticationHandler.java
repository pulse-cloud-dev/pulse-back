package pulse.back.domain.chat.handler;

import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.config.auth.TokenProvider;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import reactor.core.publisher.Mono;

@Slf4j
@NonNullApi
@Component
@RequiredArgsConstructor
public class AuthenticationHandler extends HandshakeWebSocketService {
    private final TokenProvider tokenProvider;

    @Override
    public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {
        final String token = exchange.getRequest().getQueryParams().getFirst("token");
        log.info("token : {}", token);
        if (token == null || !tokenProvider.validateToken(token)) {
            return Mono.error(new CustomException(ErrorCodes.UNAUTHORIZED));
        }
        return super.handleRequest(exchange, handler);
    }
}
