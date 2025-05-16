package com.ssafy.happymeal.domain.food.dto; // 실제 DTO 패키지 경로

  import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodPagingSortCriteria {
    private String sortBy; // 정렬 기준 필드명 (예: "name", "calories")
    private int page;      // 요청 페이지 번호 (0부터 시작)
    private int size;      // 페이지 당 아이템 수
    // 필요시 정렬 방향 (ASC, DESC) 필드 추가 가능
    // private String sortDirection;
}