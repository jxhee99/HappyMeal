package com.ssafy.happymeal.domain.food.controller;

import com.ssafy.happymeal.domain.food.entity.Food; // Food DTO로 사용
import com.ssafy.happymeal.domain.food.service.FoodService;
import com.ssafy.happymeal.domain.food.service.FoodServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.access.prepost.PreAuthorize; // Spring Security 사용 시 권한 관리

import java.util.List;

@Slf4j // 로깅을 위한 Lombok 어노테이션
@RestController
@RequestMapping("/api/foods") // API 기본 경로 설정
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
public class FoodController {

    private final FoodService foodService; // FoodService 인터페이스 타입으로 주입

    /**
     * 음식 정보 검색 (이름으로 검색)
     * GET /api/v1/foods/search?name={음식이름}
     * 접근 권한: ALL (요구사항에 명시됨)
     */
    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFoodsByName(@RequestParam String name) {
        log.info("음식 정보 검색 요청: name={}", name);
        List<Food> foods = foodService.searchFoodsByName(name);
        if (foods.isEmpty()) {
            log.info("검색된 음식 정보 없음: name={}", name);
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        log.info("음식 정보 검색 완료: {} 건", foods.size());
        return ResponseEntity.ok(foods); // 200 OK
    }

    /**
     * 모든 음식 정보 조회
     * GET /api/v1/foods
     * 접근 권한: ALL (일반적으로 모든 사용자 또는 인증된 사용자)
     */
    @GetMapping
    public ResponseEntity<List<Food>> getAllFoods() {
        log.info("모든 음식 정보 조회 요청");
        List<Food> foods = foodService.getAllFoods();
        log.info("모든 음식 정보 조회 완료: {} 건", foods.size());
        return ResponseEntity.ok(foods); // 200 OK
    }

    /**
     * 특정 음식 정보 조회 (ID 기반)
     * GET /api/v1/foods/{foodId}
     * 접근 권한: ALL
     */
    @GetMapping("/{foodId}")
    public ResponseEntity<Food> getFoodById(@PathVariable Long foodId) {
        log.info("특정 음식 정보 조회 요청: foodId={}", foodId);
        // EntityNotFoundException은 서비스에서 발생하며,
        // @ControllerAdvice를 사용한 전역 예외 처리기에서 404로 변환하는 것이 일반적입니다.
        // 여기서는 명시적으로 처리하지 않고 예외가 전파되도록 두거나, 필요시 try-catch를 사용할 수 있습니다.
        // 전역 예외 처리기가 없다면 Spring Boot 기본 에러 핸들러가 처리합니다.
        Food food = foodService.getFoodById(foodId);
        log.info("특정 음식 정보 조회 완료: foodId={}", foodId);
        return ResponseEntity.ok(food); // 200 OK
    }



}