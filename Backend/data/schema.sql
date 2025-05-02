-- 데이터베이스 생성 및 선택
DROP DATABASE IF EXISTS nyamnyam;
CREATE DATABASE IF NOT EXISTS nyamnyam;
USE nyamnyam;
-- MySQL 기준 SQL 문 (Google 로그인 반영, ENUM 타입, 수정된 FK, 컬럼 제약조건 수정, MealLog.img_url 추가)

-- 사용자 정보 테이블 (password 삭제, role 변경, profile_image_url 추가)
CREATE TABLE User (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 고유 내부 PK ID',
    google_id VARCHAR(255) NOT NULL UNIQUE COMMENT 'Google 사용자 고유 ID (sub)',
    email VARCHAR(255) UNIQUE COMMENT '사용자 이메일 (Google 제공, Null 허용)',
    nickname VARCHAR(50) NOT NULL COMMENT '사용자 닉네임 (Unique)',
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER' COMMENT '사용자 권한 (String 타입, 예: ROLE_USER, ROLE_ADMIN)',
    profile_image_url VARCHAR(512) NULL COMMENT '사용자 프로필 이미지 URL', -- 추가됨
    -- password 컬럼 삭제됨
    create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입 일시'
) ENGINE=InnoDB COMMENT '사용자 정보';

-- 음식 영양 정보 테이블 (컬럼 제약조건 수정됨)
CREATE TABLE Food (
                      food_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '음식 고유 PK ID',
                      name VARCHAR(100) NOT NULL COMMENT '음식 이름',
                      calories DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '칼로리 (100g 기준)',
                      protein DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '단백질 (g, 100g 기준)',
                      carbs DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '탄수화물 (g, 100g 기준)',
                      fat DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '지방 (g, 100g 기준)',
                      serving_size DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '1회 제공량',
                      unit VARCHAR(10) NOT NULL DEFAULT '' COMMENT '단위 (예: g, ml, 개)',
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '정보 생성 일시',
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '정보 수정 일시'
) ENGINE=InnoDB COMMENT '음식 영양 정보';

-- 사용자 음식 등록 요청 테이블 (컬럼 제약조건 수정됨)
CREATE TABLE FoodRequest (
                             food_request_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '음식 요청 고유 PK ID',
                             name VARCHAR(100) NOT NULL COMMENT '요청 음식 이름',
                             calories DECIMAL(10, 2) NOT NULL DEFAULT 0,
                             protein DECIMAL(10, 2) NOT NULL DEFAULT 0,
                             carbs DECIMAL(10, 2) NOT NULL DEFAULT 0,
                             fat DECIMAL(10, 2) NOT NULL DEFAULT 0,
                             serving_size DECIMAL(10, 2) NOT NULL DEFAULT 0,
                             unit VARCHAR(10) NOT NULL DEFAULT '',
                             is_registered ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING' COMMENT '등록 처리 여부',
                             user_id BIGINT NOT NULL COMMENT '요청 사용자 ID (User 테이블 PK 참조)',
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '요청 생성 일시',
                             FOREIGN KEY (user_id) REFERENCES User(user_id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT '사용자 음식 등록 요청';

-- 사용자 식사 기록 테이블 (img_url 컬럼 추가됨)
CREATE TABLE MealLog (
                         log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '식사 기록 고유 PK ID',
                         user_id BIGINT NOT NULL COMMENT '기록한 사용자 ID (User 테이블 PK 참조)',
                         food_id BIGINT NOT NULL COMMENT '섭취한 음식 ID (Food 테이블 PK 참조)',
                         meal_date DATE NOT NULL COMMENT '식사 날짜',
                         meal_type ENUM('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK') NOT NULL COMMENT '식사 종류',
                         quantity DECIMAL(10, 2) NOT NULL COMMENT '섭취량 (g 단위)',
                         img_url VARCHAR(512) NULL COMMENT '식단 사진 이미지 URL', -- 추가됨
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '기록 생성 일시',
                         FOREIGN KEY (user_id) REFERENCES User(user_id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,
                         FOREIGN KEY (food_id) REFERENCES Food(food_id)
                             ON DELETE RESTRICT
                             ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT '사용자 식사 기록';

-- 커뮤니티 게시판 테이블
CREATE TABLE Board (
                       post_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 고유 PK ID',
                       user_id BIGINT NOT NULL COMMENT '작성자 ID (User 테이블 PK 참조)',
                       title VARCHAR(255) NOT NULL COMMENT '게시글 제목',
                       content TEXT NOT NULL COMMENT '게시글 내용',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성 일시',
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
                       FOREIGN KEY (user_id) REFERENCES User(user_id)
                           ON DELETE CASCADE
                           ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT '커뮤니티 게시판';