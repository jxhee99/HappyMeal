package com.ssafy.happymeal.auth.service;

import com.ssafy.happymeal.auth.dao.AuthDAO;
import com.ssafy.happymeal.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService{

    private final AuthDAO authDao;

    public AuthServiceImpl(AuthDAO authDao) {
        this.authDao = authDao;
    }

    // 회원가입 로직
    @Override
    public void signup(User user) {
        log.info("회원가입 성공");
        authDao.signup(user);
    }
}
