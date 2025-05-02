package com.ssafy.happymeal.security.jwt; // JWT 관련 패키지 경로

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils; // StringUtils 사용
import org.springframework.web.filter.OncePerRequestFilter; // OncePerRequestFilter 상속

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 클라이언트 요청 시마다 JWT의 유효성을 검증하고,
 * 유효하다면 SecurityContext에 인증(Authentication) 객체를 저장하는 필터.
 * SecurityConfig에 등록되어 특정 필터(예: UsernamePasswordAuthenticationFilter) 전에 실행됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 요청당 한 번만 실행 보장

    public static final String AUTHORIZATION_HEADER = "Authorization"; // 헤더 이름 상수
    public static final String BEARER_PREFIX = "Bearer ";        // Bearer 타입 접두사 상수

    private final JwtTokenProvider jwtTokenProvider; // JWT 처리 유틸리티 주입

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 토큰 추출
        String jwt = resolveToken(request);
        String requestURI = request.getRequestURI(); // 현재 요청 URI 로깅용

        // 2. 토큰 유효성 검증
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            // 3. 토큰이 유효할 경우 토큰에서 Authentication 객체 가져오기
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            // 4. SecurityContext에 Authentication 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authentication information saved in SecurityContext for user: {}, uri: {}", authentication.getName(), requestURI);
        } else {
            log.debug("No valid JWT found, uri: {}", requestURI);
        }

        // 5. 다음 필터로 제어 넘기기
        filterChain.doFilter(request, response);
    }

    /**
     * HttpServletRequest의 헤더에서 'Authorization' 값을 추출하고,
     * 'Bearer ' 접두사를 제거하여 순수 토큰 문자열을 반환합니다.
     *
     * @param request HttpServletRequest 객체
     * @return 추출된 토큰 문자열 (없거나 형식이 맞지 않으면 null)
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()); // "Bearer " 다음의 문자열 반환
        }
        return null;
    }
}