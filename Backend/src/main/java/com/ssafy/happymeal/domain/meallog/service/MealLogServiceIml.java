package com.ssafy.happymeal.domain.meallog.service;

import ch.qos.logback.core.spi.ErrorCodes;
import com.ssafy.happymeal.domain.food.dao.FoodDAO;
import com.ssafy.happymeal.domain.food.entity.Food;
import com.ssafy.happymeal.domain.meallog.dao.MealLogDAO;
import com.ssafy.happymeal.domain.meallog.dto.MealLogDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogRequestDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogResponseDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogStatsDto;
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
import java.util.Optional;
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

        mealLog.setUserId(userId);
        mealLog.setFoodId(mealLogDto.getFoodId());
        mealLog.setMealDate(LocalDate.parse(mealLogDto.getMealDate()));
        mealLog.setMealType(mealLogDto.getMealType());
        mealLog.setQuantity(mealLogDto.getQuantity());
        mealLog.setImgUrl(mealLogDto.getImgUrl());

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
    public void updateMealLog(Long userId, Long logId, MealLogRequestDto requestDto) {
        // 일치하는 식단 기록이 없을 경우
        MealLog mealLog = mealLogDAO.findById(logId)
                .orElseThrow(() -> new NoMealLogFoundException("logId={"+logId+"}와/과 일치하는 식단 기록이 존재하지 않습니다."));

        boolean isChanged = false;

        // 1. food 변경
        Food food = null;
        // dto.getFoodId()가 기존 mealLog의 foodId와 다르거나, mealLog에 foodId가 없었다면 새로 조회
        if(mealLog.getFoodId() == null || !requestDto.getFoodId().equals(mealLog.getFoodId())) {
            food = foodDAO.findById(requestDto.getFoodId())
                    .orElseThrow(() -> new RuntimeException("foodId={"+requestDto.getFoodId()+"}와/과 일치하는 음식이 존재하지 않습니다."));
            mealLog.setFoodId(food.getFoodId());
            isChanged = true;
        }
//        else {
//            food = foodDAO.findById(mealLog.getFoodId())
//                    // 전역예외처리에서 처리하고 싶음
//        }

        // 2. 수량 변경
        if(requestDto.getQuantity() != null && requestDto.getQuantity().compareTo(mealLog.getQuantity()) != 0) {
            mealLog.setQuantity(requestDto.getQuantity());
            isChanged = true;
        }

        // 3. mealType 변경
        if(requestDto.getMealType() != null && !requestDto.getMealType().equals(mealLog.getMealType())) {
            mealLog.setMealType(requestDto.getMealType());
            isChanged = true;
        }

        // 4. mealDate 변경
        if(requestDto.getMealDate() != null && !requestDto.getMealDate().equals(mealLog.getMealDate())) {
            mealLog.setMealDate(requestDto.getMealDate());
            isChanged = true;
        }

        // 5. imgUrl 변경
        if(requestDto.getImgUrl() != null && !requestDto.getImgUrl().equals(mealLog.getImgUrl())) {
            mealLog.setImgUrl(requestDto.getImgUrl());
        }

        if(isChanged) {
            mealLogDAO.updateMealLog(mealLog);
            log.info("식단기록 업데이트 완료 : logId={}, userId={}", logId, userId);
        } else {
            log.info("식단 기록 업데이트 요청 : 변경 내용 없음.  logId={}, userId={}", logId, userId);
        }
//        return responseMealLog;

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
