package com.ssafy.happymeal.domain.meallog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
// 식단 통계 조회 DTO
public class MealLogStatsDto {
    private LocalDate date;
    private BigDecimal totalCalories;
    private BigDecimal totalCarbs;
    private BigDecimal totalSugar;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;


    // 기록이 없는 요청을 위한 생성자
    public static MealLogStatsDto empty(LocalDate date) {
         return new MealLogStatsDto(date, BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0));
    }
}
