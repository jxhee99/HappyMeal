package com.ssafy.happymeal.domain.food.dao; // DAO 인터페이스 패키지 경로

import com.ssafy.happymeal.domain.food.entity.Food; // Food 엔티티 경로 (FoodDto가 아님)
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper // 이 인터페이스가 MyBatis 매퍼임을 나타냅니다.
public interface FoodDAO {

    // 모든 SELECT 쿼리에서 일관되게 사용할 컬럼 목록 정의
    // DB 컬럼명은 스네이크 케이스(create_at, update_at) 그대로 사용. Java 필드(createdAt, updatedAt)와는 MyBatis 설정(mapUnderscoreToCamelCase)으로 자동 매핑.
    String BASE_COLUMNS = "food_id, name, category, serving_size, unit, calories, carbs, sugar, protein, fat, img_url, food_code, create_at, update_at";

    /**
     * 음식 정보 검색 (이름으로 LIKE 검색)
     */
    @Select("SELECT " + BASE_COLUMNS + " " +
            "FROM Food " +
            "WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Food> searchByName(@Param("name") String name);

    /**
     * 모든 음식 정보 조회
     */
    @Select("SELECT " + BASE_COLUMNS + " FROM Food")
    List<Food> findAll();

    /**
     * 특정 ID의 음식 정보 조회
     */
    @Select("SELECT " + BASE_COLUMNS + " " +
            "FROM Food " +
            "WHERE food_id = #{foodId}")
    Optional<Food> findById(@Param("foodId") Long foodId);

    /**
     * 음식 정보 추가
     * create_at은 DB의 DEFAULT CURRENT_TIMESTAMP 활용
     * update_at은 INSERT 시 명시적으로 현재 시간 설정 또는 DB 기본값 활용 가능 (여기서는 명시적 설정)
     */
    @Insert("INSERT INTO Food (name, category, serving_size, unit, calories, carbs, sugar, protein, fat, food_code, img_url, update_at) " + // create_at 컬럼 생략 (DB 기본값 사용)
            "VALUES (#{name}, #{category}, #{servingSize}, #{unit}, #{calories}, #{carbs}, #{sugar}, #{protein}, #{fat}, #{foodCode}, #{imgUrl}, NOW())") // imgURL -> imgUrl, create_at 생략, update_at는 NOW()로 설정
    @Options(useGeneratedKeys = true, keyProperty = "foodId", keyColumn = "food_id")
    int save(Food food); // 파라미터 타입은 Food 엔티티

    /**
     * 음식 정보 수정
     * update_at은 DB의 ON UPDATE CURRENT_TIMESTAMP를 활용하거나, 명시적으로 NOW()로 설정 가능 (여기서는 DB 자동 업데이트 활용)
     */
    @Update("UPDATE Food SET " +
            "name = #{name}, " +
            "category = #{category}, " +
            "serving_size = #{servingSize}, " +
            "unit = #{unit}, " +
            "calories = #{calories}, " +
            "carbs = #{carbs}, " +
            "sugar = #{sugar}, " +
            "protein = #{protein}, " +
            "fat = #{fat}, " +
            "img_url = #{imgUrl}, " +
            "food_code = #{foodCode} " +
            // update_at 컬럼 생략 (DB의 ON UPDATE CURRENT_TIMESTAMP 기능 활용)
            "WHERE food_id = #{foodId}")
    int update(Food food); // 파라미터 타입은 Food 엔티티

    /**
     * 음식 정보 삭제
     */
    @Delete("DELETE FROM Food WHERE food_id = #{foodId}")
    int delete(@Param("foodId") Long foodId);

    /**
     * 간결화된 추천 음식 조회 메소드 (영양소 수치 기반 필터링, 랜덤 3개)
     */
    @Select("<script>" +
            "SELECT " + BASE_COLUMNS + " FROM Food " +
            "<where>" +
            // 영양소 기준 필터링
            "   <if test='params.maxCalories != null'> AND calories &lt;= #{params.maxCalories} </if>" +
            "   <if test='params.minCalories != null'> AND calories &gt;= #{params.minCalories} </if>" +
            "   <if test='params.maxCarbs != null'> AND carbs &lt;= #{params.maxCarbs} </if>" +
            "   <if test='params.minCarbs != null'> AND carbs &gt;= #{params.minCarbs} </if>" +
            "   <if test='params.maxSugar != null'> AND sugar &lt;= #{params.maxSugar} </if>" +
            "   <if test='params.minSugar != null'> AND sugar &gt;= #{params.minSugar} </if>" +
            "   <if test='params.maxProtein != null'> AND protein &lt;= #{params.maxProtein} </if>" +
            "   <if test='params.minProtein != null'> AND protein &gt;= #{params.minProtein} </if>" +
            "   <if test='params.maxFat != null'> AND fat &lt;= #{params.maxFat} </if>" +
            "   <if test='params.minFat != null'> AND fat &gt;= #{params.minFat} </if>" +
            "</where>" +
            " ORDER BY RAND() LIMIT 4" + // 항상 랜덤 3개
            "</script>")
    List<Food> findRecommendFoods(@Param("params") Map<String, Object> params);
}