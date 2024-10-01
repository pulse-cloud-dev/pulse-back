//package pulse.back.common;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.server.WebFilterExchange;
//import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import pulse.back.common.config.auth.TokenProvider;
//import pulse.back.common.config.auth.TokenResponseDto;
//import pulse.back.common.enums.MemberRole;
//import reactor.core.publisher.Mono;
//
//@Component
//@RequiredArgsConstructor
//public class OAuth2AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
//
//    private final TokenProvider tokenProvider;
//
//    @Override
//    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
//        // 카카오 OAuth2 인증 성공 후 사용자 정보 추출
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        String kakaoId = oAuth2User.getName(); // 카카오 고유 ID 추출
//        String email = (String) oAuth2User.getAttributes().get("kakao_account.email");
//
//        // 사용자 역할 설정 (기본적으로 USER로 설정)
//        MemberRole role = MemberRole.USER;
//
//        // JWT 생성
//        TokenResponseDto tokenResponse = tokenProvider.generateTokenDto(kakaoId, role);
//
//        // JWT를 응답 헤더에 추가
//        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
//        response.getHeaders().add("Authorization", "Bearer " + tokenResponse.getAccessToken());
//
//        return response.setComplete();
//    }
//}
//
