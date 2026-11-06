-- ============================================================
-- 斗破苍穹 · 斗气榜 + 宗门建表脚本
-- 数据库：doupo（utf8mb4）
-- 说明：供「斗气榜」与「宗门」页面展示。
--       单独执行本文件即可建表并写入种子数据，不影响其它表。
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. 斗气境界表：斗气榜页面（修炼等级晋升之路）
--    sort = 晋升顺序，1 = 斗之气（起点），越大境界越高
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_douqi`;
CREATE TABLE `t_douqi`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '境界名称（唯一），如：斗者 / 斗帝',
  `tier_desc` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '阶位特征，如：内视识海，斗气成旋',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '境界简介',
  `sub_level` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '细分层级，如：一至九段 / 五星斗尊',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前（晋升顺序）',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '斗气境界表' ROW_FORMAT = Dynamic;

-- ------------------------------------------------------------
-- 2. 宗门/势力表：宗门页面（按势力榜名次排序）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_sect`;
CREATE TABLE `t_sect`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '势力名称（唯一）',
  `alias` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '别称',
  `grade` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '势力等级，如：超级 / 一流 / 二流',
  `leader` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '掌权者',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '势力简介',
  `rank_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '势力榜名次，如：势力榜 · 第一',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前（对应势力榜名次）',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宗门势力表' ROW_FORMAT = Dynamic;

-- ------------------------------------------------------------
-- 种子数据
-- ------------------------------------------------------------

-- 斗气境界（12 境，sort = 晋升顺序）
INSERT INTO `t_douqi` (`name`, `tier_desc`, `description`, `sub_level`, `sort`) VALUES
('斗之气', '感应天地斗气，凝于体内', '修炼之始，分一至九段，段满则可晋斗者，是为踏入斗气大陆的第一步。', '一至九段', 1),
('斗者',   '内视识海，斗气成旋',     '可内视己身，体内斗气旋涡初成，正式踏入修炼之途，寿元略增。',           '斗气旋',   2),
('斗师',   '斗气纱衣护体',           '斗气外放成纱衣，攻防兼具，已能御使简单斗技。',                         '纱衣',     3),
('大斗师', '斗气凝甲',               '斗气凝为铠甲，防御大增，可短暂滞空。',                                 '铠甲',     4),
('斗灵',   '斗气凝物',               '斗气化形，可凝兵器战技，战力远超前者。',                               '凝物',     5),
('斗王',   '斗气化翼，凌空飞行',     '背后生斗气双翼，可翱翔九天，威压常人，一方强者。',                       '化翼',     6),
('斗皇',   '空间扭曲',               '举手间扭曲空间，移山填海，宗门顶尖战力。',                             '空间',     7),
('斗宗',   '空间封锁',               '可封锁一方空间，斗气如渊，大陆名宿。',                                 '封空',     8),
('斗尊',   '引动星辰',               '调动星辰之力，一念碎虚空，星域之内我为尊。',                           '星力',     9),
('斗圣',   '位面之主',               '天地色变，弹指生灭城池，大陆顶尖战力，可开辟空间。',                   '圣域',     10),
('斗帝',   '源气加身',               '引动斗气大陆本源源气，立于万巅之巅，传说之境。',                       '源气',     11),
('斗帝之上', '不灭传说',             '超脱斗帝，传说中唯有陀舍古帝抵达，不灭不损。',                         '传说',     12);

-- 宗门势力（9 方，势力榜名次 = sort 越小越靠前）
INSERT INTO `t_sect` (`name`, `alias`, `grade`, `leader`, `description`, `rank_name`, `sort`) VALUES
('魂殿',     '',           '超级', '殿主（魂灭生）', '专司擒拿灵魂、炼化魂力，暗中操控大陆，诡谲凶残，为魂族爪牙。',           '势力榜 · 第一', 1),
('古族',     '古家',       '超级', '古元',           '远古八族之一，血脉传承古帝之力，底蕴深不可测，隐居古界。',               '势力榜 · 第二', 2),
('丹塔',     '',           '超级', '玄空子',         '大陆炼药师圣地，丹会云集天下药道强者，一言可定丹药品阶。',               '势力榜 · 第三', 3),
('星陨阁',   '',           '一流', '萧炎',           '由药尘创立，萧炎执掌后声名鹊起，阁众云集，中州新贵。',                 '势力榜 · 第四', 4),
('迦南学院', '',           '一流', '千木/玄衣',      '大陆第一学府，内院天焚练气塔藏异火，培育无数强者。',                   '势力榜 · 第五', 5),
('萧家',     '乌坦萧家',   '二流', '萧炎',           '加玛帝国没落家族，因萧炎崛起而重振声威，一门双斗圣。',                 '势力榜 · 第六', 6),
('云岚宗',   '',           '二流', '云韵',           '加玛帝国第一宗门，曾权倾朝野，后解体，今由云韵重整旗鼓。',             '势力榜 · 第七', 7),
('蛇人族',   '',           '二流', '美杜莎',         '塔戈尔沙漠之主，后结盟迦南，异火共治，族运昌隆。',                     '势力榜 · 第八', 8),
('魔炎谷',   '',           '二流', '地魔老鬼',       '黑角域凶名赫赫的邪派，以吞噬修炼为道，与星陨阁宿怨颇深。',             '势力榜 · 第九', 9);

SET FOREIGN_KEY_CHECKS = 1;
