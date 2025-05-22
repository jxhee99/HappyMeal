package com.ssafy.happymeal.domain.meallog.service;

import com.ssafy.happymeal.domain.meallog.dto.*;
import com.ssafy.happymeal.domain.meallog.entity.MealLog;
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

    MealLogUpdateResponseDto updateMealLog(Long userId, Long logId, MealLogRequestDto requestDto);
}
