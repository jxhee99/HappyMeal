package com.ssafy.happymeal.domain.user.controller;

import com.ssafy.happymeal.domain.user.service.UserService;
import com.ssafy.happymeal.domain.user.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/mypages")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* 마이페이지 사용자 정보 조회
     * GET api/mypages/profile
     *  접근 권한: USER */

    /* 본인이 작성한 게시글 조회
     * GET api/mypages/posts
     *  접근 권한: USER */

    /* 본인이 작성한 댓글 조회
     * GET api/mypages/comments
     *  접근 권한: USER */

    /* 좋아요한 게시글 조회
     * GET api/mypages/likes
     *  접근 권한: USER */


}
