package com.ssafy.happymeal.domain.user.service;

import com.ssafy.happymeal.domain.user.dto.*;
import org.springframework.data.domain.Page;

public interface UserService {

    UserDto getMyProfile(Long userId);

    Page<MyBoardResponseDto> getMyPosts(MyPageCriteria criteria);

    Page<MyCommentResponseDto> getMyComments(MyPageCriteria criteria);

    Page<MyBoardResponseDto> getMyLikes(MyPageCriteria criteria);

    UserDto updateProfile(Long userId, ProfileUpdateRequestDto requestDto);
}
