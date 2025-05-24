package com.ssafy.happymeal.scheduler;

import com.ssafy.happymeal.domain.food.service.FoodService; // FoodService 인터페이스 경로
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j // Lombok을 사용하여 로깅 기능을 쉽게 추가합니다.
@Component // 이 클래스를 Spring이 관리하는 컴포넌트로 등록합니다.
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어 의존성을 주입합니다.
public class RecommendationScheduler {

    private final FoodService foodService; // FoodService를 주입받아 캐시 업데이트 작업을 위임합니다.

    // 주기적으로 캐시를 예열(warm-up)할 대상 카테고리 목록입니다.
    // 프론트엔드에서 주로 사용하는 카테고리들을 여기에 포함시킬 수 있습니다.
    // 이 목록은 설정 파일(application.properties 또는 yml)에서 읽어오도록 개선할 수도 있습니다.
    private static final List<String> TARGET_CATEGORIES_FOR_WARMING = Arrays.asList(
            "diet",
            "healthy",
            "bulk-up",
            "cheating"
            // 필요에 따라 다른 주요 카테고리 추가
    );

    /**
     * 매일 새벽 2시에 실행되어 주요 카테고리들의 추천 음식 캐시를 갱신합니다.
     * cron 표현식: "초 분 시 일 월 요일" (연도는 생략 가능)
     * "0 0 2 * * ?" 의미: 매일(어떤 요일이든), 어떤 달이든, 어떤 날이든, 새벽 2시 0분 0초에 실행
     */
    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시에 실행
    // 테스트를 위해 짧은 주기로 변경 가능: 예) "0 */5 * * * ?" (매 5분마다 0초에 실행)
    public void refreshRecommendationCache() {
        log.info("추천 음식 캐시 갱신 스케줄 작업 시작...");

        for (String categoryName : TARGET_CATEGORIES_FOR_WARMING) {
            try {
                log.info("카테고리 '{}'에 대한 캐시 갱신 시도...", categoryName);
                // FoodService의 getRecommendedFoods 메서드를 호출합니다.
                // 이 메서드 내부에 캐시 조회, DB 조회, 캐시 저장 로직이 모두 포함되어 있으므로,
                // 단순히 호출하는 것만으로 캐시가 채워지거나 갱신됩니다.
                foodService.getRecommendedFoods(categoryName);
                log.info("카테고리 '{}' 캐시 갱신 성공 또는 이미 최신 상태임.", categoryName);
            } catch (Exception e) {
                // 특정 카테고리 처리 중 예외가 발생하더라도 다른 카테고리 처리는 계속 진행되도록 합니다.
                log.error("카테고리 '{}' 캐시 갱신 중 오류 발생!", categoryName, e);
            }
        }
        log.info("추천 음식 캐시 갱신 스케줄 작업 완료.");
    }

    // (선택 사항) 애플리케이션 시작 시 한번 캐시를 채우는 로직 추가 가능
    // @PostConstruct
    // public void initRecommendationCacheOnStartup() {
    //    log.info("애플리케이션 시작 시 추천 음식 캐시 초기화 작업 시작...");
    //    refreshRecommendationCache(); // 위 스케줄링 메서드 재활용
    //    log.info("애플리케이션 시작 시 추천 음식 캐시 초기화 작업 완료.");
    // }
}