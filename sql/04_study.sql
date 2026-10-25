CREATE DATABASE IF NOT EXISTS passjava_study
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE passjava_study;

-- 学习浏览记录
CREATE TABLE sms_view_log (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              question_id BIGINT NOT NULL,
                              view_count INT NOT NULL DEFAULT 1,
                              last_view_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              UNIQUE KEY uk_user_question (user_id, question_id),
                              INDEX idx_user_time (user_id, last_view_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户收藏
CREATE TABLE sms_favorite (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              question_id BIGINT NOT NULL,
                              create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              UNIQUE KEY uk_user_question (user_id, question_id),
                              INDEX idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 错题本
CREATE TABLE sms_wrong_question (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    user_id BIGINT NOT NULL,
                                    question_id BIGINT NOT NULL,
                                    wrong_count INT NOT NULL DEFAULT 1,
                                    last_wrong_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    UNIQUE KEY uk_user_question (user_id, question_id),
                                    INDEX idx_user_time (user_id, last_wrong_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 每日学习统计
CREATE TABLE sms_daily_stat (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                user_id BIGINT NOT NULL,
                                study_date DATE NOT NULL,
                                view_count INT NOT NULL DEFAULT 0,
                                UNIQUE KEY uk_user_date (user_id, study_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 已处理事件
CREATE TABLE sms_processed_event (
                                     event_id VARCHAR(64) PRIMARY KEY,
                                     processed_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 尚未成功发布到 RabbitMQ 的业务事件
CREATE TABLE sms_event_outbox (
                                  event_id VARCHAR(64) PRIMARY KEY,

                                  user_id BIGINT NOT NULL,
                                  question_id BIGINT NOT NULL,
                                  occurred_at DATETIME NOT NULL,

                                  status TINYINT NOT NULL DEFAULT 0,
                                  retry_count INT NOT NULL DEFAULT 0,

                                  next_retry_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  locked_at DATETIME NULL,

                                  last_error VARCHAR(500),

                                  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                      ON UPDATE CURRENT_TIMESTAMP,

                                  INDEX idx_status_retry (status, next_retry_at),
                                  INDEX idx_status_locked (status, locked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;