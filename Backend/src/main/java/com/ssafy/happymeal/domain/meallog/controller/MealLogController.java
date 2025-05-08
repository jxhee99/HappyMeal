package com.ssafy.happymeal.domain.meallog.controller;

import com.ssafy.happymeal.domain.meallog.dto.MealLogDto;
import com.ssafy.happymeal.domain.meallog.service.MealLogService;
import com.ssafy.happymeal.domain.meallog.service.MealLogServiceIml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/meallogs")
@RequiredArgsConstructor
public class MealLogController {

    private final MealLogServiceIml mealLogService;

    @PostMapping
    public ResponseEntity<?> addMealLog(@RequestBody MealLogDto mealLogDto, @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails : 어떤 사용자가 식단을 등록하는지 알아야 MealLog.user_id에 넣을 수 있기 때문에 필요함
        Long userId = Long.parseLong(userDetails.getUsername());
        mealLogService.addMealLog(userId, mealLogDto);
        log.info("식단 기록 요청 : " +userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
