package com.ssafy.happymeal.domain.meallog.entity;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLog {

    private Long logId;
    private Long userId; // FK - User 테이블 참조
    private Long foodId; // FK - Food 테이블 참조
    private LocalDate mealDate;
    private String mealType;
    private BigDecimal quantity;
    private String imgUrl;
    private Timestamp createAt;
}
