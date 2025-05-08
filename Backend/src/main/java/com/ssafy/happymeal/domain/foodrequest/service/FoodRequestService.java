package com.ssafy.happymeal.domain.foodrequest.service;

import com.ssafy.happymeal.domain.foodrequest.dto.FoodRequestDto;

import java.util.List;

public interface FoodRequestService {

    /**
     * 사용자가 음식 등록 요청 생성
     * @param createDto 요청 생성 DTO
     * @param userId 요청자 ID (인증 정보에서 추출)
     * @return 생성된 요청 정보 DTO
     */
    FoodRequestDto.Response createFoodRequest(FoodRequestDto.Create createDto, Long userId);

    /**
     * 특정 음식 등록 요청 상세 조회
     * @param foodRequestId 요청 ID
     * @param userId 요청자 ID (본인 요청 또는 관리자 확인용)
     * @return 요청 정보 DTO
     */
    FoodRequestDto.Response getFoodRequestById(Long foodRequestId, Long userId); // userId 추가 (권한 확인용)

    /**
     * 특정 사용자의 모든 음식 등록 요청 목록 조회
     * @param userId 사용자 ID
     * @return 요청 정보 DTO 리스트
     */
    List<FoodRequestDto.Response> getFoodRequestsByUserId(Long userId);

    /**
     * (관리자용) 모든 음식 등록 요청 목록 조회
     * @return 모든 요청 정보 DTO 리스트
     */
    List<FoodRequestDto.Response> getAllFoodRequests();

    /**
     * (관리자용) 음식 등록 요청 상태 변경
     * @param foodRequestId 요청 ID
     * @param updateStatusDto 상태 변경 DTO
     * @return 변경된 요청 정보 DTO
     */
    FoodRequestDto.Response updateFoodRequestStatus(Long foodRequestId, FoodRequestDto.UpdateStatus updateStatusDto);
}