package pulse.back.domain.social.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pulse.back.common.config.SocialComponent;
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
    private final SocialComponent socialComponent;

    public Mono<ResultData<NaverLoginResponseDto>> getMemberInfo(String accessToken) {
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

    public Mono<ResultData<String>> getMemberEmail(String accessToken, String state) {
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
                            String email = responseObject.has("email") ? responseObject.get("email").getAsString() : null;
                            if(email == null) {
                                log.warn("네이버 프로필에 이메일이 없습니다. : {}", response);
                                return Mono.error(new CustomException(ErrorCodes.SOCIAL_NOT_FOUND));
                            }

                            return Mono.just(new ResultData<>(socialComponent.maskEmail(email), "네이버 로그인을 통한 사용자 이메일 조회에 성공하였습니다. "));
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

}
