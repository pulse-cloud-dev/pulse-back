package pulse.back.common.config.auth;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.server.ServerWebExchange;
import pulse.back.common.GlobalVariables;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.common.enums.MemberRole;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


@Slf4j
@Component
public class TokenProvider implements InitializingBean {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTH = "auth";

    private final String secret;
    private SecretKey key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret
    ) {
        this.secret = secret;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * 인증된 authentication으로 access token, refresh token 발급
     * */
    public TokenResponseDto generateTokenDto(ObjectId id, MemberRole role) {

        return this.createTokens(id.toString(), role);
    }

    public TokenResponseDto generateTokenDto(Authentication authentication) {
        // 권한들 가져오기
        String roleName = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority).orElse("");
        if(roleName.isEmpty()) throw new CustomException(ErrorCodes.FORBIDDEN);

        MemberRole role = MemberRole.valueOf(roleName);
        return createTokens(authentication.getName(), role);
    }


    /**
     * access token 재발행
     * */
    public TokenResponseDto reissueAccessToken(String id, MemberRole role) {
        String accessToken = createAccessToken(id, role);

        return new TokenResponseDto(
                accessToken,
                null
        );
    }


    /**
     * @private
     * access token, refresh token 생성후 dto로 리턴
     * */
    private TokenResponseDto createTokens(String id, MemberRole role) {
        String accessToken = createAccessToken(id, role);
        String refreshToken = createRefreshToken(id);

        return new TokenResponseDto(
                accessToken,
                refreshToken
        );
    }


    /**
     * @private
     * access token 생성
     * */
    private String createAccessToken(String id, MemberRole role) {
        long now = System.currentTimeMillis();
        Date accessTokenValidity = new Date(now + GlobalVariables.getAccessTokenExpiredTime());

        // 최신 JJWT API 사용
        return Jwts.builder()
                .setSubject(id)
                .claim("auth", role.name())  // 커스텀 클레임 설정
                .setExpiration(accessTokenValidity)
                .signWith(key, SignatureAlgorithm.HS256) // 서명 키와 알고리즘을 명시적으로 지정
                .compact();
    }

    /**
     * @private
     * refresh token 생성
     * */
    private String createRefreshToken(String id) {
        long now = System.currentTimeMillis();
        Date refreshTokenValidity = new Date(now + GlobalVariables.getRefreshTokenExpiredTime());

        // 최신 JJWT API 사용
        return Jwts.builder()
                .setSubject(id)
                .setExpiration(refreshTokenValidity)
                .signWith(key, SignatureAlgorithm.HS256) // 서명 키와 알고리즘을 명시적으로 지정
                .compact();
    }

    /**
     * token 검증
     * */
    public boolean validateToken(String token) {
        try {
            // 최신 JJWT API 사용
            Jwts.parserBuilder()
                    .setSigningKey(key) // 서명 키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱 및 검증

            return true; // 유효한 토큰

        } catch (SecurityException | MalformedJwtException e) {
            log.info("malformed token"); // 구조적 문제가 있는 토큰
        } catch (ExpiredJwtException e) {
            log.info("expired token"); // 만료된 토큰
        } catch (UnsupportedJwtException e) {
            log.info("unsupported token"); // 지원하지 않는 토큰
        } catch (IllegalArgumentException e) {
            log.info("illegal token"); // 빈 문자열 등 유효하지 않은 토큰
        }
        return false; // 유효하지 않은 토큰
    }


    /**
     * access token에서 security 인증 토큰 로드
     * */
    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTH) == null) {
            throw new CustomException(ErrorCodes.UNAUTHORIZED);
        }

        String[] roleName = { claims.get(AUTH).toString() };
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(roleName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public ObjectId getMemberId(ServerWebExchange exchange){
        Claims claims = this.parseClaimsByAuthorization(exchange);
        return new ObjectId(claims.getSubject());
    }

    public MemberRole getRole(ServerWebExchange exchange){
        Claims claims = this.parseClaimsByAuthorization(exchange);
        return MemberRole.valueOf(claims.get(AUTH).toString());
    }

    /**
     * @private
     * Request에서 claim을 가져옴
     * */
    private Claims parseClaimsByAuthorization(ServerWebExchange exchange){
        String authorization = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CustomException(ErrorCodes.UNAUTHORIZED);
        }
        authorization = authorization.substring(7);
        return this.parseClaims(authorization);
    }

    /**
     * @private
     * 토큰에서 secret으로 페이로드 호출
     * */
    private Claims parseClaims(String accessToken) {
        try {
            // 새로운 JJWT API 사용
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCodes.TOKEN_EXPIRED);
        }
    }
}
