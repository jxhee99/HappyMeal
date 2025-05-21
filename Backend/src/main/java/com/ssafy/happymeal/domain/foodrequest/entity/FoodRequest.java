package com.ssafy.happymeal.domain.foodrequest.entity;

import com.ssafy.happymeal.domain.foodrequest.constant.FoodRequestStatus;
import lombok.*; // Getter, Setter, Builder, NoArgsConstructor, AllArgsConstructor

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FoodRequest {

    private Long foodRequestId; // food_request_id (PK)
    private Long userId;        // user_id (FK)

    private String name;
    private String category;    // nullable

    private BigDecimal servingSize;
    private String unit;

    private BigDecimal calories;
    private BigDecimal carbs;
    private BigDecimal sugar;
    private BigDecimal protein;
    private BigDecimal fat;

    private String imgUrl;      // 추가


    private FoodRequestStatus isRegistered; // ENUM('PENDING', 'APPROVED', 'REJECTED')
    private LocalDateTime createAt;         // create_at (DB default CURRENT_TIMESTAMP)

    // MyBatis는 주로 기본 생성자와 getter/setter를 사용합니다.
    // @Builder 등은 개발 편의를 위해 사용합니다.
    // is_registered 필드는 DB에서는 문자열로 저장되지만, 자바에서는 Enum으로 다룹니다.
    // MyBatis 설정에서 org.apache.ibatis.type.EnumTypeHandler 를 기본 핸들러로 사용하거나,
    // 특정 TypeHandler를 지정하여 Enum <-> DB 문자열 간 변환을 처리합니다.
}