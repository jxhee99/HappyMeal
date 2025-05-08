package com.ssafy.happymeal.domain.meallog.service;

import com.ssafy.happymeal.domain.meallog.dao.MealLogDAO;
import com.ssafy.happymeal.domain.meallog.dto.MealLogDto;
import com.ssafy.happymeal.domain.meallog.entity.MealLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealLogServiceIml implements MealLogService{

    private final MealLogDAO mealLogDAO;

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
        log.info("✅ 최종 저장할 MealLog 객체: {}", mealLog);
    }
}
