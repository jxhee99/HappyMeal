package com.ssafy.happymeal.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.happymeal.domain.user.dao.UserDAO;
import com.ssafy.happymeal.domain.user.entity.User;
import com.ssafy.happymeal.security.jwt.JwtTokenProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User; // DefaultOAuth2User 사용
import org.springframework.security.oauth2.core.user.OAuth2User; // OAuth2User 임포트
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // DB 조회를 위해 추가

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
// @Transactional // 핸들러 자체보다는 DAO 호출 시 트랜잭션이 적용되므로 필수 아님 (필요시 추가)

// AccessToken 생성
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final UserDAO userDAO; // DB 조회를 위해 userDAO 주입

    @Override
    @Transactional(readOnly = true) // DB 조회가 있으므로 읽기 전용 트랜잭션 권장
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login successful! Attempting to issue JWT.");

        // 1. 인증된 사용자 정보 가져오기 (DefaultOAuth2User 타입)
        // CustomOAuth2UserService에서 반환한 DefaultOAuth2User 객체가 principal로 들어옴
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 2. Google ID (Subject) 추출
        // DefaultOAuth2User의 getName()은 생성 시 사용된 userNameAttributeName("sub")에 해당하는 값을 반환
        String googleId = oAuth2User.getName();
        log.debug("Extracted googleId (sub) from principal: {}", googleId);

        // 3. Google ID를 사용하여 DB에서 사용자 정보 다시 조회
        Optional<User> userOptional = userDAO.findByGoogleId(googleId);

        if (userOptional.isEmpty()) {
            // CustomOAuth2UserService에서 사용자를 저장했어야 하므로, 여기서 찾을 수 없다면 심각한 오류 상황
            log.error("FATAL: Cannot find user in DB after successful OAuth2 login. googleId: {}", googleId);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"error\": \"User not found after login.\"}");
            return; // 처리 중단
        }

        // 4. 조회된 User 객체에서 userId와 role 추출
        User user = userOptional.get();
        Long userId = user.getUserId();
        String role = user.getRole(); // User 엔티티의 String 타입 role 필드
        log.info("User found in DB: userId={}, role={}", userId, role);

        // 5. JwtTokenProvider를 사용하여 Access Token 및 Refresh Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(userId, role);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);
        log.debug("Generated Access Token: {}", accessToken);
        log.debug("Generated Refresh Token: {}", refreshToken);

        // 6. 응답 설정 및 본문에 토큰 담아 전송 (이전과 동일)
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
    }

    // 간단한 토큰 응답 DTO
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