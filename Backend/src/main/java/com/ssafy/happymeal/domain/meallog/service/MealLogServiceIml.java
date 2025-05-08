package com.ssafy.happymeal.domain.meallog.service;

import com.ssafy.happymeal.domain.meallog.dao.MealLogDAO;
import com.ssafy.happymeal.domain.meallog.dto.MealLogDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogResponseDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogStatsDto;
import com.ssafy.happymeal.domain.meallog.entity.MealLog;
import com.ssafy.happymeal.domain.user.dao.UserDAO;
import com.ssafy.happymeal.global.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealLogServiceIml implements MealLogService{

    private final MealLogDAO mealLogDAO;
    private final UserDAO userDAO;

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

    @Override
    public List<MealLogResponseDto> findByUserAndDate(Long userId, LocalDate mealDate) {
        return mealLogDAO.findByUserAndDate(userId, mealDate);
    }

    @Override
    public void deleteMealLog(Long userId, Long logId) throws NotFoundException {
        // 일치하는 식단 기록이 없을 경우
        MealLog mealLog = mealLogDAO.findById(logId)
                .orElseThrow(() -> new NotFoundException("식단 기록이 존재하지 않습니다."));

        // 일치하는 사용자가 아닌 경우
        if(!mealLog.getUserId().equals(userId)) {
            throw new ForbiddenException("본인의 기록만 삭제할 수 있습니다.");
        }
        mealLogDAO.deleteMealLog(userId, logId);
    }

    @Override
    public MealLogStatsDto getDailyMealStats(Long userId, LocalDate mealDate) {
        return mealLogDAO.getDailyMealStats(userId, mealDate);
    }

    @Override
    public List<MealLogResponseDto> getAllMealLogs(Long userId) {
        return mealLogDAO.getAllMealLogs(userId);
    }
}
