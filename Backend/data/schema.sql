drop database if exists nyamnyam;
create database if not exists nyamnyam;
use nyamnyam;

CREATE TABLE Food (
    food_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 고유 ID (PK)
    name VARCHAR(100) NOT NULL,                 -- 음식명 (CSV: 식품명)
    category VARCHAR(50),                       -- 대분류 (CSV: 식품대분류명) - 선택적 추가 권장
    serving_size DECIMAL(10, 2) NOT NULL,       -- 기준량food (CSV: 영양성분함량기준량 숫자) - 예: 100
    unit VARCHAR(10) NOT NULL,                  -- 기준량 단위 (CSV: 영양성분함량기준량 단위) - 예: 'g'
    calories DECIMAL(10, 2) NOT NULL,           -- 칼로리 (CSV: 에너지(kcal))
    carbs DECIMAL(10, 2) NULL,              -- 탄수화물 (CSV: 탄수화물(g))
    sugar DECIMAL(10, 2) NULL,              -- 당류 (CSV: 당류(g))
    protein DECIMAL(10, 2) NULL,            -- 단백질 (CSV: 단백질(g))
    fat DECIMAL(10, 2) NULL,                -- 지방 (CSV: 지방(g))
    food_code VARCHAR(50),                      -- 원본 식품 코드 (CSV: 식품코드) - 선택적 추가 권장
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE User (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 고유 숫자 ID (PK) ★★★ 추가
    user_id VARCHAR(50) NOT NULL UNIQUE,         -- 사용자 로그인 ID (Unique) ★★★ 역할 변경
    password VARCHAR(255) NOT NULL,             -- 비밀번호 (BCrypt 등으로 해싱하여 저장)
    nickname VARCHAR(50) NOT NULL UNIQUE,         -- 닉네임 (Unique)
    role VARCHAR(10) NOT NULL DEFAULT 'USER',   -- 사용자 권한 ('USER', 'ADMIN')
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- 가입 일시
);

select * from User;

CREATE TABLE MealLog (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,     -- 식사 기록 고유 ID (PK)
    user_id BIGINT NOT NULL,                    -- 사용자 고유 숫자 ID (FK - User 테이블의 'id' 참조) ★★★ 타입 변경
    food_id BIGINT NOT NULL,                    -- 음식 ID (FK - Food 테이블 참조)
    meal_date DATE NOT NULL,                    -- 식사 날짜
    meal_type VARCHAR(20) NOT NULL,             -- 식사 종류 (예: 'BREAKFAST', 'LUNCH', 'DINNER', 'SNACK')
    quantity DECIMAL(10, 2) NOT NULL,           -- 섭취량 (단위: g)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 기록 생성 일시
    FOREIGN KEY (user_id) REFERENCES User(id),    -- ★★★ 참조 컬럼 변경
    FOREIGN KEY (food_id) REFERENCES Food(food_id)
);

CREATE TABLE Board (
    post_id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- 게시글 고유 ID (PK)
    user_id BIGINT NOT NULL,                    -- 작성자 고유 숫자 ID (FK - User 테이블의 'id' 참조) ★★★ 타입 변경
    title VARCHAR(255) NOT NULL,                -- 게시글 제목
    content TEXT NOT NULL,                      -- 게시글 내용
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 작성 일시
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 일시
    FOREIGN KEY (user_id) REFERENCES User(id)     -- ★★★ 참조 컬럼 변경
);

commit;
