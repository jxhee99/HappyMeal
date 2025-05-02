package com.ssafy.happymeal.domain.user.dto;

import com.ssafy.happymeal.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
    private Long userId;
    private String nickName;
    private String email;
    private String role;


    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .nickName(user.getNickname())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
