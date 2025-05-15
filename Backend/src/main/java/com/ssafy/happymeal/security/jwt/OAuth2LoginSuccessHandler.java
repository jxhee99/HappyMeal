package com.ssafy.happymeal.security.jwt; // 패키지명은 OAuth2LoginSuccessHandler의 실제 위치에 맞게 조정

// import com.fasterxml.jackson.databind.ObjectMapper; // JSON 직접 응답 시 필요, 리디렉션 방식에서는 불필요
import com.ssafy.happymeal.domain.user.dao.UserDAO;
import com.ssafy.happymeal.domain.user.entity.User;
// import com.ssafy.happymeal.security.jwt.JwtTokenProvider; // JwtTokenProvider 경로는 올바르다고 가정
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // 프론트엔드 리디렉션 URI를 설정 파일에서 읽어오기 위함 (선택 사항)
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy; // RedirectStrategy 사용
import org.springframework.security.web.RedirectStrategy;       // RedirectStrategy 사용
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder; // URL 생성을 위해 추가

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    // private final ObjectMapper objectMapper; // JSON 직접 응답이 아니므로 제거 또는 주석 처리
    private final UserDAO userDAO;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy(); // 리디렉션 전략 객체 생성

    // application.properties 등에서 프론트엔드 콜백 URI를 설정하고 주입받을 수 있습니다.
    // 예: frontend.redirect-uri=http://localhost:3000/oauth/redirect
    @Value("${frontend.redirect-uri:http://localhost:3000/oauth/redirect}") // 기본값 설정 가능
    private String frontendRedirectUri;

    @Override
    @Transactional(readOnly = true)
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login successful! Attempting to issue JWT and redirect to frontend.");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String googleId = oAuth2User.getName();
        log.debug("Extracted googleId (sub) from principal: {}", googleId);

        Optional<User> userOptional = userDAO.findByGoogleId(googleId);

        if (userOptional.isEmpty()) {
            log.error("FATAL: Cannot find user in DB after successful OAuth2 login. googleId: {}", googleId);
            // 프론트엔드의 에러 처리 페이지로 리디렉션하거나, 적절한 에러 응답을 보낼 수 있습니다.
            // 여기서는 간단히 에러 페이지로 리디렉션하는 예시 (프론트엔드에 /oauth/error 경로 필요)
            String errorTargetUrl = UriComponentsBuilder.fromUriString(frontendRedirectUri.replace("/redirect", "/error"))
                    .queryParam("error", "user_processing_failed")
                    .build().toUriString();
            redirectStrategy.sendRedirect(request, response, errorTargetUrl);
            return;
        }

        User user = userOptional.get();
        Long userId = user.getUserId();
        String role = user.getRole();
        // 닉네임을 UTF-8로 URL 인코딩합니다.
        String encodedNickname = URLEncoder.encode(user.getNickname(), StandardCharsets.UTF_8.toString());
        log.info("User found in DB: userId={}, role={}", userId, role);

        String accessToken = jwtTokenProvider.generateAccessToken(userId, role);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);
        log.debug("Generated Access Token for redirect: {}", accessToken);
        log.debug("Generated Refresh Token for redirect: {}", refreshToken);

        // === 수정된 부분: JSON 응답 대신 프론트엔드로 리디렉션 ===
        // 1. 프론트엔드 리디렉션 대상 URL 생성 (쿼리 파라미터에 토큰 포함)
        String targetUrl = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("userId", userId) // 선택: 사용자 ID도 전달
//                .queryParam("nickname", user.getNickname()) // 선택: 닉네임도 전달
                .queryParam("nickname", encodedNickname) // 선택: 닉네임도 전달
                .build().toUriString();

        // 2. 리디렉션 수행
        // clearAuthenticationAttributes(request); // SimpleUrlAuthenticationSuccessHandler를 상속받지 않았으므로 직접 호출 불가. 필요시 로직 추가.
        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
        log.info("Redirecting to frontend: {} with tokens for userId: {}", frontendRedirectUri, userId);
        // JSON 응답 로직은 제거됨
        /*
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            TokenResponseDto tokenResponse = new TokenResponseDto(accessToken, refreshToken);
            response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
            log.info("Successfully sent tokens to client for userId: {}", userId);
        } catch (IOException e) {
            log.error("Error writing token response to output stream", e);
            throw e;
        }
        */
    }

    // TokenResponseDto는 이 핸들러에서 직접 사용되지 않으므로 주석 처리 또는 삭제 가능
    // (만약 /api/auth/refresh 등 다른 곳에서 이 DTO를 사용한다면 별도 파일로 관리)

    @Getter
    @Setter
    private static class TokenResponseDto {
        private String accessToken;
        private String refreshToken;

        public TokenResponseDto(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

}