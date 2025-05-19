package com.ssafy.happymeal.domain.user.service;

import com.ssafy.happymeal.domain.user.dto.MyBoardResponseDto;
import com.ssafy.happymeal.domain.user.dto.MyCommentResponseDto;
import com.ssafy.happymeal.domain.user.dto.MyPageCriteria;
import com.ssafy.happymeal.domain.user.dto.UserDto;
import org.springframework.data.domain.Page;

public interface UserService {

    UserDto getMyProfile(Long userId);

    Page<MyBoardResponseDto> getMyPosts(MyPageCriteria criteria);

    Page<MyCommentResponseDto> getMyComments(MyPageCriteria criteria);

    Page<MyBoardResponseDto> getMyLikes(MyPageCriteria criteria);
}
