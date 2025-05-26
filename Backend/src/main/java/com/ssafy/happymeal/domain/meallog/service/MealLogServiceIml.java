package com.ssafy.happymeal.domain.meallog.service;

import ch.qos.logback.core.spi.ErrorCodes;
import com.ssafy.happymeal.domain.food.dao.FoodDAO;
import com.ssafy.happymeal.domain.food.entity.Food;
import com.ssafy.happymeal.domain.meallog.dao.MealLogDAO;
import com.ssafy.happymeal.domain.meallog.dto.*;
import com.ssafy.happymeal.domain.meallog.entity.MealLog;
import com.ssafy.happymeal.domain.user.dao.UserDAO;
import com.ssafy.happymeal.global.exception.CustomException;
import com.ssafy.happymeal.global.exception.ForbiddenException;
import com.ssafy.happymeal.global.exception.NoMealLogFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealLogServiceIml implements MealLogService{

    private final MealLogDAO mealLogDAO;
    private final UserDAO userDAO;
    private final FoodDAO foodDAO;

    /* 식단 기록 추가 */
    @Override
    public void addMealLog(Long userId, MealLogDto mealLogDto) {
        MealLog mealLog = new MealLog();

        String imgUrl = mealLogDto.getImgUrl();

        if(imgUrl==null) {
            Food food = foodDAO.findById(mealLogDto.getFoodId())
                    .orElseThrow(() -> new RuntimeException("foodId={"+mealLog.getFoodId()+"}와/과 일치하는 음식이 존재하지 않습니다."));
            imgUrl = food.getImgUrl();
        }

        mealLog.setUserId(userId);
        mealLog.setFoodId(mealLogDto.getFoodId());
        mealLog.setMealDate(LocalDate.parse(mealLogDto.getMealDate()));
        mealLog.setMealType(mealLogDto.getMealType());
        mealLog.setQuantity(mealLogDto.getQuantity());
        mealLog.setImgUrl(imgUrl);

        mealLogDAO.insertMealLog(mealLog);
    }

    /* 전체 식단 기록 조회 */
    @Override
    public List<MealLogResponseDto> getAllMealLogs(Long userId) {
        return mealLogDAO.getAllMealLogs(userId);
    }

    /* 특정 날짜 식단 기록 조회 */
    @Override
    public List<MealLogResponseDto> findByUserAndDate(Long userId, LocalDate mealDate) {
        int count = mealLogDAO.findByDate(mealDate);
        if(count==0) {
            throw new NoMealLogFoundException("해당 날짜("+mealDate+")애 대한 식단 기록이 존재하지 않습니다.");
        }
        return mealLogDAO.findByUserAndDate(userId, mealDate);
    }

    /* 특정 날짜 식단 통계 조회 */
    @Override
    public MealLogStatsDto getDailyMealStats(Long userId, LocalDate mealDate) {
        int count = mealLogDAO.findByDate(mealDate);
        if(count==0) {
            throw new NoMealLogFoundException("해당 날짜("+mealDate+")애 대한 식단 기록이 존재하지 않습니다.");
        }
        return mealLogDAO.getDailyMealStats(userId, mealDate);
    }

    /* 주간 식단 통계 조회 - mealDate를 포함한 이전 7일간의 각 일별 통계 리스트 반환 */
    @Override
    public List<MealLogStatsDto> getWeeklyMealLogStats(Long userId, LocalDate endDate) {
        // 1. 통계 기간의 시작일 계산 (endDate로부터 6일 전)
        // 예: endDate가 2025-05-01 이면 startDate는 2025-04-25
        // 예: endDate가 2025-01-01 이면 startDate는 2024-12-26 (연도, 월 자동 계산)
        LocalDate startDate = endDate.minusDays(6);

        // 2. DAO를 통해 해당 기간(startDate ~ endDate)의 일별 식단 통계 데이터 가져옴
        // 이 DAO 메소드는 날짜별로 그룹화된 통계 DTO 리스트를 반환하도록 설계
        // 만약 특정 날짜에 기록이 없다면, 해당 날짜의 DTO는 이 리스트에 포함되지 않을 수 있음.
        List<MealLogStatsDto> statsFromDB = mealLogDAO.getDailyStatsForDateRange(userId, startDate, endDate);

        // 3. 결과를 날짜별로 쉽게 조회할 수 있도록 Map으로 변환
        // key : 날짜, value : MealLogStatsDto 객체
        Map<LocalDate, MealLogStatsDto> statsMap = statsFromDB.stream()
                .collect(Collectors.toMap(MealLogStatsDto::getDate, dto -> dto));

        // 7일치 일별 통계 식단 기록 반환
        List<MealLogStatsDto> weeklyStatsList = new ArrayList<>();
        LocalDate currentDate = startDate;
        for(int i = 0; i < 7; i++) {
            MealLogStatsDto dailyStats = statsMap.get(currentDate);

            // currentDate에 해당하는 키가 statsMap 없을 때
            // 즉, DB에서 해당 날짜의 통계가 없는 경우
            if(dailyStats==null) {
                dailyStats = MealLogStatsDto.empty(currentDate);
            }

            weeklyStatsList.add(dailyStats);
            currentDate = currentDate.plusDays(1); // 다음 날짜로 이동
        }
        return weeklyStatsList;
    }

    /* 식단 기록 수정 */
    @Override
    @Transactional
    public MealLogUpdateResponseDto updateMealLog(Long userId, Long logId, MealLogRequestDto requestDto) {
        // 기존 식단 기록 조회 및 존재 여부 확인
        MealLog mealLog = mealLogDAO.findById(logId)
                .orElseThrow(() -> new NoMealLogFoundException("logId={"+logId+"}와/과 일치하는 식단 기록이 존재하지 않습니다."));

        boolean isChanged = false; // 실제 DB 업데이트 여부

        // 1. food 변경
        Food newFood = null; // 새로 참조할 food 엔티티
        // DTO의 foodId는 @NotNull로 인해 null이 아님을 보장받음 (Bean Validation 선행)
        // -> mealLog.getFoodId() == null 검사 할 필요 없음
        // dto.getFoodId()가 기존 mealLog의 foodId와 다르거나, mealLog에 foodId가 없었다면 새로 조회
        if(!requestDto.getFoodId().equals(mealLog.getFoodId())) {
            newFood = foodDAO.findById(requestDto.getFoodId())
                    .orElseThrow(() -> new RuntimeException("foodId={"+requestDto.getFoodId()+"}와/과 일치하는 음식이 존재하지 않습니다."));
            mealLog.setFoodId(newFood.getFoodId());
            isChanged = true;
        }

        // 2. 수량 변경
        // DTO의 quantity는 @NotNull, @DecimalMin(0.0, inclusive=false)으로 유효성 보장
        if(mealLog.getQuantity() == null || requestDto.getQuantity().compareTo(mealLog.getQuantity()) != 0) {
            mealLog.setQuantity(requestDto.getQuantity());
            isChanged = true;
        }

        // 3. mealType 변경
        if(mealLog.getMealType() == null || !requestDto.getMealType().equals(mealLog.getMealType())) {
            mealLog.setMealType(requestDto.getMealType());
            isChanged = true;
        }

        // 4. imgUrl 변경
        /* requestDto.imgUrl 없으면 -> foodId 변경시 새 food 이미지 / foodId 그대로면 기존 이미지 유지. */
        // foodId가 변경되지 않았고 DTO에 imgUrl도 없다면, 기존 imgUrl 유지
        String updateImgUrl = mealLog.getImgUrl();

        // 요청 DTO에 사용자가 명시적으로 이미지 URL을 제공한 경우:
        if(requestDto.getImgUrl() != null && !requestDto.getImgUrl().trim().isEmpty()) {
            updateImgUrl = requestDto.getImgUrl();
            isChanged = true;
        }
        // 2. 요청 DTO에 이미지 URL이 제공되지 않았거나 빈 문자열("")인 경우:
        else {
            if(newFood != null) { // food가 변경되었다면 새 food의 이미지 url을 받음
                // food의 이미지가 없는 경우도 허용
                String newFoodImgUrl = newFood.getImgUrl();
                // 기존 이미지와 새 이미지가 다른 경우에만 업데이트
                if(!Objects.equals(newFoodImgUrl, mealLog.getImgUrl())) {
                    updateImgUrl = newFoodImgUrl; // null이어도 허용
                    isChanged = true;
                }
            }
        }

        mealLog.setImgUrl(updateImgUrl); // 최종으로 결정된 이미지url 저장 (null 허용)

        // 5.  변경사항이 있을 경우에만 DB 업데이트
        if(isChanged) {
            mealLogDAO.updateMealLog(mealLog);
            log.info("식단기록 업데이트 완료 : logId={}, userId={}", logId, userId);
        } else {
            log.info("식단 기록 업데이트 요청 : 변경 내용 없음.  logId={}, userId={}", logId, userId);
        }

        // 6. 수정 내용 반환
        return MealLogUpdateResponseDto.builder()
                .logId(mealLog.getLogId())
                .foodId(mealLog.getFoodId())
                .quantity(mealLog.getQuantity())
                .mealType(mealLog.getMealType())
                .imgUrl(mealLog.getImgUrl())
                .build();
    }

    /* 식단 기록 상세 조회 */
    @Override
    public MealLogResponseDto getDetailMealLog(Long userId, Long logId) {
        // 일치하는 식단 기록이 없을 경우
        MealLog mealLog = mealLogDAO.findById(logId)
                .orElseThrow(() -> new NoMealLogFoundException("logId={"+logId+"}와/과 일치하는 식단 기록이 존재하지 않습니다."));

        // 일치하는 사용자가 아닌 경우
//        if(!mealLog.getUserId().equals(userId)) {
//            throw new ForbiddenException("본인의 기록만 조회할 수 있습니다.");
//        }

        return mealLogDAO.getDetailMealLog(userId, logId);
    }

    /* 식단 기록 삭제 */
    @Override
    public void deleteMealLog(Long userId, Long logId) throws NotFoundException {
        // 일치하는 식단 기록이 없을 경우
        MealLog mealLog = mealLogDAO.findById(logId)
                .orElseThrow(() -> new NoMealLogFoundException("logId={"+logId+"}와/과 일치하는 식단 기록이 존재하지 않습니다."));

        // 일치하는 사용자가 아닌 경우
//        if(!mealLog.getUserId().equals(userId)) {
//            throw new ForbiddenException("본인의 기록만 삭제할 수 있습니다.");
//        }
        mealLogDAO.deleteMealLog(userId, logId);
    }
}
