package com.ssafy.happymeal.domain.food.service;

import com.ssafy.happymeal.domain.food.dto.FoodNameSearchCriteria;
import com.ssafy.happymeal.domain.food.dto.FoodPagingSortCriteria;
import com.ssafy.happymeal.domain.food.entity.Food; // Food DTO로 사용
import org.springframework.data.domain.Page;

import java.util.List;

public interface FoodService {

//    /**
//     * 음식 정보 검색 (이름 기반)
//     * @param name 검색할 음식 이름 (부분 일치)
//     * @return 검색된 음식 정보 DTO 리스트
//     */
//    List<Food> searchFoodsByName(String name);
//
//    /**
//     * 모든 음식 정보 조회
//     * @return 모든 음식 정보 DTO 리스트
//     */
//    List<Food> getAllFoods();

    /**
     * 특정 ID의 음식 정보 조회
     * @param foodId 조회할 음식 ID
     * @return 조회된 음식 정보 DTO
     * @throws jakarta.persistence.EntityNotFoundException 해당 ID의 음식이 없을 경우 (또는 사용자 정의 예외)
     */
    Food getFoodById(Long foodId);

    /**
     * 음식 정보 추가
     * @param foodDto 추가할 음식 정보 DTO (Food 객체를 DTO로 사용)
     * @return 추가된 음식 정보 DTO (ID 포함)
     */
    Food addFood(Food foodDto);

    /**
     * 음식 정보 수정
     * @param foodId 수정할 음식 ID
     * @param foodDetailsToUpdate 수정될 음식 정보 DTO
     * @return 수정된 음식 정보 DTO
     * @throws jakarta.persistence.EntityNotFoundException 해당 ID의 음식이 없을 경우
     */
    Food updateFood(Long foodId, Food foodDetailsToUpdate);

    /**
     * 음식 정보 삭제
     * @param foodId 삭제할 음식 ID
     * @throws jakarta.persistence.EntityNotFoundException 해당 ID의 음식이 없을 경우 (삭제 시도 전에 확인)
     */
    void deleteFood(Long foodId);

    // 이름으로 음식 검색 (페이징 및 정렬 추가)
    Page<Food> searchFoodsByName(FoodNameSearchCriteria criteria);

    // 모든 음식 조회 (페이징 및 정렬 추가)
    Page<Food> getAllFoods(FoodPagingSortCriteria criteria);


    List<Food> getRecommendedFoods(String categoryName); // 반환 타입 변경



}