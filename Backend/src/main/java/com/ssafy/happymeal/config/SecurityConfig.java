package com.ssafy.happymeal.config; // 프로젝트 패키지 구조에 맞게 수정하세요

import com.ssafy.happymeal.security.jwt.JwtAuthenticationFilter;
import com.ssafy.happymeal.security.jwt.JwtTokenProvider;
import com.ssafy.happymeal.security.jwt.JwtAccessDeniedHandler;
import com.ssafy.happymeal.security.jwt.JwtAuthenticationEntryPoint;
import com.ssafy.happymeal.domain.user.service.CustomOAuth2UserService;
import com.ssafy.happymeal.security.jwt.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor // Lombok: final 필드 생성자 주입
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 기본 설정 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form Login 비활성화
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (Stateless JWT 사용 시)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않음 (Stateless)
                )

                // 2. CORS 설정 (아래 corsConfigurationSource Bean 사용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. 인가(Authorization) 규칙 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 명세서 기반 경로별 접근 권한 설정
                        .requestMatchers(
                                "/", // 루트
                                "/error", // 에러 페이지
                                "/favicon.ico",
                                "/css/**", "/js/**", "/images/**", // 정적 리소스
                                "/api/auth/**", // 인증 관련 API (로그인 시작, 콜백 등)
                                "/api/foods", // 음식 검색 (GET) - 명세상 모든 사용자 접근 가능
                                "/api/foods/{foodId}", // 음식 상세 조회 (GET) - 명세상 모든 사용자 접근 가능
                                "/api/board", // 게시판 목록 조회 (GET)
                                "/api/board/{postId}" // 게시판 상세 조회 (GET)
                                // 필요시 h2-console 접근 허용 (개발용)
                                // , "/h2-console/**"
                        ).permitAll() // 위 경로는 인증 없이 접근 허용

                        .requestMatchers("/api/admin/**", "/api/foods/**") // 관리자 기능 및 음식 정보 관리(추가/수정/삭제)
                        .hasRole("ADMIN") // ADMIN 역할 필요

                        .requestMatchers(
                                "/api/users/me/**", // 내 정보 관련
                                "/api/meallogs/**", // 식단 기록 관련
                                "/api/food-requests", // 음식 요청 (POST)
                                "/api/board" // 게시글 작성(POST), 수정(PUT), 삭제(DELETE) - 세부 검증은 컨트롤러/서비스에서
                        ).hasAnyRole("USER", "ADMIN") // USER 또는 ADMIN 역할 필요
//                        ).hasAuthority("USER") // JWT에 "USER"라고 넣었을 때만 일치

                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 4. OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                                // .loginPage("/login") // 커스텀 로그인 페이지 (필요 시)
                                // OAuth2 로그인 성공 후 처리 로직
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService) // 사용자 정보 처리 서비스
                                )
                                .successHandler(oAuth2LoginSuccessHandler) // 로그인 성공 핸들러 (JWT 발급)
                        // .failureHandler(oAuth2LoginFailureHandler) // 로그인 실패 핸들러 (선택 사항)
                )

                // 5. JWT 인증 필터 추가
                // UsernamePasswordAuthenticationFilter 전에 JwtAuthenticationFilter를 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                // 6. 예외 처리 설정
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(jwtAccessDeniedHandler) // 인가 실패 시 처리 (403)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패 시 처리 (401)

                );


        // H2 Console 사용 시 frameOptions 비활성화 (개발 환경에서만 사용)
        // http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    // CORS 설정 Bean (React 연동 시 필요)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // TODO: 실제 배포 시에는 프론트엔드 서버 주소만 허용하도록 수정해야 합니다.
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 예: React 개발 서버 주소
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", // JWT 인증 헤더
                "Cache-Control",
                "Content-Type" // 요청/응답 컨텐츠 타입
        ));
        configuration.setAllowCredentials(true); // 자격 증명(쿠키 등) 허용
        configuration.setMaxAge(3600L); // pre-flight 요청 캐시 시간 (초)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // /api/** 경로에 대해 위 설정 적용
        return source;
    }

    // PasswordEncoder Bean (Google 로그인만 사용하면 당장은 필요 없지만, 향후 확장 고려)
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
}