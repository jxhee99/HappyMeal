package com.ssafy.happymeal.domain.food.dto; // 실제 DTO 패키지 경로

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodNameSearchCriteria extends FoodPagingSortCriteria {
    private String name; // 검색할 음식 이름

    public FoodNameSearchCriteria(String name, String sortBy, int page, int size) {
        super(sortBy, page, size);
        this.name = name;
    }
}