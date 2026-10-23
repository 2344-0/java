-- ============================================================
-- 斗破苍穹 · 登录注册 · 数据库初始化脚本
-- 数据库：MySQL 8.0    账号：root    密码：123456
-- 后端：D:/workSpace/springBootTest（dev 端口 8081）
-- 用法：在 MySQL 客户端或 IDEA Database 控制台执行本文件即可。
-- ============================================================

CREATE DATABASE IF NOT EXISTS doupo
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE doupo;

CREATE TABLE IF NOT EXISTS t_user (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  username   VARCHAR(64)  NOT NULL                COMMENT '斗者账号（唯一）',
  password   VARCHAR(100) NOT NULL                COMMENT '斗气秘钥（BCrypt 密文）',
  phone      VARCHAR(20)  DEFAULT NULL            COMMENT '绑定手机号（唯一）',
  created_at DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username),
  UNIQUE KEY uk_phone (phone)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '斗者表';
