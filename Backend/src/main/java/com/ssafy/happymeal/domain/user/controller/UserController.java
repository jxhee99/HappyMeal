package com.ssafy.happymeal.domain.user.controller;

import com.ssafy.happymeal.domain.commonDto.PageResponse;
import com.ssafy.happymeal.domain.user.dto.*;
import com.ssafy.happymeal.domain.user.entity.User;
import com.ssafy.happymeal.domain.user.service.UserService;
import com.ssafy.happymeal.domain.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/mypages")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* 마이페이지 사용자 정보 조회
     * GET api/mypages/profile
     *  접근 권한: USER */
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        UserDto userInfo = userService.getMyProfile(userId);
        log.info("사용자 정보 요청 아이디 : userId={}",userId);
        return ResponseEntity.ok(userInfo);
    }

    /* 본인이 작성한 게시글 조회
     * GET api/mypages/posts
     *  접근 권한: USER */
    @GetMapping("/posts")
    public ResponseEntity<PageResponse<MyBoardResponseDto>> getMyPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = Long.parseLong(userDetails.getUsername());
        MyPageCriteria criteria = new MyPageCriteria(userId, sortBy, page, size);
        Page<MyBoardResponseDto> userPage = userService.getMyPosts(criteria);

        if(userPage.getContent().isEmpty()) {
            log.info("사용자가 작성한 게시글 없음 userId={}",userId);
            return ResponseEntity.noContent().build();
        }

        PageResponse<MyBoardResponseDto> response = new PageResponse<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements()
        );
        return ResponseEntity.ok(response);

    }


    /* 본인이 작성한 댓글 조회
     * GET api/mypages/comments
     *  접근 권한: USER */
    @GetMapping("/comments")
    public ResponseEntity<PageResponse<MyCommentResponseDto>> getMyComments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = Long.parseLong(userDetails.getUsername());

        MyPageCriteria criteria = new MyPageCriteria(userId, sortBy, page, size);
        Page<MyCommentResponseDto> userPage = userService.getMyComments(criteria);

        if(userPage.getContent().isEmpty()) {
            log.info("사용자가 작성한 댓글 없음 userId={}",userId);
            return ResponseEntity.noContent().build();
        }

        PageResponse<MyCommentResponseDto> response = new PageResponse<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    /* 좋아요한 게시글 조회
     * GET api/mypages/likes
     *  접근 권한: USER */
    @GetMapping("/likes")
    public ResponseEntity<PageResponse<MyBoardResponseDto>> getMyLikes(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = Long.parseLong(userDetails.getUsername());
        MyPageCriteria criteria = new MyPageCriteria(userId, sortBy, page,size);

        Page<MyBoardResponseDto> userPage = userService.getMyLikes(criteria);

        if(userPage.getContent().isEmpty()) {
            log.info("사용자가 좋아요 한 게시글 없음 userId={}",userId);
            return ResponseEntity.noContent().build();
        }

        PageResponse<MyBoardResponseDto> response = new PageResponse<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    /* 마이페이지 사용자 정보(닉네임, 이미지) 수정
     * PUT api/mypages/profile
     *  접근 권한: USER */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            // ProfileUpdateRequestDto에 정의한 유효성 검사가 자동으로 적용되도록 @Valid 추가
            @Valid @RequestBody ProfileUpdateRequestDto requestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("개인 정보 업데이트 요청 아이디 userId={}",userId);
        UserDto userInfo = userService.updateProfile(userId, requestDto);
        return ResponseEntity.ok(userInfo);
    }

}
