package com.ssafy.happymeal.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 필요한 접근 권한이 없을 때 403 Forbidden 에러를 반환하는 컴포넌트.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper; // JSON 응답 생성을 위해 주입

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("Responding with forbidden error. Message - {}", accessDeniedException.getMessage());

        // 응답 상태 코드 설정 (403 Forbidden)
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // 응답 컨텐츠 타입 설정 (JSON)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 문자 인코딩 설정 (UTF-8)
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // JSON 응답 본문 생성
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Forbidden");
        errorDetails.put("message", "접근 권한이 없습니다: " + accessDeniedException.getMessage());
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