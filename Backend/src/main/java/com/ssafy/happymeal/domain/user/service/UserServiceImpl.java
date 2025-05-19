package com.ssafy.happymeal.domain.user.service;

import com.ssafy.happymeal.domain.user.dao.UserDAO;
import com.ssafy.happymeal.domain.user.dto.MyBoardResponseDto;
import com.ssafy.happymeal.domain.user.dto.MyCommentResponseDto;
import com.ssafy.happymeal.domain.user.dto.MyPageCriteria;
import com.ssafy.happymeal.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserDAO userDAO;

    // 사용자 정보 목록 조회
    @Override
    public UserDto getMyProfile(Long userId) {
        return userDAO.getMyProfile(userId);
    }

    // 사용자가 작성한 게시글 목록 조회
    @Override
    public Page<MyBoardResponseDto> getMyPosts(MyPageCriteria criteria) {
        List<MyBoardResponseDto> myPosts = userDAO.getMyPosts(criteria);
        long totalElements = userDAO.countMyPosts(criteria);
        return new PageImpl<>(myPosts, PageRequest.of(criteria.getPage(), criteria.getSize()),totalElements);
    }

    // 사용자가 작성한 댓글 목록 조회
    @Override
    public Page<MyCommentResponseDto> getMyComments(MyPageCriteria criteria) {
        List<MyCommentResponseDto> myComments = userDAO.getMyComments(criteria);
        long totalElements = userDAO.countMyComments(criteria);
        return new PageImpl<>(myComments, PageRequest.of(criteria.getPage(), criteria.getSize()), totalElements);
    }

    // 사용자가 좋아요 한 게시글 목록 조회
    @Override
    public Page<MyBoardResponseDto> getMyLikes(MyPageCriteria criteria) {
        List<MyBoardResponseDto> myLikes = userDAO.getMyLikes(criteria);
        Long totalElements = userDAO.countMyLikes(criteria);
        return new PageImpl<>(myLikes, PageRequest.of(criteria.getPage(), criteria.getSize()),totalElements);
    }
}
