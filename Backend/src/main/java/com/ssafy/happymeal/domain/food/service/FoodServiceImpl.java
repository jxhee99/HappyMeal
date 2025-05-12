package com.ssafy.happymeal.domain.food.service;

import com.ssafy.happymeal.domain.food.dao.FoodDAO;
import com.ssafy.happymeal.domain.food.entity.Food;
import jakarta.persistence.EntityNotFoundException; // 표준 예외 또는 사용자 정의 예외 사용
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j // 로그 사용을 위한 Lombok 어노테이션
@Service // 이 클래스가 Spring의 서비스 구현체임을 나타냅니다.
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService { // FoodService 인터페이스 구현

    private final FoodDAO foodDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Food> searchFoodsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return foodDAO.findAll();
        }
        return foodDAO.searchByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Food> getAllFoods() {
        return foodDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Food getFoodById(Long foodId) {
        return foodDAO.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food not found with id: " + foodId));
    }

    @Override
    @Transactional
    public Food addFood(Food foodDto) {
        foodDAO.save(foodDto);
        return foodDto;
    }

    @Override
    @Transactional
    public Food updateFood(Long foodId, Food foodDetailsToUpdate) {
        foodDAO.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food not found with id: " + foodId + ". Cannot update."));

        foodDetailsToUpdate.setFoodId(foodId);

        int affectedRows = foodDAO.update(foodDetailsToUpdate);
        if (affectedRows == 0) {
            throw new RuntimeException("Failed to update food with id: " + foodId + ". No rows affected.");
        }
        return foodDAO.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food disappeared after update with id: " + foodId));
    }

    @Override
    @Transactional
    public void deleteFood(Long foodId) {
        foodDAO.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food not found with id: " + foodId + ". Cannot delete."));

        int affectedRows = foodDAO.delete(foodId);
        // 삭제 시 affectedRows가 0인 경우는 findById에서 이미 걸러졌거나,
        // 동시에 다른 트랜잭션에서 삭제된 경우일 수 있습니다.
        // 필요하다면 로깅 또는 추가적인 예외 처리를 할 수 있습니다.
        if (affectedRows == 0) {
            System.err.println("Attempted to delete non-existing or already deleted food with id: " + foodId + " (concurrency issue or already handled by findById check)");
        }
    }

    @Override
    public List<Food> getRecommendedFoods(String categoryName) { // 반환 타입 List<Food>로 변경
        log.info("Fetching simplified recommendations for category: {}", categoryName);

        Map<String, Object> params = new HashMap<>();

        switch (categoryName.toLowerCase()) {
            case "diet":
                log.debug("Applying simplified 'diet' criteria");
                params.put("maxCalories", new BigDecimal("150"));
                params.put("minProtein", new BigDecimal("10"));
                params.put("maxFat", new BigDecimal("10").subtract(BigDecimal.valueOf(0.01)));
                params.put("maxSugar", new BigDecimal("5").subtract(BigDecimal.valueOf(0.01)));
                break;
            case "healthy":
                log.debug("Applying simplified 'healthy' criteria");
                params.put("minCalories", new BigDecimal("100"));
                params.put("maxCalories", new BigDecimal("250"));
                params.put("minProtein", new BigDecimal("10"));
                params.put("minFat", new BigDecimal("5"));
                params.put("maxFat", new BigDecimal("15"));
                params.put("maxSugar", new BigDecimal("10").subtract(BigDecimal.valueOf(0.01)));
                break;
            case "bulk-up":
                log.debug("Applying simplified 'bulk-up' criteria");
                params.put("minCalories", new BigDecimal("200"));
                params.put("minProtein", new BigDecimal("15"));
                params.put("minFat", new BigDecimal("10"));
                break;
            case "cheating":
                log.debug("Applying simplified 'cheating' criteria (high calorie)");
                params.put("minCalories", new BigDecimal("400"));
                break;
            default:
                log.warn("Unknown category for simplified recommendation: {}. Returning general random foods.", categoryName);
                break;
        }

        // 공통 파라미터는 DAO SQL에 하드코딩 되어 있음 (RAND() LIMIT 3)
        // DAO가 Map을 받으므로, params에 limit, offset, randomSort를 넣을 필요 없음 (findSimplifiedRandomFoods의 경우)
        // 만약 DAO의 findSimplifiedRandomFoods가 여전히 limit, offset 등을 params에서 받는다면 아래 코드 필요
        // params.put("limit", RECOMMENDATION_COUNT);
        // params.put("offset", 0);
        // params.put("randomSort", true); // DAO에서 사용한다면

        log.debug("DAO parameters for simplified recommendation: {}", params);
        // DTO 변환 로직 제거, DAO가 반환하는 엔티티 리스트를 그대로 반환
        return foodDAO.findRecommendFoods(params);
    }


}