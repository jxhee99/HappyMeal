package com.ssafy.happymeal.domain.meallog.service;

import com.ssafy.happymeal.domain.meallog.dto.MealLogDto;

public interface MealLogService {

    void addMealLog(Long userId, MealLogDto mealLogDto);
}
