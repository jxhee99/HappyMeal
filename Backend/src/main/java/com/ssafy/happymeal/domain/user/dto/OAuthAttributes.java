package com.ssafy.happymeal.domain.user.dto; // 패키지 경로는 실제 프로젝트에 맞게 조정하세요

// import com.ssafy.happymeal.domain.user.Role; // Role Enum 대신 String 사용
import com.ssafy.happymeal.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * OAuth2 인증 과정에서 얻은 사용자 속성(attributes)을 담고 처리하는 DTO 클래스.
 * - profileImageUrl 필드 추가 반영
 * - role 타입을 String으로 처리 반영
 */
@Slf4j
@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String googleId;
    private String nickname;
    private String email;
    private String picture; // Google 프로필 사진 URL ('picture' 속성)

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String googleId, String nickname, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.googleId = googleId;
        this.nickname = nickname;
        this.email = email;
        this.picture = picture; // picture 필드 초기화 추가
    }

    /**
     * OAuth2 공급자 정보와 속성을 기반으로 OAuthAttributes 객체를 생성합니다.
     */
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        log.debug("Creating OAuthAttributes for registrationId: {}", registrationId);
        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        }
        // 다른 소셜 로그인 추가 시 여기에 분기
        log.error("Unsupported registrationId: {}", registrationId);
        return null;
    }

    /**
     * Google 속성을 기반으로 OAuthAttributes 객체를 생성합니다.
     * 'picture' 속성을 추가로 추출합니다.
     */
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        log.debug("Parsing Google attributes. Key for ID: {}", userNameAttributeName);
        return OAuthAttributes.builder()
                .googleId((String) attributes.get(userNameAttributeName)) // "sub"
                .nickname((String) attributes.get("name"))      // "name"
                .email((String) attributes.get("email"))        // "email"
                .picture((String) attributes.get("picture"))    // "picture" 추가
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    /**
     * OAuthAttributes 정보를 바탕으로 User 엔티티 객체를 생성합니다.
     * - profileImageUrl 필드 매핑 추가
     * - role 필드를 String("ROLE_USER")으로 설정
     *
     * @return 생성된 User 엔티티 객체 (DB 저장 전 상태)
     */
    public User toEntity() {
        log.debug("Converting OAuthAttributes to User entity for googleId: {}", googleId);
        return User.builder()
                .googleId(this.googleId)
                .nickname(this.nickname)
                .email(this.email)
                .profileImageUrl(this.picture) // picture 필드를 profileImageUrl에 매핑
                .role("USER")           // Role Enum 대신 String "ROLE_USER" 사용
                // password 필드는 null
                // userId, createdAt 필드는 설정하지 않음 (DB에서 처리)
                .build();
    }
}