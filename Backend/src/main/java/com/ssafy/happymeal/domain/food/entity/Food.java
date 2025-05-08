package com.ssafy.happymeal.domain.food.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    private Long foodId;
    private String name;
    private String category;
    private BigDecimal servingSize;
    private String unit;
    private BigDecimal calories;
    private BigDecimal carbs;
    private BigDecimal sugar;
    private BigDecimal protein;
    private BigDecimal fat;
    private String foodCode;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}