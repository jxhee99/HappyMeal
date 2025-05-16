package com.ssafy.happymeal.domain.food.service;

import com.ssafy.happymeal.domain.food.dao.FoodDAO;
import com.ssafy.happymeal.domain.food.dto.FoodNameSearchCriteria;
import com.ssafy.happymeal.domain.food.dto.FoodPagingSortCriteria;
import com.ssafy.happymeal.domain.food.entity.Food;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page; // Spring Data Page
import org.springframework.data.domain.PageImpl; // Spring Data Page 구현체
import org.springframework.data.domain.PageRequest; // 페이징 정보 객체
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // StringUtils 사용

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set; // 허용된 정렬 필드 검증용

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodServiceImpl implements FoodService {

    private final FoodDAO foodDAO;
    private static final int MAX_PAGE_SIZE = 100; // 최대 페이지 크기 제한

    // 허용된 정렬 필드명 (SQL Injection 방지용 화이트리스트)
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "calories", "protein", "fat", "sugar", "carbs", "category", "create_at", "update_at");

    private String buildOrderByClause(String sortByWithDirection) {
        if (!StringUtils.hasText(sortByWithDirection)) {
            return "name ASC"; // 기본 정렬
        }
        // 예: "name,calories DESC" -> "name ASC, calories DESC"
        // sortBy=name:asc,calories:desc
        StringBuilder orderByClause = new StringBuilder();
        String[] sortParams = sortByWithDirection.split(",");
        for (String sortParam : sortParams) {
            String[] parts = sortParam.trim().split("\\s+"); // 공백 기준 분리 또는 ":" 기준 분리
            String field = parts[0].toLowerCase();
            String direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1])) ? "DESC" : "ASC";

            if (ALLOWED_SORT_FIELDS.contains(field)) {
                // DB 컬럼명으로 변환 (필요시)
                // 예: create_at -> createdAt, 여기서는 mapUnderscoreToCamelCase가 DB->Java, Java->DB는 아니므로 직접 사용
                // DB 컬럼명이 스네이크 케이스라면 필드명도 스네이크로 변환해야 함.
                // 여기서는 params.orderByClause에 Java 필드명 기준(카멜)으로 넣고, DB가 알아서 매핑하도록 하거나
                // 아니면 DB 컬럼명 기준으로만 정렬 가능하도록 명시. 여기서는 DB 컬럼명 기준 가정
                String dbColumn = field; // 간단히 Java 필드명=DB 컬럼명(스네이크 변환 후) 가정
                // 만약 Java 필드명(카멜)을 DB 컬럼명(스네이크)으로 변환해야 한다면 로직 추가
                // 예를 들어 field "createdAt" -> DB "create_at"

                if (orderByClause.length() > 0) {
                    orderByClause.append(", ");
                }
                orderByClause.append(dbColumn).append(" ").append(direction);
            } else {
                log.warn("Invalid sort field provided: {}. Ignoring.", field);
            }
        }
        return orderByClause.length() > 0 ? orderByClause.toString() : "name ASC"; // 유효한 정렬 없으면 기본값
    }


    @Override
    public Page<Food> searchFoodsByName(FoodNameSearchCriteria criteria) {
        log.info("음식 이름 검색 서비스: name={}, sortBy={}, page={}, size={}",
                criteria.getName(), criteria.getSortBy(), criteria.getPage(), criteria.getSize());

        int pageSize = Math.min(criteria.getSize(), MAX_PAGE_SIZE); // 페이지 크기 제한
        int offset = criteria.getPage() * pageSize;

        Map<String, Object> params = new HashMap<>();
        params.put("name", criteria.getName());
        params.put("orderByClause", buildOrderByClause(criteria.getSortBy()));
        params.put("limit", pageSize);
        params.put("offset", offset);

        List<Food> foods = foodDAO.searchByNamePaginatedAndSorted(params);
        long totalElements = foodDAO.countSearchByName(criteria.getName());

        return new PageImpl<>(foods, PageRequest.of(criteria.getPage(), pageSize), totalElements);
    }

    @Override
    public Page<Food> getAllFoods(FoodPagingSortCriteria criteria) {
        log.info("모든 음식 조회 서비스: sortBy={}, page={}, size={}",
                criteria.getSortBy(), criteria.getPage(), criteria.getSize());

        int pageSize = Math.min(criteria.getSize(), MAX_PAGE_SIZE);
        int offset = criteria.getPage() * pageSize;

        Map<String, Object> params = new HashMap<>();
        params.put("orderByClause", buildOrderByClause(criteria.getSortBy()));
        params.put("limit", pageSize);
        params.put("offset", offset);

        List<Food> foods = foodDAO.findAllPaginatedAndSorted(params);
        long totalElements = foodDAO.countAll();

        return new PageImpl<>(foods, PageRequest.of(criteria.getPage(), pageSize), totalElements);
    }

    // --- 기존 단건 조회, CUD, 추천 API용 서비스 메소드는 유지 ---
    @Override
    public Food getFoodById(Long foodId) {
        return foodDAO.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food not found with id: " + foodId));
    }

    @Override
    @Transactional // 쓰기 작업
    public Food addFood(Food food) {
        foodDAO.save(food);
        return food; // foodId가 설정된 객체
    }

    @Override
    @Transactional // 쓰기 작업
    public Food updateFood(Long foodId, Food foodDetailsToUpdate) {
        foodDAO.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food not found with id: " + foodId + ". Cannot update."));
        foodDetailsToUpdate.setFoodId(foodId); // ID 설정 확실히
        int affectedRows = foodDAO.update(foodDetailsToUpdate);
        if (affectedRows == 0) throw new RuntimeException("Food update failed for id: " + foodId);
        return foodDAO.findById(foodId).orElseThrow(); // 업데이트된 정보 다시 조회
    }

    @Override
    @Transactional // 쓰기 작업
    public void deleteFood(Long foodId) {
        foodDAO.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food not found with id: " + foodId + ". Cannot delete."));
        foodDAO.delete(foodId);
    }

    @Override
    public List<Food> getRecommendedFoods(String categoryName) {
        log.info("Fetching simplified recommendations for category: {}", categoryName);
        Map<String, Object> params = new HashMap<>();
        // 카테고리별 영양소 기준치 설정 ... (이전 답변과 동일)
        switch (categoryName.toLowerCase()) {
            case "diet":
                params.put("maxCalories", new BigDecimal("150"));
                params.put("minProtein", new BigDecimal("10"));
                params.put("maxFat", new BigDecimal("10").subtract(BigDecimal.valueOf(0.01)));
                params.put("maxSugar", new BigDecimal("5").subtract(BigDecimal.valueOf(0.01)));
                break;
            case "healthy":
                params.put("minCalories", new BigDecimal("100"));
                params.put("maxCalories", new BigDecimal("250"));
                params.put("minProtein", new BigDecimal("10"));
                params.put("minFat", new BigDecimal("5"));
                params.put("maxFat", new BigDecimal("15"));
                params.put("maxSugar", new BigDecimal("10").subtract(BigDecimal.valueOf(0.01)));
                break;
            case "bulk-up":
                params.put("minCalories", new BigDecimal("200"));
                params.put("minProtein", new BigDecimal("15"));
                params.put("minFat", new BigDecimal("10"));
                break;
            case "cheating":
                params.put("minCalories", new BigDecimal("400"));
                break;
            default:
                log.warn("Unknown category for simplified recommendation: {}.", categoryName);
                break;
        }
        return foodDAO.findRecommendFoods(params); // DAO는 이미 랜덤 3개 반환
    }
}