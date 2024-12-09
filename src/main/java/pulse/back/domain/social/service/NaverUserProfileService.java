package pulse.back.domain.social.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverUserProfileService {

    private final WebClient webClient;

    public Mono<Map<String, String>> getUserProfile(String accessToken) {
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
                        if (responseObject != null) {
                            // 필요한 필드를 파싱
                            Map<String, String> profileData = new HashMap<>();
                            profileData.put("email", responseObject.has("email") ? responseObject.get("email").getAsString() : null);
                            profileData.put("name", responseObject.has("name") ? responseObject.get("name").getAsString() : null);
                            profileData.put("nickname", responseObject.has("nickname") ? responseObject.get("nickname").getAsString() : null);
                            profileData.put("profile_image", responseObject.has("profile_image") ? responseObject.get("profile_image").getAsString() : null);
                            profileData.put("gender", responseObject.has("gender") ? responseObject.get("gender").getAsString() : null);
                            profileData.put("birthday", responseObject.has("birthday") ? responseObject.get("birthday").getAsString() : null);
                            profileData.put("age", responseObject.has("age") ? responseObject.get("age").getAsString() : null);
                            profileData.put("birthyear", responseObject.has("birthyear") ? responseObject.get("birthyear").getAsString() : null);
                            profileData.put("mobile", responseObject.has("mobile") ? responseObject.get("mobile").getAsString() : null);

                            return Mono.just(profileData);
                        } else {
                            log.warn("No user profile data found: {}", response);
                            return Mono.error(new IllegalArgumentException("No user profile data found"));
                        }
                    } catch (JsonSyntaxException | IllegalStateException e) {
                        log.error("Failed to parse JSON response: {}", response, e);
                        return Mono.error(new IllegalArgumentException("Invalid JSON response"));
                    }
                });
    }
}
