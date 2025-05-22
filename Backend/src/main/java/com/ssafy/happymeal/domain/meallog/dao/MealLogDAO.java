package com.ssafy.happymeal.domain.meallog.dao;

import com.ssafy.happymeal.domain.meallog.dto.MealLogResponseDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogStatsDto;
import com.ssafy.happymeal.domain.meallog.entity.MealLog;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
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

    @Select("Select count(*) " +
            "From MealLog " +
            "Where meal_date=#{mealDate}")
    int findByDate(LocalDate mealDate);

    /* 식단 기록 추가 */
    @Insert("Insert into MealLog(user_id, food_id, meal_date, meal_type, quantity, img_url, create_at)" +
            " Values(#{userId}, #{foodId}, #{mealDate}, #{mealType}, #{quantity}, #{imgUrl}, NOW())")
    void insertMealLog(MealLog mealLog);

    /* 전체 식단 기록 조회 */
    @Select("Select m.log_id AS logId, f.name AS foodName, f.food_id AS foodId, m.img_url AS imgUrl, " +
            "m.meal_type AS mealType, m.quantity, " +
            "f.calories*m.quantity/100 AS calories, f.carbs*m.quantity/100 AS carbs, f.sugar*m.quantity/100 AS sugar, " +
            "f.protein*m.quantity/100 AS protein, f.fat*m.quantity/100 AS fat, " +
            "m.user_id AS userId " + // SELECT 컬럼과 DTO 필드가 정확히 일치해야 하기 때문에 Alias를 사용
            "From MealLog m " +
            "Join Food f ON m.food_id = f.food_id " +
            "Where m.user_id = #{userId}")
    List<MealLogResponseDto> getAllMealLogs(Long userId);

    /* 특정 날짜 식단 기록 조회 */
    @Select("Select m.log_id AS logId, f.name AS foodName, f.food_id AS foodId, m.img_url AS imgUrl, " +
            "m.meal_type AS mealType, m.quantity, " +
            "f.calories*m.quantity/100 AS calories, f.carbs*m.quantity/100 AS carbs, f.sugar*m.quantity/100 AS sugar, " +
            "f.protein*m.quantity/100 AS protein, f.fat*m.quantity/100 AS fat, " +
            "m.user_id AS userId " + // SELECT 컬럼과 DTO 필드가 정확히 일치해야 하기 때문에 Alias를 사용
            "From MealLog m " +
            "Join Food f ON m.food_id = f.food_id " +
            "Where m.user_id = #{userId} AND m.meal_date = #{mealDate}")
    List<MealLogResponseDto> findByUserAndDate(@Param("userId") Long userId, @Param("mealDate") LocalDate mealDate);

    /* 특정 날짜 식단 통계 조회 */
    @Select("Select m.meal_date AS date, sum(f.calories * m.quantity/100) AS totalCalories, sum(f.carbs * m.quantity/100) AS totalCarbs, " +
            "sum(f.sugar * m.quantity/100) AS totalSugar, sum(f.protein * m.quantity/100) AS totalProtein, sum(f.fat * m.quantity/100) AS totalFat " +
            "From MealLog m " +
            "Join Food f ON m.food_id = f.food_id " +
            "Where m.user_id=#{userId} AND m.meal_date=#{mealDate}")
    MealLogStatsDto getDailyMealStats(@Param("userId") Long userId, @Param("mealDate") LocalDate mealDate);

    /* 해당 기간(startDate ~ endDate)의 일별 식단 통계 조회 */
    @Select("Select m.meal_date AS date, sum(f.calories*m.quantity/100) AS totalCalories, sum(f.carbs*m.quantity/100) AS totalCarbs, " +
            "sum(f.sugar*m.quantity/100) AS totalSugar, sum(f.protein*m.quantity/100) AS totalProtein, sum(f.fat*m.quantity/100) AS totalFat " +
            "From MealLog m " +
            "Join Food f ON m.food_id = f.food_id " +
            "Where m.user_id=#{userId} AND m.meal_date Between #{startDate} And #{endDate}" +
            "Group By m.meal_date " +
            "Order By m.meal_date ASC")
    List<MealLogStatsDto> getDailyStatsForDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /* 식단 기록 상세 조회 */
    @Select("Select m.log_id AS logId, f.name AS foodName, f.food_id AS foodId, m.img_url AS imgUrl, " +
            "m.meal_type AS mealType, m.quantity, " +
            "f.calories*m.quantity/100 AS calories, f.carbs*m.quantity/100 AS carbs, f.sugar*m.quantity/100 AS sugar, " +
            "f.protein*m.quantity/100 AS protein, f.fat*m.quantity/100 AS fat, " +
            "m.user_id AS userId " + // SELECT 컬럼과 DTO 필드가 정확히 일치해야 하기 때문에 Alias를 사용
            "From MealLog m " +
            "Join Food f ON m.food_id = f.food_id " +
            "Where m.user_id = #{userId} AND m.log_id = #{logId}")
    MealLogResponseDto getDetailMealLog(Long userId, Long logId);

    /* 식단 기록 삭제 */
    @Delete("Delete From MealLog " +
            "Where user_id = #{userId} AND log_id = #{logId}")
    void deleteMealLog(Long userId, Long logId);

    /* 식단 기록 수정(meal_date 제외) */
    @Update("update MealLog " +
            "set " +
            "food_id=#{foodId}, " +
            "meal_date=#{mealDate}, " +
            "meal_type=#{mealType}, quantity=#{quantity}, " +
            "img_url=#{imgUrl} " +
            "where user_id=#{userId} AND log_id=#{logId}")
    int updateMealLog(MealLog mealLog);
}
