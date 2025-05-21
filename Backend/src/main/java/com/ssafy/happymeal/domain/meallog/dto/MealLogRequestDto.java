package com.ssafy.happymeal.domain.meallog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class MealLogRequestDto {

    @NotNull(message = "음식 ID를 입력해주세요.")
    private Long foodId;

    @NotNull(message = "수량을 입력해주세요.")
    @DecimalMin(value = "0.0", inclusive = false, message = "수량은 0보다 커야 합니다.")
    private BigDecimal quantity;

    @NotNull(message = "식사 날짜를 입력해주세요.")
    @PastOrPresent(message = "식사 날짜는 오늘 또는 과거여야 합니다.") // 오늘 또는 과거 날짜만 허용
    private LocalDate mealDate;

    @NotNull(message = "식사 유형을 선택해주세요.")
    private MealType mealType;

    private String imgUrl;

}
