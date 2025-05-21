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
    Optional<FoodRequest> findById(@Param("foodRequestId") Long foodRequestId);

    @Select("SELECT food_request_id, user_id, name, category, serving_size, unit, calories, carbs, sugar, protein, fat, is_registered, create_at " +
            "FROM FoodRequest WHERE user_id = #{userId} ORDER BY create_at DESC")
    List<FoodRequest> findByUserId(@Param("userId") Long userId);

    @Select("SELECT food_request_id, user_id, name, category, serving_size, unit, calories, carbs, sugar, protein, fat, is_registered, create_at " +
            "FROM FoodRequest ORDER BY create_at DESC")
    List<FoodRequest> findAll();

    @Update("UPDATE FoodRequest SET is_registered = #{status} WHERE food_request_id = #{foodRequestId}")
    int updateStatus(@Param("foodRequestId") Long foodRequestId, @Param("status") FoodRequestStatus status);
}