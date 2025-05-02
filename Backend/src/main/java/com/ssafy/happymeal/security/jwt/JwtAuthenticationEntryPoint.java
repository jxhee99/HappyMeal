package com.ssafy.happymeal.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 유효한 자격증명(인증 토큰 등)을 제공하지 않고 접근하려 할 때
 * 401 Unauthorized 에러를 반환하는 컴포넌트.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // JSON 응답 생성을 위해 주입

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Responding with unauthorized error. Message - {}", authException.getMessage());

        // 응답 상태 코드 설정 (401 Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 응답 컨텐츠 타입 설정 (JSON)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 문자 인코딩 설정 (UTF-8)
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // JSON 응답 본문 생성
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", "인증이 필요합니다: " + authException.getMessage());
        // 필요시 추가 정보 포함 가능 (예: timestamp, path)
        // errorDetails.put("timestamp", System.currentTimeMillis());
        // errorDetails.put("path", request.getRequestURI());

        // ObjectMapper를 사용하여 Map을 JSON 문자열로 변환 후 응답 스트림에 쓰기
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
        } catch (IOException e) {
            log.error("Error writing JSON error response", e);
            throw e;
        }
    }
}