package com.ssafy.happymeal.domain.meallog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
// 특정 날짜 식단 기록 조회용
// 식단 기록 상세 조회용
public class MealLogResponseDto {
    private Long logId;
    private String foodName; // 조회 시 음식 반환을 위해 사용
    private Long foodId;
    private String imgUrl;
    private String mealType;
    private BigDecimal quantity;
    private BigDecimal calories;
    private BigDecimal carbs;
    private BigDecimal sugar;
    private BigDecimal protein;
    private BigDecimal fat;

}