package com.ssafy.happymeal.domain.meallog.controller;

import com.ssafy.happymeal.domain.meallog.dto.MealLogDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogRequestDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogResponseDto;
import com.ssafy.happymeal.domain.meallog.dto.MealLogStatsDto;
import com.ssafy.happymeal.domain.meallog.entity.MealLog;
import com.ssafy.happymeal.domain.meallog.service.MealLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/meallogs")
@RequiredArgsConstructor
public class MealLogController {

    private final MealLogService mealLogService;

    // 식단 기록 추가
    @PostMapping
    public ResponseEntity<?> addMealLog(@RequestBody MealLogDto mealLogDto, @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails : 어떤 사용자가 식단을 등록하는지 알아야 MealLog.user_id에 넣을 수 있기 때문에 필요함
        Long userId = Long.parseLong(userDetails.getUsername());
        mealLogService.addMealLog(userId, mealLogDto);
        log.info("식단 기록 요청 : userId={}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 전체 식단 기록 조회
    @GetMapping
    public ResponseEntity<List<MealLogResponseDto>> getAllMealLogs(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<MealLogResponseDto> logs = mealLogService.getAllMealLogs(userId);
        log.info("식단 조회 요청 아이디 : userId={}", userId);
        return ResponseEntity.ok(logs);
    }

    // 특정 날짜 식단 기록 조회
    @GetMapping(params = "date")
    public ResponseEntity<List<MealLogResponseDto>> getMealLogsByMealDate(@RequestParam("date") String date, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        LocalDate mealDate = LocalDate.parse(date); // 프론트엔드에서 날짜 포맷은 반드시 YYYY-MM-DD 형식으로 맞춰 보내야함
        List<MealLogResponseDto> logs = mealLogService.findByUserAndDate(userId, mealDate);
        log.info("요청 조회 날짜 : mealDate={}", mealDate);
        return ResponseEntity.ok(logs);
    }

    // 특정 날짜 식단 통계 조회
    @GetMapping("/stats")
    public ResponseEntity<MealLogStatsDto> getMealLogStatsByMealDate(@RequestParam("date") String date, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        LocalDate mealDate = LocalDate.parse(date);
        MealLogStatsDto stats = mealLogService.getDailyMealStats(userId, mealDate);
        log.info("통계 요청 날짜 : mealDate={}", mealDate);
        return ResponseEntity.ok(stats);
    }

    // 주간 식단 통계 조회
    @GetMapping("/stats/weekly")
    public ResponseEntity<List<MealLogStatsDto>> getWeeklyMealLogStats(@RequestParam("date") String date, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        LocalDate endDate = LocalDate.parse(date);
        List<MealLogStatsDto> weeklyStats = mealLogService.getWeeklyMealLogStats(userId, endDate);
        log.info("주간 식단 통계 요청 날짜 : mealDate={}", date);
        return ResponseEntity.ok(weeklyStats);
    }

    // 식단 기록 상세 조회
    @GetMapping("/{logId}")
    public ResponseEntity<MealLogResponseDto> getDetailMealLog(@PathVariable Long logId, @AuthenticationPrincipal UserDetails userDetails) throws NotFoundException {
        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("상세 식단기록 요청 : userId={}, logId={}", userId, logId);
        MealLogResponseDto log = mealLogService.getDetailMealLog(userId, logId);
        return ResponseEntity.ok(log);
    }

    // 식단 기록 삭제
    @DeleteMapping("/{logId}")
    public ResponseEntity<?> deleteMealLog(@PathVariable Long logId, @AuthenticationPrincipal UserDetails userDetails) throws NotFoundException {
        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("식단 삭제 요청 : userId={}, logId={}", userId, logId);
        mealLogService.deleteMealLog(userId, logId);
        log.info("식단 삭제 완료 : logId={}", logId);
        return ResponseEntity.noContent().build();
    }

    // 식단 기록 수정
    @PutMapping("/{logId}")
    public ResponseEntity<?> updateMealLog(
            @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MealLogRequestDto requestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("식단 수정 요청 : userId={}, logId={}", userId, logId);
//        MealLog updateMealLog =
        mealLogService.updateMealLog(userId, logId, requestDto);
        log.info("식단 수정 완료 : logId={}",logId);
        return ResponseEntity.ok().build();

    }
}
