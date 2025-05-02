package com.ssafy.happymeal.domain.user.service; // OAuth 관련 클래스 패키지 경로 예시

import com.ssafy.happymeal.domain.user.dao.UserDAO;
import com.ssafy.happymeal.domain.user.entity.User;
import com.ssafy.happymeal.domain.user.dto.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로깅을 위한 Lombok 어노테이션
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // DB 작업을 위한 트랜잭션

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Google OAuth2 로그인 성공 후 사용자 정보를 처리하는 서비스.
 * DefaultOAuth2UserService를 확장하여 구현합니다.
 */
@Slf4j // 로그 사용을 위한 Lombok 어노테이션
@Service
@RequiredArgsConstructor
@Transactional // 이 서비스 내의 메소드는 DB 작업 시 트랜잭션 안에서 실행됩니다.
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserDAO userDAO; // MyBatis Mapper(DAO) 주입

    /**
     * OAuth2 공급자(Google)로부터 사용자 정보를 받아온 후 호출됩니다.
     * 사용자 정보를 기반으로 DB에서 사용자를 찾거나 새로 생성하고,
     * 인증된 사용자 정보를 나타내는 OAuth2User 객체를 반환합니다.
     *
     * @param userRequest OAuth2 사용자 정보 요청 객체
     * @return 인증된 사용자 정보를 담은 OAuth2User 객체
     * @throws OAuth2AuthenticationException 인증 처리 중 예외 발생 시
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("OAuth2UserRequest received: {}", userRequest);

        // 1. 기본 OAuth2UserService를 사용하여 사용자 정보 로드
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        log.debug("OAuth2User loaded from provider: {}", oAuth2User.getAttributes());

        // 2. 현재 로그인 진행 중인 서비스(provider) ID 확인 (예: "google")
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.debug("Registration ID: {}", registrationId);

        // 3. OAuth2 로그인 시 키가 되는 필드값(Primary Key 역할) 확인 (Google: "sub")
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        log.debug("User name attribute name: {}", userNameAttributeName);

        // 4. OAuth2UserService를 통해 가져온 OAuth2User의 attribute들을 DTO로 변환
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);
        log.debug("Parsed OAuthAttributes: googleId={}, nickname={}, email={}",
                oAuthAttributes.getGoogleId(), oAuthAttributes.getNickname(), oAuthAttributes.getEmail());

        // 5. DB에서 사용자 조회 또는 저장
        User user = saveOrUpdate(oAuthAttributes);
        log.info("User processed: userId={}, googleId={}, nickname={}, role={}",
                user.getUserId(), user.getGoogleId(), user.getNickname(), user.getRole());

        // 6. 인증된 사용자 정보를 담은 Principal 객체(DefaultOAuth2User) 반환
        //    Spring Security Context에 저장되어 인증된 사용자로 관리됩니다.
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())), // 사용자의 권한 정보
                attributes, // OAuth2 공급자로부터 받은 원본 속성
                userNameAttributeName // 사용자 이름(ID) 속성 키 ("sub")
        );

        /* 참고: 만약 UserDetails 인터페이스까지 구현한 커스텀 Principal 객체를 사용하고 싶다면,
           PrincipalDetails 클래스를 만들고 아래와 같이 반환할 수 있습니다.
           return new PrincipalDetails(user, attributes);
           PrincipalDetails 클래스는 UserDetails와 OAuth2User 인터페이스를 구현해야 합니다.
        */
    }

    /**
     * OAuth2 속성 정보를 기반으로 사용자를 찾거나 새로 생성하여 저장(또는 업데이트)합니다.
     *
     * @param attributes OAuth2 사용자 속성 DTO
     * @return 저장되거나 업데이트된 User 엔티티 객체
     */
    private User saveOrUpdate(OAuthAttributes attributes) {
        // googleId로 사용자 조회
        Optional<User> userOptional = userDAO.findByGoogleId(attributes.getGoogleId());
        User user;

        if (userOptional.isPresent()) {
            // 기존 사용자: 이름이나 이메일이 변경되었을 수 있으므로 업데이트 시도
            user = userOptional.get();
            log.debug("Existing user found: userId={}", user.getUserId());
            // User 엔티티에 updateOAuthInfo 메소드가 있다고 가정 (닉네임, 이메일 업데이트)
            // 필요에 따라 업데이트 로직을 상세화할 수 있습니다.
            user.updateOAuthInfo(attributes.getNickname(), attributes.getEmail(), attributes.getPicture());
            userDAO.update(user); // DB 업데이트 실행
            log.debug("Existing user updated.");
        } else {
            // 신규 사용자: OAuthAttributes DTO를 User 엔티티로 변환하여 저장
            user = attributes.toEntity(); // User 엔티티 생성 (Role은 기본 USER)
            log.debug("New user detected. Attempting to save...");
            // TODO: 닉네임 중복 처리 로직 필요 시 여기에 추가
            // 예: String uniqueNickname = generateUniqueNickname(user.getNickname()); user.updateNickname(uniqueNickname);
            userDAO.save(user); // DB 저장 실행 (@Options에 의해 user 객체에 userId가 설정됨)
            log.debug("New user saved with userId={}", user.getUserId());
            // @Options가 잘 동작한다면 아래 재조회는 불필요. 만약을 위해 로그 확인.
            // user = userDAO.findByGoogleId(attributes.getGoogleId())
            //               .orElseThrow(() -> new OAuth2AuthenticationException("Failed to fetch user after save."));
        }
        return user;
    }

    // TODO: 필요시 닉네임 중복 처리 및 유니크 닉네임 생성 로직 구현
    // private String generateUniqueNickname(String baseNickname) { ... }
}