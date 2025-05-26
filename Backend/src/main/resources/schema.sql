CREATE TABLE IF NOT EXISTS FoodRequest (
    food_request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    serving_size DOUBLE NOT NULL,
    unit VARCHAR(20) NOT NULL,
    calories DOUBLE NOT NULL,
    carbs DOUBLE NOT NULL,
    sugar DOUBLE NOT NULL,
    protein DOUBLE NOT NULL,
    fat DOUBLE NOT NULL,
    img_url LONGTEXT,
    is_registered VARCHAR(20) NOT NULL,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE IF NOT EXISTS Food (
    food_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    serving_size DOUBLE NOT NULL,
    unit VARCHAR(20) NOT NULL,
    calories DOUBLE NOT NULL,
    carbs DOUBLE NOT NULL,
    sugar DOUBLE NOT NULL,
    protein DOUBLE NOT NULL,
    fat DOUBLE NOT NULL,
    img_url LONGTEXT,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); 