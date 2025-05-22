package com.ssafy.happymeal.domain.meallog.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MealLogUpdateResponseDto {
    private Long logId;
    private Long foodId;
    private BigDecimal quantity;
    private MealType mealType;
    private String imgUrl;
}
