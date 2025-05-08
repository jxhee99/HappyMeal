package com.ssafy.happymeal.domain.food.service;

import com.ssafy.happymeal.domain.food.dao.FoodDAO;
import com.ssafy.happymeal.domain.food.entity.Food;
import jakarta.persistence.EntityNotFoundException; // 표준 예외 또는 사용자 정의 예외 사용
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}