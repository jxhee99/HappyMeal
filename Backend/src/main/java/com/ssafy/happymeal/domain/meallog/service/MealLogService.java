package com.ssafy.happymeal.domain.meallog.service;

import com.ssafy.happymeal.domain.meallog.dto.MealLogDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogResponseDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogStatsDto;
import org.apache.ibatis.javassist.NotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface MealLogService {

    void addMealLog(Long userId, MealLogDto mealLogDto);

    List<MealLogResponseDto> findByUserAndDate(Long userId, LocalDate mealDate);

    void deleteMealLog(Long userId, Long logId) throws NotFoundException;

    MealLogStatsDto getDailyMealStats(Long userId, LocalDate mealDate);

    List<MealLogResponseDto> getAllMealLogs(Long userId);

    MealLogResponseDto getDetailMealLog(Long userId, Long logId) throws NotFoundException;

    List<MealLogStatsDto> getWeeklyMealLogStats(Long userId, LocalDate endDate);
}
