package com.ssafy.happymeal.domain.user.service;

import com.ssafy.happymeal.domain.user.dao.UserDAO;
import com.ssafy.happymeal.domain.user.dto.*;
import com.ssafy.happymeal.domain.user.entity.User;
import com.ssafy.happymeal.global.exception.GlobalExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    // 사용자 정보 수정
    @Transactional
    @Override
    public UserDto updateProfile(Long userId, ProfileUpdateRequestDto requestDto) {

        String newNickname = requestDto.getNickname();
        String newProfileImgUrl = requestDto.getProfileImgUrl();
        // 1. 닉네임 중복 검사
        Optional<User> existingUser = userDAO.findByNickname(newNickname);

        if(existingUser.isPresent() && !existingUser.get().getUserId().equals(userId)) {
            // 새로운 닉네임을 가진 사용자가 존재하고, 그 사용자가 현재 사용자가 아니라면 중복임
            throw new GlobalExceptionHandler.DuplicateNicknameException("이미 사용 중인 닉네임입니다: "+newNickname);
        }

        // 2. 현재 사용자의 정보 조회
        User currentUser = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userId: "+userId));

        // 3. 사용자 정보 업데이트
        boolean changed = false;
        if(newNickname != null && !newNickname.equals(currentUser.getNickname())) {
            currentUser.setNickname(newNickname);
            changed = true;
        }
        if(newProfileImgUrl != null && !newProfileImgUrl.equals(currentUser.getProfileImageUrl())) {
            currentUser.setProfileImageUrl(newProfileImgUrl);
            changed = true;
        }

        // 4. 실제 변경된 사항이 있을 때만 DB 업데이트
        if(changed) {
            try {
                userDAO.update(currentUser);
                log.info("사용자 프로필이 업데이트 완료 userId={}",userId);
            } catch (DataAccessException e) {
                log.error("DB 업데이트 중 오류 발생 userId={}", userId);
                throw new RuntimeException("프로필 업데이트 중 데이터베이스 오류 발생", e);
            }
        }
        else {
            log.info("프로필 업데이트 요청 : 변경된 내용이 없음. userId={}",userId);
        }

        // 변경이 없더라도 현재 사용자 정보를 DTO로 변환하여 반환 (성공으로 간주)
        return convertToUserDto(currentUser);
    }

    // User 엔티티를 UserDto로 변환하는 메서드
    private UserDto convertToUserDto(User user) {
        if(user==null) return null;
        return UserDto.builder()
                .userId(user.getUserId())
                .nickName(user.getNickname())
                .email(user.getEmail())
                .profileImgUrl(user.getProfileImageUrl())
                .build();
    }
}



