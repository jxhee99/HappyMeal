package com.ssafy.happymeal.domain.foodrequest.controller;

import com.ssafy.happymeal.domain.foodrequest.dto.FoodRequestDto;
import com.ssafy.happymeal.domain.foodrequest.service.FoodRequestService;
import com.ssafy.happymeal.domain.foodrequest.service.FoodRequestServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails; // ⭐️ UserDetails 사용
// import org.springframework.security.access.prepost.PreAuthorize; // 필요시 활성화
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/food-requests")
@RequiredArgsConstructor
public class FoodRequestController {

//    @Autowired
    private final FoodRequestService foodRequestService;

    /**
     * 사용자 음식 등록 요청 생성
     * POST /api/food-requests
     */
    @PostMapping
    // @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<FoodRequestDto.Response> createFoodRequest(
            @RequestBody FoodRequestDto.Create createDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            log.warn("FoodRequest 생성 시 인증된 사용자 정보를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.parseLong(userDetails.getUsername());

        // 상세 로그 추가
        log.info("음식 등록 요청 생성 시작: userId={}", userId);
        log.info("요청 데이터: name={}, category={}, servingSize={}, unit={}, calories={}, carbs={}, sugar={}, protein={}, fat={}, imgUrl={}",
                createDto.getName(),
                createDto.getCategory(),
                createDto.getServingSize(),
                createDto.getUnit(),
                createDto.getCalories(),
                createDto.getCarbs(),
                createDto.getSugar(),
                createDto.getProtein(),
                createDto.getFat(),
                createDto.getImgUrl());

        try {
            FoodRequestDto.Response responseDto = foodRequestService.createFoodRequest(createDto, userId);
            log.info("음식 등록 요청 생성 성공: foodRequestId={}", responseDto.getFoodRequestId());
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("음식 등록 요청 생성 실패: error={}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 음식 등록 요청 상세 조회
     * GET /api/food-requests/{foodRequestId}
     */
    @GetMapping("/{foodRequestId}")
    // @PreAuthorize("hasRole('ADMIN') or @foodRequestServiceImpl.isOwner(#foodRequestId, authentication.principal.userId)")
    public ResponseEntity<FoodRequestDto.Response> getFoodRequestById(
            @PathVariable Long foodRequestId,
            @AuthenticationPrincipal UserDetails userDetails) { // ⭐️ UserDetails 주입

        if (userDetails == null) {
            log.warn("FoodRequest 상세 조회 시 인증된 사용자 정보를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long currentUserId = Long.parseLong(userDetails.getUsername()); // ⭐️ Long 타입으로 변환

        log.info("음식 등록 요청 상세 조회: foodRequestId={}, currentUserId={}", foodRequestId, currentUserId);

        // 서비스 계층에서 currentUserId를 사용하여 본인 요청인지, 또는 관리자인지 등의 권한 검사를 수행할 수 있습니다.
        FoodRequestDto.Response responseDto = foodRequestService.getFoodRequestById(foodRequestId, currentUserId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 현재 인증된 사용자의 음식 등록 요청 목록 조회
     * GET /api/food-requests/my
     */
    @GetMapping("/my")
    // @PreAuthorize("isAuthenticated()") // 또는 hasAnyRole('USER', 'ADMIN')
    public ResponseEntity<List<FoodRequestDto.Response>> getMyFoodRequests(
            @AuthenticationPrincipal UserDetails userDetails) { // ⭐️ UserDetails 주입

        if (userDetails == null) {
            log.warn("내 FoodRequest 목록 조회 시 인증된 사용자 정보를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = Long.parseLong(userDetails.getUsername()); // ⭐️ Long 타입으로 변환

        log.info("내 음식 등록 요청 목록 조회: userId={}", userId);

        List<FoodRequestDto.Response> responseDtos = foodRequestService.getFoodRequestsByUserId(userId);
        if (responseDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * (관리자용) 모든 음식 등록 요청 목록 조회
     * GET /api/food-requests
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // ⭐️ ADMIN만 접근 가능하도록 명시적 설정 예시
    public ResponseEntity<List<FoodRequestDto.Response>> getAllFoodRequests() {
        // 이 API는 ADMIN만 호출하므로 @AuthenticationPrincipal UserDetails가 필수는 아닐 수 있으나,
        // 호출한 관리자 로깅 등을 위해 사용할 수도 있습니다.
        log.info("모든 음식 등록 요청 목록 조회 (관리자)");
        List<FoodRequestDto.Response> responseDtos = foodRequestService.getAllFoodRequests();
        if (responseDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * (관리자용) 음식 등록 요청 상태 변경
     * PATCH /api/food-requests/{foodRequestId}/status
     */
    @PatchMapping("/{foodRequestId}/status")
    @PreAuthorize("hasRole('ADMIN')") // ⭐️ ADMIN만 접근 가능하도록 명시적 설정 예시
    public ResponseEntity<FoodRequestDto.Response> updateFoodRequestStatus(
            @PathVariable Long foodRequestId,
            @RequestBody FoodRequestDto.UpdateStatus updateStatusDto) {
        // 이 API도 ADMIN만 호출
        log.info("음식 등록 요청 상태 변경: foodRequestId={}, newStatus={}", foodRequestId, updateStatusDto.getIsRegistered());
        FoodRequestDto.Response responseDto = foodRequestService.updateFoodRequestStatus(foodRequestId, updateStatusDto);
        return ResponseEntity.ok(responseDto);
    }
}
