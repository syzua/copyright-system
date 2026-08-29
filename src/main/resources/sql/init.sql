CREATE DATABASE IF NOT EXISTS copyright_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE copyright_db;

DROP TABLE IF EXISTS copyright_records;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码(SHA-256)',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER/ADMIN',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE copyright_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    reg_no VARCHAR(50) NOT NULL COMMENT '版权登记号',
    title VARCHAR(200) NOT NULL COMMENT '作品标题',
    author_name VARCHAR(100) NOT NULL COMMENT '作者姓名',
    user_id BIGINT NOT NULL COMMENT '登记用户ID',
    work_type VARCHAR(20) DEFAULT 'TEXT' COMMENT '作品类型: TEXT/IMAGE/AUDIO/VIDEO',
    content_hash VARCHAR(64) NOT NULL COMMENT '内容SHA-256哈希',
    block_hash VARCHAR(64) NOT NULL COMMENT '区块哈希(模拟链上存储)',
    timestamp BIGINT NOT NULL COMMENT '存证时间戳(毫秒)',
    status VARCHAR(20) DEFAULT 'REGISTERED' COMMENT '状态: REGISTERED/REVOKED',
    description TEXT COMMENT '作品描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    UNIQUE KEY uk_content_hash (content_hash),
    UNIQUE KEY uk_reg_no (reg_no),
    KEY idx_user_id (user_id),
    KEY idx_author (author_name),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='版权登记记录表';

INSERT INTO users (username, password, email, role) VALUES
('admin', '5e884898da28047151d0e56f8dc629757028e64f1c7a6c2b6c0c7c0c7c0c7c0c', 'admin@syzua.com', 'ADMIN'),
('testuser', '5e884898da28047151d0e56f8dc629757028e64f1c7a6c2b6c0c7c0c7c0c7c0c', 'test@syzua.com', 'USER');
