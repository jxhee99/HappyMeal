package com.ssafy.happymeal.domain.food.service;

import com.ssafy.happymeal.domain.food.dao.FoodDAO;
import com.ssafy.happymeal.domain.food.dto.FoodNameSearchCriteria;
import com.ssafy.happymeal.domain.food.dto.FoodPagingSortCriteria;
import com.ssafy.happymeal.domain.food.entity.Food;
import com.ssafy.happymeal.util.CacheConstants; // 1-2 단계에서 만든 캐시 상수 클래스
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate; // RedisTemplate import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections; // Collections.emptyList() 사용을 위해 import
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit; // TimeUnit import

@Slf4j
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
@Transactional(readOnly = true) // 클래스 레벨 트랜잭션 (읽기 전용)
public class FoodServiceImpl implements FoodService {

    private final FoodDAO foodDAO; // 기존 의존성
    private final RedisTemplate<String, List<Food>> redisTemplateListFood; // 새로 추가된 의존성 (1-1 단계에서 설정)

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "calories", "protein", "fat", "sugar", "carbs", "category", "create_at", "update_at");

    // 메인 페이지 추천에 사용할 기본 카테고리 (이 값은 스케줄러에서도 동일하게 사용될 수 있습니다)
    private static final String MAIN_PAGE_CATEGORY_1 = "healthy";
    private static final String MAIN_PAGE_CATEGORY_2 = "diet";
    private static final String MAIN_PAGE_CATEGORY_3 = "healthy";
//    private static final String DEFAULT_MAIN_PAGE_CATEGORY = "healthy"; // 예시로 "healthy" 카테고리 사용

    // 캐시 동시 업데이트 방지를 위한 락(lock) 객체
