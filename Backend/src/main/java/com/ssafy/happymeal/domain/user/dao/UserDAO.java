package com.ssafy.happymeal.domain.user.dao;

import com.ssafy.happymeal.domain.user.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface UserDAO {

    // 로그인을 위한 User google 아이디를 가져오기
    @Select("select * " +
            "from User " +
            "where google_id=#{googleId}")
    Optional<User> findByGoogleId(String googleId);

    // User 조회
    @Select("select * " +
            "from User " +
            "where user_id=#{userId}"
    )
    Optional<User> findById(Long userId);

    // User 정보 저장
    @Insert("insert into User(google_id, email, nickname, role, profile_image_url, create_at)"
            + " values(#{googleId}, #{email}, #{nickname}, #{role}, #{profileImageUrl}, NOW())"
    )
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    int save(User user);

    // User 정보 수정
    @Update("update User"
            + " set nickname=#{nickname}, profile_image_url=#{profileImageUrl}"
            + " where user_id=#{userId}"
    )
    int update(User user);
}
