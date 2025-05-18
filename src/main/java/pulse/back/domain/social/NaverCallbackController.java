package pulse.back.domain.social;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pulse.back.common.config.SocialComponent;
import pulse.back.common.response.ResultData;
import pulse.back.domain.social.dto.NaverLoginResponseDto;
import pulse.back.domain.social.service.NaverAuthService;
import pulse.back.domain.social.service.NaverUserProfileService;
import reactor.core.publisher.Mono;

import javax.xml.transform.Result;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/social/naver")
public class NaverCallbackController {
    private final NaverAuthService naverAuthService;
    private final NaverUserProfileService userProfileService;
    private final SocialComponent socialComponent;

    @GetMapping("/join-info")
    @Operation(operationId = "", summary = "[회원가입] 네이버_로그인_이후_사용자_정보_반환", description = """
            ### [ 설명 ]
            - 네이버 소셜 로그인을 진행한 후 코드 값을 이용하여 사용자 정보를 반환합니다.
            <br>
            ### [ 주의사항 ]
            - 회원가입 용도 입니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  :
            - Response : [ResultCodes<NaverLoginResponseDto>]
            ```
            """)
    public Mono<ResultData<NaverLoginResponseDto>> getNaverInfo(@RequestParam("code") String code) {
        return naverAuthService.getAccessToken(code, UUID.randomUUID().toString())
                .flatMap(accessToken -> {
                    log.info("Access Token: {}", accessToken);
                    // accessToken을 사용해 사용자 프로필 조회
                    return userProfileService.getMemberInfo(accessToken);
                })
                .map(info -> {
                    log.info("Member Info : {}", info);
                    return info;
                });
    }

    @Deprecated
    // 네이버 콜백 처리 및 사용자 프로필 조회 통합 API
    @GetMapping("/login/naver/callback")
    public Mono<ResultData<NaverLoginResponseDto>> handleNaverCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        return naverAuthService.getAccessToken(code, state)
                .flatMap(accessToken -> {
                    log.info("Access Token: {}", accessToken);
                    // accessToken을 사용해 사용자 프로필 조회
                    return userProfileService.getMemberInfo(accessToken);
                })
                .map(info -> {
                    log.info("Member Info : {}", info);
                    return info;
                });
    }

    @GetMapping("/find-email")
    @Operation(operationId = "", summary = "[아이디찾기] 네이버_로그인_이후_사용자_정보_반환", description = """
            ### [ 설명 ]
            - 네이버 소셜 로그인을 진행한 후 코드 값을 이용하여 사용자 정보를 반환합니다.
            <br>
            ### [ 주의사항 ]
            - 아이디 찾기 용도 입니다.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  :
            - Response : [ResultCodes<NaverLoginResponseDto>]
            ```
            """)
    public Mono<ResultData<String>> handleNaverFindEmail(@RequestParam("code") String code) {
        String state = UUID.randomUUID().toString();
        return naverAuthService.getAccessToken(code, state)
                .flatMap(accessToken -> {
                    log.info("Access Token: {}", accessToken);
                    // accessToken을 사용해 사용자 프로필 조회
                    return userProfileService.getMemberEmail(accessToken, state);
                })
                .map(email -> {
                    log.info("Member Email : {}", email);
                    return email;
                });
    }

    @Deprecated
    @GetMapping("/login/naver/callEmail")
    public Mono<ResultData<String>> handleNaverCallbackEmail(@RequestParam("code") String code, @RequestParam("state") String state) {
        return naverAuthService.getAccessToken(code, state)
                .flatMap(accessToken -> {
                    log.info("Access Token: {}", accessToken);
                    // accessToken을 사용해 사용자 프로필 조회
                    return userProfileService.getMemberEmail(accessToken, state);
                })
                .map(email -> {
                    log.info("Member Email : {}", email);
                    return email;
                });
    }

//    @GetMapping("/login/naver/callback")
//    public Mono<String> handleNaverCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
//        return naverAuthService.getAccessToken(code, state)
//                .map(accessToken -> "Access Token: " + accessToken);
//    }

    // 사용자 프로필 조회 API
//    @GetMapping("/user-info/{accessToken}")
//    public Mono<Map<String, String>> getUserProfile(@PathVariable("accessToken") String accessToken) {
//        log.debug("accessToken : {}", accessToken);
//        return userProfileService.getUserProfile(accessToken)
//                .map(profile -> {
//                    log.debug("User Profile: {}", profile);
//                    return profile;
//                });
//    }

}
