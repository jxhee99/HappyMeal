-- 데이터베이스 생성 및 선택
DROP DATABASE IF EXISTS nyamnyam;
CREATE DATABASE IF NOT EXISTS nyamnyam;
USE nyamnyam;

-- User 테이블 (컬럼명 변경: id <-> user_id)
CREATE TABLE User (
                      user_id BIGINT AUTO_INCREMENT PRIMARY KEY,      -- 고유 숫자 ID (PK, 자동 증가) ★★★ 이름 변경
                      id VARCHAR(50) NOT NULL UNIQUE,               -- 사용자 로그인 ID (Unique) ★★★ 이름 변경
                      password VARCHAR(255) NOT NULL,               -- 비밀번호 (해싱하여 저장)
                      nickname VARCHAR(50) NOT NULL UNIQUE,           -- 닉네임 (Unique)
                      role VARCHAR(10) NOT NULL DEFAULT 'USER',     -- 사용자 권한 ('USER', 'ADMIN')
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- 가입 일시
);

-- Food 테이블 (변경 없음)
CREATE TABLE Food (
                      food_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 고유 ID (PK)
                      name VARCHAR(100) NOT NULL,                 -- 음식명
                      category VARCHAR(50),                       -- 대분류
                      serving_size DECIMAL(10, 2) NOT NULL,       -- 기준량
                      unit VARCHAR(10) NOT NULL,                  -- 기준량 단위
                      calories DECIMAL(10, 2) NULL,               -- 칼로리 (NULL 허용)
                      protein DECIMAL(10, 2) NULL,                -- 단백질 (NULL 허용)
                      carbs DECIMAL(10, 2) NULL,                  -- 탄수화물 (NULL 허용)
                      fat DECIMAL(10, 2) NULL,                    -- 지방 (NULL 허용)
                      sugar DECIMAL(10, 2) NULL,                  -- 당류
                      food_code VARCHAR(50),                      -- 원본 식품 코드
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- MealLog 테이블 (User 테이블의 변경된 PK 'user_id' 참조)
CREATE TABLE MealLog (
                         log_id BIGINT AUTO_INCREMENT PRIMARY KEY,     -- 식사 기록 고유 ID (PK)
                         user_id BIGINT NOT NULL,                    -- 사용자 고유 숫자 ID (FK - User 테이블의 'user_id' 참조) ★★★ 참조 대상 컬럼명 확인
                         food_id BIGINT NOT NULL,                    -- 음식 ID (FK - Food 테이블 참조)
                         meal_date DATE NOT NULL,                    -- 식사 날짜
                         meal_type VARCHAR(20) NOT NULL,             -- 식사 종류 (예: 'BREAKFAST', 'LUNCH', 'DINNER', 'SNACK')
                         quantity DECIMAL(10, 2) NOT NULL,           -- 섭취량 (단위: g)
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 기록 생성 일시
                         FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE, -- ★★★ User(user_id) 참조
                         FOREIGN KEY (food_id) REFERENCES Food(food_id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Board 테이블 (User 테이블의 변경된 PK 'user_id' 참조)
CREATE TABLE Board (
                       post_id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- 게시글 고유 ID (PK)
                       user_id BIGINT NOT NULL,                    -- 작성자 고유 숫자 ID (FK - User 테이블의 'user_id' 참조) ★★★ 참조 대상 컬럼명 확인
                       title VARCHAR(255) NOT NULL,                -- 게시글 제목
                       content TEXT NOT NULL,                      -- 게시글 내용
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 작성 일시
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 일시
                       FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE -- ★★★ User(user_id) 참조
);

COMMIT;