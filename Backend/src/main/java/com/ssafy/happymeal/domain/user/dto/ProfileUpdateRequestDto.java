package com.ssafy.happymeal.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequestDto {

    // Bean Validation (JSR 303/380) 스펙을 활용한 유효성 검사 규칙을 정의
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣_]*$",message = "닉네임은 한글, 영문, 숫자, 밑줄(_)만 사용 가능합니다.")
    private String nickname;
    private String profileImgUrl;
}
