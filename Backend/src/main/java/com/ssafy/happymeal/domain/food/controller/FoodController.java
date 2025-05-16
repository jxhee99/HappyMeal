package com.ssafy.happymeal.domain.food.controller;

import com.ssafy.happymeal.domain.commonDto.PageResponse;
import com.ssafy.happymeal.domain.food.dto.FoodNameSearchCriteria;
import com.ssafy.happymeal.domain.food.dto.FoodPagingSortCriteria;
import com.ssafy.happymeal.domain.food.entity.Food; // Food DTO로 사용
import com.ssafy.happymeal.domain.food.service.FoodService;
import com.ssafy.happymeal.domain.food.service.FoodServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<PageResponse<Food>> searchFoodsByName(
            @Parameter(description = "검색할 음식 이름 키워드", required = true, example = "닭가슴살") @RequestParam String name,
            @Parameter(description = "정렬 기준 (예: name, calories DESC, protein ASC,fat DESC)", example = "name ASC") @RequestParam(defaultValue = "name ASC") String sortBy,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 당 아이템 수", example = "10") @RequestParam(defaultValue = "10") int size) {
        log.info("음식 정보 검색 요청: name={}, sortBy={}, page={}, size={}", name, sortBy, page, size);

        FoodNameSearchCriteria criteria = new FoodNameSearchCriteria(name, sortBy, page, size);
        Page<Food> foodPage = foodService.searchFoodsByName(criteria);

        if (foodPage == null || foodPage.getContent().isEmpty()) {
            log.info("검색된 음식 정보 없음: name={}", name);
            return ResponseEntity.noContent().build();
        }

        PageResponse<Food> response = new PageResponse<>(
                foodPage.getContent(), foodPage.getNumber(), foodPage.getSize(), foodPage.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }
    /**
     * 모든 음식 정보 조회
     * GET /api/v1/foods
     * 접근 권한: ALL (일반적으로 모든 사용자 또는 인증된 사용자)
     */
    @GetMapping
    public ResponseEntity<PageResponse<Food>> getAllFoods(
            @Parameter(description = "정렬 기준 (예: name, calories DESC)", example = "name ASC") @RequestParam(defaultValue = "name ASC") String sortBy,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 당 아이템 수", example = "10") @RequestParam(defaultValue = "10") int size) {
        log.info("모든 음식 정보 조회 요청: sortBy={}, page={}, size={}", sortBy, page, size);

        FoodPagingSortCriteria criteria = new FoodPagingSortCriteria(sortBy, page, size);
        Page<Food> foodPage = foodService.getAllFoods(criteria);

        if (foodPage == null || foodPage.getContent().isEmpty()) {
            log.info("등록된 음식 정보 없음");
            return ResponseEntity.noContent().build();
        }
        PageResponse<Food> response = new PageResponse<>(
                foodPage.getContent(), foodPage.getNumber(), foodPage.getSize(), foodPage.getTotalElements()
        );
        return ResponseEntity.ok(response);
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

//    @Operation(summary = "카테고리별 간단 음식 추천", description = "지정된 카테고리(예: diet, healthy, bulk-up, cheating)의 영양소 기준에 맞는 음식 목록을 랜덤으로 3개 추천합니다.")
    @GetMapping("/recommendations")
    public ResponseEntity<List<Food>> getSimplifiedRecommendations(
//            @Parameter(description = "추천받을 음식 카테고리 (diet, healthy, bulk-up, cheating 등)", required = true, example = "diet")
            @RequestParam String category) {
        log.info("카테고리별 간단 음식 추천 요청: category={}", category);

        if (category == null || category.trim().isEmpty()) {
            log.warn("추천 카테고리 파라미터가 비어있거나 null입니다.");
            // return ResponseEntity.badRequest().body(null); // 400 Bad Request 와 함께 메시지 전달 가능
            return ResponseEntity.badRequest().build(); // 간단히 400 상태 코드만 반환
        }

        List<Food> recommendations = foodService.getRecommendedFoods(category);

        if (recommendations == null || recommendations.isEmpty()) {
            log.info("카테고리 '{}'에 대한 추천 음식 없음", category);
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        log.info("카테고리 '{}' 추천 음식 조회 완료: {} 건", category, recommendations.size());
        return ResponseEntity.ok(recommendations);
    }




}