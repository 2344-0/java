-- ============================================================
-- 斗破苍穹 · 宗门加入功能 · 增量脚本
-- 数据库：doupo
-- 说明：为 t_user 增加 sect（所属宗门）字段，用于记录斗者归属的宗门。
--       本脚本幂等，可重复执行（字段已存在时自动跳过）。
-- ============================================================

USE doupo;

-- 幂等添加 sect 字段
SET @col_exists = (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = 'doupo' AND TABLE_NAME = 't_user' AND COLUMN_NAME = 'sect'
);
SET @add_sql = IF(@col_exists = 0,
  'ALTER TABLE t_user ADD COLUMN sect VARCHAR(64) NULL DEFAULT NULL COMMENT ''所属宗门（对应 t_sect.name），NULL 表示未加入'' AFTER phone',
  'SELECT 1'
);
PREPARE stmt FROM @add_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 说明：sect 为 NULL 或空串即表示尚未加入任何宗门，无需额外初始化数据。
