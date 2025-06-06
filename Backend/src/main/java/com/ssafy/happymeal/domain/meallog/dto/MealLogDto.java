package com.ssafy.happymeal.domain.meallog.dto;

import com.ssafy.happymeal.domain.meallog.entity.MealLog;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 식단 기록 저장용 DTO
public class MealLogDto {
    private String mealDate; // 타입 : LocalDate
    @NotNull(message="식사유형을 선택해주세요")
    private MealType mealType;
    private Long foodId;
    private BigDecimal quantity;
    private String imgUrl;

    /* 사용자 식단 기록 요청 -> DB 저장용 객체 변환 */
    // 사용자가 직접 넣지 않는 값은 toEntity 파라미터로 받음
//    public MealLog toEntity(Long userId) {
//        MealLog mealLog = new MealLog();
//
//        mealLog.setUserId(userId);
//        mealLog.setFoodId(this.foodId);
//        mealLog.setMealDate(LocalDate.parse(this.mealDate));
//        mealLog.setMealType(this.mealType.toUpperCase());
//        mealLog.setQuantity(this.quantity);
//        mealLog.setImgUrl(this.imgUrl);
//
//        return mealLog;
//    }
}
