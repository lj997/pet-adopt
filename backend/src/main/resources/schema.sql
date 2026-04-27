-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet_adopt DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet_adopt;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    avatar VARCHAR(255),
    introduction TEXT,
    city VARCHAR(50),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 宠物表
CREATE TABLE IF NOT EXISTS pets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    breed VARCHAR(100),
    age INT,
    gender VARCHAR(10),
    health_condition TEXT,
    personality TEXT,
    sterilized BOOLEAN DEFAULT FALSE,
    vaccinated BOOLEAN DEFAULT FALSE,
    city VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    rescuer_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_type (type),
    INDEX idx_city (city),
    INDEX idx_rescuer_id (rescuer_id),
    FOREIGN KEY (rescuer_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 宠物照片表
CREATE TABLE IF NOT EXISTS pet_photos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    photo_url VARCHAR(255) NOT NULL,
    INDEX idx_pet_id (pet_id),
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 领养申请表
CREATE TABLE IF NOT EXISTS adoption_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    adopter_id BIGINT NOT NULL,
    personal_intro TEXT,
    pet_experience TEXT,
    housing_condition TEXT,
    contact_info VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    review_note TEXT,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    review_time DATETIME,
    INDEX idx_pet_id (pet_id),
    INDEX idx_adopter_id (adopter_id),
    INDEX idx_status (status),
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (adopter_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏表
CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_pet (user_id, pet_id),
    INDEX idx_user_id (user_id),
    INDEX idx_pet_id (pet_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
