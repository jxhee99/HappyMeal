package com.ssafy.happymeal.domain.meallog.dao;

import com.ssafy.happymeal.domain.meallog.entity.MealLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MealLogDAO {

    @Insert("insert into MealLog(user_id, food_id, meal_date, meal_type, quantity, img_url, create_at)" +
            " values(#{userId}, #{foodId}, #{mealDate}, #{mealType}, #{quantity}, #{imgUrl}, NOW())")
    void insertMealLog(MealLog mealLog);

}
