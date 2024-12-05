package pulse.back.domain.social.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverUserProfileService {

    private final WebClient webClient;

    public Mono<String> getUserProfile(String accessToken) {
        return webClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        // JSON 형식 확인 및 파싱
                        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
                        log.debug("User Profile: {}", json);

                        JsonObject responseObject = json.getAsJsonObject("response");
                        if (responseObject != null && responseObject.has("email")) {
                            return Mono.just(responseObject.get("email").getAsString());
                        } else {
                            log.warn("Email not found in user profile: {}", response);
                            return Mono.error(new IllegalArgumentException("Email not found in user profile"));
                        }
                    } catch (JsonSyntaxException | IllegalStateException e) {
                        log.error("Failed to parse JSON response: {}", response, e);
                        return Mono.error(new IllegalArgumentException("Invalid JSON response"));
                    }
                });
    }
}
