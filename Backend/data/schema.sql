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
                      category VARCHAR(20) NULL COMMENT '음식 분류',
                      serving_size DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '1회 제공량',
                      unit VARCHAR(10) NOT NULL DEFAULT '' COMMENT '단위 (예: g, ml, 개)',
                      calories DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '칼로리 (100g 기준)',
                      carbs DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '탄수화물 (g, 100g 기준)',
                      sugar DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '당류 (g, 100g 기준)',
                      protein DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '단백질 (g, 100g 기준)',
                      fat DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '지방 (g, 100g 기준)',
                      img_url LONGTEXT NULL COMMENT '음식 대표 이미지 URL', -- VARCHAR(512)에서 LONGTEXT로 변경
                      food_code VARCHAR(100) NULL COMMENT '음식 고유 코드',
                      create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '정보 생성 일시',
                      update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '정보 수정 일시'
) ENGINE=InnoDB COMMENT '음식 영양 정보';

-- 수정된 버전
CREATE TABLE FoodRequest (
                             food_request_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '음식 요청 고유 PK ID',
                             user_id BIGINT NOT NULL COMMENT '요청 사용자 ID (User 테이블 PK 참조)',
                             name VARCHAR(100) NOT NULL COMMENT '요청 음식 이름',
                             category VARCHAR(20) NULL COMMENT '요청 음식 분류 (선택 사항, 관리자 최종 결정 가능)', -- 선택적 추가
                             serving_size DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '1회 제공량',
                             unit VARCHAR(10) NOT NULL DEFAULT '' COMMENT '단위 (예: g, ml, 개)',
                             calories DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '칼로리',
                             carbs DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '탄수화물 (g)',
                             sugar DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '당류 (g)', -- 필수 추가 제안
                             protein DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '단백질 (g)',
                             fat DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '지방 (g)',
                             img_url LONGTEXT NULL COMMENT '음식 대표 이미지 URL', -- VARCHAR(512)에서 LONGTEXT로 변경
                             is_registered ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING' COMMENT '등록 처리 여부',
    -- admin_comment TEXT NULL COMMENT '관리자 의견 또는 거절 사유 (선택적 추가)',
                             create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '요청 생성 일시',
    -- update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '요청 수정 일시 (선택적 추가)',
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
                         create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '기록 생성 일시',
                         FOREIGN KEY (user_id) REFERENCES User(user_id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,
                         FOREIGN KEY (food_id) REFERENCES Food(food_id)
                             ON DELETE RESTRICT
                             ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT '사용자 식사 기록';

-- 커뮤니티 게시판 테이블
CREATE TABLE Board (
                       board_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 고유 ID',
                       user_id BIGINT NOT NULL COMMENT '작성자 ID (User 테이블 PK 참조)', -- BIGINT로 수정
                       category_id INT NULL COMMENT '카테고리 ID (BoardCategory 테이블 PK 참조)', -- BoardCategory 테이블 참조
                       title VARCHAR(255) NOT NULL COMMENT '게시글 제목',
                       create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '게시글 생성 일시',
                       update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '게시글 수정 일시',
                       views INT DEFAULT 0 COMMENT '조회수',
                       likes_count INT DEFAULT 0 COMMENT '좋아요 수',
                       comments_count INT DEFAULT 0 COMMENT '댓글 수',
                       CONSTRAINT fk_board_user_id FOREIGN KEY (user_id) REFERENCES User(user_id)
                           ON DELETE CASCADE
) ENGINE=InnoDB COMMENT '커뮤니티 게시판';

-- 게시글 콘텐츠 블록 테이블
CREATE TABLE Block (
                       block_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '블록 고유 ID',
                       board_id BIGINT NOT NULL COMMENT '게시글 ID (Board 테이블 PK 참조)', -- post_id에서 board_id로 변경하여 일관성 유지
                       order_index INT NOT NULL COMMENT '게시글 내 블록 순서',
                       block_type VARCHAR(50) NOT NULL COMMENT '블록 타입 (예: text, image, video)',
                       content_text TEXT NULL COMMENT '텍스트 내용 (block_type이 text인 경우)',
                       image_url VARCHAR(512) NULL COMMENT '이미지 URL (block_type이 image인 경우)', -- 길이 수정
                       image_caption VARCHAR(255) NULL COMMENT '이미지 캡션',
                       create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '블록 생성 일시',
                       update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '블록 수정 일시',
                       CONSTRAINT fk_block_board_id FOREIGN KEY (board_id) REFERENCES Board(board_id) -- FK 컬럼명 일치
                           ON DELETE CASCADE, -- 게시글이 삭제되면 해당 블록들도 모두 삭제
                       INDEX idx_board_order (board_id, order_index) -- 특정 게시물의 콘텐츠 블록을 순서대로 가져올 때 성능 향상
) ENGINE=InnoDB COMMENT '게시글 콘텐츠 블록';

-- 게시글 좋아요 테이블
CREATE TABLE BoardLike (
                           user_id BIGINT NOT NULL COMMENT '좋아요를 누른 사용자 ID (User 테이블 PK 참조)',
                           board_id BIGINT NOT NULL COMMENT '좋아요 대상 게시글 ID (Board 테이블 PK 참조)',
                           create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '좋아요 누른 일시',

                           PRIMARY KEY (user_id, board_id) COMMENT '사용자 ID와 게시글 ID 조합으로 PK 설정 (중복 좋아요 방지)',

                           CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES User(user_id)
                               ON DELETE CASCADE, -- 사용자 삭제 시 해당 사용자 좋아요 기록 삭제
                           CONSTRAINT fk_like_board FOREIGN KEY (board_id) REFERENCES Board(board_id)
                               ON DELETE CASCADE, -- 게시글 삭제 시 해당 게시글 좋아요 기록 삭제

                           INDEX idx_like_board (board_id) COMMENT '게시글별 좋아요 수 조회를 위한 인덱스 (선택적)'
) ENGINE=InnoDB COMMENT '게시글 좋아요 정보';

-- 댓글 테이블
CREATE TABLE Comment (
                         comment_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 고유 ID',
                         board_id BIGINT NOT NULL COMMENT '댓글이 달린 게시글 ID (Board 테이블 PK 참조)',
                         user_id BIGINT NOT NULL COMMENT '댓글 작성자 ID (User 테이블 PK 참조)',
                         content TEXT NOT NULL COMMENT '댓글 내용',
                         parent_comment_id BIGINT NULL COMMENT '부모 댓글 ID (대댓글인 경우)', -- 대댓글 위한 컬럼 추가
                         create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '댓글 생성 일시',
                         update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '댓글 수정 일시',
                         CONSTRAINT fk_comment_board FOREIGN KEY (board_id) REFERENCES Board(board_id)
                             ON DELETE CASCADE, -- 게시글 삭제 시 관련 댓글 모두 삭제
                         CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES User(user_id)
                             ON DELETE CASCADE, -- 사용자 삭제 시 해당 사용자 댓글 모두 삭제 (정책에 따라 변경 가능)
                         CONSTRAINT fk_comment_parent FOREIGN KEY (parent_comment_id) REFERENCES Comment(comment_id)
                             ON DELETE CASCADE, -- 부모 댓글 삭제 시 자식 댓글도 삭제 (정책에 따라 변경 가능)
                         INDEX idx_comment_board (board_id) COMMENT '게시글별 댓글 조회를 위한 인덱스',
                         INDEX idx_board_user (board_id, user_id) COMMENT '게시글 ID 및 사용자 ID 복합 인덱스'
) ENGINE=InnoDB COMMENT '게시글 댓글 정보';

select * from food;