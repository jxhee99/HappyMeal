package com.ssafy.happymeal.auth.dao;

import com.ssafy.happymeal.domain.user.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface AuthDAO {

    @Insert("Insert Into User(id, password, nickname) Values(#{id}, #{password}, #{nickname})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // DB에서 자동 생성된 PK(user_id)가 자동으로 넣어짐
    public void signup(User user);
}
