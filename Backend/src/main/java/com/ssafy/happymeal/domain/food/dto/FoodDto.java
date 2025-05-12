package com.ssafy.happymeal.domain.food.dto;

import com.ssafy.happymeal.domain.food.entity.Food;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDto {

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
    private String imgUrl;
    private String foodCode;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    // 엔티티를 DTO로 변환하는 정적 메서드 (필요에 따라 사용)
    public static FoodDto fromEntity(Food entity) {
        return FoodDto.builder()
                .foodId(entity.getFoodId())
                .name(entity.getName())
                .category(entity.getCategory())
                .servingSize(entity.getServingSize())
                .unit(entity.getUnit())
                .calories(entity.getCalories())
                .carbs(entity.getCarbs())
                .sugar(entity.getSugar())
                .protein(entity.getProtein())
                .fat(entity.getFat())
                .imgUrl(entity.getImgUrl())
                .foodCode(entity.getFoodCode())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .build();
    }

    // DTO를 엔티티로 변환하는 메서드 (필요에 따라 사용)
    public Food toEntity() {
        Food food = new Food();
        food.setFoodId(this.foodId); // ID는 주로 DB에서 자동 생성되므로, 업데이트 시에만 필요할 수 있음
        food.setName(this.name);
        food.setCategory(this.category);
        food.setServingSize(this.servingSize);
        food.setUnit(this.unit);
        food.setCalories(this.calories);
        food.setCarbs(this.carbs);
        food.setSugar(this.sugar);
        food.setProtein(this.protein);
        food.setFat(this.fat);
        food.setImgUrl(this.imgUrl);
        food.setFoodCode(this.foodCode);
        // createAt, updateAt은 DB에서 자동 관리되므로 여기서 설정하지 않는 것이 일반적입니다.
        return food;
    }
}