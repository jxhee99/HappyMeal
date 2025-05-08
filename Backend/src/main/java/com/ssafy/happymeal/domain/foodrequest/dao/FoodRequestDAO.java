package com.ssafy.happymeal.domain.foodrequest.dao;

import com.ssafy.happymeal.domain.foodrequest.constant.FoodRequestStatus;
import com.ssafy.happymeal.domain.foodrequest.entity.FoodRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FoodRequestDAO {

    @Insert("INSERT INTO FoodRequest (user_id, name, category, serving_size, unit, calories, carbs, sugar, protein, fat, is_registered, create_at) " +
            "VALUES (#{userId}, #{name}, #{category}, #{servingSize}, #{unit}, #{calories}, #{carbs}, #{sugar}, #{protein}, #{fat}, #{isRegistered}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "foodRequestId", keyColumn = "food_request_id")
    int save(FoodRequest foodRequest);

    @Select("SELECT food_request_id, user_id, name, category, serving_size, unit, calories, carbs, sugar, protein, fat, is_registered, create_at " +
            "FROM FoodRequest WHERE food_request_id = #{foodRequestId}")
    // MyBatis는 EnumTypeHandler를 통해 DB의 문자열을 Java Enum으로 자동 변환 시도
    Optional<FoodRequest> findById(@Param("foodRequestId") Long foodRequestId);

    @Select("SELECT food_request_id, user_id, name, category, serving_size, unit, calories, carbs, sugar, protein, fat, is_registered, create_at " +
            "FROM FoodRequest WHERE user_id = #{userId} ORDER BY create_at DESC")
    List<FoodRequest> findByUserId(@Param("userId") Long userId);

    @Select("SELECT food_request_id, user_id, name, category, serving_size, unit, calories, carbs, sugar, protein, fat, is_registered, create_at " +
            "FROM FoodRequest ORDER BY create_at DESC")
    List<FoodRequest> findAll();

    @Update("UPDATE FoodRequest SET is_registered = #{isRegistered} " +
            // ", admin_comment = #{adminComment} " + // 관리자 코멘트 추가 시
            "WHERE food_request_id = #{foodRequestId}")
    int updateStatus(@Param("foodRequestId") Long foodRequestId,
                     @Param("isRegistered") FoodRequestStatus isRegistered
                     /* @Param("adminComment") String adminComment */); // 관리자 코멘트 추가 시
}