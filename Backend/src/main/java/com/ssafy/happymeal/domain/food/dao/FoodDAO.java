package com.ssafy.happymeal.domain.food.dao; // DAO 인터페이스 패키지 경로

import com.ssafy.happymeal.domain.food.entity.Food; // Food DTO 경로
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper // 이 인터페이스가 MyBatis 매퍼임을 나타냅니다.
public interface FoodDAO {

    /**
     * 음식 정보 검색 (이름으로 LIKE 검색)
     * 접근 권한: ALL
     */
    @Select("SELECT food_id, name, category, serving_size, unit, calories, carbs, sugar, protein, fat, food_code, create_at, update_at " +
            "FROM Food " +
            "WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Food> searchByName(@Param("name") String name);

    /**
     * 모든 음식 정보 조회 (선택 사항: 필요에 따라 추가)
     * 접근 권한: ALL (또는 특정 권한)
     */
    @Select("SELECT food_id, name, category, serving_size, unit, calories, carbs, sugar, protein, fat, food_code, create_at, update_at " +
            "FROM Food")
    List<Food> findAll();

    /**
     * 특정 ID의 음식 정보 조회
     * 접근 권한: ALL (또는 특정 권한)
     */
    @Select("SELECT food_id, name, category, serving_size, unit, calories, carbs, sugar, protein, fat, food_code, create_at, update_at " +
            "FROM Food " +
            "WHERE food_id = #{foodId}")
    Optional<Food> findById(@Param("foodId") Long foodId);

    /**
     * 음식 정보 추가
     * 접근 권한: User, Admin
     * create_at은 NOW() 또는 CURRENT_TIMESTAMP로, update_at은 DB의 ON UPDATE CURRENT_TIMESTAMP를 활용하거나 명시적으로 설정합니다.
     * 테이블 DDL에 create_at DEFAULT CURRENT_TIMESTAMP가 있다면 해당 컬럼은 INSERT 문에서 생략 가능합니다.
     */
    @Insert("INSERT INTO Food (name, category, serving_size, unit, calories, carbs, sugar, protein, fat, food_code, create_at, update_at) " +
            "VALUES (#{name}, #{category}, #{servingSize}, #{unit}, #{calories}, #{carbs}, #{sugar}, #{protein}, #{fat}, #{foodCode}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "foodId", keyColumn = "food_id")
    int save(Food food);

    /**
     * 음식 정보 수정
     * 접근 권한: User, Admin
     * update_at은 DB의 ON UPDATE CURRENT_TIMESTAMP를 활용하거나 명시적으로 NOW() 또는 CURRENT_TIMESTAMP로 설정합니다.
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
            "food_code = #{foodCode}, " +
            "update_at = NOW() " + // 명시적으로 현재 시간으로 업데이트
            "WHERE food_id = #{foodId}")
    int update(Food food);

    /**
     * 음식 정보 삭제
     * 접근 권한: User, Admin
     */
    @Delete("DELETE FROM Food WHERE food_id = #{foodId}")
    int delete(@Param("foodId") Long foodId);
}