//    private final Object cacheUpdateLock = new Object();


    private String buildOrderByClause(String sortByWithDirection) {
        if (!StringUtils.hasText(sortByWithDirection)) {
            return "name ASC"; // 기본 정렬
        }
        StringBuilder orderByClause = new StringBuilder();
        String[] sortParams = sortByWithDirection.split(",");
        for (String sortParam : sortParams) {
            String[] parts = sortParam.trim().split("\\s+");
            String field = parts[0].toLowerCase();
            String direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1])) ? "DESC" : "ASC";

            if (ALLOWED_SORT_FIELDS.contains(field)) {
                String dbColumn = field;
                if (orderByClause.length() > 0) {
                    orderByClause.append(", ");
                }
                orderByClause.append(dbColumn).append(" ").append(direction);
            } else {
                log.warn("Invalid sort field provided: {}. Ignoring.", field);
            }
        }
        return orderByClause.length() > 0 ? orderByClause.toString() : "name ASC";
    }

    @Override
    public Page<Food> searchFoodsByName(FoodNameSearchCriteria criteria) {
        log.info("음식 이름 검색 서비스: name={}, sortBy={}, page={}, size={}",
                criteria.getName(), criteria.getSortBy(), criteria.getPage(), criteria.getSize());

        int pageSize = Math.min(criteria.getSize(), MAX_PAGE_SIZE);
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

    @Override
    public Food getFoodById(Long foodId) {
        return foodDAO.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food not found with id: " + foodId));
    }

    @Override
    @Transactional // 쓰기 작업이므로 readOnly = false 적용
    public Food addFood(Food food) {
        foodDAO.save(food);
        return food;
    }

    @Override
    @Transactional // 쓰기 작업
    public Food updateFood(Long foodId, Food foodDetailsToUpdate) {
        foodDAO.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food not found with id: " + foodId + ". Cannot update."));
        foodDetailsToUpdate.setFoodId(foodId);
        int affectedRows = foodDAO.update(foodDetailsToUpdate);
        if (affectedRows == 0) throw new RuntimeException("Food update failed for id: " + foodId);
        return foodDAO.findById(foodId).orElseThrow();
    }

    @Override
    @Transactional // 쓰기 작업
    public void deleteFood(Long foodId) {
        foodDAO.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food not found with id: " + foodId + ". Cannot delete."));
        foodDAO.delete(foodId);
    }

    /**
     * 특정 카테고리에 해당하는 추천 음식 목록을 가져옵니다.
     * 먼저 Redis 캐시를 확인하고, 없으면 DB에서 조회 후 카테고리별 영양소 기준을 Map에 담아 DAO를 호출
     * 그리고 마지막으로 캐시에 저장하고 반환합니다.
     */
    @Override
    public List<Food> getRecommendedFoods(String categoryName) {
        // 1. 카테고리명을 기반으로 동적 캐시 키 생성
        String cacheKey = CacheConstants.RECOMMENDATIONS_KEY_PRIPIX + categoryName.toLowerCase();

        log.debug("카테고리 '{}' 추천 음식 요청: 캐시 확인 시작 (Key: {})", categoryName, cacheKey);
        List<Food> recommendations;

        // 2. Redis 캐시에서 데이터 조회 시도
        try {
            recommendations = redisTemplateListFood.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.error("Redis 캐시 조회 중 오류 발생! DB에서 직접 조회 시도. (Key: {})", cacheKey, e);
            recommendations = null; // 오류 발생 시 캐시 미스로 간주
        }

        if (recommendations == null) { // 3. 캐시 미스(Cache Miss) 발생!
            log.warn("캐시 미스 발생 (Key: {}). DB에서 조회하여 캐시를 업데이트합니다.", cacheKey);

            // --- DB 조회 로직 (기존 getRecommendedFoods 로직과 동일) ---
            Map<String, Object> params = new HashMap<>();
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
                    log.warn("알 수 없는 추천 카테고리 '{}' (Key: {}). 빈 리스트를 반환합니다.", categoryName, cacheKey);
                    return Collections.emptyList(); // 유효하지 않은 카테고리는 빈 리스트 반환
            }
            log.info("DB에서 카테고리 '{}'에 대한 추천 음식 조회 중... (Key: {})", categoryName, cacheKey);
            recommendations = foodDAO.findRecommendFoods(params); // DAO는 랜덤 3개를 반환한다고 가정

            if (recommendations == null) { // DAO가 null을 반환할 수 있다면 NPE 방지
                recommendations = Collections.emptyList();
            }
            // --- DB 조회 로직 끝 ---

            // 4. DB 조회 결과를 캐시에 저장 (결과가 비어있지 않은 경우에만)
            if (!recommendations.isEmpty()) {
                try {
                    redisTemplateListFood.opsForValue().set(cacheKey, recommendations, 25, TimeUnit.HOURS);
                    log.info("카테고리 '{}'의 추천 음식 ({}개)을 Redis 캐시에 성공적으로 저장했습니다. (Key: {})", categoryName, recommendations.size(), cacheKey);
                } catch (Exception e) {
                    log.error("Redis 캐시에 추천 음식 저장 중 오류 발생 (Key: {})!", cacheKey, e);
                    // 캐시 저장 실패 시에도 DB에서 가져온 데이터는 반환해야 하므로, 여기서 반환하지 않음.
                }
            } else {
                log.info("카테고리 '{}'에 대한 추천 음식을 DB에서 찾을 수 없거나 비어있어 캐시하지 않습니다. (Key: {})", categoryName, cacheKey);
                // 비어 있는 결과라도 짧은 시간 동안 캐싱하여 반복적인 DB 조회를 막을 수도 있습니다. (선택 사항)
                // 예: redisTemplateListFood.opsForValue().set(cacheKey, Collections.emptyList(), 5, TimeUnit.MINUTES);
            }
            log.info("DB에서 조회한 데이터로 응답하고 캐시를 업데이트했습니다. (Key: {})", cacheKey);

        } else { // 5. 캐시 히트(Cache Hit)!
            log.info("캐시 히트! Redis 캐시에서 카테고리 '{}' 추천 음식을 제공합니다. ({}개 항목, Key: {})", categoryName, recommendations.size(), cacheKey);
        }

        // 동시성 문제를 고려하지 않기로 했으므로, 락(lock) 관련 코드는 포함하지 않음
        // 만약 트래픽이 매우 많아 캐시 미스 시 동일 카테고리에 대한 동시 DB 접근이 우려된다면,
        // 이전에 논의했던 `synchronized` 블록이나 분산 락 등을 고려

        return recommendations;
    }
}