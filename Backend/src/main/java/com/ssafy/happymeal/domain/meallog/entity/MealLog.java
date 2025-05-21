package com.ssafy.happymeal.domain.meallog.entity;

import com.ssafy.happymeal.domain.meallog.dto.MealType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING) // enum 값을 DB에 문자열로 저장
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;
    private BigDecimal quantity;
    private String imgUrl;
    private Timestamp createAt;
}
