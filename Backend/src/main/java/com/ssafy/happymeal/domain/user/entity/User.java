package com.ssafy.happymeal.domain.user.entity;

import java.sql.Timestamp;

public class User {
    private Long userId;
    private String id;
    private String password;
    private String nickname;
    private String role;
    private Timestamp createAt;

    public User() {
    }

    public User(String id, String password, String nickname) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
    }

    public User(Long userId, String id, String password, String nickname, String role, Timestamp createAt) {
        this.userId = userId;
        this.id = id;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.createAt = createAt;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "createAt=" + createAt +
                ", userId=" + userId +
                ", id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
