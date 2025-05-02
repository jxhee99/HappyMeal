package com.ssafy.happymeal.domain.user.entity;

import lombok.*;

import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long userId;
    private String googleId;
    private String email;
    private String nickname;
    private String role;
    private String profileImageUrl;
    private Timestamp createAt;

//    public User() {
//    }

    public User(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public User updateOAuthInfo(String nickname, String email, String profileImageUrl) {
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl; // 프로필 이미지 URL 업데이트 추가
        // this.lastLoginAt = LocalDateTime.now(); // 마지막 로그인 시간 업데이트 로직 (필요하다면)
        return this;
    }

}
