-- ============================================================
-- 斗破苍穹 · 后台管理员账号
-- 数据库：doupo（utf8mb4）
-- 说明：
--   1. 管理员现在与前台斗者共用 t_user 表，通过 permission 字段区分：
--          permission = 1  普通斗者（注册时默认）
--          permission = 0  管理员（可进入后台管理系统）
--   2. 密码按需求明文保存，不做加密。
--   3. 本脚本可重复执行，不会产生重复账号，也不会改动已有账号的密码。
--   4. 后端 AdminInitializer 也会在启动时检查并补建默认管理员，
--      因此本脚本仅用于手动建号 / 重置场景。
--   5. 旧的 t_admin 表已废弃，后台登录不再读取该表。
-- ============================================================

SET NAMES utf8mb4;

-- 默认管理员：admin / admin123
INSERT INTO `t_user` (`username`, `password`, `permission`)
SELECT 'admin', 'admin123', 0
WHERE NOT EXISTS (
  SELECT 1 FROM `t_user` WHERE `username` = 'admin'
);

-- 需要额外管理员时，把下面这行改个账号密码执行即可
-- INSERT INTO `t_user` (`username`, `password`, `permission`) VALUES ('zhangsan', '123456', 0);

-- 把已有普通斗者提升为管理员：
-- UPDATE `t_user` SET `permission` = 0 WHERE `username` = 'xxx';
