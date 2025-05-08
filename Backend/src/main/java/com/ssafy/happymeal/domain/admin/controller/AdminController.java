package com.ssafy.happymeal.domain.admin.controller;

import com.ssafy.happymeal.domain.food.entity.Food; // Food DTO로 사용
import com.ssafy.happymeal.domain.food.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // 로깅을 위한 Lombok 어노테이션
@RestController
@RequestMapping("/api/admin") // API 기본 경로 설정
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
public class AdminController {

    private final FoodService foodService; // FoodService 인터페이스 타입으로 주입

    /**
     * 음식 정보 추가
     * POST /api/v1/foods
     * 접근 권한: User, Admin (요구사항에 명시됨 - 실제 권한 처리는 Spring Security 등 필요)
     */
    @PostMapping("/foods")
    // @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Spring Security 사용 시 권한 예시
    public ResponseEntity<Food> addFood(@RequestBody Food foodDto) {
        // TODO: 입력값 검증(@Valid) 로직 추가 권장
        log.info("음식 정보 추가 요청: {}", foodDto);
        Food createdFood = foodService.addFood(foodDto);
        log.info("음식 정보 추가 완료: {}", createdFood);
        return new ResponseEntity<>(createdFood, HttpStatus.CREATED); // 201 Created
    }

    /**
     * 음식 정보 수정
     * PUT /api/v1/foods/{foodId}
     * 접근 권한: User, Admin (요구사항에 명시됨 - 실제 권한 처리는 Spring Security 등 필요)
     */
    @PutMapping("/foods/{foodId}")
    // @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Spring Security 사용 시 권한 예시
    public ResponseEntity<Food> updateFood(@PathVariable Long foodId, @RequestBody Food foodDto) {
        // TODO: 입력값 검증(@Valid) 로직 추가 권장
        log.info("음식 정보 수정 요청: foodId={}, DTO={}", foodId, foodDto);
        Food updatedFood = foodService.updateFood(foodId, foodDto);
        log.info("음식 정보 수정 완료: {}", updatedFood);
        return ResponseEntity.ok(updatedFood); // 200 OK
    }

    /**
     * 음식 정보 삭제
     * DELETE /api/v1/foods/{foodId}
     * 접근 권한: User, Admin (요구사항에 명시됨 - 실제 권한 처리는 Spring Security 등 필요)
     */
    @DeleteMapping("/foods/{foodId}")
    // @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Spring Security 사용 시 권한 예시
    public ResponseEntity<Void> deleteFood(@PathVariable Long foodId) {
        log.info("음식 정보 삭제 요청: foodId={}", foodId);
        foodService.deleteFood(foodId);
        log.info("음식 정보 삭제 완료: foodId={}", foodId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
