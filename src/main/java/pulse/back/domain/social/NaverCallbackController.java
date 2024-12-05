package pulse.back.domain.social;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pulse.back.domain.social.service.NaverAuthService;
import pulse.back.domain.social.service.NaverUserProfileService;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NaverCallbackController {
    private final NaverAuthService naverAuthService;
    private final NaverUserProfileService userProfileService;

    @Deprecated
    @GetMapping("/login/naver/callback")
    public Mono<String> handleNaverCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        return naverAuthService.getAccessToken(code, state)
                .map(accessToken -> "Access Token: " + accessToken);
    }

    // 사용자 프로필 조회 API
    @GetMapping("/user-info/{accessToken}")
    public Mono<String> getUserProfile(@PathVariable("accessToken") String accessToken) {
        log.debug("accessToken : {}", accessToken);
        return userProfileService.getUserProfile(accessToken)
                .map(profile -> "User Profile: " + profile);
    }
}
