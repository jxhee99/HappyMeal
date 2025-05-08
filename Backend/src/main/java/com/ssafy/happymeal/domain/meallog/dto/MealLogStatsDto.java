package com.ssafy.happymeal.domain.meallog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
// 식단 통계 조회 DTO
public class MealLogStatsDto {
    private BigDecimal totalCalories;
    private BigDecimal totalCarbs;
    private BigDecimal totalSugar;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;

}
