USE passjava_question;

CREATE TABLE ums_user (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          username VARCHAR(50) NOT NULL,
                          password VARCHAR(100) NOT NULL,
                          nickname VARCHAR(50),
                          role VARCHAR(20) NOT NULL DEFAULT 'USER',
                          status TINYINT NOT NULL DEFAULT 1,
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,
                          UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
