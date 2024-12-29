package pulse.back.domain.social.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class NaverAuthService {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;

    @Autowired
    public NaverAuthService(WebClient.Builder webClientBuilder,
                            @Value("${spring.security.oauth2.client.registration.naver.client-id}") String clientId,
                            @Value("${spring.security.oauth2.client.registration.naver.client-secret}") String clientSecret) {
        this.webClient = webClientBuilder.baseUrl("https://nid.naver.com").build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public Mono<String> getAccessToken(String code, String state) {
        String tokenRequestUrl = UriComponentsBuilder.fromUriString("/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("state", state)
                .build()
                .toString();

        return webClient.post()
                .uri(tokenRequestUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    log.info("네이버 응답값 확인 response : {}", response);
                    // JSON 파싱 후 access_token 값 추출
                    JsonObject json = JsonParser.parseString(response).getAsJsonObject();
                    return json.get("access_token").getAsString();
                });
    }

}