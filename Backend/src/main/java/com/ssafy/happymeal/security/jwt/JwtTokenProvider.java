package com.ssafy.happymeal.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// import javax.annotation.PostConstruct; // @PostConstruct 사용 안 하므로 주석 처리 또는 삭제

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";

    private final String secret;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    private final Key key; // Key 객체를 final로 선언

    /**
     * 생성자를 통해 application.properties의 설정값을 주입받고,
     * 주입받은 secret 값을 사용하여 JWT 서명에 사용할 Key 객체를 즉시 생성합니다.
     *
     * @param secret                           JWT 비밀 키 (Base64 인코딩된 값)
     * @param accessTokenValidityInMilliseconds Access Token 유효 시간 (ms)
     * @param refreshTokenValidityInMilliseconds Refresh Token 유효 시간 (ms)
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshTokenValidityInMilliseconds) {

        this.secret = secret; // 생성자 파라미터로 받은 secret 값 저장
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;

        // === 생성자 내에서 Key 객체 초기화 ===
        byte[] keyBytes = Decoders.BASE64.decode(secret); // Base64 디코딩
        this.key = Keys.hmacShaKeyFor(keyBytes);          // HMAC SHA 알고리즘 Key 생성 및 final 필드에 할당
        log.info("JWT Key initialized successfully within constructor.");
        // ==================================
    }

    /* @PostConstruct 메소드는 더 이상 필요 없음
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.info("JWT Key initialized successfully.");
    }
    */

    /**
     * 인증 정보를 기반으로 Access Token을 생성합니다.
     * (이하 메소드들은 이전과 동일)
     */
//    public String generateAccessToken(Long userId, String role) {
//        long now = (new Date()).getTime();
//        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);
//
//        return Jwts.builder()
//                .setSubject(String.valueOf(userId))
//                .claim(AUTHORITIES_KEY, role)
//                .setIssuedAt(new Date(now))
//                .setExpiration(validity)
//                .signWith(key, SignatureAlgorithm.HS512)
//                .compact();
//    }

    // postman 요청 시 접근 권한 확인 위한 token 로그 출력
    public String generateAccessToken(Long userId, String role) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))
//                .claim(AUTHORITIES_KEY, role)
                /*  fix : Spring Security에서 .hasAnyRole("USER") 같은 메서드를 사용할 예정이라면
                "ROLE_USER" 형태여야 정상 동작하기 때문에 "ROLE_"을 강제로 명시 */
                .claim(AUTHORITIES_KEY, "ROLE_"+role)
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // 로그로 출력!
//        log.info("Generated JWT access token for userId {}: {}", userId, token);

        return token;
    }

    /**
     * 사용자 ID를 기반으로 Refresh Token을 생성합니다.
     */
    public String generateRefreshToken(Long userId) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Access Token을 복호화하여 인증(Authentication) 객체를 생성합니다.
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        // 식제 할 것
//        log.info("🔐 JWT subject (userId): {}", claims.getSubject());
//        log.info("🔐 JWT role: {}", claims.get("auth"));

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 이 부분에서 UserDetails 구현체에 "username" 역할로 userId가 들어감
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰의 유효성을 검증합니다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.", e);
        }
        return false;
    }

    /**
     * 토큰에서 클레임(Payload) 정보를 추출합니다.
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * 토큰에서 사용자 ID (Subject)를 추출합니다.
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰에서 사용자 역할(Role)을 추출합니다.
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return (String) claims.get(AUTHORITIES_KEY);
    }
}