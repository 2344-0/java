-- ============================================================
-- 斗破苍穹 · 丹药坊建表脚本（丹榜 + 品阶 + 功效 + 主材）
-- 数据库：doupo（utf8mb4）
-- 说明：供「丹药坊」页面展示，与异火榜 / 功法榜同属排行榜体系。
--       单独执行本文件即可建表并写入种子数据，不影响其它表。
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 丹药表：丹药坊页面卡片 + 按丹榜名次排序
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_pill`;
CREATE TABLE `t_pill`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '丹名（唯一）',
  `alias` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '别称',
  `grade` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品阶，如：九品宝丹 / 六品 / 三品',
  `effect` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '功效概述',
  `material` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主材料',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '丹药简介',
  `rank_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '丹榜名次，如：丹榜 · 第一',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前（对应丹榜名次）',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '丹药坊表' ROW_FORMAT = Dynamic;

-- ------------------------------------------------------------
-- 种子数据（丹榜名次 = sort 越小越靠前）
-- ------------------------------------------------------------
INSERT INTO `t_pill` (`name`, `alias`, `grade`, `effect`, `material`, `description`, `rank_name`, `sort`) VALUES
('菩提大还丹', '菩提化体涎所炼', '九品宝丹', '助人超脱生死桎梏，晋入斗圣之境', '菩提化体涎、菩提心', '天地间最难炼制的宝丹之一，服之可洗髓易经，于生死关头破而后立，乃斗气大陆传说级灵丹。', '丹榜 · 第一', 1),
('破宗丹',     '',                 '八品',     '助斗宗突破至斗尊之境',       '多种高阶灵药',       '斗宗巅峰强者梦寐以求，服下后冲破尊境壁垒，然凶险异常，炼制时丹雷九重。',         '丹榜 · 第二', 2),
('噬生丹',     '',                 '七品',     '强催潜力，助斗皇破入斗宗（折损寿元）', '噬生草',     '以寿元为代价强行提阶，乃绝境搏命之丹，非万不得已不可轻服。',                 '丹榜 · 第三', 3),
('破厄丹',     '',                 '六品',     '助斗王突破至斗皇之境',       '破厄龙血',         '温养经脉、化解突破瓶颈，是斗王问鼎斗皇的常备灵丹，迦南学院拍卖会常客。',       '丹榜 · 第四', 4),
('复魂丹',     '',                 '六品',     '修复灵魂创伤，凝聚碎裂之魂', '魂婴果、养魂木',   '炼药师与灵魂体最为倚重，可令受创之魂重新凝实，药老曾赖以续魂。',               '丹榜 · 第五', 5),
('阴阳命魂丹', '',                 '六品',     '为灵魂体重铸肉身根基',       '阴阳玄龙丹残方',   '传闻萧炎借此丹为药老复生肉身，堪称起死回生之奇药。',                           '丹榜 · 第六', 6),
('血气丹',     '',                 '五品',     '迅速疗伤，恢复斗气与气血',   '血蟒草、地心火',   '战场与秘境中常备，外伤内损皆可速愈，丹香血色，入腹如暖流贯体。',                 '丹榜 · 第七', 7),
('回春丹',     '',                 '三品',     '解毒疗伤，温养体魄',         '回春草',           '低阶斗者入门灵丹，价廉效稳，宗门与商队皆大量备货。',                             '丹榜 · 第八', 8),
('聚气丹',     '',                 '二品',     '辅助修炼，加速斗气凝聚',     '聚气花',           '炼气期斗者日常服用，筑基固本之上品，是修行路上第一味丹药。',                       '丹榜 · 第九', 9);

SET FOREIGN_KEY_CHECKS = 1;
