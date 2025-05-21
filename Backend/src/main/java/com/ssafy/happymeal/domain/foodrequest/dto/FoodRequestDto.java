package com.ssafy.happymeal.domain.foodrequest.dto;

import com.ssafy.happymeal.domain.foodrequest.constant.FoodRequestStatus; // 위에서 정의한 Enum
import lombok.*; // Getter, Setter, Builder, NoArgsConstructor, AllArgsConstructor

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FoodRequestDto {

    /**
     * 음식 요청 생성을 위한 DTO (Client -> Server)
     * 사용자가 음식 등록 요청 시 보내는 데이터
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Create {
        // user_id는 JWT 토큰 등에서 인증된 사용자 정보를 가져와 서비스단에서 설정하므로 DTO에는 불필요.
        private String name;        // 요청 음식 이름 (필수)
        private String category;    // 요청 음식 분류 (선택)
        private BigDecimal servingSize; // 1회 제공량 (필수)
        private String unit;        // 단위 (필수)
        private BigDecimal calories;    // 칼로리 (필수)
        private BigDecimal carbs;       // 탄수화물 (필수)
        private BigDecimal sugar;       // 당류 (필수)
        private BigDecimal protein;     // 단백질 (필수)
        private BigDecimal fat;         // 지방 (필수)

        // 이 DTO를 FoodRequest 엔티티로 변환하는 메서드 (서비스 레이어에서 사용)
        // public FoodRequest toEntity(Long userId) {
        //     return FoodRequest.builder()
        //             .userId(userId) // 서비스에서 인증된 사용자 ID를 받아 설정
        //             .name(this.name)
        //             .category(this.category)
        //             .servingSize(this.servingSize)
        //             .unit(this.unit)
        //             .calories(this.calories)
        //             .carbs(this.carbs)
        //             .sugar(this.sugar)
        //             .protein(this.protein)
        //             .fat(this.fat)
        //             .isRegistered(FoodRequestStatus.PENDING) // 생성 시 기본 상태는 PENDING
        //             // create_at은 DB에서 자동 생성되거나, 엔티티에서 @CreationTimestamp로 처리
        //             .build();
        // }
    }

    /**
     * 음식 요청 응답을 위한 DTO (Server -> Client)
     * 음식 요청 조회 시 반환되는 데이터
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Response {
        private Long foodRequestId;
        private Long userId; // 요청한 사용자 ID
        // private String userNickname; // 필요하다면 요청자 닉네임 등 추가 정보 포함 가능
        private String name;
        private String category;
        private BigDecimal servingSize;
        private String unit;
        private BigDecimal calories;
        private BigDecimal carbs;
        private BigDecimal sugar;
        private BigDecimal protein;
        private BigDecimal fat;
        private String imgUrl;      // 추가
        private FoodRequestStatus isRegistered; // Enum 타입으로 상태 표시
        private LocalDateTime createAt;
        // private String adminComment; // 관리자 코멘트가 있다면 추가

        // FoodRequest 엔티티를 이 DTO로 변환하는 정적 메서드 (서비스 레이어에서 사용)
        // public static Response fromEntity(FoodRequest foodRequest) {
        //     return Response.builder()
        //             .foodRequestId(foodRequest.getFoodRequestId())
        //             .userId(foodRequest.getUserId())
        //             .name(foodRequest.getName())
        //             .category(foodRequest.getCategory())
        //             .servingSize(foodRequest.getServingSize())
        //             .unit(foodRequest.getUnit())
        //             .calories(foodRequest.getCalories())
        //             .carbs(foodRequest.getCarbs())
        //             .sugar(foodRequest.getSugar())
        //             .protein(foodRequest.getProtein())
        //             .fat(foodRequest.getFat())
        //             .isRegistered(foodRequest.getIsRegistered())
        //             .createAt(foodRequest.getCreateAt())
        //             .build();
        // }
    }

    /**
     * (선택 사항) 음식 요청 상태 변경을 위한 DTO (관리자가 사용)
     * ADMIN이 음식 요청의 상태(승인/거절)를 변경할 때 사용
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class UpdateStatus {
        private FoodRequestStatus isRegistered;
        // private String adminComment; // 상태 변경 시 관리자 코멘트 추가 가능
    }

}