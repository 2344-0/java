-- ============================================================
-- 斗破苍穹 · 后台管理系统管理员表
-- 数据库：doupo（utf8mb4）
-- 说明：
--   1. 本脚本只建表，使用 IF NOT EXISTS，可重复执行，不会破坏已有数据。
--   2. 默认管理员账号由后端 AdminInitializer 在首次启动时自动写入
--      （密码用 BCrypt 加密，避免把密文硬编码在 SQL 里）：
--          账号：admin     密码：admin123
--   3. 与前台斗者表 t_user 完全隔离，后台没有注册入口。
-- ============================================================

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `t_admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员账号（唯一）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（BCrypt 密文）',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '显示名称',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台管理员表' ROW_FORMAT = Dynamic;
