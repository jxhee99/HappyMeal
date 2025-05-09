package com.ssafy.happymeal.domain.meallog.dao;

import com.ssafy.happymeal.domain.meallog.dto.MealLogResponseDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogStatsDto;
import com.ssafy.happymeal.domain.meallog.entity.MealLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MealLogDAO {

    @Select("Select * " +
            "From MealLog " +
            "Where log_id=#{logId}"
    )
    Optional<MealLog> findById(Long logId);

    @Insert("Insert into MealLog(user_id, food_id, meal_date, meal_type, quantity, img_url, create_at)" +
            " Values(#{userId}, #{foodId}, #{mealDate}, #{mealType}, #{quantity}, #{imgUrl}, NOW())")
    void insertMealLog(MealLog mealLog);

    @Select("Select m.log_id AS logId, f.name AS foodName, f.food_id AS foodId, img_url AS imgUrl, " +
            "m.meal_type AS mealType, m.quantity, " +
            "f.calories*m.quantity/100 AS calories, f.carbs*m.quantity/100 AS carbs, f.sugar*m.quantity/100 AS sugar, " +
            "f.protein*m.quantity/100 AS protein, f.fat*m.quantity/100 AS fat " + // SELECT 컬럼과 DTO 필드가 정확히 일치해야 하기 때문에 Alias를 사용
            "From MealLog m " +
            "Join Food f ON m.food_id = f.food_id " +
            "Where m.user_id = #{userId}")
    List<MealLogResponseDto> getAllMealLogs(Long userId);

    @Select("Select m.log_id AS logId, f.name AS foodName, f.food_id AS foodId, img_url AS imgUrl, " +
            "m.meal_type AS mealType, m.quantity, " +
            "f.calories*m.quantity/100 AS calories, f.carbs*m.quantity/100 AS carbs, f.sugar*m.quantity/100 AS sugar, " +
            "f.protein*m.quantity/100 AS protein, f.fat*m.quantity/100 AS fat " + // SELECT 컬럼과 DTO 필드가 정확히 일치해야 하기 때문에 Alias를 사용
            "From MealLog m " +
            "Join Food f ON m.food_id = f.food_id " +
            "Where m.user_id = #{userId} AND m.meal_date = #{mealDate}")
    // List<MealLogDto> findMealLogsByUserAndDate(Long userId, LocalDate mealDate);
    // MyBatis는 순서 기반 바인딩을 하다가 혼동할 수 있음 : @Param 사용 (⭕️필수는 아님)
    List<MealLogResponseDto> findByUserAndDate(@Param("userId") Long userId, @Param("mealDate") LocalDate mealDate);

    @Delete("Delete From MealLog " +
            "Where user_id = #{userId} AND log_id = #{logId}")
    void deleteMealLog(Long userId, Long logId);


    @Select("Select sum(f.calories*m.quantity/100) AS totalCalories, sum(f.carbs*m.quantity/100) AS totalCarbs, " +
            "sum(f.sugar*m.quantity/100) AS totalSugar, sum(f.protein*m.quantity/100) AS totalProtein, sum(f.fat*m.quantity/100) AS totalFat " +
            "From MealLog m " +
            "Join Food f ON m.food_id = f.food_id " +
            "Where m.user_id=#{userId} AND m.meal_date=#{mealDate}")
    MealLogStatsDto getDailyMealStats(@Param("userId") Long userId, @Param("mealDate") LocalDate mealDate);

}
