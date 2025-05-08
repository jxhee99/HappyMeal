package com.ssafy.happymeal.domain.foodrequest.constant;

public enum FoodRequestStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거절됨");

    private final String description;

    FoodRequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}