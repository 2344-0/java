-- ============================================================
-- 斗破苍穹 · 首页功能建表脚本（搜索 + 首页展示）
-- 数据库：doupo（utf8mb4）
-- 说明：与前端 HomePanel.vue 的展示内容一一对应，
--       把原本写死在页面的数据迁移到数据库。
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. 异火图鉴表：首页「异火图鉴」卡片 + 搜索命中对象之一
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_fire`;
CREATE TABLE `t_fire`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '异火名称（唯一）',
  `rank_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '榜单名次，如：异火榜 · 第三',
  `level` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品阶描述，如：榜三 · 灵智化生',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '异火简介',
  `keywords` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索关键词（空格分隔），如：净莲妖火 妖火 莲台',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '异火配图路径（相对站点根，如：/images/fires/jinglian_yaohuo.png）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '异火图鉴表' ROW_FORMAT = Dynamic;

-- ------------------------------------------------------------
-- 2. 功法榜表：首页「热门功法」列表 + 搜索命中对象之一
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_gongfa`;
CREATE TABLE `t_gongfa`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '功法/斗技名称（唯一）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '功法简介',
  `hot_value` bigint NOT NULL DEFAULT 0 COMMENT '热度值（用于排序展示）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '热门功法表' ROW_FORMAT = Dynamic;

-- ------------------------------------------------------------
-- 3. 斗者动态表：首页「斗者动态」信息流
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_feed_post`;
CREATE TABLE `t_feed_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `author` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布者（斗者名）',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '动态内容',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间（后端据此换算「片刻前 / 昨日」等文案）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_created`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '斗者动态表' ROW_FORMAT = Dynamic;

-- ------------------------------------------------------------
-- 4. 热搜词表：首页搜索框下方「热门：」词条
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_hot_word`;
CREATE TABLE `t_hot_word`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `word` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '热词内容（唯一）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_word`(`word` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '首页热搜词表' ROW_FORMAT = Dynamic;

-- ------------------------------------------------------------
-- 5. 丹药坊表：丹药坊页面卡片（丹榜名次 + 品阶 + 功效 + 主材）
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

-- ============================================================
-- 种子数据（与前端 HomePanel.vue 原写死内容一致）
-- ============================================================

-- 异火图鉴（8 道，image 指向前端 public/images/fires/ 下的配图）
INSERT INTO `t_fire` (`name`, `rank_name`, `level`, `description`, `keywords`, `image`, `sort`) VALUES
('净莲妖火',     '异火榜 · 第三',  '榜三 · 灵智化生', '呈莲花之形，可化人形，焚尽万物而灵智自生，乃异火中罕见具魂者。', '净莲妖火 妖火 莲台', '/images/fires/jinglian_yaohuo.png', 3),
('青莲地心火',   '异火榜 · 第十九','榜十九 · 地心所孕','生于地心熔岩，莲台模样，可炼体亦可炼丹，温润而炽烈。',           '青莲地心火 青莲 地心', '/images/fires/qinglian_dixinhuo.png', 19),
('陨落心炎',     '异火榜 · 第十四','榜十四 · 助修炼', '潜伏体内助长斗气，修炼事半功倍，然反噬亦烈，需以心智制之。',     '陨落心炎 心炎 修炼', '/images/fires/yunluo_xinyan.png', 14),
('骨灵冷火',     '异火榜 · 第十一','榜十一 · 冰火相济','韩枫师徒所掌之异火，与焚决相融可成冰火双重，诡谲非常。',         '骨灵冷火 骨灵 冷火', '/images/fires/guling_lenghuo.png', 11),
('海心焰',       '异火榜 · 第十五','榜十五 · 深海洋流','生于万丈深海之底，呈幽蓝之色，炽热中藏冰凉，炼药之上品。',       '海心焰 海心 海洋', '/images/fires/haixin_yan.png', 15),
('三千焱炎火',   '异火榜 · 第九',  '榜九 · 星辰不灭', '又称三千星空焱炎，具不灭之体，受创可自星空重生，威能滔天。',     '三千焱炎火 三千 星空', '/images/fires/sanqian_yanyanhuo.png', 9),
('九幽金祖火',   '异火榜 · 第六',  '榜六 · 金芒镇幽', '幽冥之中生出金芒，可焚魂炼魄，与火山石焰融合可衍新生异火。',     '九幽金祖火 九幽 金祖', '/images/fires/jiuyou_jinzuhuo.png', 6),
('陀舍古帝烛火', '异火榜 · 第一',  '榜一 · 万火之祖', '异火榜魁首，相传为万火之始，集千家异火于一身，可焚天煮海。',     '陀舍古帝烛火 陀舍 古帝 帝火', '/images/fires/tuoshe_gudi_zhuhuo.png', 1);

-- 热门功法（5 部）
INSERT INTO `t_gongfa` (`name`, `description`, `hot_value`, `sort`) VALUES
('焚决',       '可吞噬异火进阶的至强功法', 98000, 1),
('九转斗者决', '稳固根基，斗气绵长不绝',   62000, 2),
('八极崩',     '近身斗技，一击叠浪八重',   55000, 3),
('焰分噬浪尺', '玄重尺所施，尺出焰浪滔天', 49000, 4),
('三千雷动',   '身法斗技，雷影瞬息千里',   41000, 5);

-- 斗者动态（4 条）
INSERT INTO `t_feed_post` (`author`, `content`, `created_at`) VALUES
('药老',   '新得一味「龙血芝」，可助斗王突破瓶颈，欲求者速来丹药坊。', DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
('萧炎',   '于迦南学院秘境收服青莲地心火，斗气大涨，叩谢诸位相助。',   DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('美杜莎', '蛇人族与迦南结盟，异火共享事宜已在宗门议定。',             DATE_SUB(NOW(), INTERVAL 5 HOUR)),
('薰儿',   '古族藏书阁新添《异火通鉴》，已向诸位斗者开放查阅。',       DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 热搜词（5 个）
INSERT INTO `t_hot_word` (`word`, `sort`) VALUES
('净莲妖火', 1),
('焚决',     2),
('玄重尺',   3),
('药老',     4),
('萧炎',     5);

-- 丹药坊（9 味，丹榜名次 = sort 越小越靠前）
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
