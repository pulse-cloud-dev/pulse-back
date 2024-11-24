package pulse.back.domain.social.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
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
                .uri("/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    // 필요한 사용자 정보 파싱 (예: JSON에서 닉네임이나 이메일 추출)
                    JsonObject json = JsonParser.parseString(response).getAsJsonObject();
                    log.debug("User Profile: {}", json);
                    return json.get("response").getAsJsonObject().get("email").getAsString();
                });
    }
}
