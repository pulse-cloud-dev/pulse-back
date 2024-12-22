package pulse.back.domain.social.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.common.response.ResultData;
import pulse.back.domain.social.dto.NaverLoginResponseDto;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverUserProfileService {

    private final WebClient webClient;

    public Mono<ResultData<NaverLoginResponseDto>> getUserProfile(String accessToken) {
        return webClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        // JSON 형식 확인 및 파싱
                        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
                        log.info("전달받은 프로필 Json : {}", json);

                        JsonObject responseObject = json.getAsJsonObject("response");
                        if (responseObject != null) {
                            // NaverLoginResponseDto 생성
                            NaverLoginResponseDto profile = new NaverLoginResponseDto(
                                    responseObject.has("email") ? responseObject.get("email").getAsString() : null,
                                    responseObject.has("name") ? responseObject.get("name").getAsString() : null,
                                    responseObject.has("nickname") ? responseObject.get("nickname").getAsString() : null,
                                    responseObject.has("profile_image") ? responseObject.get("profile_image").getAsString() : null,
                                    responseObject.has("gender") ? responseObject.get("gender").getAsString() : null,
                                    responseObject.has("birthday") ? responseObject.get("birthday").getAsString() : null,
                                    responseObject.has("age") ? responseObject.get("age").getAsString() : null,
                                    responseObject.has("birthyear") ? responseObject.get("birthyear").getAsString() : null,
                                    responseObject.has("mobile") ? responseObject.get("mobile").getAsString() : null
                            );

                            return Mono.just(new ResultData<>(profile, "네이버 로그인을 통한 사용자 정보 조회에 성공하였습니다. "));
                        } else {
                            log.warn("네이버 프로필을 찾지 못했습니다. : {}", response);
                            return Mono.error(new CustomException(ErrorCodes.SOCIAL_NOT_FOUND));
                        }
                    } catch (JsonSyntaxException | IllegalStateException e) {
                        log.error("Failed to parse JSON response: {}", response, e);
                        return Mono.error(new CustomException(ErrorCodes.INVALID_JSON));
                    }
                });
    }


//    public Mono<Map<String, String>> getUserProfile(String accessToken) {
//        return webClient.get()
//                .uri("https://openapi.naver.com/v1/nid/me")
//                .header("Authorization", "Bearer " + accessToken)
//                .retrieve()
//                .bodyToMono(String.class)
//                .flatMap(response -> {
//                    try {
//                        // JSON 형식 확인 및 파싱
//                        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
//                        log.debug("User Profile: {}", json);
//
//                        JsonObject responseObject = json.getAsJsonObject("response");
//                        if (responseObject != null) {
//                            // 필요한 필드를 파싱
//                            Map<String, String> profileData = new HashMap<>();
//                            profileData.put("email", responseObject.has("email") ? responseObject.get("email").getAsString() : null);
//                            profileData.put("name", responseObject.has("name") ? responseObject.get("name").getAsString() : null);
//                            profileData.put("nickname", responseObject.has("nickname") ? responseObject.get("nickname").getAsString() : null);
//                            profileData.put("profile_image", responseObject.has("profile_image") ? responseObject.get("profile_image").getAsString() : null);
//                            profileData.put("gender", responseObject.has("gender") ? responseObject.get("gender").getAsString() : null);
//                            profileData.put("birthday", responseObject.has("birthday") ? responseObject.get("birthday").getAsString() : null);
//                            profileData.put("age", responseObject.has("age") ? responseObject.get("age").getAsString() : null);
//                            profileData.put("birthyear", responseObject.has("birthyear") ? responseObject.get("birthyear").getAsString() : null);
//                            profileData.put("mobile", responseObject.has("mobile") ? responseObject.get("mobile").getAsString() : null);
//
//                            return Mono.just(profileData);
//                        } else {
//                            log.warn("No user profile data found: {}", response);
//                            return Mono.error(new IllegalArgumentException("No user profile data found"));
//                        }
//                    } catch (JsonSyntaxException | IllegalStateException e) {
//                        log.error("Failed to parse JSON response: {}", response, e);
//                        return Mono.error(new IllegalArgumentException("Invalid JSON response"));
//                    }
//                });
//    }
}
