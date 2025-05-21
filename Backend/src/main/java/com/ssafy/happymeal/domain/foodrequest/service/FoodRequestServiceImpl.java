package com.ssafy.happymeal.domain.foodrequest.service;

import com.ssafy.happymeal.domain.foodrequest.constant.FoodRequestStatus;
import com.ssafy.happymeal.domain.foodrequest.dao.FoodRequestDAO;
import com.ssafy.happymeal.domain.foodrequest.dto.FoodRequestDto;
import com.ssafy.happymeal.domain.foodrequest.entity.FoodRequest;
import com.ssafy.happymeal.domain.food.entity.Food;
import com.ssafy.happymeal.domain.food.service.FoodService;
import jakarta.persistence.EntityNotFoundException; // 이 부분을 사용합니다.
// import com.ssafy.happymeal.global.error.exception.AccessDeniedException; // 필요시 사용
// import com.ssafy.happymeal.domain.user.dao.UserDAO;
// import com.ssafy.happymeal.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodRequestServiceImpl implements FoodRequestService {

    private final FoodRequestDAO foodRequestDAO;
    private final FoodService foodService;
    // private final UserDAO userDAO;

    @Override
    @Transactional
    public FoodRequestDto.Response createFoodRequest(FoodRequestDto.Create createDto, Long userId) {
        FoodRequest foodRequest = FoodRequest.builder()
                .userId(userId)
                .name(createDto.getName())
                .category(createDto.getCategory())
                .servingSize(createDto.getServingSize())
                .unit(createDto.getUnit())
                .calories(createDto.getCalories())
                .carbs(createDto.getCarbs())
                .sugar(createDto.getSugar())
                .protein(createDto.getProtein())
                .fat(createDto.getFat())
                .isRegistered(FoodRequestStatus.PENDING)
                // .createAt(LocalDateTime.now()) // DAO에서 NOW() 사용 시 생략 가능
                .build();

        foodRequestDAO.save(foodRequest);
        // 저장 후 ID가 채워진 객체를 다시 조회하거나, createAt 등 DB 기본값을 포함하여 정확한 상태로 DTO 변환
        FoodRequest savedRequest = foodRequestDAO.findById(foodRequest.getFoodRequestId())
                .orElseThrow(() -> new EntityNotFoundException("Failed to retrieve saved FoodRequest with id: " + foodRequest.getFoodRequestId())); // 방어 코드
        return convertToResponseDto(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodRequestDto.Response getFoodRequestById(Long foodRequestId, Long currentUserId) {
        FoodRequest foodRequest = foodRequestDAO.findById(foodRequestId)
                .orElseThrow(() -> new EntityNotFoundException("FoodRequest not found with id: " + foodRequestId)); // 변경

        // TODO: 본인 요청 또는 관리자만 조회 가능한 로직 추가 (필요 시)
        // if (!foodRequest.getUserId().equals(currentUserId) && !isAdmin(currentUserId)) {
        //    throw new AccessDeniedException("You do not have permission to view this food request.");
        // }
        return convertToResponseDto(foodRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodRequestDto.Response> getFoodRequestsByUserId(Long userId) {
        List<FoodRequest> foodRequests = foodRequestDAO.findByUserId(userId);
        return foodRequests.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodRequestDto.Response> getAllFoodRequests() {
        List<FoodRequest> foodRequests = foodRequestDAO.findAll();
        return foodRequests.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FoodRequestDto.Response updateFoodRequestStatus(Long foodRequestId, FoodRequestDto.UpdateStatus updateStatusDto) {
        FoodRequest foodRequest = foodRequestDAO.findById(foodRequestId)
                .orElseThrow(() -> new EntityNotFoundException("FoodRequest not found with id: " + foodRequestId + " for status update."));

        // 상태 업데이트
        foodRequestDAO.updateStatus(foodRequestId, updateStatusDto.getIsRegistered());

        // 승인된 경우 Food로 등록
        if (updateStatusDto.getIsRegistered() == FoodRequestStatus.APPROVED) {
            Food food = Food.builder()
                    .name(foodRequest.getName())
                    .category(foodRequest.getCategory())
                    .servingSize(foodRequest.getServingSize())
                    .unit(foodRequest.getUnit())
                    .calories(foodRequest.getCalories())
                    .carbs(foodRequest.getCarbs())
                    .sugar(foodRequest.getSugar())
                    .protein(foodRequest.getProtein())
                    .fat(foodRequest.getFat())
                    .imgUrl(foodRequest.getImgUrl())
                    .build();

            try {
                foodService.addFood(food);
                log.info("FoodRequest 승인 및 Food 등록 완료: foodRequestId={}, foodName={}", foodRequestId, food.getName());
            } catch (Exception e) {
                log.error("Food 등록 실패: foodRequestId={}, error={}", foodRequestId, e.getMessage());
                throw new RuntimeException("Food 등록 중 오류가 발생했습니다.", e);
            }
        }

        FoodRequest updatedFoodRequest = foodRequestDAO.findById(foodRequestId)
                .orElseThrow(() -> new EntityNotFoundException("FoodRequest not found with id: " + foodRequestId + " (after status update attempt)"));
        return convertToResponseDto(updatedFoodRequest);
    }

    private FoodRequestDto.Response convertToResponseDto(FoodRequest foodRequest) {
        return FoodRequestDto.Response.builder()
                .foodRequestId(foodRequest.getFoodRequestId())
                .userId(foodRequest.getUserId())
                .name(foodRequest.getName())
                .category(foodRequest.getCategory())
                .servingSize(foodRequest.getServingSize())
                .unit(foodRequest.getUnit())
                .calories(foodRequest.getCalories())
                .carbs(foodRequest.getCarbs())
                .sugar(foodRequest.getSugar())
                .protein(foodRequest.getProtein())
                .fat(foodRequest.getFat())
                .isRegistered(foodRequest.getIsRegistered())
                .createAt(foodRequest.getCreateAt())
                .build();
    }
}