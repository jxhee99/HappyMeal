package com.ssafy.happymeal.auth.controller;

import com.ssafy.happymeal.auth.service.AuthService;
import com.ssafy.happymeal.domain.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    // 회원가입
    @PostMapping("/auth/signup")
    public ResponseEntity<Void> signup(@RequestBody User user) {
        authService.signup(user);
        return new ResponseEntity<Void>(HttpStatus.CREATED); // 임시 에러 처리
    }


    // 로그인(성공 시 session 생성)

    // 로그아웃(session 무효화)

    // 현재 로그인 상태 확인(session 유효성)

}
