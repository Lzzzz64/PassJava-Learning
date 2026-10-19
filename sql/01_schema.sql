CREATE DATABASE IF NOT EXISTS passjava_question
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE passjava_question;

-- 题目分类表
CREATE TABLE IF NOT EXISTS qms_type (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        type VARCHAR(100) NOT NULL,
    comments VARCHAR(255),
    logo_url VARCHAR(255),
    del_flag TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 题目表
CREATE TABLE IF NOT EXISTS qms_question (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            title VARCHAR(255) NOT NULL,
    answer TEXT,
    level TINYINT NOT NULL DEFAULT 1,
    display_order INT NOT NULL DEFAULT 0,
    sub_title VARCHAR(255),
    type BIGINT NOT NULL,
    enable TINYINT NOT NULL DEFAULT 1,
    del_flag TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (type)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
