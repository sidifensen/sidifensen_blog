/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : sidifensen_blog

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 09/10/2025 10:58:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for album
-- ----------------------------
DROP TABLE IF EXISTS `album`;
CREATE TABLE `album`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '相册id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '相册名称',
  `cover_url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相册封面',
  `show_status` tinyint NOT NULL DEFAULT 0 COMMENT '展示状态 0-公开 1-私密',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_show_status`(`show_status` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of album
-- ----------------------------
INSERT INTO `album` VALUES (1, 1, '风景1', 'https://image.sidifensen.com/sidifensen-blog/album/1/1/d888b05f9a4345e7b49c59d569e851d3.webp', 0, '2025-08-15 01:12:18', '2025-08-15 01:12:18', 0);
INSERT INTO `album` VALUES (2, 1, '壁纸', 'https://image.sidifensen.com/sidifensen-blog/album/1/2/658334308098465fbe66b5d4b3266ef9.webp', 0, '2025-08-15 01:16:51', '2025-08-15 01:16:51', 0);
INSERT INTO `album` VALUES (3, 1, '电脑壁纸', 'https://image.sidifensen.com/sidifensen-blog/album/1/3/1d29f054b0f34070a6889dc49bf35ae3.webp', 0, '2025-08-15 01:17:16', '2025-08-15 01:17:16', 0);
INSERT INTO `album` VALUES (4, 1, '精美壁纸', 'https://image.sidifensen.com/sidifensen-blog/album/1/4/4bed147d7d1e48e2a5cffe66e2c31a24.webp', 0, '2025-08-15 01:24:06', '2025-08-15 01:24:06', 0);
INSERT INTO `album` VALUES (5, 1, '1', NULL, 0, '2025-08-15 02:11:13', '2025-08-15 02:11:13', 1);
INSERT INTO `album` VALUES (6, 1, '优美壁纸', 'https://image.sidifensen.com/sidifensen-blog/album/1/6/61d91658e974434186b382cd10955d82.webp', 0, '2025-08-17 00:23:38', '2025-08-17 00:23:38', 0);
INSERT INTO `album` VALUES (7, 1, '手机壁纸', 'https://image.sidifensen.com/sidifensen-blog/album/1/7/651c690cbca74ecda4b794b2d8d21f81.webp', 0, '2025-08-17 00:24:19', '2025-08-17 00:24:19', 0);
INSERT INTO `album` VALUES (8, 1, '大自然壁纸', 'https://image.sidifensen.com/sidifensen-blog/album/1/8/253c190cf34e493096297e258fc25c42.webp', 0, '2025-08-17 00:45:42', '2025-08-17 00:45:42', 0);
INSERT INTO `album` VALUES (9, 1, '人文壁纸', 'https://image.sidifensen.com/sidifensen-blog/album/1/9/7eb75aa3fe134277829afa0861bfe6f7.webp', 0, '2025-08-17 00:45:47', '2025-08-17 00:45:47', 0);
INSERT INTO `album` VALUES (10, 1, '人像', 'https://image.sidifensen.com/sidifensen-blog/album/1/10/6df7d4da9241446a8471c4ddaee6860d.webp', 0, '2025-08-17 00:45:52', '2025-08-17 00:45:52', 0);
INSERT INTO `album` VALUES (11, 1, '宠物', 'https://image.sidifensen.com/sidifensen-blog/album/1/11/e1a22085ec6d4c2a8e9020c4766920e6.webp', 0, '2025-08-17 00:46:01', '2025-08-17 00:46:01', 0);
INSERT INTO `album` VALUES (12, 1, '科幻', 'https://image.sidifensen.com/sidifensen-blog/album/1/12/5636972df0e04b37b68c3268969e1d00.webp', 0, '2025-08-17 00:46:17', '2025-08-17 00:46:17', 0);
INSERT INTO `album` VALUES (13, 1, '宇宙', 'https://image.sidifensen.com/sidifensen-blog/album/1/13/fd760317ef5745968a71200c65033c8b.webp', 0, '2025-08-18 01:32:00', '2025-08-18 01:32:00', 0);
INSERT INTO `album` VALUES (14, 2, '科幻世界', 'https://image.sidifensen.com/sidifensen-blog/album/2/14/3ad10d18a3ff47d59e049211981b4ac9.webp', 0, '2025-08-23 22:18:21', '2025-08-23 22:18:21', 0);
INSERT INTO `album` VALUES (15, 1, '世界', 'https://image.sidifensen.com/sidifensen-blog/album/1/15/20987aa177e443d3b53aee3d9fc0d55d.webp', 0, '2025-08-26 21:40:09', '2025-08-26 21:40:09', 0);
INSERT INTO `album` VALUES (16, 1, '落霞', 'https://image.sidifensen.com/sidifensen-blog/album/1/16/fbf2a30cea1b4a2fb5c519420abb9374.webp', 0, '2025-08-26 21:40:22', '2025-08-26 21:40:22', 0);
INSERT INTO `album` VALUES (17, 1, '孤雁', 'https://image.sidifensen.com/sidifensen-blog/album/1/17/8077a7764d464ccdbeb18a4883ebb8ee.webp', 0, '2025-08-26 21:40:28', '2025-08-26 21:40:28', 0);
INSERT INTO `album` VALUES (18, 1, '大海', 'https://image.sidifensen.com/sidifensen-blog/album/1/18/34ceaf19b2c14f088fe644e69b610812.webp', 0, '2025-08-26 21:40:43', '2025-08-26 21:40:43', 0);
INSERT INTO `album` VALUES (19, 1, '月球', 'https://image.sidifensen.com/sidifensen-blog/album/1/19/267a024586df4aa18593c5099221e544.webp', 0, '2025-08-26 21:40:48', '2025-08-26 21:40:48', 0);

-- ----------------------------
-- Table structure for album_photo
-- ----------------------------
DROP TABLE IF EXISTS `album_photo`;
CREATE TABLE `album_photo`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `album_id` int NOT NULL COMMENT '相册id',
  `photo_id` int NOT NULL COMMENT '图片id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_album_id`(`album_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of album_photo
-- ----------------------------
INSERT INTO `album_photo` VALUES (1, 2, 1001);
INSERT INTO `album_photo` VALUES (2, 2, 1002);
INSERT INTO `album_photo` VALUES (3, 2, 1003);
INSERT INTO `album_photo` VALUES (4, 2, 1004);
INSERT INTO `album_photo` VALUES (5, 3, 1005);
INSERT INTO `album_photo` VALUES (6, 2, 1006);
INSERT INTO `album_photo` VALUES (7, 4, 1008);
INSERT INTO `album_photo` VALUES (8, 6, 1009);
INSERT INTO `album_photo` VALUES (9, 7, 1010);
INSERT INTO `album_photo` VALUES (10, 8, 1011);
INSERT INTO `album_photo` VALUES (11, 9, 1012);
INSERT INTO `album_photo` VALUES (12, 10, 1013);
INSERT INTO `album_photo` VALUES (13, 11, 1014);
INSERT INTO `album_photo` VALUES (14, 12, 1015);
INSERT INTO `album_photo` VALUES (15, 13, 1016);
INSERT INTO `album_photo` VALUES (16, 2, 1017);
INSERT INTO `album_photo` VALUES (17, 1, 1018);
INSERT INTO `album_photo` VALUES (18, 1, 1019);
INSERT INTO `album_photo` VALUES (19, 1, 1020);
INSERT INTO `album_photo` VALUES (20, 1, 1021);
INSERT INTO `album_photo` VALUES (21, 14, 1022);
INSERT INTO `album_photo` VALUES (22, 14, 1023);
INSERT INTO `album_photo` VALUES (23, 14, 1024);
INSERT INTO `album_photo` VALUES (24, 15, 1025);
INSERT INTO `album_photo` VALUES (25, 16, 1026);
INSERT INTO `album_photo` VALUES (26, 16, 1027);
INSERT INTO `album_photo` VALUES (27, 17, 1028);
INSERT INTO `album_photo` VALUES (28, 17, 1029);
INSERT INTO `album_photo` VALUES (29, 18, 1030);
INSERT INTO `album_photo` VALUES (30, 19, 1031);
INSERT INTO `album_photo` VALUES (31, 8, 1068);
INSERT INTO `album_photo` VALUES (32, 1, 1070);

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '文章id',
  `user_id` int NOT NULL COMMENT '用户id',
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '描述',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `cover_url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面url',
  `read_count` int NOT NULL DEFAULT 0 COMMENT '阅读量',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞量',
  `comment_count` int NOT NULL DEFAULT 0 COMMENT '评论数',
  `collect_count` int NOT NULL DEFAULT 0 COMMENT '收藏量',
  `examine_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0-待审核 1-审核通过 2-审核未通过',
  `edit_status` tinyint NOT NULL DEFAULT 0 COMMENT '编辑状态 0-已发布 1-草稿箱 2-回收站',
  `visible_range` tinyint NOT NULL DEFAULT 0 COMMENT '可见范围 0-全部可见 1-仅我可见 2-粉丝可见 3-vip可见',
  `reprint_type` tinyint NOT NULL DEFAULT 0 COMMENT '转载类型 0-原创 1-转载',
  `reprint_url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转载链接',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id_examine_edit_visible_status_create_time`(`user_id` ASC, `examine_status` ASC, `edit_status` ASC, `visible_range` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_examine_edit_visible_status_create_time`(`examine_status` ASC, `edit_status` ASC, `visible_range` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (1, 1, '数据库架构,哈希算法,linux', '深入理解 Spring Boot 微服务架构设计与实践指南', '本文详细介绍了 Spring Boot 微服务架构的核心概念、设计原则和最佳实践。通过实际案例讲解服务拆分、服务治理、配置中心、服务网关等关键技术点，帮助开发者构建高可用、高性能的微服务系统。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">Spring Boot 微服务架构设计与实践</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、技术背景与应用场景</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">在现代软件开发中，微服务架构已成为构建大型分布式系统的主流选择。Spring Boot 作为 Java 生态中最受欢迎的微服务框架，凭借其简洁的配置、强大的功能和丰富的生态系统，帮助开发者快速构建生产级别的应用程序。本文将深入探讨 Spring Boot 微服务架构的核心要点。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、核心概念与原理剖析</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">微服务架构将单体应用拆分成多个独立的小服务，每个服务专注于特定的业务功能。服务之间通过轻量级的通信机制（如 REST API）进行交互。这种架构风格带来了更好的可扩展性、可维护性和技术多样性。</p><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">服务注册与发现：使用 Eureka 或 Consul 实现服务的自动注册和动态发现</li><li style=\"margin-bottom: 8px;\">负载均衡：Ribbon 提供客户端负载均衡，合理分配请求流量</li><li style=\"margin-bottom: 8px;\">服务容错：Hystrix 实现熔断降级，防止故障蔓延</li><li style=\"margin-bottom: 8px;\">API 网关：Zuul 或 Gateway 统一管理外部请求入口</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、环境搭建与配置指南</h2><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>@SpringBootApplication\n@EnableEurekaClient\npublic class OrderServiceApplication {\n    public static void main(String[] args) {\n        SpringApplication.run(OrderServiceApplication.class, args);\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、实战案例与代码示例</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">通过电商订单系统的实战案例，我们将学习如何将订单服务、商品服务、用户服务等拆分为独立的微服务，并实现服务间的协同工作。掌握配置中心、消息队列、分布式事务等关键技术的实际应用。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">五、常见问题与解决方案</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">在微服务开发中，常见的问题包括服务雪崩、分布式事务一致性、服务间通信超时等。针对这些问题，我们将提供经过实践验证的解决方案，帮助你避免常见陷阱。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">六、性能优化与最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">合理的服务拆分粒度、缓存策略、异步处理、数据库优化等都是提升微服务性能的关键。本节将分享生产环境中的性能优化经验和最佳实践建议。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>专家建议：</strong>在设计微服务架构时，要权衡服务拆分的细粒度与系统复杂度，遵循单一职责原则，避免过度拆分导致的分布式系统复杂性问题。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/15/20987aa177e443d3b53aee3d9fc0d55d.webp', 1, 0, 8, 0, 1, 0, 0, 0, NULL, '2025-08-25 11:35:08', '2025-08-25 15:00:55', 0);
INSERT INTO `article` VALUES (2, 1, '人工智能,规格说明书,myeclipse', 'Vue 3 组合式 API 完全指南与最佳实践', '深入探讨 Vue 3 组合式 API 的设计理念和使用技巧，包括响应式原理、组合函数封装、状态管理等内容。通过丰富的代码示例和实战项目，让你快速掌握 Vue 3 开发的精髓，提升前端开发效率。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">Vue 3 组合式 API 完全指南</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、技术背景与应用场景</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Vue 3 带来了全新的组合式 API（Composition API），这是一种基于函数的 API 风格，相比 Vue 2 的选项式 API，它提供了更好的逻辑复用能力和类型推导支持。组合式 API 让代码组织更加灵活，特别适合大型项目的开发。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、核心概念与原理剖析</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>响应式系统：</strong>ref 和 reactive 实现数据的响应式追踪</li><li style=\"margin-bottom: 8px;\"><strong>生命周期钩子：</strong>onMounted、onUpdated 等组合式钩子函数</li><li style=\"margin-bottom: 8px;\"><strong>计算属性：</strong>computed 创建派生状态</li><li style=\"margin-bottom: 8px;\"><strong>侦听器：</strong>watch 和 watchEffect 监听数据变化</li></ul><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>import { ref, computed, onMounted } from \'vue\'\n\nexport default {\n  setup() {\n    const count = ref(0)\n    const doubled = computed(() => count.value * 2)\n    \n    onMounted(() => {\n      console.log(\'Component mounted!\')\n    })\n    \n    return { count, doubled }\n  }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、组合函数的封装与复用</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">通过创建自定义组合函数（Composables），可以将可复用的逻辑提取出来，在多个组件中共享。这大大提高了代码的可维护性和复用性，是 Vue 3 最强大的特性之一。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、状态管理与 Pinia</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Pinia 是 Vue 3 官方推荐的状态管理库，完美支持 TypeScript 和组合式 API。相比 Vuex，Pinia 提供了更简洁的 API 和更好的开发体验，是现代 Vue 应用的首选状态管理方案。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>提示：</strong>组合式 API 并不是要完全替代选项式 API，两者可以在同一项目中混合使用。根据实际场景选择最合适的 API 风格才是明智的做法。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/3dfb410027984c7eb61ba8e94e49290c.jpg', 2, 0, 8, 0, 1, 0, 1, 1, NULL, '2025-08-25 11:35:43', '2025-08-31 23:52:31', 0);
INSERT INTO `article` VALUES (3, 1, 'turbopack,个人开发,solr,objective-c', 'MySQL 高性能优化实战：索引与查询优化技巧', '全面解析 MySQL 性能优化的方方面面，从索引原理、执行计划分析到查询优化技巧。结合实际业务场景，提供可落地的性能优化方案，让你的数据库性能提升数倍，为高并发应用打下坚实基础。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">MySQL 高性能优化实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、MySQL 性能瓶颈分析</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">在高并发场景下，数据库往往成为系统性能的瓶颈。慢查询、锁竞争、连接池耗尽等问题会严重影响用户体验。系统的性能优化需要从多个维度入手，包括索引设计、查询优化、表结构设计等。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、索引原理与优化策略</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">B+树索引结构原理与查找过程</li><li style=\"margin-bottom: 8px;\">聚簇索引与非聚簇索引的区别</li><li style=\"margin-bottom: 8px;\">联合索引与最左前缀原则</li><li style=\"margin-bottom: 8px;\">覆盖索引优化查询性能</li></ul><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>-- 创建联合索引\nCREATE INDEX idx_user_age_city ON users(age, city);\n\n-- 分析查询执行计划\nEXPLAIN SELECT * FROM users WHERE age = 25 AND city = \'Beijing\';</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、查询优化实战技巧</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">避免全表扫描、减少回表查询、优化JOIN操作、合理使用子查询等都是提升查询性能的有效手段。通过 EXPLAIN 分析执行计划，找出慢查询的根本原因，然后针对性优化。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、表结构设计最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">合理的表结构设计是性能优化的基础。选择合适的数据类型、避免过度范式化、适当的字段冗余、分库分表策略等都需要根据业务特点进行权衡。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">五、锁机制与事务优化</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">理解InnoDB的锁机制（行锁、间隙锁、Next-Key锁）对避免死锁和提升并发性能至关重要。合理设置事务隔离级别，避免长事务，减少锁持有时间。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>性能优化金句：</strong>不要过早优化，但要时刻准备优化。在设计阶段考虑性能，在开发阶段关注性能，在上线后持续监控性能。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/3c2bfc0c40f4428489c3df9cdabd1a3b.webp', 1, 0, 8, 0, 0, 0, 0, 0, NULL, '2025-08-30 15:07:22', '2025-08-30 15:07:22', 0);
INSERT INTO `article` VALUES (4, 1, 'docker,clickhouse,深度学习', 'Docker 容器化部署完整教程与实践经验分享', '系统讲解 Docker 容器技术的核心概念和实践应用，包括镜像构建、容器编排、网络配置、数据持久化等。通过真实项目案例，让你轻松掌握容器化部署的完整流程，提升应用部署效率。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">Docker 容器化部署教程</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、Docker 核心概念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Docker 通过容器技术实现应用的打包、分发和运行。镜像(Image)是只读模板，容器(Container)是镜像的运行实例。通过 Dockerfile 定义镜像构建过程，实现环境的标准化和可复制性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>FROM openjdk:11-jre-slim\nWORKDIR /app\nCOPY target/*.jar app.jar\nEXPOSE 8080\nENTRYPOINT [\"java\", \"-jar\", \"app.jar\"]</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、镜像构建与优化</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">多阶段构建减小镜像体积</li><li style=\"margin-bottom: 8px;\">利用缓存加速构建过程</li><li style=\"margin-bottom: 8px;\">选择合适的基础镜像</li><li style=\"margin-bottom: 8px;\">清理不必要的文件和依赖</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、容器编排与 Docker Compose</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Docker Compose 用于定义和运行多容器应用。通过 YAML 文件声明式地配置服务、网络和卷，一键启动整个应用栈，极大简化了开发和测试环境的搭建。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>最佳实践：</strong>容器应该是无状态的，持久化数据应该存储在外部卷中。遵循单一职责原则，一个容器只运行一个主进程。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/2/add4b5d841924e8499b1230010ffeea9.jpg', 1, 0, 8, 0, 0, 0, 0, 0, NULL, '2025-08-30 15:08:58', '2025-08-30 15:08:58', 0);
INSERT INTO `article` VALUES (5, 1, 'ruby,合成复用原则,职场和发展', 'Redis 缓存架构设计与性能优化实战指南', '深度剖析 Redis 缓存架构设计与性能优化策略。涵盖数据结构选择、缓存穿透/雪崩/击穿解决方案、持久化机制、集群部署等核心知识点，助你构建高性能缓存系统，支撑海量并发访问。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">Redis 缓存架构设计</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、Redis 核心数据结构</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Redis 提供了丰富的数据结构：String（字符串）、Hash（哈希）、List（列表）、Set（集合）、Sorted Set（有序集合）。选择合适的数据结构对性能和功能实现都至关重要。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 设置缓存并指定过期时间\nSET user:1001 \"UserData\" EX 3600\n\n// 使用 Hash 存储对象\nHMSET user:1001 name \"张三\" age 25 city \"北京\"</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、缓存常见问题与解决方案</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>缓存穿透：</strong>布隆过滤器或缓存空值</li><li style=\"margin-bottom: 8px;\"><strong>缓存雪崩：</strong>设置随机过期时间、多级缓存</li><li style=\"margin-bottom: 8px;\"><strong>缓存击穿：</strong>互斥锁或逻辑过期</li><li style=\"margin-bottom: 8px;\"><strong>数据一致性：</strong>先更新数据库再删除缓存</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、Redis 持久化机制</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">RDB（快照）和 AOF（追加文件）两种持久化方式各有优劣。RDB 适合备份和灾难恢复，AOF 提供更好的数据安全性。生产环境通常采用混合持久化策略。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、Redis 集群架构</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">主从复制、哨兵模式、集群模式三种架构满足不同的高可用需求。掌握数据分片、故障转移、扩容缩容等运维技能，确保缓存服务的稳定可靠。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>架构建议：</strong>缓存不是万能的，要根据业务特点合理使用。高频读、低频写的场景最适合使用缓存。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/1/d888b05f9a4345e7b49c59d569e851d3.webp', 1, 0, 8, 0, 0, 1, 0, 0, NULL, '2025-08-30 15:09:14', '2025-08-30 15:09:14', 0);
INSERT INTO `article` VALUES (6, 1, 'git,matlab,rust,dnn', '前端工程化实践：从 Webpack 到 Vite 的演进之路', '梳理前端工程化的发展历程，对比 Webpack 和 Vite 的优劣。详细讲解项目配置、性能优化、构建部署等实践经验，帮助团队提升开发效率和项目质量，构建现代化的前端开发体系。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">前端工程化实践</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、前端工程化的发展历程</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">从最初的手动管理 JS 文件，到 Grunt、Gulp 的自动化构建，再到 Webpack 的模块化打包，前端工程化经历了巨大的变革。Vite 的出现代表了下一代构建工具的方向，利用 ES Module 实现极速的开发体验。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、Webpack 核心概念</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">Entry（入口）：定义打包的起点</li><li style=\"margin-bottom: 8px;\">Output（输出）：配置打包结果</li><li style=\"margin-bottom: 8px;\">Loader（加载器）：处理非 JS 文件</li><li style=\"margin-bottom: 8px;\">Plugin（插件）：扩展构建功能</li></ul><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// webpack.config.js\nmodule.exports = {\n  entry: \'./src/index.js\',\n  output: {\n    filename: \'bundle.js\',\n    path: path.resolve(__dirname, \'dist\')\n  },\n  module: {\n    rules: [\n      { test: /\\.css$/, use: [\'style-loader\', \'css-loader\'] }\n    ]\n  }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、Vite 的优势与特点</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Vite 在开发环境使用原生 ESM，无需打包即可运行。这带来了毫秒级的热更新和秒级的服务启动。生产环境使用 Rollup 打包，输出更小的包体积。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、构建性能优化策略</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">代码分割、Tree Shaking、资源压缩、CDN 加速、懒加载等技术手段可以显著提升应用性能。配合性能监控工具，持续优化用户体验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>技术选型：</strong>新项目推荐使用 Vite，老项目可以逐步迁移。工具只是手段，工程化思维才是核心。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/7cb89614b735483b9644fa307e9ccbc9.jpg', 1, 0, 0, 0, 0, 1, 0, 0, NULL, '2025-08-30 15:14:15', '2025-08-30 15:14:15', 0);
INSERT INTO `article` VALUES (7, 1, 'visual studio,batch,visual studio code', 'Java 并发编程详解：线程池与异步编程最佳实践', '全面讲解 Java 并发编程的核心概念和实践技巧。深入剖析线程池原理、异步编程模型、并发工具类的使用，通过实战案例帮助你写出高效、安全的并发代码，充分发挥多核处理器的性能优势。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">Java 并发编程详解</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、并发编程基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">在多核处理器时代，并发编程是提升应用性能的关键。Java 提供了丰富的并发工具，包括线程、锁、原子类、并发集合等。理解这些工具的原理和使用场景，是编写高性能应用的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 创建线程池\nExecutorService executor = Executors.newFixedThreadPool(10);\n\n// 提交任务\nexecutor.submit(() -> {\n    System.out.println(\"Task executed by \" + Thread.currentThread().getName());\n});</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、线程池深度解析</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">ThreadPoolExecutor 是线程池的核心实现。理解核心线程数、最大线程数、任务队列、拒绝策略等参数的含义，合理配置线程池，避免资源浪费和任务堆积。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、CompletableFuture 异步编程</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">CompletableFuture 提供了强大的异步编程能力，支持链式调用、异常处理、结果组合等操作。相比传统的 Future，它提供了更灵活的 API 和更好的可读性。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>安全提示：</strong>并发编程需要特别注意线程安全问题。共享可变状态是万恶之源，尽量使用不可变对象和线程安全的数据结构。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/8dd5d4a2b0a74e7ebd9001fa26675bd6.jpg', 0, 0, 3, 0, 0, 1, 3, 0, NULL, '2025-08-30 15:15:13', '2025-08-30 15:15:13', 0);
INSERT INTO `article` VALUES (8, 1, '嵌入式实时数据库,信息与通信,全文检索,vagrant', 'RESTful API 设计规范与最佳实践指南', '详细阐述 RESTful API 的设计原则和规范，包括资源命名、HTTP 方法使用、状态码定义、版本控制等。结合实际项目经验，提供可参考的 API 设计最佳实践，构建优雅易用的接口服务。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">RESTful API 设计规范</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、REST 架构风格</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">REST（Representational State Transfer）是一种软件架构风格，强调资源的表述和状态转移。RESTful API 使用标准的 HTTP 方法，通过 URL 定位资源，返回标准的状态码和数据格式。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、资源命名规范</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">使用名词复数形式：/users、/articles</li><li style=\"margin-bottom: 8px;\">层级关系清晰：/users/{id}/articles</li><li style=\"margin-bottom: 8px;\">使用小写字母和连字符：/user-profiles</li><li style=\"margin-bottom: 8px;\">避免动词和过深嵌套</li></ul><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>GET    /api/v1/users          # 获取用户列表\nGET    /api/v1/users/123      # 获取指定用户\nPOST   /api/v1/users          # 创建用户\nPUT    /api/v1/users/123      # 更新用户\nDELETE /api/v1/users/123      # 删除用户</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、HTTP 状态码的正确使用</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">200 OK、201 Created、204 No Content、400 Bad Request、401 Unauthorized、403 Forbidden、404 Not Found、500 Internal Server Error 等状态码都有明确的语义，合理使用能提升 API 的可理解性。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、版本控制与文档化</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">API 版本控制保证向后兼容，常见方式包括 URL 路径版本、请求头版本等。完善的 API 文档是提升开发效率的关键，推荐使用 Swagger/OpenAPI 规范。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>设计理念：</strong>API 是给开发者使用的产品，要站在使用者的角度思考，追求简洁、一致、易用。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/1/e6f9dadfca504dd2b505734ad59c9e9b.webp', 0, 0, 3, 0, 0, 1, 3, 0, NULL, '2025-08-30 15:15:38', '2025-08-30 15:15:38', 0);
INSERT INTO `article` VALUES (9, 1, 'zookeeper,制造,qt6.3,numpy', 'Git 版本控制高级技巧与团队协作实践', '深入讲解 Git 版本控制的高级用法，包括分支管理策略、冲突解决技巧、代码回滚方法等。分享团队协作的最佳实践，提升代码管理效率和团队协作质量，让版本管理变得简单高效。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">Git 版本控制高级技巧</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、Git 工作流程与分支策略</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Git Flow、GitHub Flow、GitLab Flow 是三种常见的分支管理策略。选择合适的工作流程能够规范团队协作，减少冲突，提高代码质量。master/main 分支保持稳定，develop 分支用于日常开发，feature 分支用于新功能开发。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># 创建功能分支\ngit checkout -b feature/user-login\n\n# 合并分支\ngit checkout develop\ngit merge --no-ff feature/user-login\n\n# 删除已合并的分支\ngit branch -d feature/user-login</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、冲突解决与代码回滚</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">理解冲突产生的原因，掌握冲突标记的含义，使用合适的工具解决冲突。revert 创建新提交来撤销更改，reset 重置到指定提交，cherry-pick 挑选特定提交应用到当前分支。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、高级操作与技巧</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">使用 rebase 保持提交历史清晰</li><li style=\"margin-bottom: 8px;\">stash 暂存工作区的修改</li><li style=\"margin-bottom: 8px;\">reflog 找回\"丢失\"的提交</li><li style=\"margin-bottom: 8px;\">子模块管理项目依赖</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、团队协作最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">编写清晰的提交信息，遵循约定式提交规范。定期同步远程分支，及时解决冲突。使用 Pull Request 进行代码审查，提升代码质量。配置 Git Hooks 自动化检查和部署。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>协作原则：</strong>小步快跑，频繁提交。保持提交粒度适中，每个提交只做一件事。良好的提交记录是项目的宝贵财富。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/ba47b23e794d46cea0a767f28af41f3b.jpg', 0, 0, 3, 0, 2, 1, 3, 0, NULL, '2025-08-30 15:16:28', '2025-08-30 15:16:28', 0);
INSERT INTO `article` VALUES (10, 1, '职场和发展,kind,ue4,xamarin', 'TypeScript 从入门到精通：类型系统深度解析', '系统学习 TypeScript 的类型系统，从基础类型到高级类型，从类型推导到类型体操。通过大量实例和真实项目场景，让你深刻理解 TypeScript 的强大之处，编写更安全、更易维护的代码。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">TypeScript 类型系统解析</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、为什么选择 TypeScript</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">TypeScript 是 JavaScript 的超集，添加了静态类型检查。类型系统能在编译阶段发现潜在错误，提供更好的代码提示和重构支持，使大型项目的维护变得更加容易。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 定义接口\ninterface User {\n  id: number;\n  name: string;\n  email?: string;  // 可选属性\n}\n\n// 使用泛型\nfunction identity<T>(arg: T): T {\n  return arg;\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、基础类型与高级类型</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">基础类型：number、string、boolean、null、undefined</li><li style=\"margin-bottom: 8px;\">对象类型：interface、type、class</li><li style=\"margin-bottom: 8px;\">联合类型与交叉类型</li><li style=\"margin-bottom: 8px;\">字面量类型与枚举类型</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、泛型与类型推导</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">泛型是 TypeScript 最强大的特性之一，它提供了编写可复用代码的能力。类型推导让我们在享受类型安全的同时，不必编写过多的类型注解，代码更加简洁。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、实战应用与最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">在 React、Vue、Node.js 等项目中集成 TypeScript，配置 tsconfig.json，使用第三方类型声明。掌握类型守卫、类型断言等高级技巧，让类型系统为你的项目保驾护航。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>学习建议：</strong>TypeScript 的学习曲线相对平缓，从基础类型开始，逐步深入。多写代码，在实践中感受类型系统的价值。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/17/d8d5cca07d634a7580d76aa3195822ca.webp', 0, 0, 3, 5, 2, 1, 3, 0, NULL, '2025-08-30 17:38:50', '2025-08-30 17:38:50', 0);
INSERT INTO `article` VALUES (11, 1, 'openshift,uml,视觉检测,贪心算法', '微服务架构下的分布式事务解决方案全解析', '探讨微服务架构下的分布式事务问题和解决方案。详细讲解 2PC、3PC、TCC、Saga 等分布式事务模式，结合实际业务场景提供可落地的实施方案，确保数据一致性和系统可靠性。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">分布式事务解决方案</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、分布式事务的挑战</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">在微服务架构中，一个业务操作可能涉及多个服务的数据修改。传统的 ACID 事务无法跨服务使用，如何保证分布式环境下的数据一致性成为一大挑战。CAP 理论告诉我们，一致性、可用性和分区容错性不可兼得。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、分布式事务解决方案对比</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>2PC/3PC：</strong>强一致性，性能较差，适合短事务</li><li style=\"margin-bottom: 8px;\"><strong>TCC：</strong>补偿机制，开发复杂度高，适合核心业务</li><li style=\"margin-bottom: 8px;\"><strong>Saga：</strong>最终一致性，适合长事务</li><li style=\"margin-bottom: 8px;\"><strong>本地消息表：</strong>实现简单，适合异步场景</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、Seata 分布式事务框架</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Seata 是阿里开源的分布式事务解决方案，支持 AT、TCC、Saga、XA 四种事务模式。AT 模式无需修改业务代码，通过拦截 SQL 实现自动补偿，是最常用的模式。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>架构建议：</strong>分布式事务会增加系统复杂度和性能开销，应该在设计阶段就考虑如何减少跨服务事务，通过合理的服务划分和数据冗余来简化问题。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/12/5636972df0e04b37b68c3268969e1d00.webp', 0, 0, 0, 5, 2, 1, 0, 0, NULL, '2025-08-30 17:39:31', '2025-08-30 17:39:31', 0);
INSERT INTO `article` VALUES (12, 1, 'reactjs,java18,ceph,substance designer', 'Nginx 高性能配置与负载均衡实战教程', '深度解析 Nginx 的工作原理和配置技巧。涵盖负载均衡策略、反向代理配置、性能优化、HTTPS 部署等内容，帮助你构建高性能的 Web 服务器架构，支撑海量并发访问。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">Nginx 高性能配置</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、Nginx 核心架构</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Nginx 采用事件驱动的异步非阻塞架构，使用 epoll 实现高效的 I/O 多路复用。master-worker 进程模型保证了服务的稳定性和热升级能力。单个 worker 进程可以处理数千个并发连接。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># 负载均衡配置\nupstream backend {\n    server 192.168.1.10:8080 weight=3;\n    server 192.168.1.11:8080 weight=2;\n    server 192.168.1.12:8080 backup;\n}\n\nserver {\n    listen 80;\n    location / {\n        proxy_pass http://backend;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、负载均衡策略</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">轮询（Round Robin）：默认策略</li><li style=\"margin-bottom: 8px;\">加权轮询：根据服务器性能分配权重</li><li style=\"margin-bottom: 8px;\">IP Hash：同一客户端固定分配到同一服务器</li><li style=\"margin-bottom: 8px;\">最少连接：优先分配给连接数少的服务器</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、性能优化配置</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">合理配置 worker 进程数、连接数、缓冲区大小。开启 Gzip 压缩、启用 HTTP/2、配置缓存策略。使用 limit_req 和 limit_conn 模块进行流量控制，防止恶意攻击。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>运维经验：</strong>Nginx 配置修改后，先使用 nginx -t 测试配置正确性，再使用 nginx -s reload 平滑重载配置，避免服务中断。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/3da96b67814842d3bd8cae0e517ac458.jpg', 1, 0, 0, 5, 2, 0, 0, 0, NULL, '2024-07-30 23:54:50', '2025-08-30 23:54:50', 0);
INSERT INTO `article` VALUES (13, 1, 'maya,java-zookeeper,hbase', 'Python 数据分析实战：Pandas 与 NumPy 深度应用', '全面介绍 Python 数据分析的核心库 Pandas 和 NumPy 的使用方法。通过真实数据集的分析实践，掌握数据清洗、处理、分析、可视化的完整流程，成为数据分析领域的实战高手。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">Python 数据分析实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、数据分析工具链</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Python 是数据分析领域最受欢迎的语言。NumPy 提供高性能的数组运算，Pandas 提供强大的数据结构和分析工具，Matplotlib 和 Seaborn 用于数据可视化，Jupyter Notebook 提供交互式开发环境。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>import pandas as pd\nimport numpy as np\n\n# 读取CSV文件\ndf = pd.read_csv(\'data.csv\')\n\n# 数据清洗和处理\ndf = df.dropna()  # 删除空值\ndf[\'age_group\'] = pd.cut(df[\'age\'], bins=[0, 18, 35, 60, 100])</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、Pandas 核心操作</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">数据选择与过滤</li><li style=\"margin-bottom: 8px;\">数据分组与聚合</li><li style=\"margin-bottom: 8px;\">数据合并与连接</li><li style=\"margin-bottom: 8px;\">时间序列分析</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据可视化实战</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">使用 Matplotlib 和 Seaborn 创建各类图表：折线图、柱状图、饼图、散点图、热力图等。选择合适的可视化方式能够更直观地展示数据特征和规律。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>分析技巧：</strong>数据分析的核心是提出正确的问题。掌握工具只是基础，更重要的是培养数据思维和业务理解能力。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/84467f00772d41dba9db3204caf675b1.jpg', 0, 0, 0, 5, 2, 0, 2, 1, NULL, '2024-07-30 23:54:50', '2025-08-30 23:57:23', 0);
INSERT INTO `article` VALUES (14, 1, 'rocketmq,bert,rabbitmq,c#', 'React Hooks 深度解析与性能优化实践指南', '深入剖析 React Hooks 的设计原理和使用技巧。讲解自定义 Hooks 的编写、性能优化策略、常见陷阱规避等内容，助你写出更优雅、高效的 React 代码，构建高性能的前端应用。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">React Hooks 深度解析</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、Hooks 的诞生背景</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">React Hooks 在 16.8 版本引入，解决了类组件的诸多痛点。它让函数组件也能拥有状态和生命周期，代码更加简洁优雅。Hooks 的出现改变了 React 的开发范式，成为现代 React 开发的标准。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>import { useState, useEffect, useMemo } from \'react\';\n\nfunction UserProfile({ userId }) {\n  const [user, setUser] = useState(null);\n  \n  useEffect(() => {\n    fetchUser(userId).then(setUser);\n  }, [userId]);\n  \n  const displayName = useMemo(() => {\n    return user?.name.toUpperCase();\n  }, [user]);\n  \n  return <div>{displayName}</div>;\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常用 Hooks 详解</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>useState：</strong>添加状态管理</li><li style=\"margin-bottom: 8px;\"><strong>useEffect：</strong>处理副作用</li><li style=\"margin-bottom: 8px;\"><strong>useMemo：</strong>缓存计算结果</li><li style=\"margin-bottom: 8px;\"><strong>useCallback：</strong>缓存函数引用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、性能优化策略</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">合理使用 useMemo 和 useCallback 避免不必要的重新渲染。理解 React 的渲染机制，使用 React.memo 包裹组件。避免在循环、条件语句中使用 Hooks，遵守 Hooks 规则。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>优化建议：</strong>过早优化是万恶之源。先保证代码正确性和可读性，再通过性能分析工具找出瓶颈，针对性优化。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/b086412fe72c448291fc16eedb251fd6.jpg', 2, 0, 0, 0, 2, 0, 2, 1, NULL, '2024-07-30 23:54:50', '2025-08-31 17:40:30', 0);
INSERT INTO `article` VALUES (15, 1, 'java-consul,游戏引擎,运维开发,android', 'Kubernetes 容器编排完整实践指南与最佳实践', '系统学习 Kubernetes 容器编排平台的核心概念和实践应用。从集群搭建到应用部署，从服务发现到自动扩缩容，全方位掌握云原生应用的部署管理，构建弹性可扩展的容器化平台。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">Kubernetes 容器编排</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、Kubernetes 核心概念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kubernetes（K8s）是容器编排领域的事实标准。Pod 是最小部署单元，Deployment 管理应用部署，Service 提供服务发现和负载均衡，Ingress 管理外部访问。理解这些核心概念是使用 K8s 的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>apiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: nginx-deployment\nspec:\n  replicas: 3\n  template:\n    spec:\n      containers:\n      - name: nginx\n        image: nginx:1.21\n        ports:\n        - containerPort: 80</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、集群部署与管理</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">使用 kubeadm 搭建集群</li><li style=\"margin-bottom: 8px;\">配置网络插件（Flannel、Calico）</li><li style=\"margin-bottom: 8px;\">部署 Dashboard 可视化管理</li><li style=\"margin-bottom: 8px;\">配置持久化存储</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、应用部署最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">使用 ConfigMap 和 Secret 管理配置，通过 Liveness 和 Readiness 探针保证服务健康，配置资源限制避免资源耗尽。使用 HPA 实现自动扩缩容，应对流量波动。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>云原生建议：</strong>Kubernetes 学习曲线较陡，建议从基础概念开始，通过实践加深理解。掌握 kubectl 命令是基本功。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/13/fd760317ef5745968a71200c65033c8b.webp', 5, 0, 2, 2, 1, 0, 0, 1, 'http://localhost:7000/editor?articleId=15', '2024-07-30 23:54:50', '2025-09-14 20:46:11', 0);
INSERT INTO `article` VALUES (16, 1, 'excel,lucene,散列表,lumberyard', '消息队列 RabbitMQ 实战：异步处理与削峰填谷', '详细讲解 RabbitMQ 消息队列的工作原理和应用场景。涵盖消息可靠性保证、死信队列处理、集群部署等核心内容，帮助你构建稳定可靠的异步处理系统，提升系统解耦和性能。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">RabbitMQ 消息队列实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、消息队列的应用场景</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">消息队列是异步通信的核心组件，主要应用于：应用解耦、异步处理、流量削峰、消息分发等场景。RabbitMQ 基于 AMQP 协议，提供了可靠的消息传递保证。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 发送消息\nchannel.basicPublish(\n    \"exchange_name\",\n    \"routing_key\",\n    MessageProperties.PERSISTENT_TEXT_PLAIN,\n    message.getBytes()\n);\n\n// 消费消息\nchannel.basicConsume(\"queue_name\", false, consumer);</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、RabbitMQ 核心概念</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Exchange：</strong>消息路由器（Direct、Topic、Fanout、Headers）</li><li style=\"margin-bottom: 8px;\"><strong>Queue：</strong>消息队列</li><li style=\"margin-bottom: 8px;\"><strong>Binding：</strong>绑定关系</li><li style=\"margin-bottom: 8px;\"><strong>Virtual Host：</strong>虚拟主机</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、消息可靠性保证</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">通过消息持久化、发送确认、消费确认三重保障，确保消息不丢失。配置死信队列处理失败消息，设置消息 TTL 防止队列堆积。使用镜像队列实现高可用。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>可靠性与性能权衡：</strong>消息可靠性和系统性能往往是矛盾的。根据业务需求选择合适的可靠性级别，不必一味追求强一致性。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/17/8077a7764d464ccdbeb18a4883ebb8ee.webp', 2, 0, 0, 0, 1, 0, 2, 1, NULL, '2024-07-30 23:54:50', '2025-08-31 19:17:20', 0);
INSERT INTO `article` VALUES (17, 1, 'python,电脑,AI-native,自动驾驶', '前端性能优化实战：从原理到实践全面提升', '全面解析前端性能优化的理论和实践。从浏览器渲染原理到具体优化手段，从代码层面到工程层面，提供系统化的性能优化解决方案，让你的 Web 应用飞起来，提供极致的用户体验。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">前端性能优化实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、性能优化的重要性</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">页面加载时间每增加 1 秒，转化率降低 7%，用户体验直接影响业务指标。前端性能优化包括加载性能、渲染性能、交互性能等多个维度，需要系统化的优化策略。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、加载性能优化</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">资源压缩与合并</li><li style=\"margin-bottom: 8px;\">HTTP/2 多路复用</li><li style=\"margin-bottom: 8px;\">CDN 加速与缓存策略</li><li style=\"margin-bottom: 8px;\">懒加载与预加载</li><li style=\"margin-bottom: 8px;\">代码分割与按需加载</li></ul><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 动态导入实现代码分割\nconst LazyComponent = React.lazy(() => import(\'./LazyComponent\'));\n\n// 图片懒加载\n<img loading=\"lazy\" src=\"image.jpg\" alt=\"Image\" /></code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、渲染性能优化</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">减少重排和重绘、使用 CSS3 动画代替 JS 动画、虚拟列表优化长列表渲染、使用 Web Worker 处理计算密集型任务。理解浏览器渲染流程是优化的基础。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能监控与分析</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">使用 Lighthouse、WebPageTest 等工具进行性能评估。配置性能监控平台，收集真实用户的性能数据。基于数据驱动，持续优化用户体验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>优化理念：</strong>性能优化是一个持续的过程，没有银弹。要根据实际情况选择合适的优化策略，平衡性能和开发成本。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/082048343f0442fcb3a515ad8048100e.jpg', 3, 0, 1, 7, 1, 0, 0, 1, NULL, '2024-02-28 23:54:50', '2025-08-31 19:20:42', 0);
INSERT INTO `article` VALUES (18, 1, '华为,dnn,语言模型,c5底层', 'Spring Cloud 微服务全家桶实战完整教程', '深入学习 Spring Cloud 微服务生态体系，包括 Eureka、Ribbon、Feign、Hystrix、Zuul、Config 等组件的使用。通过完整项目实战，掌握微服务开发的全流程，构建企业级分布式系统。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">Spring Cloud 微服务全家桶</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、Spring Cloud 生态概览</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Spring Cloud 是基于 Spring Boot 的微服务开发工具集，提供了服务注册发现、配置管理、负载均衡、熔断降级、API 网关等功能。它简化了微服务的开发和部署，是 Java 微服务的首选方案。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Feign 声明式服务调用\n@FeignClient(\"user-service\")\npublic interface UserClient {\n    @GetMapping(\"/users/{id}\")\n    User getUserById(@PathVariable Long id);\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、核心组件详解</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Eureka：</strong>服务注册与发现</li><li style=\"margin-bottom: 8px;\"><strong>Ribbon：</strong>客户端负载均衡</li><li style=\"margin-bottom: 8px;\"><strong>Hystrix：</strong>熔断降级</li><li style=\"margin-bottom: 8px;\"><strong>Gateway：</strong>API 网关</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、微服务治理</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">服务监控使用 Spring Boot Admin，链路追踪使用 Sleuth + Zipkin，配置中心使用 Config Server。这些组件共同构成了完整的微服务治理体系。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>技术选型：</strong>Spring Cloud Alibaba 也是不错的选择，提供了 Nacos、Sentinel 等国产优秀组件，更适合国内业务场景。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/97b0a3c6abca401591776ec349792d7c.png', 3, 0, 1, 7, 1, 0, 0, 1, NULL, '2024-02-28 23:54:50', '2025-08-31 19:23:59', 0);
INSERT INTO `article` VALUES (19, 1, 'hololens,业界资讯,kylin,springboot', 'ElasticSearch 全文搜索引擎实战与性能优化', '系统讲解 ElasticSearch 全文搜索引擎的核心概念和实践应用。涵盖索引设计、查询优化、聚合分析、集群部署等内容，助你构建高性能的搜索系统，提供秒级响应的搜索体验。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">ElasticSearch 搜索引擎实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、ElasticSearch 基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">ElasticSearch 是基于 Lucene 的分布式搜索引擎，提供了强大的全文检索、数据分析和可视化能力。它使用 JSON 格式的 RESTful API，易于使用和集成。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 创建索引\nPUT /articles\n{\n  \"mappings\": {\n    \"properties\": {\n      \"title\": { \"type\": \"text\", \"analyzer\": \"ik_max_word\" },\n      \"content\": { \"type\": \"text\", \"analyzer\": \"ik_max_word\" },\n      \"createTime\": { \"type\": \"date\" }\n    }\n  }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、查询 DSL 详解</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">全文查询：match、multi_match</li><li style=\"margin-bottom: 8px;\">精确查询：term、terms</li><li style=\"margin-bottom: 8px;\">范围查询：range</li><li style=\"margin-bottom: 8px;\">布尔查询：bool（must、should、must_not）</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、聚合分析与可视化</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">聚合（Aggregations）提供了强大的数据分析能力，包括指标聚合、桶聚合、管道聚合等。配合 Kibana 可以实现丰富的数据可视化和实时监控。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>性能建议：</strong>合理设计索引映射，选择合适的分词器。避免深度分页，使用 scroll 或 search_after。定期清理过期数据，保持集群健康。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/e03e6f972ad94635957b743ebda2d389.webp', 3, 0, 1, 7, 1, 0, 0, 1, NULL, '2024-02-28 23:54:50', '2025-08-31 19:32:32', 0);
INSERT INTO `article` VALUES (20, 1, 'xhtml,visual studio,batch,visual studio code', '软件工程实践：设计模式与代码重构完全指南', '深入探讨软件工程中的设计模式和代码重构技巧。通过经典案例分析，学习如何编写可维护、可扩展、高质量的代码，提升软件开发的工程化水平，成为架构设计的高手。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">设计模式与代码重构</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、软件设计原则</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">SOLID 原则是面向对象设计的基石：单一职责、开闭原则、里氏替换、接口隔离、依赖倒置。遵循这些原则能够提高代码的可维护性和可扩展性。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常用设计模式</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>创建型：</strong>单例、工厂、建造者</li><li style=\"margin-bottom: 8px;\"><strong>结构型：</strong>适配器、装饰器、代理</li><li style=\"margin-bottom: 8px;\"><strong>行为型：</strong>策略、观察者、模板方法</li></ul><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 策略模式示例\ninterface PaymentStrategy {\n    void pay(double amount);\n}\n\nclass CreditCardPayment implements PaymentStrategy {\n    public void pay(double amount) {\n        System.out.println(\"Credit card payment: \" + amount);\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、代码重构技巧</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">识别代码坏味道，如重复代码、过长方法、过大类等。通过提取方法、引入参数对象、用多态取代条件表达式等手法进行重构。保持小步快跑，每次重构后运行测试。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、单元测试与 TDD</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">测试驱动开发（TDD）提倡先写测试再写代码。单元测试是重构的安全网，能够及时发现回归问题。使用 JUnit、Mockito 等工具编写高质量的测试。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>重构理念：</strong>重构不是重写，而是在保持功能不变的前提下改善代码结构。持续重构，而不是等到代码腐化才大规模重构。</p></blockquote>', 'http://localhost:9000/sidifensen-blog/article/1/e609a624d54340179101b5a172d3d738.png', 5, 0, 1, 7, 1, 0, 1, 0, NULL, '2024-02-28 23:54:50', '2025-08-31 19:34:45', 0);
INSERT INTO `article` VALUES (21, 1, 'webview,数据库开发,everything', '容器化架构设计：从 Docker 到 Kubernetes 的实践之路', '全面讲解容器化技术的演进历程和实战经验。从 Docker 基础到 Kubernetes 编排，深入探讨容器网络、存储、安全等核心问题，帮助团队构建云原生应用架构。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">云原生架构设计与实践</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、云原生核心理念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">云原生（Cloud Native）是一种构建和运行应用的方法论，充分利用云计算的优势。它强调容器化、微服务架构、动态编排和 DevOps 文化，让应用更加灵活、可扩展和高可用。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Dockerfile 示例\nFROM node:16-alpine\nWORKDIR /app\nCOPY package*.json ./\nRUN npm ci --only=production\nCOPY . .\nEXPOSE 3000\nCMD [\"node\", \"server.js\"]</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、云原生技术栈</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">容器技术：Docker、Containerd</li><li style=\"margin-bottom: 8px;\">编排平台：Kubernetes、Docker Swarm</li><li style=\"margin-bottom: 8px;\">服务网格：Istio、Linkerd</li><li style=\"margin-bottom: 8px;\">可观测性：Prometheus、Grafana、Jaeger</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、DevOps 与 CI/CD</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">自动化是云原生的核心。通过 GitLab CI、Jenkins、ArgoCD 等工具实现持续集成和持续部署，提升交付效率。基础设施即代码（IaC）让环境管理变得简单可靠。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、最佳实践与案例</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">十二要素应用、不可变基础设施、声明式 API、自动化测试和部署等最佳实践，帮助团队构建高质量的云原生应用。通过实际案例学习大厂的云原生实践经验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>转型建议：</strong>云原生转型是一个渐进的过程，需要技术和文化的双重变革。从小处着手，逐步积累经验，避免一步到位带来的风险。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/4b9729fb121e43ceaf6c522330b3f565.jpg', 5, 0, 9, 9, 1, 2, 1, 0, NULL, '2024-02-28 23:54:50', '2025-09-02 15:52:20', 0);
INSERT INTO `article` VALUES (22, 1, '硬件工程,cryengine,kylin', '实时数据处理：Kafka + Flink 流式计算平台搭建', '深入讲解实时数据处理的技术架构和实现方案。通过 Kafka 作为消息中间件，Flink 进行流式计算，构建高吞吐量的实时数据处理平台。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">云原生架构设计与实践</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、云原生核心理念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">云原生（Cloud Native）是一种构建和运行应用的方法论，充分利用云计算的优势。它强调容器化、微服务架构、动态编排和 DevOps 文化，让应用更加灵活、可扩展和高可用。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Dockerfile 示例\nFROM node:16-alpine\nWORKDIR /app\nCOPY package*.json ./\nRUN npm ci --only=production\nCOPY . .\nEXPOSE 3000\nCMD [\"node\", \"server.js\"]</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、云原生技术栈</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">容器技术：Docker、Containerd</li><li style=\"margin-bottom: 8px;\">编排平台：Kubernetes、Docker Swarm</li><li style=\"margin-bottom: 8px;\">服务网格：Istio、Linkerd</li><li style=\"margin-bottom: 8px;\">可观测性：Prometheus、Grafana、Jaeger</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、DevOps 与 CI/CD</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">自动化是云原生的核心。通过 GitLab CI、Jenkins、ArgoCD 等工具实现持续集成和持续部署，提升交付效率。基础设施即代码（IaC）让环境管理变得简单可靠。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、最佳实践与案例</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">十二要素应用、不可变基础设施、声明式 API、自动化测试和部署等最佳实践，帮助团队构建高质量的云原生应用。通过实际案例学习大厂的云原生实践经验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>转型建议：</strong>云原生转型是一个渐进的过程，需要技术和文化的双重变革。从小处着手，逐步积累经验，避免一步到位带来的风险。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/2/e9ef1601e7fd477da8a8819fa7ab25f9.webp', 5, 0, 9, 9, 1, 2, 1, 0, NULL, '2024-02-28 23:54:50', '2025-09-02 15:57:22', 0);
INSERT INTO `article` VALUES (23, 1, 'aac,unix,嵌入式实时数据库,威胁分析', '微服务监控体系：Prometheus + Grafana 实战指南', '系统讲解微服务监控的完整方案。使用 Prometheus 采集指标，Grafana 可视化展示，配置告警规则，构建全方位的监控体系，保障系统稳定运行。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">云原生架构设计与实践</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、云原生核心理念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">云原生（Cloud Native）是一种构建和运行应用的方法论，充分利用云计算的优势。它强调容器化、微服务架构、动态编排和 DevOps 文化，让应用更加灵活、可扩展和高可用。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Dockerfile 示例\nFROM node:16-alpine\nWORKDIR /app\nCOPY package*.json ./\nRUN npm ci --only=production\nCOPY . .\nEXPOSE 3000\nCMD [\"node\", \"server.js\"]</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、云原生技术栈</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">容器技术：Docker、Containerd</li><li style=\"margin-bottom: 8px;\">编排平台：Kubernetes、Docker Swarm</li><li style=\"margin-bottom: 8px;\">服务网格：Istio、Linkerd</li><li style=\"margin-bottom: 8px;\">可观测性：Prometheus、Grafana、Jaeger</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、DevOps 与 CI/CD</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">自动化是云原生的核心。通过 GitLab CI、Jenkins、ArgoCD 等工具实现持续集成和持续部署，提升交付效率。基础设施即代码（IaC）让环境管理变得简单可靠。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、最佳实践与案例</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">十二要素应用、不可变基础设施、声明式 API、自动化测试和部署等最佳实践，帮助团队构建高质量的云原生应用。通过实际案例学习大厂的云原生实践经验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>转型建议：</strong>云原生转型是一个渐进的过程，需要技术和文化的双重变革。从小处着手，逐步积累经验，避免一步到位带来的风险。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/a718f9f6a06246f29cde4b2591a19634.jpg', 5, 0, 9, 9, 0, 1, 1, 0, NULL, '2025-05-31 22:58:55', '2025-09-02 15:58:34', 0);
INSERT INTO `article` VALUES (24, 1, 'layui,android,区块链', '分布式缓存架构：Redis Cluster 集群部署与优化', '深度解析 Redis 集群架构的设计原理和部署实践。涵盖数据分片、故障转移、扩容缩容等核心内容，打造高可用的分布式缓存系统。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">云原生架构设计与实践</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、云原生核心理念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">云原生（Cloud Native）是一种构建和运行应用的方法论，充分利用云计算的优势。它强调容器化、微服务架构、动态编排和 DevOps 文化，让应用更加灵活、可扩展和高可用。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Dockerfile 示例\nFROM node:16-alpine\nWORKDIR /app\nCOPY package*.json ./\nRUN npm ci --only=production\nCOPY . .\nEXPOSE 3000\nCMD [\"node\", \"server.js\"]</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、云原生技术栈</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">容器技术：Docker、Containerd</li><li style=\"margin-bottom: 8px;\">编排平台：Kubernetes、Docker Swarm</li><li style=\"margin-bottom: 8px;\">服务网格：Istio、Linkerd</li><li style=\"margin-bottom: 8px;\">可观测性：Prometheus、Grafana、Jaeger</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、DevOps 与 CI/CD</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">自动化是云原生的核心。通过 GitLab CI、Jenkins、ArgoCD 等工具实现持续集成和持续部署，提升交付效率。基础设施即代码（IaC）让环境管理变得简单可靠。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、最佳实践与案例</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">十二要素应用、不可变基础设施、声明式 API、自动化测试和部署等最佳实践，帮助团队构建高质量的云原生应用。通过实际案例学习大厂的云原生实践经验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>转型建议：</strong>云原生转型是一个渐进的过程，需要技术和文化的双重变革。从小处着手，逐步积累经验，避免一步到位带来的风险。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/2/91a312e5917142069d18b13e3e507ab7.webp', 5, 0, 9, 9, 1, 2, 1, 0, NULL, '2025-05-31 22:58:55', '2025-08-31 22:59:27', 0);
INSERT INTO `article` VALUES (25, 1, 'powerpoint,postcss,chatgpt,dubbo', '现代前端框架对比：Vue vs React vs Angular 全解析', '客观对比三大前端框架的特点和适用场景。从技术架构、生态系统、学习曲线、性能表现等多个维度分析，帮助团队做出正确的技术选型。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">云原生架构设计与实践</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、云原生核心理念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">云原生（Cloud Native）是一种构建和运行应用的方法论，充分利用云计算的优势。它强调容器化、微服务架构、动态编排和 DevOps 文化，让应用更加灵活、可扩展和高可用。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Dockerfile 示例\nFROM node:16-alpine\nWORKDIR /app\nCOPY package*.json ./\nRUN npm ci --only=production\nCOPY . .\nEXPOSE 3000\nCMD [\"node\", \"server.js\"]</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、云原生技术栈</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">容器技术：Docker、Containerd</li><li style=\"margin-bottom: 8px;\">编排平台：Kubernetes、Docker Swarm</li><li style=\"margin-bottom: 8px;\">服务网格：Istio、Linkerd</li><li style=\"margin-bottom: 8px;\">可观测性：Prometheus、Grafana、Jaeger</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、DevOps 与 CI/CD</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">自动化是云原生的核心。通过 GitLab CI、Jenkins、ArgoCD 等工具实现持续集成和持续部署，提升交付效率。基础设施即代码（IaC）让环境管理变得简单可靠。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、最佳实践与案例</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">十二要素应用、不可变基础设施、声明式 API、自动化测试和部署等最佳实践，帮助团队构建高质量的云原生应用。通过实际案例学习大厂的云原生实践经验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>转型建议：</strong>云原生转型是一个渐进的过程，需要技术和文化的双重变革。从小处着手，逐步积累经验，避免一步到位带来的风险。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/1/c70f0f15ce8541a9820df4829e1274ed.webp', 9, 0, 9, 9, 0, 0, 0, 0, NULL, '2025-05-31 22:58:55', '2025-09-02 16:20:15', 0);
INSERT INTO `article` VALUES (26, 1, '云原生,fpga开发,程序人生', '数据库分库分表实战：从单库到分布式数据库的演进', '详细讲解数据库分库分表的设计方案和实施步骤。解决单表数据量过大、写入性能瓶颈等问题，构建支撑亿级数据的分布式数据库架构。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">大数据技术架构实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、大数据技术生态</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">大数据处理涉及数据采集、存储、计算、分析和可视化等多个环节。Hadoop 生态提供了分布式存储和批处理能力，Spark 提供了内存计算引擎，Flink 擅长实时流处理，Kafka 作为数据管道连接各个系统。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Spark 数据处理示例\nval df = spark.read.parquet(\"hdfs://data/users\")\nval result = df.filter($\"age\" > 18)\n  .groupBy(\"city\")\n  .count()\n  .orderBy($\"count\".desc)\nresult.write.parquet(\"hdfs://output/result\")</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、实时计算与流处理</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">流处理框架：Flink、Storm、Spark Streaming</li><li style=\"margin-bottom: 8px;\">消息队列：Kafka、RocketMQ、Pulsar</li><li style=\"margin-bottom: 8px;\">实时数仓：ClickHouse、Druid</li><li style=\"margin-bottom: 8px;\">数据同步：Canal、Debezium</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据仓库与 OLAP</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">数据仓库采用维度建模，分为 ODS、DWD、DWS、ADS 等层次。OLAP 引擎如 Kylin、Doris 提供秒级的多维分析能力。掌握数据建模和 SQL 优化是数据分析师的核心技能。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能优化与运维</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">合理的数据分区、选择合适的文件格式（Parquet、ORC）、调优任务参数、监控集群资源等都是提升大数据处理性能的关键。通过性能分析工具定位瓶颈，针对性优化。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>学习路线：</strong>大数据技术栈庞大，建议从基础开始，先掌握一个方向（如离线计算或实时计算），再逐步扩展到其他领域。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/117f9f2a16e34e19849800dbe99bbea0.webp', 88, 0, 9, 9, 0, 0, 0, 0, NULL, '2025-05-31 22:58:55', '2025-09-02 16:20:18', 0);
INSERT INTO `article` VALUES (27, 1, 'easyui,odps,ogre', 'GraphQL API 设计：下一代 API 查询语言实战', '全面介绍 GraphQL 的核心概念和实践应用。相比 RESTful API，GraphQL 提供更灵活的数据查询能力，减少网络请求，提升开发效率。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">大数据技术架构实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、大数据技术生态</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">大数据处理涉及数据采集、存储、计算、分析和可视化等多个环节。Hadoop 生态提供了分布式存储和批处理能力，Spark 提供了内存计算引擎，Flink 擅长实时流处理，Kafka 作为数据管道连接各个系统。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Spark 数据处理示例\nval df = spark.read.parquet(\"hdfs://data/users\")\nval result = df.filter($\"age\" > 18)\n  .groupBy(\"city\")\n  .count()\n  .orderBy($\"count\".desc)\nresult.write.parquet(\"hdfs://output/result\")</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、实时计算与流处理</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">流处理框架：Flink、Storm、Spark Streaming</li><li style=\"margin-bottom: 8px;\">消息队列：Kafka、RocketMQ、Pulsar</li><li style=\"margin-bottom: 8px;\">实时数仓：ClickHouse、Druid</li><li style=\"margin-bottom: 8px;\">数据同步：Canal、Debezium</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据仓库与 OLAP</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">数据仓库采用维度建模，分为 ODS、DWD、DWS、ADS 等层次。OLAP 引擎如 Kylin、Doris 提供秒级的多维分析能力。掌握数据建模和 SQL 优化是数据分析师的核心技能。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能优化与运维</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">合理的数据分区、选择合适的文件格式（Parquet、ORC）、调优任务参数、监控集群资源等都是提升大数据处理性能的关键。通过性能分析工具定位瓶颈，针对性优化。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>学习路线：</strong>大数据技术栈庞大，建议从基础开始，先掌握一个方向（如离线计算或实时计算），再逐步扩展到其他领域。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/11/e1a22085ec6d4c2a8e9020c4766920e6.webp', 88, 0, 9, 9, 0, 0, 0, 0, NULL, '2025-08-02 16:20:19', '2025-09-02 16:20:19', 0);
INSERT INTO `article` VALUES (28, 1, 'r语言-4.2.1,knative,tdesign,墨刀', '服务网格 Istio：微服务治理的终极解决方案', '深入探讨 Istio 服务网格的架构设计和使用方法。实现流量管理、安全通信、可观测性等功能，让微服务治理变得简单高效。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">大数据技术架构实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、大数据技术生态</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">大数据处理涉及数据采集、存储、计算、分析和可视化等多个环节。Hadoop 生态提供了分布式存储和批处理能力，Spark 提供了内存计算引擎，Flink 擅长实时流处理，Kafka 作为数据管道连接各个系统。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Spark 数据处理示例\nval df = spark.read.parquet(\"hdfs://data/users\")\nval result = df.filter($\"age\" > 18)\n  .groupBy(\"city\")\n  .count()\n  .orderBy($\"count\".desc)\nresult.write.parquet(\"hdfs://output/result\")</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、实时计算与流处理</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">流处理框架：Flink、Storm、Spark Streaming</li><li style=\"margin-bottom: 8px;\">消息队列：Kafka、RocketMQ、Pulsar</li><li style=\"margin-bottom: 8px;\">实时数仓：ClickHouse、Druid</li><li style=\"margin-bottom: 8px;\">数据同步：Canal、Debezium</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据仓库与 OLAP</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">数据仓库采用维度建模，分为 ODS、DWD、DWS、ADS 等层次。OLAP 引擎如 Kylin、Doris 提供秒级的多维分析能力。掌握数据建模和 SQL 优化是数据分析师的核心技能。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能优化与运维</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">合理的数据分区、选择合适的文件格式（Parquet、ORC）、调优任务参数、监控集群资源等都是提升大数据处理性能的关键。通过性能分析工具定位瓶颈，针对性优化。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>学习路线：</strong>大数据技术栈庞大，建议从基础开始，先掌握一个方向（如离线计算或实时计算），再逐步扩展到其他领域。</p></blockquote>', 'http://115.190.116.72:40000/sidifensen-blog/album/1/1/40243394761a40e6b40295eda2b55e66.webp', 88, 0, 9, 9, 0, 0, 0, 0, NULL, '2025-07-02 16:20:20', '2025-09-02 16:20:20', 0);
INSERT INTO `article` VALUES (29, 1, '能源,git,kmeans', 'WebAssembly 技术探索：浏览器中的高性能计算', 'WebAssembly 为 Web 带来接近原生的性能。本文介绍 WASM 的原理、工具链和应用场景，探索前端性能优化的新方向。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">大数据技术架构实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、大数据技术生态</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">大数据处理涉及数据采集、存储、计算、分析和可视化等多个环节。Hadoop 生态提供了分布式存储和批处理能力，Spark 提供了内存计算引擎，Flink 擅长实时流处理，Kafka 作为数据管道连接各个系统。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Spark 数据处理示例\nval df = spark.read.parquet(\"hdfs://data/users\")\nval result = df.filter($\"age\" > 18)\n  .groupBy(\"city\")\n  .count()\n  .orderBy($\"count\".desc)\nresult.write.parquet(\"hdfs://output/result\")</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、实时计算与流处理</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">流处理框架：Flink、Storm、Spark Streaming</li><li style=\"margin-bottom: 8px;\">消息队列：Kafka、RocketMQ、Pulsar</li><li style=\"margin-bottom: 8px;\">实时数仓：ClickHouse、Druid</li><li style=\"margin-bottom: 8px;\">数据同步：Canal、Debezium</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据仓库与 OLAP</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">数据仓库采用维度建模，分为 ODS、DWD、DWS、ADS 等层次。OLAP 引擎如 Kylin、Doris 提供秒级的多维分析能力。掌握数据建模和 SQL 优化是数据分析师的核心技能。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能优化与运维</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">合理的数据分区、选择合适的文件格式（Parquet、ORC）、调优任务参数、监控集群资源等都是提升大数据处理性能的关键。通过性能分析工具定位瓶颈，针对性优化。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>学习路线：</strong>大数据技术栈庞大，建议从基础开始，先掌握一个方向（如离线计算或实时计算），再逐步扩展到其他领域。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/5b407324dfb2441794d88b39026ff33f.webp', 88, 0, 9, 9, 0, 0, 0, 0, NULL, '2025-07-02 16:20:20', '2025-09-02 16:20:20', 0);
INSERT INTO `article` VALUES (30, 1, '散列表,gin,substance designer', 'Serverless 架构实践：函数即服务的应用与挑战', '详细讲解 Serverless 架构的理念和实践。使用 AWS Lambda、阿里云函数计算等服务，构建按需付费、弹性伸缩的应用系统。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">大数据技术架构实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、大数据技术生态</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">大数据处理涉及数据采集、存储、计算、分析和可视化等多个环节。Hadoop 生态提供了分布式存储和批处理能力，Spark 提供了内存计算引擎，Flink 擅长实时流处理，Kafka 作为数据管道连接各个系统。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Spark 数据处理示例\nval df = spark.read.parquet(\"hdfs://data/users\")\nval result = df.filter($\"age\" > 18)\n  .groupBy(\"city\")\n  .count()\n  .orderBy($\"count\".desc)\nresult.write.parquet(\"hdfs://output/result\")</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、实时计算与流处理</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">流处理框架：Flink、Storm、Spark Streaming</li><li style=\"margin-bottom: 8px;\">消息队列：Kafka、RocketMQ、Pulsar</li><li style=\"margin-bottom: 8px;\">实时数仓：ClickHouse、Druid</li><li style=\"margin-bottom: 8px;\">数据同步：Canal、Debezium</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据仓库与 OLAP</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">数据仓库采用维度建模，分为 ODS、DWD、DWS、ADS 等层次。OLAP 引擎如 Kylin、Doris 提供秒级的多维分析能力。掌握数据建模和 SQL 优化是数据分析师的核心技能。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能优化与运维</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">合理的数据分区、选择合适的文件格式（Parquet、ORC）、调优任务参数、监控集群资源等都是提升大数据处理性能的关键。通过性能分析工具定位瓶颈，针对性优化。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>学习路线：</strong>大数据技术栈庞大，建议从基础开始，先掌握一个方向（如离线计算或实时计算），再逐步扩展到其他领域。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/35e091cda96446dbb65f37075d6a6981.png', 88, 0, 9, 9, 0, 1, 0, 0, NULL, '2025-07-02 16:20:20', '2025-09-02 16:20:21', 0);
INSERT INTO `article` VALUES (31, 1, '网络,idm,笔记', 'Go 语言高并发编程：协程与通道的艺术', 'Go 语言以其强大的并发能力著称。深入讲解 goroutine 和 channel 的使用技巧，通过实战项目掌握 Go 的并发编程模式，构建高性能服务。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">人工智能与机器学习实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、机器学习基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">机器学习让计算机从数据中学习规律，而无需显式编程。监督学习用于分类和回归，无监督学习用于聚类和降维，强化学习用于决策优化。理解各种算法的适用场景是应用 AI 的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>import tensorflow as tf\nfrom tensorflow import keras\n\n# 构建神经网络模型\nmodel = keras.Sequential([\n    keras.layers.Dense(128, activation=\'relu\', input_shape=(784,)),\n    keras.layers.Dropout(0.2),\n    keras.layers.Dense(10, activation=\'softmax\')\n])\n\nmodel.compile(optimizer=\'adam\', loss=\'sparse_categorical_crossentropy\', metrics=[\'accuracy\'])</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、深度学习框架</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">TensorFlow：Google 开源的端到端机器学习平台</li><li style=\"margin-bottom: 8px;\">PyTorch：Facebook 开源的深度学习框架</li><li style=\"margin-bottom: 8px;\">Keras：高级神经网络 API</li><li style=\"margin-bottom: 8px;\">MXNet、PaddlePaddle：国产深度学习框架</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、计算机视觉与 NLP</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">CV 领域的卷积神经网络（CNN）在图像分类、目标检测、图像分割等任务上表现出色。NLP 领域的 Transformer 架构革新了自然语言处理，BERT、GPT 等大模型展现了强大的语言理解能力。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、AI 应用与落地</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">从模型训练到部署上线，需要考虑数据准备、模型优化、模型压缩、推理加速等问题。TensorFlow Serving、TorchServe、ONNX 等工具帮助我们将模型部署到生产环境。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>AI 学习建议：</strong>扎实的数学基础（线性代数、概率统计、微积分）是学习 AI 的基石。多动手实践，从经典案例开始，逐步深入。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/e922e46e96ba41f3baca346e212b6ba6.jpg', 9, 0, 9, 9, 0, 1, 0, 0, NULL, '2025-07-02 16:20:20', '2025-09-02 16:20:21', 0);
INSERT INTO `article` VALUES (32, 1, 'vue,mllib,超分辨率重建,需求分析', '持续集成与持续部署：Jenkins Pipeline 完全指南', '详细讲解 Jenkins Pipeline 的编写和最佳实践。从代码提交到自动部署，构建完整的 CI/CD 流水线，提升软件交付效率和质量。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">人工智能与机器学习实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、机器学习基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">机器学习让计算机从数据中学习规律，而无需显式编程。监督学习用于分类和回归，无监督学习用于聚类和降维，强化学习用于决策优化。理解各种算法的适用场景是应用 AI 的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>import tensorflow as tf\nfrom tensorflow import keras\n\n# 构建神经网络模型\nmodel = keras.Sequential([\n    keras.layers.Dense(128, activation=\'relu\', input_shape=(784,)),\n    keras.layers.Dropout(0.2),\n    keras.layers.Dense(10, activation=\'softmax\')\n])\n\nmodel.compile(optimizer=\'adam\', loss=\'sparse_categorical_crossentropy\', metrics=[\'accuracy\'])</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、深度学习框架</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">TensorFlow：Google 开源的端到端机器学习平台</li><li style=\"margin-bottom: 8px;\">PyTorch：Facebook 开源的深度学习框架</li><li style=\"margin-bottom: 8px;\">Keras：高级神经网络 API</li><li style=\"margin-bottom: 8px;\">MXNet、PaddlePaddle：国产深度学习框架</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、计算机视觉与 NLP</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">CV 领域的卷积神经网络（CNN）在图像分类、目标检测、图像分割等任务上表现出色。NLP 领域的 Transformer 架构革新了自然语言处理，BERT、GPT 等大模型展现了强大的语言理解能力。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、AI 应用与落地</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">从模型训练到部署上线，需要考虑数据准备、模型优化、模型压缩、推理加速等问题。TensorFlow Serving、TorchServe、ONNX 等工具帮助我们将模型部署到生产环境。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>AI 学习建议：</strong>扎实的数学基础（线性代数、概率统计、微积分）是学习 AI 的基石。多动手实践，从经典案例开始，逐步深入。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/5c02be27899a47b9aa21283148eacfaf.webp', 9, 0, 9, 9, 0, 1, 0, 0, NULL, '2025-09-02 16:20:21', '2025-09-02 16:20:21', 0);
INSERT INTO `article` VALUES (33, 1, '其他,twitter,h.266', '单页应用 SPA 架构设计：路由、状态管理与性能优化', '系统讲解单页应用的架构设计和技术要点。涵盖路由管理、状态管理、代码分割、预渲染等内容，打造流畅的用户体验。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">人工智能与机器学习实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、机器学习基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">机器学习让计算机从数据中学习规律，而无需显式编程。监督学习用于分类和回归，无监督学习用于聚类和降维，强化学习用于决策优化。理解各种算法的适用场景是应用 AI 的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>import tensorflow as tf\nfrom tensorflow import keras\n\n# 构建神经网络模型\nmodel = keras.Sequential([\n    keras.layers.Dense(128, activation=\'relu\', input_shape=(784,)),\n    keras.layers.Dropout(0.2),\n    keras.layers.Dense(10, activation=\'softmax\')\n])\n\nmodel.compile(optimizer=\'adam\', loss=\'sparse_categorical_crossentropy\', metrics=[\'accuracy\'])</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、深度学习框架</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">TensorFlow：Google 开源的端到端机器学习平台</li><li style=\"margin-bottom: 8px;\">PyTorch：Facebook 开源的深度学习框架</li><li style=\"margin-bottom: 8px;\">Keras：高级神经网络 API</li><li style=\"margin-bottom: 8px;\">MXNet、PaddlePaddle：国产深度学习框架</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、计算机视觉与 NLP</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">CV 领域的卷积神经网络（CNN）在图像分类、目标检测、图像分割等任务上表现出色。NLP 领域的 Transformer 架构革新了自然语言处理，BERT、GPT 等大模型展现了强大的语言理解能力。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、AI 应用与落地</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">从模型训练到部署上线，需要考虑数据准备、模型优化、模型压缩、推理加速等问题。TensorFlow Serving、TorchServe、ONNX 等工具帮助我们将模型部署到生产环境。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>AI 学习建议：</strong>扎实的数学基础（线性代数、概率统计、微积分）是学习 AI 的基石。多动手实践，从经典案例开始，逐步深入。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/1/75af8b4e0d5a40cfa4b3cbbb896c4160.webp', 9, 0, 9, 9, 0, 1, 0, 0, NULL, '2025-09-02 16:20:22', '2025-09-02 16:20:22', 0);
INSERT INTO `article` VALUES (34, 1, 'c5全栈,sqoop,sharepoint', 'OAuth 2.0 与 JWT：现代化身份认证与授权方案', '深入解析 OAuth 2.0 授权协议和 JWT 令牌机制。学习如何实现安全的身份认证和授权系统，保护 API 接口和用户数据。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">人工智能与机器学习实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、机器学习基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">机器学习让计算机从数据中学习规律，而无需显式编程。监督学习用于分类和回归，无监督学习用于聚类和降维，强化学习用于决策优化。理解各种算法的适用场景是应用 AI 的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>import tensorflow as tf\nfrom tensorflow import keras\n\n# 构建神经网络模型\nmodel = keras.Sequential([\n    keras.layers.Dense(128, activation=\'relu\', input_shape=(784,)),\n    keras.layers.Dropout(0.2),\n    keras.layers.Dense(10, activation=\'softmax\')\n])\n\nmodel.compile(optimizer=\'adam\', loss=\'sparse_categorical_crossentropy\', metrics=[\'accuracy\'])</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、深度学习框架</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">TensorFlow：Google 开源的端到端机器学习平台</li><li style=\"margin-bottom: 8px;\">PyTorch：Facebook 开源的深度学习框架</li><li style=\"margin-bottom: 8px;\">Keras：高级神经网络 API</li><li style=\"margin-bottom: 8px;\">MXNet、PaddlePaddle：国产深度学习框架</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、计算机视觉与 NLP</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">CV 领域的卷积神经网络（CNN）在图像分类、目标检测、图像分割等任务上表现出色。NLP 领域的 Transformer 架构革新了自然语言处理，BERT、GPT 等大模型展现了强大的语言理解能力。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、AI 应用与落地</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">从模型训练到部署上线，需要考虑数据准备、模型优化、模型压缩、推理加速等问题。TensorFlow Serving、TorchServe、ONNX 等工具帮助我们将模型部署到生产环境。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>AI 学习建议：</strong>扎实的数学基础（线性代数、概率统计、微积分）是学习 AI 的基石。多动手实践，从经典案例开始，逐步深入。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/3e5f6e3d8c7b4c108ff5fc345c2d7473.webp', 99, 0, 9, 9, 0, 0, 0, 0, NULL, '2025-09-03 17:05:48', '2025-09-03 17:05:48', 0);
INSERT INTO `article` VALUES (35, 1, 'binder,substance designer,教育电商,redis', '搜索引擎优化 SEO：让你的网站在搜索结果中脱颖而出', '全面讲解 SEO 的核心技术和实践方法。从关键词优化、网站结构、内容质量到外链建设，提升网站在搜索引擎中的排名。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">人工智能与机器学习实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、机器学习基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">机器学习让计算机从数据中学习规律，而无需显式编程。监督学习用于分类和回归，无监督学习用于聚类和降维，强化学习用于决策优化。理解各种算法的适用场景是应用 AI 的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>import tensorflow as tf\nfrom tensorflow import keras\n\n# 构建神经网络模型\nmodel = keras.Sequential([\n    keras.layers.Dense(128, activation=\'relu\', input_shape=(784,)),\n    keras.layers.Dropout(0.2),\n    keras.layers.Dense(10, activation=\'softmax\')\n])\n\nmodel.compile(optimizer=\'adam\', loss=\'sparse_categorical_crossentropy\', metrics=[\'accuracy\'])</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、深度学习框架</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">TensorFlow：Google 开源的端到端机器学习平台</li><li style=\"margin-bottom: 8px;\">PyTorch：Facebook 开源的深度学习框架</li><li style=\"margin-bottom: 8px;\">Keras：高级神经网络 API</li><li style=\"margin-bottom: 8px;\">MXNet、PaddlePaddle：国产深度学习框架</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、计算机视觉与 NLP</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">CV 领域的卷积神经网络（CNN）在图像分类、目标检测、图像分割等任务上表现出色。NLP 领域的 Transformer 架构革新了自然语言处理，BERT、GPT 等大模型展现了强大的语言理解能力。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、AI 应用与落地</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">从模型训练到部署上线，需要考虑数据准备、模型优化、模型压缩、推理加速等问题。TensorFlow Serving、TorchServe、ONNX 等工具帮助我们将模型部署到生产环境。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>AI 学习建议：</strong>扎实的数学基础（线性代数、概率统计、微积分）是学习 AI 的基石。多动手实践，从经典案例开始，逐步深入。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/2/80e6bcc62397422bb9b6749d03ff5ab7.webp', 99, 0, 9, 9, 1, 0, 0, 0, NULL, '2025-09-03 17:07:30', '2025-09-04 22:03:00', 0);
INSERT INTO `article` VALUES (36, 1, 'rxswift,美食,智能音箱', '响应式 Web 设计：移动优先的前端开发策略', '详细讲解响应式设计的原理和实现技巧。使用 CSS 媒体查询、弹性布局、rem 单位等技术，让网站在各种设备上完美展示。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">区块链技术原理与应用</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、区块链核心概念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">区块链是一种分布式账本技术，通过密码学和共识机制保证数据的不可篡改性和一致性。它具有去中心化、透明性、可追溯等特点，在金融、供应链、版权保护等领域有广泛应用前景。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Solidity 智能合约示例\npragma solidity ^0.8.0;\n\ncontract SimpleStorage {\n    uint256 private storedData;\n    \n    function set(uint256 x) public {\n        storedData = x;\n    }\n    \n    function get() public view returns (uint256) {\n        return storedData;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、共识机制详解</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">PoW（工作量证明）：比特币使用</li><li style=\"margin-bottom: 8px;\">PoS（权益证明）：以太坊 2.0 采用</li><li style=\"margin-bottom: 8px;\">DPoS（委托权益证明）：EOS 使用</li><li style=\"margin-bottom: 8px;\">PBFT（实用拜占庭容错）：联盟链常用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、智能合约与 DApp 开发</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">智能合约是运行在区块链上的程序，自动执行合约条款。使用 Solidity 语言开发以太坊智能合约，通过 Truffle、Hardhat 等工具进行测试和部署。前端使用 Web3.js 与区块链交互。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、区块链应用场景</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">DeFi（去中心化金融）、NFT（非同质化代币）、供应链溯源、数字身份认证、版权保护等都是区块链的典型应用。了解业务需求，评估是否适合使用区块链技术。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>技术选型：</strong>公链、联盟链、私链各有特点。公链去中心化程度高但性能较低，联盟链适合企业应用，私链性能最好但去中心化程度最低。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/df3dac165d99472c8df89b5df33a54f8.jpg', 99, 0, 9, 9, 1, 0, 0, 0, NULL, '2025-09-03 17:14:23', '2025-09-04 02:38:38', 0);
INSERT INTO `article` VALUES (37, 1, 'layui,创业创新,tomcat', '领域驱动设计 DDD：构建复杂业务系统的方法论', '深入探讨领域驱动设计的核心思想和实践方法。通过战略设计和战术设计，构建清晰的领域模型，应对复杂的业务逻辑。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">区块链技术原理与应用</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、区块链核心概念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">区块链是一种分布式账本技术，通过密码学和共识机制保证数据的不可篡改性和一致性。它具有去中心化、透明性、可追溯等特点，在金融、供应链、版权保护等领域有广泛应用前景。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Solidity 智能合约示例\npragma solidity ^0.8.0;\n\ncontract SimpleStorage {\n    uint256 private storedData;\n    \n    function set(uint256 x) public {\n        storedData = x;\n    }\n    \n    function get() public view returns (uint256) {\n        return storedData;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、共识机制详解</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">PoW（工作量证明）：比特币使用</li><li style=\"margin-bottom: 8px;\">PoS（权益证明）：以太坊 2.0 采用</li><li style=\"margin-bottom: 8px;\">DPoS（委托权益证明）：EOS 使用</li><li style=\"margin-bottom: 8px;\">PBFT（实用拜占庭容错）：联盟链常用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、智能合约与 DApp 开发</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">智能合约是运行在区块链上的程序，自动执行合约条款。使用 Solidity 语言开发以太坊智能合约，通过 Truffle、Hardhat 等工具进行测试和部署。前端使用 Web3.js 与区块链交互。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、区块链应用场景</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">DeFi（去中心化金融）、NFT（非同质化代币）、供应链溯源、数字身份认证、版权保护等都是区块链的典型应用。了解业务需求，评估是否适合使用区块链技术。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>技术选型：</strong>公链、联盟链、私链各有特点。公链去中心化程度高但性能较低，联盟链适合企业应用，私链性能最好但去中心化程度最低。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/2/14/3ad10d18a3ff47d59e049211981b4ac9.webp', 99, 0, 9, 9, 1, 0, 0, 0, NULL, '2025-09-03 17:17:32', '2025-09-04 02:37:23', 0);
INSERT INTO `article` VALUES (38, 1, '迪米特法则,godot,pandas,inverted-index', '代码审查最佳实践：提升团队代码质量的有效方法', '系统讲解代码审查的流程和技巧。从审查工具、审查清单到团队文化建设，通过有效的代码审查提升代码质量和团队协作。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">区块链技术原理与应用</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、区块链核心概念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">区块链是一种分布式账本技术，通过密码学和共识机制保证数据的不可篡改性和一致性。它具有去中心化、透明性、可追溯等特点，在金融、供应链、版权保护等领域有广泛应用前景。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Solidity 智能合约示例\npragma solidity ^0.8.0;\n\ncontract SimpleStorage {\n    uint256 private storedData;\n    \n    function set(uint256 x) public {\n        storedData = x;\n    }\n    \n    function get() public view returns (uint256) {\n        return storedData;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、共识机制详解</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">PoW（工作量证明）：比特币使用</li><li style=\"margin-bottom: 8px;\">PoS（权益证明）：以太坊 2.0 采用</li><li style=\"margin-bottom: 8px;\">DPoS（委托权益证明）：EOS 使用</li><li style=\"margin-bottom: 8px;\">PBFT（实用拜占庭容错）：联盟链常用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、智能合约与 DApp 开发</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">智能合约是运行在区块链上的程序，自动执行合约条款。使用 Solidity 语言开发以太坊智能合约，通过 Truffle、Hardhat 等工具进行测试和部署。前端使用 Web3.js 与区块链交互。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、区块链应用场景</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">DeFi（去中心化金融）、NFT（非同质化代币）、供应链溯源、数字身份认证、版权保护等都是区块链的典型应用。了解业务需求，评估是否适合使用区块链技术。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>技术选型：</strong>公链、联盟链、私链各有特点。公链去中心化程度高但性能较低，联盟链适合企业应用，私链性能最好但去中心化程度最低。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/16cfd6a49634443c896a3241030a3169.jpg', 99, 0, 9, 9, 1, 0, 0, 0, NULL, '2025-09-03 17:18:27', '2025-09-04 18:02:18', 0);
INSERT INTO `article` VALUES (39, 1, '安全,华为od,mpeg-1', '消息驱动架构：Event Sourcing 与 CQRS 模式详解', '深入解析事件溯源和命令查询职责分离模式。通过消息驱动架构处理复杂业务场景，实现高度解耦和可扩展的系统设计。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">区块链技术原理与应用</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、区块链核心概念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">区块链是一种分布式账本技术，通过密码学和共识机制保证数据的不可篡改性和一致性。它具有去中心化、透明性、可追溯等特点，在金融、供应链、版权保护等领域有广泛应用前景。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Solidity 智能合约示例\npragma solidity ^0.8.0;\n\ncontract SimpleStorage {\n    uint256 private storedData;\n    \n    function set(uint256 x) public {\n        storedData = x;\n    }\n    \n    function get() public view returns (uint256) {\n        return storedData;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、共识机制详解</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">PoW（工作量证明）：比特币使用</li><li style=\"margin-bottom: 8px;\">PoS（权益证明）：以太坊 2.0 采用</li><li style=\"margin-bottom: 8px;\">DPoS（委托权益证明）：EOS 使用</li><li style=\"margin-bottom: 8px;\">PBFT（实用拜占庭容错）：联盟链常用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、智能合约与 DApp 开发</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">智能合约是运行在区块链上的程序，自动执行合约条款。使用 Solidity 语言开发以太坊智能合约，通过 Truffle、Hardhat 等工具进行测试和部署。前端使用 Web3.js 与区块链交互。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、区块链应用场景</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">DeFi（去中心化金融）、NFT（非同质化代币）、供应链溯源、数字身份认证、版权保护等都是区块链的典型应用。了解业务需求，评估是否适合使用区块链技术。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>技术选型：</strong>公链、联盟链、私链各有特点。公链去中心化程度高但性能较低，联盟链适合企业应用，私链性能最好但去中心化程度最低。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/2/444ce751eebc480e96bc2bd6e61254bb.webp', 99, 0, 9, 9, 1, 0, 0, 0, NULL, '2025-09-03 17:20:02', '2025-09-04 21:12:34', 0);
INSERT INTO `article` VALUES (40, 1, '智能手表,python,正则表达式', 'Web 安全攻防：XSS、CSRF、SQL 注入防御实战', '全面讲解 Web 安全的常见漏洞和防御措施。学习如何识别和修复安全隐患，构建安全可靠的 Web 应用系统。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">区块链技术原理与应用</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、区块链核心概念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">区块链是一种分布式账本技术，通过密码学和共识机制保证数据的不可篡改性和一致性。它具有去中心化、透明性、可追溯等特点，在金融、供应链、版权保护等领域有广泛应用前景。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Solidity 智能合约示例\npragma solidity ^0.8.0;\n\ncontract SimpleStorage {\n    uint256 private storedData;\n    \n    function set(uint256 x) public {\n        storedData = x;\n    }\n    \n    function get() public view returns (uint256) {\n        return storedData;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、共识机制详解</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">PoW（工作量证明）：比特币使用</li><li style=\"margin-bottom: 8px;\">PoS（权益证明）：以太坊 2.0 采用</li><li style=\"margin-bottom: 8px;\">DPoS（委托权益证明）：EOS 使用</li><li style=\"margin-bottom: 8px;\">PBFT（实用拜占庭容错）：联盟链常用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、智能合约与 DApp 开发</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">智能合约是运行在区块链上的程序，自动执行合约条款。使用 Solidity 语言开发以太坊智能合约，通过 Truffle、Hardhat 等工具进行测试和部署。前端使用 Web3.js 与区块链交互。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、区块链应用场景</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">DeFi（去中心化金融）、NFT（非同质化代币）、供应链溯源、数字身份认证、版权保护等都是区块链的典型应用。了解业务需求，评估是否适合使用区块链技术。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>技术选型：</strong>公链、联盟链、私链各有特点。公链去中心化程度高但性能较低，联盟链适合企业应用，私链性能最好但去中心化程度最低。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/2/658334308098465fbe66b5d4b3266ef9.webp', 99, 0, 9, 9, 1, 0, 0, 0, NULL, '2025-09-03 17:24:19', '2025-09-04 02:30:26', 0);
INSERT INTO `article` VALUES (41, 1, '开闭原则,功能测试,xmind', '前端工具链演进：Babel、Webpack、Vite 的发展历程', '回顾前端构建工具的发展历程，从 Grunt、Gulp 到 Webpack，再到新一代的 Vite。理解工具背后的原理，选择最适合项目的方案。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">网络安全与渗透测试</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、网络安全基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">网络安全包括物理安全、网络安全、系统安全、应用安全、数据安全等多个层面。理解 CIA 三元组（机密性、完整性、可用性）是安全工作的基础。掌握常见的攻击手段和防御策略。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># SQL 注入防御示例\n# 错误做法 - 直接拼接 SQL\nsql = f\"SELECT * FROM users WHERE id = {user_id}\"\n\n# 正确做法 - 使用参数化查询\nsql = \"SELECT * FROM users WHERE id = ?\"\ncursor.execute(sql, (user_id,))</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常见 Web 漏洞</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">SQL 注入：数据库查询注入恶意代码</li><li style=\"margin-bottom: 8px;\">XSS（跨站脚本）：注入恶意脚本</li><li style=\"margin-bottom: 8px;\">CSRF（跨站请求伪造）：伪造用户请求</li><li style=\"margin-bottom: 8px;\">文件上传漏洞：上传恶意文件</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、渗透测试工具</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kali Linux 集成了丰富的安全工具。Nmap 用于端口扫描，Metasploit 用于漏洞利用，Burp Suite 用于 Web 测试，Wireshark 用于流量分析。掌握这些工具的使用是渗透测试的基础。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、安全开发最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">输入验证、输出编码、最小权限原则、纵深防御等都是安全开发的重要原则。定期进行安全审计和代码审查，使用 HTTPS、加密存储敏感数据、实施访问控制。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>道德约束：</strong>网络安全技术是双刃剑，必须在法律和道德框架内使用。渗透测试需要获得授权，不得进行非法攻击。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/3/1d29f054b0f34070a6889dc49bf35ae3.webp', 99, 0, 9, 9, 1, 0, 0, 0, NULL, '2025-09-03 17:30:26', '2025-09-04 02:30:14', 0);
INSERT INTO `article` VALUES (42, 1, 'kmeans,网络,结对编程,物联网', 'gRPC 微服务通信：高性能 RPC 框架实战', 'gRPC 基于 HTTP/2 和 Protocol Buffers，提供高性能的服务间通信。学习如何使用 gRPC 构建微服务，实现跨语言的服务调用。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">网络安全与渗透测试</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、网络安全基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">网络安全包括物理安全、网络安全、系统安全、应用安全、数据安全等多个层面。理解 CIA 三元组（机密性、完整性、可用性）是安全工作的基础。掌握常见的攻击手段和防御策略。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># SQL 注入防御示例\n# 错误做法 - 直接拼接 SQL\nsql = f\"SELECT * FROM users WHERE id = {user_id}\"\n\n# 正确做法 - 使用参数化查询\nsql = \"SELECT * FROM users WHERE id = ?\"\ncursor.execute(sql, (user_id,))</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常见 Web 漏洞</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">SQL 注入：数据库查询注入恶意代码</li><li style=\"margin-bottom: 8px;\">XSS（跨站脚本）：注入恶意脚本</li><li style=\"margin-bottom: 8px;\">CSRF（跨站请求伪造）：伪造用户请求</li><li style=\"margin-bottom: 8px;\">文件上传漏洞：上传恶意文件</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、渗透测试工具</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kali Linux 集成了丰富的安全工具。Nmap 用于端口扫描，Metasploit 用于漏洞利用，Burp Suite 用于 Web 测试，Wireshark 用于流量分析。掌握这些工具的使用是渗透测试的基础。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、安全开发最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">输入验证、输出编码、最小权限原则、纵深防御等都是安全开发的重要原则。定期进行安全审计和代码审查，使用 HTTPS、加密存储敏感数据、实施访问控制。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>道德约束：</strong>网络安全技术是双刃剑，必须在法律和道德框架内使用。渗透测试需要获得授权，不得进行非法攻击。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 14, 0, 9, 9, 1, 0, 0, 0, NULL, '2025-09-03 17:35:54', '2025-09-14 20:46:11', 0);
INSERT INTO `article` VALUES (43, 1, '游戏策划,文心一言,宽度优先', '数据可视化实战：ECharts 与 D3.js 图表开发', '深入讲解数据可视化的技术和实践。使用 ECharts、D3.js 等工具库，创建各类交互式图表，让数据更直观地展现。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">网络安全与渗透测试</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、网络安全基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">网络安全包括物理安全、网络安全、系统安全、应用安全、数据安全等多个层面。理解 CIA 三元组（机密性、完整性、可用性）是安全工作的基础。掌握常见的攻击手段和防御策略。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># SQL 注入防御示例\n# 错误做法 - 直接拼接 SQL\nsql = f\"SELECT * FROM users WHERE id = {user_id}\"\n\n# 正确做法 - 使用参数化查询\nsql = \"SELECT * FROM users WHERE id = ?\"\ncursor.execute(sql, (user_id,))</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常见 Web 漏洞</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">SQL 注入：数据库查询注入恶意代码</li><li style=\"margin-bottom: 8px;\">XSS（跨站脚本）：注入恶意脚本</li><li style=\"margin-bottom: 8px;\">CSRF（跨站请求伪造）：伪造用户请求</li><li style=\"margin-bottom: 8px;\">文件上传漏洞：上传恶意文件</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、渗透测试工具</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kali Linux 集成了丰富的安全工具。Nmap 用于端口扫描，Metasploit 用于漏洞利用，Burp Suite 用于 Web 测试，Wireshark 用于流量分析。掌握这些工具的使用是渗透测试的基础。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、安全开发最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">输入验证、输出编码、最小权限原则、纵深防御等都是安全开发的重要原则。定期进行安全审计和代码审查，使用 HTTPS、加密存储敏感数据、实施访问控制。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>道德约束：</strong>网络安全技术是双刃剑，必须在法律和道德框架内使用。渗透测试需要获得授权，不得进行非法攻击。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/18/34ceaf19b2c14f088fe644e69b610812.webp', 11, 0, 9, 9, 1, 0, 0, 0, NULL, '2025-09-03 17:41:20', '2025-09-14 20:46:11', 0);
INSERT INTO `article` VALUES (44, 1, 'tdd,语音识别,DALL·E 2', 'API 网关设计：Kong 与 Nginx Plus 实战对比', '全面对比 Kong 和 Nginx Plus 两大 API 网关方案。从功能特性、性能表现、扩展能力等维度分析，选择合适的网关架构。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">网络安全与渗透测试</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、网络安全基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">网络安全包括物理安全、网络安全、系统安全、应用安全、数据安全等多个层面。理解 CIA 三元组（机密性、完整性、可用性）是安全工作的基础。掌握常见的攻击手段和防御策略。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># SQL 注入防御示例\n# 错误做法 - 直接拼接 SQL\nsql = f\"SELECT * FROM users WHERE id = {user_id}\"\n\n# 正确做法 - 使用参数化查询\nsql = \"SELECT * FROM users WHERE id = ?\"\ncursor.execute(sql, (user_id,))</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常见 Web 漏洞</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">SQL 注入：数据库查询注入恶意代码</li><li style=\"margin-bottom: 8px;\">XSS（跨站脚本）：注入恶意脚本</li><li style=\"margin-bottom: 8px;\">CSRF（跨站请求伪造）：伪造用户请求</li><li style=\"margin-bottom: 8px;\">文件上传漏洞：上传恶意文件</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、渗透测试工具</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kali Linux 集成了丰富的安全工具。Nmap 用于端口扫描，Metasploit 用于漏洞利用，Burp Suite 用于 Web 测试，Wireshark 用于流量分析。掌握这些工具的使用是渗透测试的基础。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、安全开发最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">输入验证、输出编码、最小权限原则、纵深防御等都是安全开发的重要原则。定期进行安全审计和代码审查，使用 HTTPS、加密存储敏感数据、实施访问控制。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>道德约束：</strong>网络安全技术是双刃剑，必须在法律和道德框架内使用。渗透测试需要获得授权，不得进行非法攻击。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/cb7baa9e5628490b80c4f019a52c51ea.jpg', 185, 0, 39, 9, 1, 0, 0, 0, '', '2025-09-03 17:42:41', '2025-09-14 20:46:11', 0);
INSERT INTO `article` VALUES (45, 1, 'openvino,自然语言处理,scala3.1.2', '云原生数据库：TiDB 分布式 NewSQL 实践指南', 'TiDB 兼容 MySQL 协议，支持水平扩展和强一致性。学习如何部署和使用 TiDB，构建支撑海量数据的云原生数据库系统。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">网络安全与渗透测试</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、网络安全基础</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">网络安全包括物理安全、网络安全、系统安全、应用安全、数据安全等多个层面。理解 CIA 三元组（机密性、完整性、可用性）是安全工作的基础。掌握常见的攻击手段和防御策略。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># SQL 注入防御示例\n# 错误做法 - 直接拼接 SQL\nsql = f\"SELECT * FROM users WHERE id = {user_id}\"\n\n# 正确做法 - 使用参数化查询\nsql = \"SELECT * FROM users WHERE id = ?\"\ncursor.execute(sql, (user_id,))</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常见 Web 漏洞</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">SQL 注入：数据库查询注入恶意代码</li><li style=\"margin-bottom: 8px;\">XSS（跨站脚本）：注入恶意脚本</li><li style=\"margin-bottom: 8px;\">CSRF（跨站请求伪造）：伪造用户请求</li><li style=\"margin-bottom: 8px;\">文件上传漏洞：上传恶意文件</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、渗透测试工具</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kali Linux 集成了丰富的安全工具。Nmap 用于端口扫描，Metasploit 用于漏洞利用，Burp Suite 用于 Web 测试，Wireshark 用于流量分析。掌握这些工具的使用是渗透测试的基础。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、安全开发最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">输入验证、输出编码、最小权限原则、纵深防御等都是安全开发的重要原则。定期进行安全审计和代码审查，使用 HTTPS、加密存储敏感数据、实施访问控制。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>道德约束：</strong>网络安全技术是双刃剑，必须在法律和道德框架内使用。渗透测试需要获得授权，不得进行非法攻击。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/cb6db250feac40de8f43d34ed846f658.jpg', 1, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:43:25', '2025-09-14 20:46:11', 0);
INSERT INTO `article` VALUES (46, 1, 'appium,安全威胁分析,人机交互,学习', '低代码平台开发：快速构建企业应用的新方式', '探索低代码平台的技术实现和应用场景。通过可视化开发、拖拽式配置，大幅提升开发效率，降低技术门槛。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">游戏开发技术栈与实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、游戏开发概述</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">游戏开发是综合性很强的领域，涉及图形渲染、物理模拟、人工智能、网络编程、音效设计等多个方面。Unity 和 Unreal Engine 是最流行的游戏引擎，降低了游戏开发的门槛。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Unity C# 脚本示例\npublic class PlayerController : MonoBehaviour {\n    public float speed = 10f;\n    \n    void Update() {\n        float horizontal = Input.GetAxis(\"Horizontal\");\n        float vertical = Input.GetAxis(\"Vertical\");\n        \n        Vector3 movement = new Vector3(horizontal, 0, vertical);\n        transform.Translate(movement * speed * Time.deltaTime);\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、游戏引擎对比</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Unity：</strong>易上手，跨平台，2D/3D 都支持</li><li style=\"margin-bottom: 8px;\"><strong>Unreal：</strong>画质顶级，蓝图系统，适合 3A 游戏</li><li style=\"margin-bottom: 8px;\"><strong>Cocos：</strong>国产引擎，适合手游开发</li><li style=\"margin-bottom: 8px;\"><strong>Godot：</strong>开源免费，轻量级</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、核心技术要点</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">图形渲染使用 Shader 实现各种视觉效果，物理引擎处理碰撞检测和刚体运动，AI 系统控制 NPC 行为，网络同步实现多人联机。掌握这些核心技术是开发高质量游戏的关键。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、游戏设计与优化</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">游戏设计包括玩法设计、关卡设计、平衡性调整等。性能优化关注 Draw Call、内存管理、资源加载。通过 Profiler 分析性能瓶颈，对症下药提升游戏流畅度。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>开发建议：</strong>从小项目开始，完成比完美更重要。多参考优秀游戏的设计，不断迭代优化。游戏开发需要团队协作，学会沟通和项目管理。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/2/14/cd176cf2d6864167abd79760ef223333.webp', 0, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:43:32', '2025-09-14 20:46:11', 0);
INSERT INTO `article` VALUES (47, 1, '政务,vp8,etl工程师', '前端状态管理：Redux、MobX、Zustand 方案对比', '系统对比主流前端状态管理方案的特点和适用场景。从简单到复杂，选择最适合项目的状态管理库。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">游戏开发技术栈与实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、游戏开发概述</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">游戏开发是综合性很强的领域，涉及图形渲染、物理模拟、人工智能、网络编程、音效设计等多个方面。Unity 和 Unreal Engine 是最流行的游戏引擎，降低了游戏开发的门槛。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Unity C# 脚本示例\npublic class PlayerController : MonoBehaviour {\n    public float speed = 10f;\n    \n    void Update() {\n        float horizontal = Input.GetAxis(\"Horizontal\");\n        float vertical = Input.GetAxis(\"Vertical\");\n        \n        Vector3 movement = new Vector3(horizontal, 0, vertical);\n        transform.Translate(movement * speed * Time.deltaTime);\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、游戏引擎对比</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Unity：</strong>易上手，跨平台，2D/3D 都支持</li><li style=\"margin-bottom: 8px;\"><strong>Unreal：</strong>画质顶级，蓝图系统，适合 3A 游戏</li><li style=\"margin-bottom: 8px;\"><strong>Cocos：</strong>国产引擎，适合手游开发</li><li style=\"margin-bottom: 8px;\"><strong>Godot：</strong>开源免费，轻量级</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、核心技术要点</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">图形渲染使用 Shader 实现各种视觉效果，物理引擎处理碰撞检测和刚体运动，AI 系统控制 NPC 行为，网络同步实现多人联机。掌握这些核心技术是开发高质量游戏的关键。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、游戏设计与优化</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">游戏设计包括玩法设计、关卡设计、平衡性调整等。性能优化关注 Draw Call、内存管理、资源加载。通过 Profiler 分析性能瓶颈，对症下药提升游戏流畅度。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>开发建议：</strong>从小项目开始，完成比完美更重要。多参考优秀游戏的设计，不断迭代优化。游戏开发需要团队协作，学会沟通和项目管理。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/653b9d45916c477e965b5232d0264bd0.jpg', 1, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:43:36', '2025-09-14 20:46:11', 0);
INSERT INTO `article` VALUES (48, 1, 'teambition,express,risc-v', 'HTTPS 加密通信：SSL/TLS 协议原理与证书配置', '深入解析 HTTPS 加密通信的工作原理。学习如何申请和配置 SSL 证书，为网站添加 HTTPS 支持，保护数据传输安全。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">游戏开发技术栈与实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、游戏开发概述</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">游戏开发是综合性很强的领域，涉及图形渲染、物理模拟、人工智能、网络编程、音效设计等多个方面。Unity 和 Unreal Engine 是最流行的游戏引擎，降低了游戏开发的门槛。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Unity C# 脚本示例\npublic class PlayerController : MonoBehaviour {\n    public float speed = 10f;\n    \n    void Update() {\n        float horizontal = Input.GetAxis(\"Horizontal\");\n        float vertical = Input.GetAxis(\"Vertical\");\n        \n        Vector3 movement = new Vector3(horizontal, 0, vertical);\n        transform.Translate(movement * speed * Time.deltaTime);\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、游戏引擎对比</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Unity：</strong>易上手，跨平台，2D/3D 都支持</li><li style=\"margin-bottom: 8px;\"><strong>Unreal：</strong>画质顶级，蓝图系统，适合 3A 游戏</li><li style=\"margin-bottom: 8px;\"><strong>Cocos：</strong>国产引擎，适合手游开发</li><li style=\"margin-bottom: 8px;\"><strong>Godot：</strong>开源免费，轻量级</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、核心技术要点</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">图形渲染使用 Shader 实现各种视觉效果，物理引擎处理碰撞检测和刚体运动，AI 系统控制 NPC 行为，网络同步实现多人联机。掌握这些核心技术是开发高质量游戏的关键。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、游戏设计与优化</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">游戏设计包括玩法设计、关卡设计、平衡性调整等。性能优化关注 Draw Call、内存管理、资源加载。通过 Profiler 分析性能瓶颈，对症下药提升游戏流畅度。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>开发建议：</strong>从小项目开始，完成比完美更重要。多参考优秀游戏的设计，不断迭代优化。游戏开发需要团队协作，学会沟通和项目管理。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/b058688e7b7949f79e69c37b4c2d19bb.jpg', 3, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:43:38', '2025-09-14 20:46:11', 0);
INSERT INTO `article` VALUES (49, 1, '实时音视频,传媒,figma,spring cloud', '函数式编程思想：不可变数据与纯函数的魅力', '探讨函数式编程的核心理念和实践方法。通过不可变数据、纯函数、高阶函数等概念，编写更可靠、可测试的代码。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">游戏开发技术栈与实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、游戏开发概述</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">游戏开发是综合性很强的领域，涉及图形渲染、物理模拟、人工智能、网络编程、音效设计等多个方面。Unity 和 Unreal Engine 是最流行的游戏引擎，降低了游戏开发的门槛。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Unity C# 脚本示例\npublic class PlayerController : MonoBehaviour {\n    public float speed = 10f;\n    \n    void Update() {\n        float horizontal = Input.GetAxis(\"Horizontal\");\n        float vertical = Input.GetAxis(\"Vertical\");\n        \n        Vector3 movement = new Vector3(horizontal, 0, vertical);\n        transform.Translate(movement * speed * Time.deltaTime);\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、游戏引擎对比</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Unity：</strong>易上手，跨平台，2D/3D 都支持</li><li style=\"margin-bottom: 8px;\"><strong>Unreal：</strong>画质顶级，蓝图系统，适合 3A 游戏</li><li style=\"margin-bottom: 8px;\"><strong>Cocos：</strong>国产引擎，适合手游开发</li><li style=\"margin-bottom: 8px;\"><strong>Godot：</strong>开源免费，轻量级</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、核心技术要点</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">图形渲染使用 Shader 实现各种视觉效果，物理引擎处理碰撞检测和刚体运动，AI 系统控制 NPC 行为，网络同步实现多人联机。掌握这些核心技术是开发高质量游戏的关键。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、游戏设计与优化</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">游戏设计包括玩法设计、关卡设计、平衡性调整等。性能优化关注 Draw Call、内存管理、资源加载。通过 Profiler 分析性能瓶颈，对症下药提升游戏流畅度。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>开发建议：</strong>从小项目开始，完成比完美更重要。多参考优秀游戏的设计，不断迭代优化。游戏开发需要团队协作，学会沟通和项目管理。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/9/7eb75aa3fe134277829afa0861bfe6f7.webp', 2, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:43:44', '2025-09-14 20:46:11', 0);
INSERT INTO `article` VALUES (50, 1, '远程工作,金融,anti-design-vue', 'WebSocket 实时通信：构建即时聊天和推送系统', '详细讲解 WebSocket 全双工通信协议的原理和应用。实现实时聊天、消息推送、在线协作等功能，提升用户体验。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">游戏开发技术栈与实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、游戏开发概述</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">游戏开发是综合性很强的领域，涉及图形渲染、物理模拟、人工智能、网络编程、音效设计等多个方面。Unity 和 Unreal Engine 是最流行的游戏引擎，降低了游戏开发的门槛。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Unity C# 脚本示例\npublic class PlayerController : MonoBehaviour {\n    public float speed = 10f;\n    \n    void Update() {\n        float horizontal = Input.GetAxis(\"Horizontal\");\n        float vertical = Input.GetAxis(\"Vertical\");\n        \n        Vector3 movement = new Vector3(horizontal, 0, vertical);\n        transform.Translate(movement * speed * Time.deltaTime);\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、游戏引擎对比</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Unity：</strong>易上手，跨平台，2D/3D 都支持</li><li style=\"margin-bottom: 8px;\"><strong>Unreal：</strong>画质顶级，蓝图系统，适合 3A 游戏</li><li style=\"margin-bottom: 8px;\"><strong>Cocos：</strong>国产引擎，适合手游开发</li><li style=\"margin-bottom: 8px;\"><strong>Godot：</strong>开源免费，轻量级</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、核心技术要点</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">图形渲染使用 Shader 实现各种视觉效果，物理引擎处理碰撞检测和刚体运动，AI 系统控制 NPC 行为，网络同步实现多人联机。掌握这些核心技术是开发高质量游戏的关键。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、游戏设计与优化</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">游戏设计包括玩法设计、关卡设计、平衡性调整等。性能优化关注 Draw Call、内存管理、资源加载。通过 Profiler 分析性能瓶颈，对症下药提升游戏流畅度。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>开发建议：</strong>从小项目开始，完成比完美更重要。多参考优秀游戏的设计，不断迭代优化。游戏开发需要团队协作，学会沟通和项目管理。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/19/267a024586df4aa18593c5099221e544.webp', 1, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:43:50', '2025-09-14 20:46:11', 0);
INSERT INTO `article` VALUES (51, 1, '数码相机,去中心化,图形渲染', '微前端架构：大型应用的模块化拆分方案', '深入探讨微前端架构的设计理念和实现方式。通过 qiankun、Module Federation 等方案，实现前端应用的独立开发和部署。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">移动应用开发实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、移动开发技术选型</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">原生开发性能最好，但开发成本高。跨平台方案（Flutter、React Native、uni-app）一套代码多端运行，开发效率高。混合开发（Ionic、Cordova）基于 Web 技术，适合简单应用。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Flutter 示例\nclass MyHomePage extends StatelessWidget {\n  @override\n  Widget build(BuildContext context) {\n    return Scaffold(\n      appBar: AppBar(title: Text(\'Home\')),\n      body: Center(\n        child: Text(\'Hello Flutter!\',\n          style: TextStyle(fontSize: 24),\n        ),\n      ),\n    );\n  }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、iOS 开发技术栈</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">语言：Swift（推荐）、Objective-C</li><li style=\"margin-bottom: 8px;\">UI 框架：SwiftUI、UIKit</li><li style=\"margin-bottom: 8px;\">数据持久化：Core Data、Realm</li><li style=\"margin-bottom: 8px;\">网络请求：URLSession、Alamofire</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、Android 开发技术栈</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kotlin 是 Android 官方推荐语言，简洁安全。Jetpack 组件库提供了 ViewModel、LiveData、Room 等工具。Material Design 设计规范帮助开发美观的界面。掌握 Activity、Fragment、Service 等核心组件。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能优化与发布</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">启动优化、内存优化、网络优化、电量优化都是提升用户体验的关键。App Store 和 Google Play 的审核规则需要仔细阅读。做好崩溃监控和数据统计，持续改进应用质量。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>开发建议：</strong>如果团队规模小、预算有限，跨平台方案是明智选择。如果追求极致性能和用户体验，原生开发更合适。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/2/d93fa7e492d542bf834cd08a553f9955.jpg', 0, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:43:53', '2025-09-14 20:46:04', 0);
INSERT INTO `article` VALUES (52, 1, '大数据,cinema4d,fastapi,mllib', '日志系统设计：从采集到分析的完整方案', '系统讲解日志系统的架构设计和技术选型。使用 Filebeat、Logstash、Elasticsearch、Kibana 构建完整的日志处理平台。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">移动应用开发实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、移动开发技术选型</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">原生开发性能最好，但开发成本高。跨平台方案（Flutter、React Native、uni-app）一套代码多端运行，开发效率高。混合开发（Ionic、Cordova）基于 Web 技术，适合简单应用。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Flutter 示例\nclass MyHomePage extends StatelessWidget {\n  @override\n  Widget build(BuildContext context) {\n    return Scaffold(\n      appBar: AppBar(title: Text(\'Home\')),\n      body: Center(\n        child: Text(\'Hello Flutter!\',\n          style: TextStyle(fontSize: 24),\n        ),\n      ),\n    );\n  }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、iOS 开发技术栈</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">语言：Swift（推荐）、Objective-C</li><li style=\"margin-bottom: 8px;\">UI 框架：SwiftUI、UIKit</li><li style=\"margin-bottom: 8px;\">数据持久化：Core Data、Realm</li><li style=\"margin-bottom: 8px;\">网络请求：URLSession、Alamofire</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、Android 开发技术栈</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kotlin 是 Android 官方推荐语言，简洁安全。Jetpack 组件库提供了 ViewModel、LiveData、Room 等工具。Material Design 设计规范帮助开发美观的界面。掌握 Activity、Fragment、Service 等核心组件。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能优化与发布</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">启动优化、内存优化、网络优化、电量优化都是提升用户体验的关键。App Store 和 Google Play 的审核规则需要仔细阅读。做好崩溃监控和数据统计，持续改进应用质量。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>开发建议：</strong>如果团队规模小、预算有限，跨平台方案是明智选择。如果追求极致性能和用户体验，原生开发更合适。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/16/fbf2a30cea1b4a2fb5c519420abb9374.webp', 0, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:43:57', '2025-09-14 20:46:04', 0);
INSERT INTO `article` VALUES (53, 1, 'ddos,maven,idea', 'PWA 渐进式 Web 应用：让网页拥有原生应用体验', '全面介绍 PWA 技术的核心特性和实现方法。通过 Service Worker、离线缓存、桌面安装等功能，提升 Web 应用的用户体验。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">移动应用开发实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、移动开发技术选型</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">原生开发性能最好，但开发成本高。跨平台方案（Flutter、React Native、uni-app）一套代码多端运行，开发效率高。混合开发（Ionic、Cordova）基于 Web 技术，适合简单应用。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Flutter 示例\nclass MyHomePage extends StatelessWidget {\n  @override\n  Widget build(BuildContext context) {\n    return Scaffold(\n      appBar: AppBar(title: Text(\'Home\')),\n      body: Center(\n        child: Text(\'Hello Flutter!\',\n          style: TextStyle(fontSize: 24),\n        ),\n      ),\n    );\n  }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、iOS 开发技术栈</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">语言：Swift（推荐）、Objective-C</li><li style=\"margin-bottom: 8px;\">UI 框架：SwiftUI、UIKit</li><li style=\"margin-bottom: 8px;\">数据持久化：Core Data、Realm</li><li style=\"margin-bottom: 8px;\">网络请求：URLSession、Alamofire</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、Android 开发技术栈</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kotlin 是 Android 官方推荐语言，简洁安全。Jetpack 组件库提供了 ViewModel、LiveData、Room 等工具。Material Design 设计规范帮助开发美观的界面。掌握 Activity、Fragment、Service 等核心组件。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能优化与发布</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">启动优化、内存优化、网络优化、电量优化都是提升用户体验的关键。App Store 和 Google Play 的审核规则需要仔细阅读。做好崩溃监控和数据统计，持续改进应用质量。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>开发建议：</strong>如果团队规模小、预算有限，跨平台方案是明智选择。如果追求极致性能和用户体验，原生开发更合适。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/25c2f8fb52524838bc25963e3345907b.webp', 1, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:02', '2025-09-14 20:46:04', 0);
INSERT INTO `article` VALUES (54, 1, 'storm,全文检索,grafana', '分布式链路追踪：SkyWalking 全链路监控实战', '深入讲解分布式链路追踪的原理和实践。使用 SkyWalking 实现请求链路跟踪、性能分析、故障定位，提升系统可观测性。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">移动应用开发实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、移动开发技术选型</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">原生开发性能最好，但开发成本高。跨平台方案（Flutter、React Native、uni-app）一套代码多端运行，开发效率高。混合开发（Ionic、Cordova）基于 Web 技术，适合简单应用。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Flutter 示例\nclass MyHomePage extends StatelessWidget {\n  @override\n  Widget build(BuildContext context) {\n    return Scaffold(\n      appBar: AppBar(title: Text(\'Home\')),\n      body: Center(\n        child: Text(\'Hello Flutter!\',\n          style: TextStyle(fontSize: 24),\n        ),\n      ),\n    );\n  }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、iOS 开发技术栈</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">语言：Swift（推荐）、Objective-C</li><li style=\"margin-bottom: 8px;\">UI 框架：SwiftUI、UIKit</li><li style=\"margin-bottom: 8px;\">数据持久化：Core Data、Realm</li><li style=\"margin-bottom: 8px;\">网络请求：URLSession、Alamofire</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、Android 开发技术栈</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kotlin 是 Android 官方推荐语言，简洁安全。Jetpack 组件库提供了 ViewModel、LiveData、Room 等工具。Material Design 设计规范帮助开发美观的界面。掌握 Activity、Fragment、Service 等核心组件。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能优化与发布</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">启动优化、内存优化、网络优化、电量优化都是提升用户体验的关键。App Store 和 Google Play 的审核规则需要仔细阅读。做好崩溃监控和数据统计，持续改进应用质量。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>开发建议：</strong>如果团队规模小、预算有限，跨平台方案是明智选择。如果追求极致性能和用户体验，原生开发更合适。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/10daaee589334204863eebcd3213c3a6.webp', 0, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:06', '2025-09-14 20:46:04', 0);
INSERT INTO `article` VALUES (55, 1, '人机交互,ruby,剪枝', 'CSS 架构设计：BEM、CSS Modules、CSS-in-JS 方案', '系统对比 CSS 组织方案的优劣。从命名规范到模块化，从预处理器到 CSS-in-JS，选择最适合团队的 CSS 架构。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">移动应用开发实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、移动开发技术选型</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">原生开发性能最好，但开发成本高。跨平台方案（Flutter、React Native、uni-app）一套代码多端运行，开发效率高。混合开发（Ionic、Cordova）基于 Web 技术，适合简单应用。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Flutter 示例\nclass MyHomePage extends StatelessWidget {\n  @override\n  Widget build(BuildContext context) {\n    return Scaffold(\n      appBar: AppBar(title: Text(\'Home\')),\n      body: Center(\n        child: Text(\'Hello Flutter!\',\n          style: TextStyle(fontSize: 24),\n        ),\n      ),\n    );\n  }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、iOS 开发技术栈</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">语言：Swift（推荐）、Objective-C</li><li style=\"margin-bottom: 8px;\">UI 框架：SwiftUI、UIKit</li><li style=\"margin-bottom: 8px;\">数据持久化：Core Data、Realm</li><li style=\"margin-bottom: 8px;\">网络请求：URLSession、Alamofire</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、Android 开发技术栈</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Kotlin 是 Android 官方推荐语言，简洁安全。Jetpack 组件库提供了 ViewModel、LiveData、Room 等工具。Material Design 设计规范帮助开发美观的界面。掌握 Activity、Fragment、Service 等核心组件。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、性能优化与发布</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">启动优化、内存优化、网络优化、电量优化都是提升用户体验的关键。App Store 和 Google Play 的审核规则需要仔细阅读。做好崩溃监控和数据统计，持续改进应用质量。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>开发建议：</strong>如果团队规模小、预算有限，跨平台方案是明智选择。如果追求极致性能和用户体验，原生开发更合适。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/94b9019155c74ba396dbbdc3d5afc68e.webp', 1, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:32', '2025-09-14 20:46:04', 0);
INSERT INTO `article` VALUES (56, 1, 'skywalking,ux,tornado,策略模式', '配置中心实战：Nacos 与 Apollo 配置管理对比', '详细对比 Nacos 和 Apollo 两大配置中心方案。实现配置的集中管理、动态更新、灰度发布，提升系统的灵活性。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">物联网开发实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、物联网技术架构</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">物联网系统包括感知层（传感器、执行器）、网络层（WiFi、蓝牙、LoRa、NB-IoT）、平台层（云平台、边缘计算）、应用层（手机App、Web 控制台）。理解各层职责是设计 IoT 系统的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Arduino 温湿度传感器示例\n#include <DHT.h>\n\nDHT dht(2, DHT11);  // 数据引脚连接到 D2\n\nvoid setup() {\n  Serial.begin(9600);\n  dht.begin();\n}\n\nvoid loop() {\n  float temp = dht.readTemperature();\n  float humidity = dht.readHumidity();\n  Serial.print(\"Temp: \"); Serial.println(temp);\n  delay(2000);\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常用开发平台</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Arduino：</strong>入门首选，生态丰富</li><li style=\"margin-bottom: 8px;\"><strong>树莓派：</strong>性能强大，运行 Linux</li><li style=\"margin-bottom: 8px;\"><strong>ESP32：</strong>WiFi+蓝牙，性价比高</li><li style=\"margin-bottom: 8px;\"><strong>STM32：</strong>工业级应用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、通信协议与云平台</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">MQTT 是物联网常用的轻量级消息协议。阿里云 IoT、腾讯云 IoT、AWS IoT 提供设备接入、数据存储、规则引擎等服务。掌握设备认证、数据上报、远程控制等功能的实现。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、智能家居项目实战</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">构建智能灯光、智能门锁、环境监测等系统。集成语音助手（小爱同学、天猫精灵）实现语音控制。通过手机 App 远程查看和控制设备状态，打造智能生活体验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>安全提示：</strong>物联网设备安全不容忽视。设备认证、数据加密、安全更新都很重要。避免使用默认密码，定期更新固件。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/f815a6ac779e4a45a1a9a72b375c59c8.jpg', 0, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:35', '2025-09-14 20:46:04', 0);
INSERT INTO `article` VALUES (57, 1, 'vc-1,策略模式,数据库架构', '异步编程模式：Promise、async/await 深度解析', '深入讲解 JavaScript 异步编程的演进历程。从回调函数到 Promise，再到 async/await，掌握现代异步编程的最佳实践。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">物联网开发实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、物联网技术架构</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">物联网系统包括感知层（传感器、执行器）、网络层（WiFi、蓝牙、LoRa、NB-IoT）、平台层（云平台、边缘计算）、应用层（手机App、Web 控制台）。理解各层职责是设计 IoT 系统的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Arduino 温湿度传感器示例\n#include <DHT.h>\n\nDHT dht(2, DHT11);  // 数据引脚连接到 D2\n\nvoid setup() {\n  Serial.begin(9600);\n  dht.begin();\n}\n\nvoid loop() {\n  float temp = dht.readTemperature();\n  float humidity = dht.readHumidity();\n  Serial.print(\"Temp: \"); Serial.println(temp);\n  delay(2000);\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常用开发平台</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Arduino：</strong>入门首选，生态丰富</li><li style=\"margin-bottom: 8px;\"><strong>树莓派：</strong>性能强大，运行 Linux</li><li style=\"margin-bottom: 8px;\"><strong>ESP32：</strong>WiFi+蓝牙，性价比高</li><li style=\"margin-bottom: 8px;\"><strong>STM32：</strong>工业级应用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、通信协议与云平台</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">MQTT 是物联网常用的轻量级消息协议。阿里云 IoT、腾讯云 IoT、AWS IoT 提供设备接入、数据存储、规则引擎等服务。掌握设备认证、数据上报、远程控制等功能的实现。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、智能家居项目实战</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">构建智能灯光、智能门锁、环境监测等系统。集成语音助手（小爱同学、天猫精灵）实现语音控制。通过手机 App 远程查看和控制设备状态，打造智能生活体验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>安全提示：</strong>物联网设备安全不容忽视。设备认证、数据加密、安全更新都很重要。避免使用默认密码，定期更新固件。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/92b887c0b0974d05b2e733347f0355a1.jpg', 2, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:38', '2025-09-14 20:46:04', 0);
INSERT INTO `article` VALUES (58, 1, '基带工程,c4前端,actionscript', '数据仓库建模：维度建模与事实表设计实战', '系统讲解数据仓库的建模方法和最佳实践。通过星型模型、雪花模型等设计方案，构建高效的数据分析平台。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">物联网开发实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、物联网技术架构</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">物联网系统包括感知层（传感器、执行器）、网络层（WiFi、蓝牙、LoRa、NB-IoT）、平台层（云平台、边缘计算）、应用层（手机App、Web 控制台）。理解各层职责是设计 IoT 系统的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Arduino 温湿度传感器示例\n#include <DHT.h>\n\nDHT dht(2, DHT11);  // 数据引脚连接到 D2\n\nvoid setup() {\n  Serial.begin(9600);\n  dht.begin();\n}\n\nvoid loop() {\n  float temp = dht.readTemperature();\n  float humidity = dht.readHumidity();\n  Serial.print(\"Temp: \"); Serial.println(temp);\n  delay(2000);\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常用开发平台</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Arduino：</strong>入门首选，生态丰富</li><li style=\"margin-bottom: 8px;\"><strong>树莓派：</strong>性能强大，运行 Linux</li><li style=\"margin-bottom: 8px;\"><strong>ESP32：</strong>WiFi+蓝牙，性价比高</li><li style=\"margin-bottom: 8px;\"><strong>STM32：</strong>工业级应用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、通信协议与云平台</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">MQTT 是物联网常用的轻量级消息协议。阿里云 IoT、腾讯云 IoT、AWS IoT 提供设备接入、数据存储、规则引擎等服务。掌握设备认证、数据上报、远程控制等功能的实现。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、智能家居项目实战</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">构建智能灯光、智能门锁、环境监测等系统。集成语音助手（小爱同学、天猫精灵）实现语音控制。通过手机 App 远程查看和控制设备状态，打造智能生活体验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>安全提示：</strong>物联网设备安全不容忽视。设备认证、数据加密、安全更新都很重要。避免使用默认密码，定期更新固件。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/10/6df7d4da9241446a8471c4ddaee6860d.webp', 0, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:41', '2025-09-14 20:46:04', 0);
INSERT INTO `article` VALUES (59, 1, '几何学,ansible,flume,敏捷流程', '限流降级实战：Sentinel 流量控制与熔断保护', '深入讲解 Sentinel 流量控制框架的原理和使用。实现限流、降级、熔断等保护机制，保障系统在高并发下的稳定性。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">物联网开发实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、物联网技术架构</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">物联网系统包括感知层（传感器、执行器）、网络层（WiFi、蓝牙、LoRa、NB-IoT）、平台层（云平台、边缘计算）、应用层（手机App、Web 控制台）。理解各层职责是设计 IoT 系统的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Arduino 温湿度传感器示例\n#include <DHT.h>\n\nDHT dht(2, DHT11);  // 数据引脚连接到 D2\n\nvoid setup() {\n  Serial.begin(9600);\n  dht.begin();\n}\n\nvoid loop() {\n  float temp = dht.readTemperature();\n  float humidity = dht.readHumidity();\n  Serial.print(\"Temp: \"); Serial.println(temp);\n  delay(2000);\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常用开发平台</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Arduino：</strong>入门首选，生态丰富</li><li style=\"margin-bottom: 8px;\"><strong>树莓派：</strong>性能强大，运行 Linux</li><li style=\"margin-bottom: 8px;\"><strong>ESP32：</strong>WiFi+蓝牙，性价比高</li><li style=\"margin-bottom: 8px;\"><strong>STM32：</strong>工业级应用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、通信协议与云平台</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">MQTT 是物联网常用的轻量级消息协议。阿里云 IoT、腾讯云 IoT、AWS IoT 提供设备接入、数据存储、规则引擎等服务。掌握设备认证、数据上报、远程控制等功能的实现。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、智能家居项目实战</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">构建智能灯光、智能门锁、环境监测等系统。集成语音助手（小爱同学、天猫精灵）实现语音控制。通过手机 App 远程查看和控制设备状态，打造智能生活体验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>安全提示：</strong>物联网设备安全不容忽视。设备认证、数据加密、安全更新都很重要。避免使用默认密码，定期更新固件。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/8/253c190cf34e493096297e258fc25c42.webp', 0, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:44', '2025-09-14 20:46:04', 0);
INSERT INTO `article` VALUES (60, 1, '算法,sphinx,caffe', '前端性能监控：从数据采集到性能分析', '全面讲解前端性能监控的技术和工具。采集 FCP、LCP、CLS 等关键指标，分析性能瓶颈，持续优化用户体验。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">物联网开发实战</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、物联网技术架构</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">物联网系统包括感知层（传感器、执行器）、网络层（WiFi、蓝牙、LoRa、NB-IoT）、平台层（云平台、边缘计算）、应用层（手机App、Web 控制台）。理解各层职责是设计 IoT 系统的基础。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// Arduino 温湿度传感器示例\n#include <DHT.h>\n\nDHT dht(2, DHT11);  // 数据引脚连接到 D2\n\nvoid setup() {\n  Serial.begin(9600);\n  dht.begin();\n}\n\nvoid loop() {\n  float temp = dht.readTemperature();\n  float humidity = dht.readHumidity();\n  Serial.print(\"Temp: \"); Serial.println(temp);\n  delay(2000);\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、常用开发平台</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>Arduino：</strong>入门首选，生态丰富</li><li style=\"margin-bottom: 8px;\"><strong>树莓派：</strong>性能强大，运行 Linux</li><li style=\"margin-bottom: 8px;\"><strong>ESP32：</strong>WiFi+蓝牙，性价比高</li><li style=\"margin-bottom: 8px;\"><strong>STM32：</strong>工业级应用</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、通信协议与云平台</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">MQTT 是物联网常用的轻量级消息协议。阿里云 IoT、腾讯云 IoT、AWS IoT 提供设备接入、数据存储、规则引擎等服务。掌握设备认证、数据上报、远程控制等功能的实现。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、智能家居项目实战</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">构建智能灯光、智能门锁、环境监测等系统。集成语音助手（小爱同学、天猫精灵）实现语音控制。通过手机 App 远程查看和控制设备状态，打造智能生活体验。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>安全提示：</strong>物联网设备安全不容忽视。设备认证、数据加密、安全更新都很重要。避免使用默认密码，定期更新固件。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/4/4bed147d7d1e48e2a5cffe66e2c31a24.webp', 1, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:47', '2025-09-14 20:46:04', 0);
INSERT INTO `article` VALUES (61, 1, 'fiddler,uni-app,gnu', '数据库连接池原理：HikariCP 性能优化解析', '深度剖析数据库连接池的工作原理。以 HikariCP 为例，讲解连接池的配置优化、性能调优，提升数据库访问效率。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">DevOps 实践与持续交付</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、DevOps 文化与理念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">DevOps 打破开发和运维的壁垒，强调自动化、持续集成、持续交付、快速反馈。它不仅是工具和流程的改变，更是文化和思维方式的转变。目标是缩短交付周期，提高软件质量和稳定性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># GitLab CI 配置示例\nstages:\n  - build\n  - test\n  - deploy\n\nbuild:\n  stage: build\n  script:\n    - npm install\n    - npm run build\n\ndeploy:\n  stage: deploy\n  script:\n    - kubectl apply -f k8s/deployment.yaml</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、CI/CD 流水线</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">代码提交触发自动构建</li><li style=\"margin-bottom: 8px;\">运行单元测试和集成测试</li><li style=\"margin-bottom: 8px;\">构建 Docker 镜像</li><li style=\"margin-bottom: 8px;\">自动部署到测试/生产环境</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、监控与日志</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Prometheus + Grafana 监控系统指标，ELK（Elasticsearch + Logstash + Kibana）集中管理日志。配置告警规则，及时发现和解决问题。Trace 系统追踪请求链路，定位性能瓶颈。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">基础设施即代码（IaC）、不可变基础设施、蓝绿部署、金丝雀发布、A/B 测试等策略降低发布风险。建立故障恢复机制，定期进行演练，提升系统韧性。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>团队建设：</strong>DevOps 转型需要团队全员参与，培养全栈思维。鼓励分享和协作，建立学习型组织。工具只是辅助，人才是核心。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/d1d46de1f84a48809bd4e76dd32b51d2.webp', 0, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:53', '2025-09-14 20:45:55', 0);
INSERT INTO `article` VALUES (62, 1, '游戏程序,paddlepaddle,segmentfault', 'Git 工作流：GitFlow、GitHub Flow、GitLab Flow 对比', '系统对比三种主流 Git 工作流的特点和适用场景。根据团队规模和项目特点，选择最合适的分支管理策略。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">DevOps 实践与持续交付</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、DevOps 文化与理念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">DevOps 打破开发和运维的壁垒，强调自动化、持续集成、持续交付、快速反馈。它不仅是工具和流程的改变，更是文化和思维方式的转变。目标是缩短交付周期，提高软件质量和稳定性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># GitLab CI 配置示例\nstages:\n  - build\n  - test\n  - deploy\n\nbuild:\n  stage: build\n  script:\n    - npm install\n    - npm run build\n\ndeploy:\n  stage: deploy\n  script:\n    - kubectl apply -f k8s/deployment.yaml</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、CI/CD 流水线</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">代码提交触发自动构建</li><li style=\"margin-bottom: 8px;\">运行单元测试和集成测试</li><li style=\"margin-bottom: 8px;\">构建 Docker 镜像</li><li style=\"margin-bottom: 8px;\">自动部署到测试/生产环境</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、监控与日志</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Prometheus + Grafana 监控系统指标，ELK（Elasticsearch + Logstash + Kibana）集中管理日志。配置告警规则，及时发现和解决问题。Trace 系统追踪请求链路，定位性能瓶颈。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">基础设施即代码（IaC）、不可变基础设施、蓝绿部署、金丝雀发布、A/B 测试等策略降低发布风险。建立故障恢复机制，定期进行演练，提升系统韧性。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>团队建设：</strong>DevOps 转型需要团队全员参与，培养全栈思维。鼓励分享和协作，建立学习型组织。工具只是辅助，人才是核心。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/6/61d91658e974434186b382cd10955d82.webp', 0, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:55', '2025-09-14 20:45:55', 0);
INSERT INTO `article` VALUES (63, 1, 'mfc,拓扑学,帅哥,activemq', '跨域问题解决方案：CORS、JSONP、代理的全面对比', '详细讲解浏览器跨域问题的产生原因和解决方案。从 CORS 配置到 JSONP 原理，掌握各种跨域处理技巧。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">DevOps 实践与持续交付</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、DevOps 文化与理念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">DevOps 打破开发和运维的壁垒，强调自动化、持续集成、持续交付、快速反馈。它不仅是工具和流程的改变，更是文化和思维方式的转变。目标是缩短交付周期，提高软件质量和稳定性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># GitLab CI 配置示例\nstages:\n  - build\n  - test\n  - deploy\n\nbuild:\n  stage: build\n  script:\n    - npm install\n    - npm run build\n\ndeploy:\n  stage: deploy\n  script:\n    - kubectl apply -f k8s/deployment.yaml</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、CI/CD 流水线</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">代码提交触发自动构建</li><li style=\"margin-bottom: 8px;\">运行单元测试和集成测试</li><li style=\"margin-bottom: 8px;\">构建 Docker 镜像</li><li style=\"margin-bottom: 8px;\">自动部署到测试/生产环境</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、监控与日志</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Prometheus + Grafana 监控系统指标，ELK（Elasticsearch + Logstash + Kibana）集中管理日志。配置告警规则，及时发现和解决问题。Trace 系统追踪请求链路，定位性能瓶颈。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">基础设施即代码（IaC）、不可变基础设施、蓝绿部署、金丝雀发布、A/B 测试等策略降低发布风险。建立故障恢复机制，定期进行演练，提升系统韧性。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>团队建设：</strong>DevOps 转型需要团队全员参与，培养全栈思维。鼓励分享和协作，建立学习型组织。工具只是辅助，人才是核心。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/7/651c690cbca74ecda4b794b2d8d21f81.webp', 4, 1, 8, 0, 1, 0, 0, 0, '', '2025-09-14 20:44:58', '2025-09-20 11:23:53', 0);
INSERT INTO `article` VALUES (64, 1, 'taro,wwdc,sketch', '文件存储方案：OSS、MinIO、FastDFS 对象存储对比', '全面对比主流文件存储方案的特点。从公有云 OSS 到私有化部署的 MinIO、FastDFS，选择合适的文件存储架构。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">DevOps 实践与持续交付</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、DevOps 文化与理念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">DevOps 打破开发和运维的壁垒，强调自动化、持续集成、持续交付、快速反馈。它不仅是工具和流程的改变，更是文化和思维方式的转变。目标是缩短交付周期，提高软件质量和稳定性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># GitLab CI 配置示例\nstages:\n  - build\n  - test\n  - deploy\n\nbuild:\n  stage: build\n  script:\n    - npm install\n    - npm run build\n\ndeploy:\n  stage: deploy\n  script:\n    - kubectl apply -f k8s/deployment.yaml</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、CI/CD 流水线</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">代码提交触发自动构建</li><li style=\"margin-bottom: 8px;\">运行单元测试和集成测试</li><li style=\"margin-bottom: 8px;\">构建 Docker 镜像</li><li style=\"margin-bottom: 8px;\">自动部署到测试/生产环境</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、监控与日志</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Prometheus + Grafana 监控系统指标，ELK（Elasticsearch + Logstash + Kibana）集中管理日志。配置告警规则，及时发现和解决问题。Trace 系统追踪请求链路，定位性能瓶颈。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">基础设施即代码（IaC）、不可变基础设施、蓝绿部署、金丝雀发布、A/B 测试等策略降低发布风险。建立故障恢复机制，定期进行演练，提升系统韧性。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>团队建设：</strong>DevOps 转型需要团队全员参与，培养全栈思维。鼓励分享和协作，建立学习型组织。工具只是辅助，人才是核心。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/2/14/8bde0bb1ec584c778bbf9bc230a0ceda.webp', 1, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:45:01', '2025-09-14 20:45:55', 0);
INSERT INTO `article` VALUES (65, 1, '系统安全,数学建模,数码相机', '敏捷开发实践：Scrum 与看板方法论深度解析', '系统讲解敏捷开发的核心理念和实践方法。通过 Scrum 框架和看板方法，提升团队协作效率和软件交付质量。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">DevOps 实践与持续交付</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、DevOps 文化与理念</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">DevOps 打破开发和运维的壁垒，强调自动化、持续集成、持续交付、快速反馈。它不仅是工具和流程的改变，更是文化和思维方式的转变。目标是缩短交付周期，提高软件质量和稳定性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code># GitLab CI 配置示例\nstages:\n  - build\n  - test\n  - deploy\n\nbuild:\n  stage: build\n  script:\n    - npm install\n    - npm run build\n\ndeploy:\n  stage: deploy\n  script:\n    - kubectl apply -f k8s/deployment.yaml</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、CI/CD 流水线</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\">代码提交触发自动构建</li><li style=\"margin-bottom: 8px;\">运行单元测试和集成测试</li><li style=\"margin-bottom: 8px;\">构建 Docker 镜像</li><li style=\"margin-bottom: 8px;\">自动部署到测试/生产环境</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、监控与日志</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">Prometheus + Grafana 监控系统指标，ELK（Elasticsearch + Logstash + Kibana）集中管理日志。配置告警规则，及时发现和解决问题。Trace 系统追踪请求链路，定位性能瓶颈。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、最佳实践</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">基础设施即代码（IaC）、不可变基础设施、蓝绿部署、金丝雀发布、A/B 测试等策略降低发布风险。建立故障恢复机制，定期进行演练，提升系统韧性。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>团队建设：</strong>DevOps 转型需要团队全员参与，培养全栈思维。鼓励分享和协作，建立学习型组织。工具只是辅助，人才是核心。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/16/41af469a92b94636a822e049b5bb4913.webp', 0, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:45:04', '2025-09-14 20:45:55', 0);
INSERT INTO `article` VALUES (66, 1, 'nosql,edge,迪米特法则,gpt', '全文检索引擎：从 Lucene 到 Elasticsearch 的演进', '深入讲解全文检索的核心技术。从 Lucene 的倒排索引原理到 Elasticsearch 的分布式架构，构建高性能搜索系统。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">软件测试与自动化</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、测试基础理论</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">软件测试是质量保证的重要手段。测试金字塔告诉我们：单元测试最多、集成测试其次、UI 测试最少。左移测试在开发早期就介入，右移测试在生产环境持续监控。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// JUnit 单元测试示例\n@Test\npublic void testAddUser() {\n    User user = new User(\"张三\", 25);\n    userService.addUser(user);\n    \n    User saved = userService.getUserById(user.getId());\n    assertEquals(\"张三\", saved.getName());\n    assertEquals(25, saved.getAge());\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、自动化测试实践</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>单元测试：</strong>JUnit、TestNG、Jest</li><li style=\"margin-bottom: 8px;\"><strong>接口测试：</strong>Postman、RestAssured</li><li style=\"margin-bottom: 8px;\"><strong>UI 测试：</strong>Selenium、Cypress、Playwright</li><li style=\"margin-bottom: 8px;\"><strong>性能测试：</strong>JMeter、Gatling、LoadRunner</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、测试策略与流程</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">制定测试计划，设计测试用例，执行测试，缺陷跟踪，回归测试。使用 JIRA、TestRail 等工具管理测试过程。测试覆盖率是重要指标，但不是唯一目标，要关注测试的有效性。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、持续测试与质量保障</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">将测试集成到 CI/CD 流水线，每次代码提交都运行测试。建立质量门禁，测试不通过不允许合并代码。监控生产环境，及时发现和修复问题。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>质量意识：</strong>质量是团队的责任，不只是测试人员的工作。开发人员要重视测试，编写可测试的代码。全员参与才能真正提升软件质量。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/15/20987aa177e443d3b53aee3d9fc0d55d.webp', 1, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:45:06', '2025-09-14 20:45:55', 0);
INSERT INTO `article` VALUES (67, 1, '驱动开发,tdd,tcpdump', '前端测试体系：单元测试、集成测试、E2E 测试实战', '全面介绍前端测试的完整体系。使用 Jest、React Testing Library、Cypress 等工具，构建可靠的测试流程。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">系统架构设计与高并发</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、架构设计原则</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">高并发系统设计遵循几个核心原则：无状态化、服务化拆分、异步化处理、缓存优先、数据库优化。CAP 理论和 BASE 理论指导分布式系统设计，权衡一致性和可用性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 限流算法示例 - 令牌桶\npublic class TokenBucket {\n    private long tokens;\n    private long capacity;\n    private long rate;\n    private long lastTime;\n    \n    public synchronized boolean tryAcquire() {\n        long now = System.currentTimeMillis();\n        tokens = Math.min(capacity, tokens + (now - lastTime) * rate / 1000);\n        lastTime = now;\n        \n        if (tokens > 0) {\n            tokens--;\n            return true;\n        }\n        return false;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、高并发解决方案</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>缓存：</strong>多级缓存体系（浏览器、CDN、Redis）</li><li style=\"margin-bottom: 8px;\"><strong>限流：</strong>令牌桶、漏桶、计数器</li><li style=\"margin-bottom: 8px;\"><strong>降级：</strong>服务降级、熔断保护</li><li style=\"margin-bottom: 8px;\"><strong>削峰：</strong>消息队列异步处理</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据库优化策略</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">读写分离分担数据库压力，分库分表解决单表数据量过大问题。使用连接池复用连接，慢查询优化提升性能。NoSQL 数据库适合特定场景，选择合适的数据存储方案。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、架构演进之路</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">单体应用 → 垂直拆分 → SOA → 微服务 → 服务网格，架构随业务发展不断演进。每种架构都有其适用场景，不要盲目追求新技术，根据实际情况选择。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>架构之道：</strong>没有完美的架构，只有合适的架构。架构设计要考虑技术、成本、团队能力等多方面因素。持续演进，拥抱变化。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/3dfb410027984c7eb61ba8e94e49290c.jpg', 1, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:45:09', '2025-09-14 20:45:55', 0);
INSERT INTO `article` VALUES (68, 1, 'arm开发,mysql,scala', '服务熔断与降级：Hystrix 容错机制深度剖析', '深入解析 Hystrix 熔断降级框架的工作原理。实现服务隔离、熔断保护、降级策略，保障微服务系统的高可用。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">系统架构设计与高并发</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、架构设计原则</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">高并发系统设计遵循几个核心原则：无状态化、服务化拆分、异步化处理、缓存优先、数据库优化。CAP 理论和 BASE 理论指导分布式系统设计，权衡一致性和可用性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 限流算法示例 - 令牌桶\npublic class TokenBucket {\n    private long tokens;\n    private long capacity;\n    private long rate;\n    private long lastTime;\n    \n    public synchronized boolean tryAcquire() {\n        long now = System.currentTimeMillis();\n        tokens = Math.min(capacity, tokens + (now - lastTime) * rate / 1000);\n        lastTime = now;\n        \n        if (tokens > 0) {\n            tokens--;\n            return true;\n        }\n        return false;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、高并发解决方案</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>缓存：</strong>多级缓存体系（浏览器、CDN、Redis）</li><li style=\"margin-bottom: 8px;\"><strong>限流：</strong>令牌桶、漏桶、计数器</li><li style=\"margin-bottom: 8px;\"><strong>降级：</strong>服务降级、熔断保护</li><li style=\"margin-bottom: 8px;\"><strong>削峰：</strong>消息队列异步处理</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据库优化策略</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">读写分离分担数据库压力，分库分表解决单表数据量过大问题。使用连接池复用连接，慢查询优化提升性能。NoSQL 数据库适合特定场景，选择合适的数据存储方案。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、架构演进之路</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">单体应用 → 垂直拆分 → SOA → 微服务 → 服务网格，架构随业务发展不断演进。每种架构都有其适用场景，不要盲目追求新技术，根据实际情况选择。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>架构之道：</strong>没有完美的架构，只有合适的架构。架构设计要考虑技术、成本、团队能力等多方面因素。持续演进，拥抱变化。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/3c2bfc0c40f4428489c3df9cdabd1a3b.webp', 1, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:45:12', '2025-09-14 20:45:55', 0);
INSERT INTO `article` VALUES (69, 1, 'g726,mlnet,udp', '代码重构实战：识别坏味道并进行系统性改进', '系统讲解代码重构的方法和技巧。识别常见的代码坏味道，通过小步重构持续改进代码质量，保持系统的可维护性。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">系统架构设计与高并发</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、架构设计原则</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">高并发系统设计遵循几个核心原则：无状态化、服务化拆分、异步化处理、缓存优先、数据库优化。CAP 理论和 BASE 理论指导分布式系统设计，权衡一致性和可用性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 限流算法示例 - 令牌桶\npublic class TokenBucket {\n    private long tokens;\n    private long capacity;\n    private long rate;\n    private long lastTime;\n    \n    public synchronized boolean tryAcquire() {\n        long now = System.currentTimeMillis();\n        tokens = Math.min(capacity, tokens + (now - lastTime) * rate / 1000);\n        lastTime = now;\n        \n        if (tokens > 0) {\n            tokens--;\n            return true;\n        }\n        return false;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、高并发解决方案</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>缓存：</strong>多级缓存体系（浏览器、CDN、Redis）</li><li style=\"margin-bottom: 8px;\"><strong>限流：</strong>令牌桶、漏桶、计数器</li><li style=\"margin-bottom: 8px;\"><strong>降级：</strong>服务降级、熔断保护</li><li style=\"margin-bottom: 8px;\"><strong>削峰：</strong>消息队列异步处理</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据库优化策略</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">读写分离分担数据库压力，分库分表解决单表数据量过大问题。使用连接池复用连接，慢查询优化提升性能。NoSQL 数据库适合特定场景，选择合适的数据存储方案。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、架构演进之路</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">单体应用 → 垂直拆分 → SOA → 微服务 → 服务网格，架构随业务发展不断演进。每种架构都有其适用场景，不要盲目追求新技术，根据实际情况选择。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>架构之道：</strong>没有完美的架构，只有合适的架构。架构设计要考虑技术、成本、团队能力等多方面因素。持续演进，拥抱变化。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/2/add4b5d841924e8499b1230010ffeea9.jpg', 2, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:45:14', '2025-09-14 20:45:55', 0);
INSERT INTO `article` VALUES (70, 1, 'gitlab,intellij-idea,onedrive,vagrant', '云原生应用 12 要素：构建现代化应用的方法论', '详细解读云原生应用的 12 要素方法论。从代码库、依赖管理到日志处理、进程管理，构建可移植、可扩展的应用。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">系统架构设计与高并发</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、架构设计原则</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">高并发系统设计遵循几个核心原则：无状态化、服务化拆分、异步化处理、缓存优先、数据库优化。CAP 理论和 BASE 理论指导分布式系统设计，权衡一致性和可用性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 限流算法示例 - 令牌桶\npublic class TokenBucket {\n    private long tokens;\n    private long capacity;\n    private long rate;\n    private long lastTime;\n    \n    public synchronized boolean tryAcquire() {\n        long now = System.currentTimeMillis();\n        tokens = Math.min(capacity, tokens + (now - lastTime) * rate / 1000);\n        lastTime = now;\n        \n        if (tokens > 0) {\n            tokens--;\n            return true;\n        }\n        return false;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、高并发解决方案</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>缓存：</strong>多级缓存体系（浏览器、CDN、Redis）</li><li style=\"margin-bottom: 8px;\"><strong>限流：</strong>令牌桶、漏桶、计数器</li><li style=\"margin-bottom: 8px;\"><strong>降级：</strong>服务降级、熔断保护</li><li style=\"margin-bottom: 8px;\"><strong>削峰：</strong>消息队列异步处理</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据库优化策略</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">读写分离分担数据库压力，分库分表解决单表数据量过大问题。使用连接池复用连接，慢查询优化提升性能。NoSQL 数据库适合特定场景，选择合适的数据存储方案。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、架构演进之路</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">单体应用 → 垂直拆分 → SOA → 微服务 → 服务网格，架构随业务发展不断演进。每种架构都有其适用场景，不要盲目追求新技术，根据实际情况选择。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>架构之道：</strong>没有完美的架构，只有合适的架构。架构设计要考虑技术、成本、团队能力等多方面因素。持续演进，拥抱变化。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/album/1/1/d888b05f9a4345e7b49c59d569e851d3.webp', 11, 0, 0, 0, 1, 0, 0, 0, '', '2025-09-14 20:45:18', '2025-09-14 20:45:55', 0);
INSERT INTO `article` VALUES (71, 1, 'symfony,计算机外设,sdkman', '技术债务管理：平衡快速交付与长期维护', '探讨技术债务的产生原因和管理策略。通过合理规划重构时间，在快速交付和代码质量之间找到平衡点。', '<h1 style=\"line-height: 2.5rem; margin-bottom: 20px; color: #303133;\">系统架构设计与高并发</h1><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">一、架构设计原则</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">高并发系统设计遵循几个核心原则：无状态化、服务化拆分、异步化处理、缓存优先、数据库优化。CAP 理论和 BASE 理论指导分布式系统设计，权衡一致性和可用性。</p><pre style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; margin-bottom: 15px;\"><code>// 限流算法示例 - 令牌桶\npublic class TokenBucket {\n    private long tokens;\n    private long capacity;\n    private long rate;\n    private long lastTime;\n    \n    public synchronized boolean tryAcquire() {\n        long now = System.currentTimeMillis();\n        tokens = Math.min(capacity, tokens + (now - lastTime) * rate / 1000);\n        lastTime = now;\n        \n        if (tokens > 0) {\n            tokens--;\n            return true;\n        }\n        return false;\n    }\n}</code></pre><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">二、高并发解决方案</h2><ul style=\"line-height: 1.8; margin-bottom: 15px; padding-left: 2em;\"><li style=\"margin-bottom: 8px;\"><strong>缓存：</strong>多级缓存体系（浏览器、CDN、Redis）</li><li style=\"margin-bottom: 8px;\"><strong>限流：</strong>令牌桶、漏桶、计数器</li><li style=\"margin-bottom: 8px;\"><strong>降级：</strong>服务降级、熔断保护</li><li style=\"margin-bottom: 8px;\"><strong>削峰：</strong>消息队列异步处理</li></ul><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">三、数据库优化策略</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">读写分离分担数据库压力，分库分表解决单表数据量过大问题。使用连接池复用连接，慢查询优化提升性能。NoSQL 数据库适合特定场景，选择合适的数据存储方案。</p><h2 style=\"line-height: 2rem; margin: 20px 0 15px; color: #409eff;\">四、架构演进之路</h2><p style=\"line-height: 1.8; text-indent: 2em; margin-bottom: 15px;\">单体应用 → 垂直拆分 → SOA → 微服务 → 服务网格，架构随业务发展不断演进。每种架构都有其适用场景，不要盲目追求新技术，根据实际情况选择。</p><blockquote style=\"border-left: 4px solid #409eff; padding-left: 15px; margin: 20px 0; color: #606266;\"><p style=\"line-height: 1.8; margin: 0;\"><strong>架构之道：</strong>没有完美的架构，只有合适的架构。架构设计要考虑技术、成本、团队能力等多方面因素。持续演进，拥抱变化。</p></blockquote>', 'https://image.sidifensen.com/sidifensen-blog/article/1/7cb89614b735483b9644fa307e9ccbc9.jpg', 27, 0, 2, 0, 1, 0, 0, 0, '', '2025-09-17 08:34:39', '2025-09-17 08:40:22', 0);

-- ----------------------------
-- Table structure for article_column
-- ----------------------------
DROP TABLE IF EXISTS `article_column`;
CREATE TABLE `article_column`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `sort` int NOT NULL COMMENT '排序',
  `article_id` int NOT NULL COMMENT '文章id',
  `column_id` int NOT NULL COMMENT '专栏id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_article_id`(`article_id` ASC) USING BTREE,
  INDEX `idx_column_id`(`column_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article_column
-- ----------------------------
INSERT INTO `article_column` VALUES (1, 1, 1, 1, '2025-08-29 00:15:48', '2025-08-29 00:15:51', 0);
INSERT INTO `article_column` VALUES (2, 1, 1, 2, '2025-08-29 00:15:48', '2025-08-29 00:15:51', 0);
INSERT INTO `article_column` VALUES (3, 0, 1, 3, '2025-08-30 15:30:21', '2025-08-30 15:30:23', 0);
INSERT INTO `article_column` VALUES (4, 2, 2, 1, '2025-08-30 15:30:21', '2025-08-30 15:30:23', 0);
INSERT INTO `article_column` VALUES (5, 2, 2, 2, '2025-08-30 15:30:21', '2025-08-30 15:30:23', 0);
INSERT INTO `article_column` VALUES (6, 3, 10, 1, '2025-08-30 17:38:50', '2025-08-30 17:38:50', 0);
INSERT INTO `article_column` VALUES (7, 3, 10, 2, '2025-08-30 17:38:50', '2025-08-30 17:38:50', 0);
INSERT INTO `article_column` VALUES (8, 0, 10, 6, '2025-08-30 17:38:50', '2025-08-30 17:38:50', 0);
INSERT INTO `article_column` VALUES (9, 4, 11, 1, '2025-08-30 17:39:31', '2025-08-30 17:39:31', 0);
INSERT INTO `article_column` VALUES (10, 4, 11, 2, '2025-08-30 17:39:31', '2025-08-30 17:39:31', 0);
INSERT INTO `article_column` VALUES (11, 1, 11, 6, '2025-08-30 17:39:31', '2025-08-30 17:39:31', 0);
INSERT INTO `article_column` VALUES (12, 5, 12, 1, '2025-08-30 23:54:50', '2025-08-30 23:54:50', 0);
INSERT INTO `article_column` VALUES (13, 5, 12, 2, '2025-08-30 23:54:50', '2025-08-30 23:54:50', 0);
INSERT INTO `article_column` VALUES (15, 6, 13, 1, '2025-08-30 23:57:23', '2025-08-30 23:57:23', 0);
INSERT INTO `article_column` VALUES (16, 6, 13, 2, '2025-08-30 23:57:23', '2025-08-30 23:57:23', 0);
INSERT INTO `article_column` VALUES (22, 7, 15, 1, '2025-09-01 21:10:11', '2025-09-01 21:10:11', 0);
INSERT INTO `article_column` VALUES (29, 7, 44, 2, '2025-09-10 14:01:59', '2025-09-10 14:01:59', 0);
INSERT INTO `article_column` VALUES (34, 13, 3, 1, '2025-09-22 15:13:08', '2025-09-22 15:13:08', 0);
INSERT INTO `article_column` VALUES (35, 8, 4, 1, '2025-09-22 15:13:08', '2025-09-22 15:13:08', 0);
INSERT INTO `article_column` VALUES (36, 9, 5, 1, '2025-09-22 15:13:08', '2025-09-22 15:13:08', 0);
INSERT INTO `article_column` VALUES (37, 10, 6, 1, '2025-09-11 15:13:08', '2025-09-22 15:13:08', 0);
INSERT INTO `article_column` VALUES (38, 11, 7, 1, '2025-09-22 15:13:08', '2025-09-22 15:13:08', 0);
INSERT INTO `article_column` VALUES (39, 12, 8, 1, '2025-09-22 15:13:08', '2025-09-22 15:13:08', 0);

-- ----------------------------
-- Table structure for article_favorite
-- ----------------------------
DROP TABLE IF EXISTS `article_favorite`;
CREATE TABLE `article_favorite`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `article_id` int NOT NULL COMMENT '被收藏的文章id',
  `favorite_id` int NOT NULL COMMENT '收藏夹id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_article_time`(`article_id` ASC, `create_time` DESC) USING BTREE,
  INDEX `idx_favorite_time`(`favorite_id` ASC, `create_time` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_favorite
-- ----------------------------
INSERT INTO `article_favorite` VALUES (1, 1, 1, '2025-09-24 15:13:02');
INSERT INTO `article_favorite` VALUES (2, 2, 1, '2025-09-24 15:13:22');
INSERT INTO `article_favorite` VALUES (6, 3, 1, '2025-09-24 15:25:13');
INSERT INTO `article_favorite` VALUES (7, 71, 1, '2025-09-24 19:09:15');
INSERT INTO `article_favorite` VALUES (8, 71, 5, '2025-09-24 20:15:42');
INSERT INTO `article_favorite` VALUES (9, 71, 4, '2025-09-24 20:15:43');
INSERT INTO `article_favorite` VALUES (10, 71, 3, '2025-09-24 20:15:43');
INSERT INTO `article_favorite` VALUES (11, 71, 2, '2025-09-24 20:15:44');
INSERT INTO `article_favorite` VALUES (12, 71, 6, '2025-09-24 20:17:11');

-- ----------------------------
-- Table structure for blacklist
-- ----------------------------
DROP TABLE IF EXISTS `blacklist`;
CREATE TABLE `blacklist`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '黑名单id',
  `type` tinyint NOT NULL COMMENT '黑名单类型 0-用户 1-ip地址',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip地址',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '拉黑原因',
  `ban_time` datetime NOT NULL COMMENT '拉黑时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '到期时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_expire`(`user_id` ASC, `expire_time` ASC) USING BTREE,
  INDEX `idx_ip_expire`(`ip` ASC, `expire_time` ASC) USING BTREE,
  INDEX `idx_type_ban_time`(`type` ASC, `ban_time` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blacklist
-- ----------------------------

-- ----------------------------
-- Table structure for column
-- ----------------------------
DROP TABLE IF EXISTS `column`;
CREATE TABLE `column`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '专栏id',
  `user_id` int NOT NULL COMMENT '用户id',
  `sort` int NOT NULL COMMENT '排序',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专栏名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专栏描述',
  `cover_url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专栏封面',
  `show_status` tinyint NOT NULL DEFAULT 0 COMMENT '展示状态 0-公开 1-私密',
  `examine_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0-待审核 1-审核通过 2-审核未通过',
  `focus_count` int NOT NULL DEFAULT 0 COMMENT '关注数',
  `article_count` int NOT NULL DEFAULT 0 COMMENT '文章数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id_examine_status`(`user_id` ASC, `examine_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of column
-- ----------------------------
INSERT INTO `column` VALUES (1, 1, 1, 'Java专栏', '专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述专栏描述', 'https://image.sidifensen.com/sidifensen-blog/article/1/9864b2af7658453f913598e19d6e8f10.jpg', 0, 1, 2, 10, '2025-08-26 10:38:45', '2025-09-22 10:56:24', 0);
INSERT INTO `column` VALUES (2, 1, 0, 'Spring专栏', '123', 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 0, 1, 2, 9, '2025-08-26 10:40:51', '2025-09-22 10:56:24', 0);
INSERT INTO `column` VALUES (3, 1, 2, 'Vue专栏', '123', 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 0, 1, 2, 11, '2025-08-26 10:40:51', '2025-08-26 10:40:51', 0);
INSERT INTO `column` VALUES (4, 1, 3, 'Springboot专栏', NULL, 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 1, 2, 0, 0, '2025-08-29 17:41:28', '2025-08-29 17:41:28', 0);
INSERT INTO `column` VALUES (5, 1, 4, 'js专栏', '123456', 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 1, 2, 0, 0, '2025-08-29 17:45:02', '2025-08-29 17:45:02', 0);
INSERT INTO `column` VALUES (6, 1, 5, 'html专栏', '123456', 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 1, 1, 0, 0, '2025-08-29 17:45:05', '2025-10-06 14:26:03', 0);
INSERT INTO `column` VALUES (7, 1, 7, 'mysql专栏', '123456', 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 1, 0, 0, 0, '2025-08-29 17:45:07', '2025-10-06 14:25:23', 0);
INSERT INTO `column` VALUES (8, 1, 6, 'redis专栏', '123456', 'https://image.sidifensen.com/sidifensen-blog/article/1/9864b2af7658453f913598e19d6e8f10.jpg/sidifensen-blog/column/1/8/481cad47d11f472a8af58fe86ac98154.jpg', 0, 0, 0, 0, '2025-08-29 21:19:13', '2025-10-06 14:26:10', 0);
INSERT INTO `column` VALUES (9, 1, 8, 'ai专栏', '123456', 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 0, 0, 0, 0, '2025-08-29 23:26:11', '2025-08-29 23:26:11', 0);
INSERT INTO `column` VALUES (10, 1, 10, 'mybatisplus专栏', NULL, 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 0, 1, 0, 0, '2025-08-29 23:26:43', '2025-09-20 16:28:03', 0);
INSERT INTO `column` VALUES (11, 1, 9, 'Minio专栏', NULL, 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 0, 1, 0, 0, '2025-08-30 00:23:19', '2025-09-20 16:28:03', 0);
INSERT INTO `column` VALUES (12, 1, 13, 'cursor专栏', NULL, NULL, 0, 1, 0, 0, '2025-09-22 11:58:14', '2025-10-06 14:25:21', 0);
INSERT INTO `column` VALUES (13, 1, 11, 'qoder专栏', NULL, NULL, 0, 1, 0, 0, '2025-09-22 11:58:29', '2025-10-06 14:25:21', 0);
INSERT INTO `column` VALUES (14, 1, 12, 'windows', NULL, NULL, 0, 1, 0, 0, '2025-09-22 11:59:08', '2025-10-06 14:25:21', 0);
INSERT INTO `column` VALUES (15, 1, 14, 'elementplus专栏', NULL, NULL, 0, 1, 0, 0, '2025-09-22 11:59:30', '2025-09-24 08:34:25', 0);
INSERT INTO `column` VALUES (16, 1, 15, 'ant专栏1', 'ant专栏ant专栏1', 'https://image.sidifensen.com/sidifensen-blog/article/1/9864b2af7658453f913598e19d6e8f10.jpg/sidifensen-blog/column/1/84499e14d67f49d88435c026a8d81e6d.jpg', 0, 1, 0, 0, '2025-09-22 23:51:08', '2025-09-24 08:34:22', 0);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论id',
  `parent_id` int NULL DEFAULT 0 COMMENT '父级评论id',
  `article_id` int NOT NULL COMMENT '文章id',
  `user_id` int NOT NULL COMMENT '评论用户id',
  `reply_user_id` int NULL DEFAULT NULL COMMENT '回复的用户id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `examine_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0-待审核 1-审核通过 2-审核未通过',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` int NOT NULL DEFAULT 0 COMMENT '回复数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_article_id_parent_id_examine_status_create_time`(`article_id` ASC, `parent_id` ASC, `examine_status` ASC, `create_time` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 96 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 0, 44, 1, NULL, '111', 1, 0, 2, '2025-09-15 18:31:25', 0);
INSERT INTO `comment` VALUES (2, 1, 44, 1, 1, '666', 1, 0, 1, '2025-09-15 18:51:00', 0);
INSERT INTO `comment` VALUES (3, 1, 44, 1, 1, '太强了', 1, 0, 0, '2025-09-15 18:51:08', 0);
INSERT INTO `comment` VALUES (4, 2, 44, 1, 1, '无敌了', 1, 0, 1, '2025-09-15 18:51:24', 0);
INSERT INTO `comment` VALUES (5, 4, 44, 1, 1, '牛牛牛', 1, 0, 0, '2025-09-15 18:51:38', 0);
INSERT INTO `comment` VALUES (6, 0, 44, 1, NULL, '2222', 1, 0, 0, '2025-09-15 18:52:00', 0);
INSERT INTO `comment` VALUES (7, 0, 44, 1, NULL, '3333', 1, 0, 0, '2025-09-15 18:52:14', 0);
INSERT INTO `comment` VALUES (8, 0, 44, 1, NULL, '4444', 1, 0, 0, '2025-09-15 18:52:23', 0);
INSERT INTO `comment` VALUES (9, 0, 44, 1, NULL, '55555', 1, 0, 0, '2025-09-15 18:53:46', 0);
INSERT INTO `comment` VALUES (10, 0, 44, 1, NULL, '6666', 1, 0, 0, '2025-09-15 18:53:48', 0);
INSERT INTO `comment` VALUES (11, 0, 44, 1, NULL, '7777', 1, 0, 0, '2025-09-15 18:53:50', 0);
INSERT INTO `comment` VALUES (12, 0, 44, 1, NULL, '8888', 1, 0, 0, '2025-09-15 18:53:51', 0);
INSERT INTO `comment` VALUES (13, 0, 44, 1, NULL, '9999', 1, 0, 0, '2025-09-15 18:53:53', 0);
INSERT INTO `comment` VALUES (14, 0, 44, 1, NULL, '1000', 1, 0, 0, '2025-09-15 18:53:57', 0);
INSERT INTO `comment` VALUES (15, 0, 44, 1, NULL, '111', 1, 0, 0, '2025-09-15 18:53:58', 0);
INSERT INTO `comment` VALUES (16, 0, 44, 1, NULL, '121212', 1, 0, 0, '2025-09-15 18:54:02', 0);
INSERT INTO `comment` VALUES (17, 0, 44, 1, NULL, '1313', 1, 0, 0, '2025-09-15 18:54:06', 0);
INSERT INTO `comment` VALUES (18, 0, 44, 1, NULL, '1414', 1, 0, 0, '2025-09-15 18:54:11', 0);
INSERT INTO `comment` VALUES (19, 0, 44, 1, NULL, '1515', 1, 0, 0, '2025-09-15 18:54:13', 0);
INSERT INTO `comment` VALUES (20, 0, 44, 1, NULL, '1616', 1, 0, 0, '2025-09-15 18:54:16', 0);
INSERT INTO `comment` VALUES (21, 0, 44, 1, NULL, '1717', 1, 0, 0, '2025-09-15 18:54:18', 0);
INSERT INTO `comment` VALUES (22, 0, 44, 1, NULL, '1818', 1, 0, 0, '2025-09-15 18:54:20', 0);
INSERT INTO `comment` VALUES (23, 0, 44, 1, NULL, '1919', 1, 0, 0, '2025-09-15 18:54:23', 0);
INSERT INTO `comment` VALUES (24, 0, 44, 1, NULL, '2020', 2, 0, 0, '2025-09-15 18:54:26', 0);
INSERT INTO `comment` VALUES (25, 0, 44, 1, NULL, '2121', 2, 0, 0, '2025-09-15 18:54:28', 0);
INSERT INTO `comment` VALUES (26, 0, 44, 1, NULL, '2222', 0, 0, 0, '2025-09-15 18:54:29', 0);
INSERT INTO `comment` VALUES (27, 0, 44, 1, NULL, '2323', 0, 0, 5, '2025-09-15 18:54:33', 0);
INSERT INTO `comment` VALUES (57, 0, 44, 1, NULL, '大神', 1, 0, 3, '2025-09-16 15:36:34', 0);
INSERT INTO `comment` VALUES (58, 57, 44, 1, 1, '太强了', 1, 0, 0, '2025-09-16 15:36:43', 0);
INSERT INTO `comment` VALUES (59, 57, 44, 1, 1, '太牛了', 1, 0, 0, '2025-09-16 15:39:51', 0);
INSERT INTO `comment` VALUES (60, 0, 44, 1, NULL, '大佬', 1, 0, 10, '2025-09-16 15:40:12', 0);
INSERT INTO `comment` VALUES (61, 60, 44, 1, 1, '太厉害了', 1, 0, 0, '2025-09-16 15:40:18', 0);
INSERT INTO `comment` VALUES (62, 60, 44, 1, 1, '哇塞,大佬', 1, 0, 0, '2025-09-16 15:40:24', 0);
INSERT INTO `comment` VALUES (63, 60, 44, 1, 1, '无敌了', 1, 0, 0, '2025-09-16 15:40:29', 0);
INSERT INTO `comment` VALUES (64, 60, 44, 1, 1, '无敌双刀', 1, 0, 0, '2025-09-16 15:51:17', 0);
INSERT INTO `comment` VALUES (65, 60, 44, 1, 1, '无敌无敌', 1, 0, 0, '2025-09-16 15:51:25', 0);
INSERT INTO `comment` VALUES (66, 60, 44, 1, 1, '大神,太强了666', 1, 0, 0, '2025-09-16 15:51:57', 0);
INSERT INTO `comment` VALUES (67, 60, 44, 1, 1, '大佬太强了', 1, 0, 0, '2025-09-16 15:58:16', 0);
INSERT INTO `comment` VALUES (68, 60, 44, 1, 1, '这就是神吗', 1, 0, 0, '2025-09-16 15:58:25', 0);
INSERT INTO `comment` VALUES (69, 57, 44, 1, 1, '无敌', 1, 0, 0, '2025-09-16 15:58:32', 0);
INSERT INTO `comment` VALUES (70, 27, 44, 1, 1, '太强了', 1, 0, 0, '2025-09-16 15:58:37', 0);
INSERT INTO `comment` VALUES (71, 27, 44, 1, 1, '23双刀', 1, 0, 0, '2025-09-16 15:58:43', 0);
INSERT INTO `comment` VALUES (72, 27, 44, 1, 1, '无敌双刀', 1, 0, 0, '2025-09-16 15:58:47', 0);
INSERT INTO `comment` VALUES (73, 27, 44, 1, 1, '666大哥', 1, 0, 0, '2025-09-16 15:58:53', 0);
INSERT INTO `comment` VALUES (74, 27, 44, 1, 1, '大神来了', 1, 0, 0, '2025-09-16 15:58:59', 0);
INSERT INTO `comment` VALUES (75, 60, 44, 1, 1, '大牛', 1, 0, 0, '2025-09-16 22:31:06', 0);
INSERT INTO `comment` VALUES (76, 60, 44, 1, 1, '无敌双刀', 1, 0, 0, '2025-09-16 22:31:12', 0);
INSERT INTO `comment` VALUES (77, 0, 44, 1, NULL, '我去太强了吧兄弟', 0, 0, 0, '2025-09-16 22:31:20', 0);
INSERT INTO `comment` VALUES (78, 0, 44, 1, NULL, '你今天的进度呢', 1, 0, 1, '2025-09-16 22:31:30', 0);
INSERT INTO `comment` VALUES (79, 0, 44, 1, NULL, '进度', 1, 0, 0, '2025-09-16 22:31:33', 0);
INSERT INTO `comment` VALUES (80, 0, 44, 1, NULL, '不比了', 1, 0, 0, '2025-09-16 22:31:39', 0);
INSERT INTO `comment` VALUES (81, 0, 44, 1, NULL, '项目写得好慢', 1, 0, 1, '2025-09-16 22:31:47', 0);
INSERT INTO `comment` VALUES (82, 81, 44, 1, 1, '正常兄弟', 1, 0, 0, '2025-09-16 22:34:27', 0);
INSERT INTO `comment` VALUES (83, 78, 44, 1, 1, '进度为0', 1, 0, 0, '2025-09-16 22:47:46', 0);
INSERT INTO `comment` VALUES (84, 0, 44, 1, NULL, '进度', 1, 0, 0, '2025-09-16 23:01:04', 0);
INSERT INTO `comment` VALUES (85, 0, 63, 1, NULL, '博主厉害啊', 1, 0, 3, '2025-09-19 23:42:10', 0);
INSERT INTO `comment` VALUES (86, 0, 63, 1, NULL, '太强了', 2, 0, 0, '2025-09-19 23:45:07', 0);
INSERT INTO `comment` VALUES (87, 0, 71, 2, NULL, '太强了', 1, 0, 0, '2025-09-20 00:49:39', 0);
INSERT INTO `comment` VALUES (88, 0, 71, 2, NULL, '大神', 0, 0, 0, '2025-09-20 00:49:42', 0);
INSERT INTO `comment` VALUES (89, 0, 63, 1, NULL, '走过那么多路，看过那么多风景，原来最想停靠的，是内心的平静。', 1, 0, 0, '2025-09-20 10:42:39', 0);
INSERT INTO `comment` VALUES (90, 0, 63, 1, NULL, '你感到痛苦是因为你在成长。士，不可以不弘毅，任重而道远。你的焦虑是因为想得太多而做得太少。昨天的暴雨淋不湿今天的我。', 1, 0, 0, '2025-09-20 10:43:17', 0);
INSERT INTO `comment` VALUES (91, 0, 63, 1, NULL, '少年请你记住\n如果你在心里埋下了一颗争气的种子\n那么努力就变成了必修课\n在人的一生中\n最为辉煌的一天\n并不是功成名就的那天\n而是从悲叹和绝望中\n产生对人生的欲望\n并且勇敢的迈向这种挑战的那一天\n无论再苦再累\n请你跑到终点\n再尽情哭泣\n成长本就是一个孤立无援的过程\n难走的从来不是路\n而是你心里的那一关\n能困住你的\n只有你自己\n那个让你迷茫的决定\n也许正是未来的你需要的\n那些在深夜里睡不着的日子\n那些面对深渊想要逃跑的瞬间\n在日后看来都是非常珍贵的时刻\n他让我们更加真诚地面对自己\n只有认识他、接受他\n我们才可', 1, 0, 1, '2025-09-20 10:45:05', 0);
INSERT INTO `comment` VALUES (92, 85, 63, 1, 1, '太强了大神', 1, 0, 0, '2025-09-20 10:53:16', 0);
INSERT INTO `comment` VALUES (93, 85, 63, 2, 1, '有点强,兄弟', 1, 0, 0, '2025-09-20 10:53:59', 0);
INSERT INTO `comment` VALUES (94, 85, 63, 1, 2, '必须的', 1, 0, 0, '2025-09-20 14:30:17', 0);
INSERT INTO `comment` VALUES (95, 91, 63, 2, 1, '有点意思', 1, 0, 0, '2025-09-20 14:30:27', 0);

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '收藏夹id',
  `user_id` int NOT NULL COMMENT '用户id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收藏夹名称',
  `show_status` tinyint NOT NULL DEFAULT 0 COMMENT '展示状态 0-公开 1-私密',
  `article_count` int NOT NULL DEFAULT 0 COMMENT '文章数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_status_time`(`user_id` ASC, `show_status` ASC, `create_time` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (1, 1, '收藏夹1', 0, 4, '2025-09-24 15:06:25', 0);
INSERT INTO `favorite` VALUES (2, 1, '收藏夹2', 0, 1, '2025-09-24 15:31:14', 0);
INSERT INTO `favorite` VALUES (3, 1, '收藏夹3', 0, 1, '2025-09-24 15:31:17', 0);
INSERT INTO `favorite` VALUES (4, 1, '收藏夹4', 0, 1, '2025-09-24 15:31:21', 0);
INSERT INTO `favorite` VALUES (5, 1, '收藏夹5', 0, 1, '2025-09-24 15:31:23', 0);
INSERT INTO `favorite` VALUES (6, 1, '收藏夹6', 1, 1, '2025-09-24 20:17:09', 0);
INSERT INTO `favorite` VALUES (7, 1, '收藏夹7', 1, 0, '2025-09-24 22:21:13', 0);
INSERT INTO `favorite` VALUES (8, 1, '收藏夹8', 1, 0, '2025-09-24 22:21:19', 0);

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关注id',
  `follower_id` int NOT NULL COMMENT '关注者用户id',
  `followed_id` int NOT NULL COMMENT '被关注者用户id',
  `create_time` datetime NOT NULL COMMENT '关注时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_follower_time`(`follower_id` ASC, `create_time` DESC) USING BTREE,
  INDEX `idx_followed_time`(`followed_id` ASC, `create_time` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of follow
-- ----------------------------
INSERT INTO `follow` VALUES (10, 2, 1, '2025-09-26 13:07:13');
INSERT INTO `follow` VALUES (17, 1, 2, '2025-09-26 13:43:39');
INSERT INTO `follow` VALUES (18, 1, 3, '2025-09-26 13:43:39');
INSERT INTO `follow` VALUES (19, 3, 1, '2025-09-26 13:44:37');
INSERT INTO `follow` VALUES (20, 4, 1, '2025-09-26 13:44:37');
INSERT INTO `follow` VALUES (21, 5, 1, '2025-09-26 13:44:37');
INSERT INTO `follow` VALUES (22, 6, 1, '2025-09-26 13:44:37');
INSERT INTO `follow` VALUES (23, 1, 4, '2025-09-26 13:45:39');
INSERT INTO `follow` VALUES (24, 1, 5, '2025-09-26 13:45:41');
INSERT INTO `follow` VALUES (25, 1, 6, '2025-09-26 13:45:42');
INSERT INTO `follow` VALUES (26, 1, 7, '2025-09-26 13:44:37');
INSERT INTO `follow` VALUES (27, 1, 8, '2025-09-26 13:44:37');
INSERT INTO `follow` VALUES (28, 1, 9, '2025-09-26 13:44:37');
INSERT INTO `follow` VALUES (29, 1, 10, '2025-09-26 13:44:37');
INSERT INTO `follow` VALUES (30, 1, 11, '2025-09-26 14:05:46');
INSERT INTO `follow` VALUES (31, 1, 12, '2025-09-26 14:05:46');
INSERT INTO `follow` VALUES (32, 1, 18, '2025-09-26 14:19:22');
INSERT INTO `follow` VALUES (33, 1, 19, '2025-09-26 14:19:22');
INSERT INTO `follow` VALUES (34, 1, 20, '2025-09-26 14:19:22');
INSERT INTO `follow` VALUES (35, 1, 21, '2025-09-26 14:19:22');
INSERT INTO `follow` VALUES (36, 1, 22, '2025-09-26 14:19:22');
INSERT INTO `follow` VALUES (37, 1, 23, '2025-09-26 14:19:22');
INSERT INTO `follow` VALUES (38, 1, 24, '2025-09-26 14:19:22');
INSERT INTO `follow` VALUES (39, 1, 25, '2025-09-26 14:19:22');
INSERT INTO `follow` VALUES (40, 1, 26, '2025-09-26 14:28:58');
INSERT INTO `follow` VALUES (41, 1, 27, '2025-09-26 14:28:58');
INSERT INTO `follow` VALUES (42, 1, 28, '2025-09-26 14:28:58');
INSERT INTO `follow` VALUES (43, 1, 29, '2025-09-26 14:28:58');
INSERT INTO `follow` VALUES (44, 1, 30, '2025-09-26 14:28:58');
INSERT INTO `follow` VALUES (45, 1, 31, '2025-09-26 14:28:58');
INSERT INTO `follow` VALUES (46, 1, 32, '2025-09-26 14:28:58');
INSERT INTO `follow` VALUES (47, 1, 33, '2025-09-26 14:28:58');
INSERT INTO `follow` VALUES (48, 1, 34, '2025-09-26 14:28:58');
INSERT INTO `follow` VALUES (49, 1, 35, '2025-09-26 14:28:58');
INSERT INTO `follow` VALUES (50, 1, 36, '2025-09-26 14:28:58');

-- ----------------------------
-- Table structure for history
-- ----------------------------
DROP TABLE IF EXISTS `history`;
CREATE TABLE `history`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '历史id',
  `article_id` int NOT NULL COMMENT '文章id',
  `user_id` int NOT NULL COMMENT '用户id',
  `view_time` datetime NOT NULL COMMENT '浏览时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_article_user`(`article_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_view_time`(`user_id` ASC, `view_time` DESC) USING BTREE,
  INDEX `idx_history_hot_articles`(`view_time` ASC, `article_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of history
-- ----------------------------
INSERT INTO `history` VALUES (24, 70, 1, '2025-10-08 16:42:21', 0);
INSERT INTO `history` VALUES (25, 1, 1, '2025-10-08 21:49:05', 0);
INSERT INTO `history` VALUES (26, 2, 1, '2025-10-08 21:49:01', 0);
INSERT INTO `history` VALUES (27, 44, 1, '2025-09-27 16:13:25', 0);
INSERT INTO `history` VALUES (28, 45, 1, '2025-09-27 16:13:29', 0);
INSERT INTO `history` VALUES (29, 42, 1, '2025-10-08 21:36:27', 0);
INSERT INTO `history` VALUES (30, 63, 1, '2025-10-08 22:28:21', 0);
INSERT INTO `history` VALUES (31, 67, 1, '2025-10-08 15:37:46', 0);
INSERT INTO `history` VALUES (32, 71, 1, '2025-10-09 10:53:14', 0);
INSERT INTO `history` VALUES (33, 48, 2, '2025-09-28 00:10:08', 0);
INSERT INTO `history` VALUES (34, 66, 1, '2025-09-28 16:09:13', 0);
INSERT INTO `history` VALUES (35, 64, 1, '2025-09-29 11:08:45', 0);
INSERT INTO `history` VALUES (36, 63, 2, '2025-10-08 00:06:10', 0);
INSERT INTO `history` VALUES (37, 49, 1, '2025-10-08 08:53:39', 0);
INSERT INTO `history` VALUES (38, 47, 1, '2025-10-08 08:53:44', 0);
INSERT INTO `history` VALUES (39, 50, 1, '2025-10-08 08:53:43', 0);
INSERT INTO `history` VALUES (40, 55, 1, '2025-10-08 08:53:47', 0);
INSERT INTO `history` VALUES (41, 69, 1, '2025-10-08 08:53:52', 0);
INSERT INTO `history` VALUES (42, 17, 1, '2025-10-08 08:53:57', 0);
INSERT INTO `history` VALUES (43, 19, 1, '2025-10-09 10:41:05', 0);
INSERT INTO `history` VALUES (44, 15, 1, '2025-10-08 14:12:17', 0);
INSERT INTO `history` VALUES (45, 43, 1, '2025-10-08 21:47:19', 0);
INSERT INTO `history` VALUES (46, 48, 1, '2025-10-08 21:47:26', 0);
INSERT INTO `history` VALUES (47, 18, 1, '2025-10-08 21:47:32', 0);
INSERT INTO `history` VALUES (48, 53, 1, '2025-10-08 21:47:35', 0);
INSERT INTO `history` VALUES (49, 60, 1, '2025-10-09 10:46:53', 0);

-- ----------------------------
-- Table structure for like
-- ----------------------------
DROP TABLE IF EXISTS `like`;
CREATE TABLE `like`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '点赞id',
  `user_id` int NOT NULL COMMENT '点赞用户id',
  `type` tinyint NOT NULL COMMENT '点赞类型 0-文章 1-评论',
  `type_id` int NOT NULL COMMENT '点赞类型id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_type_id`(`user_id` ASC, `type` ASC, `type_id` ASC) USING BTREE,
  INDEX `idx_type_id_create_time`(`type` ASC, `type_id` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 185 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of like
-- ----------------------------
INSERT INTO `like` VALUES (183, 2, 0, 63, '2025-10-08 00:07:48');
INSERT INTO `like` VALUES (184, 1, 1, 90, '2025-10-08 17:23:51');

-- ----------------------------
-- Table structure for link
-- ----------------------------
DROP TABLE IF EXISTS `link`;
CREATE TABLE `link`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '友链id',
  `user_id` int NOT NULL COMMENT '用户id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网站名称',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网站地址',
  `cover_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网站封面',
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网站描述',
  `examine_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0-待审核 1-审核通过 2-审核未通过',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网站邮箱',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of link
-- ----------------------------
INSERT INTO `link` VALUES (1, 1, '我的世界我的世界我的世界我的世界我的世界', 'https://sidifensen.com', 'https://image.sidifensen.com/sidifensen-blog/article/1/b086412fe72c448291fc16eedb251fd6.jpg', '1', 1, '1848221808@qq.com', '2025-09-28 16:00:30', 0);
INSERT INTO `link` VALUES (2, 1, '友链1', '友链地址', 'https://image.sidifensen.com/sidifensen-blog/article/1/b086412fe72c448291fc16eedb251fd6.jpg', '友链描述', 1, '123456@qq.com', '2025-09-28 16:27:49', 0);
INSERT INTO `link` VALUES (3, 1, '友链2', '友链2', 'https://image.sidifensen.com/sidifensen-blog/article/1/b086412fe72c448291fc16eedb251fd6.jpg', '友链2', 1, '友链2', '2025-09-28 23:29:00', 0);
INSERT INTO `link` VALUES (4, 1, '友链友链友链友链友链友链友链友链友链友链', '友链', 'https://image.sidifensen.com/sidifensen-blog/article/1/b086412fe72c448291fc16eedb251fd6.jpg', '友链', 1, '友链', '2025-09-28 23:34:05', 0);
INSERT INTO `link` VALUES (5, 1, '友链友链友链友链友链友链友链友链友链友链', '友链', 'https://image.sidifensen.com/sidifensen-blog/article/1/b086412fe72c448291fc16eedb251fd6.jpg', '友链', 1, '友链', '2025-09-29 00:03:42', 0);
INSERT INTO `link` VALUES (6, 1, 'Vue.js 官方文档', 'https://vuejs.org/', 'https://vuejs.org/images/logo.png', 'Vue.js 渐进式 JavaScript 框架官方文档', 1, 'admin@vuejs.org', '2025-09-29 11:36:10', 0);
INSERT INTO `link` VALUES (7, 1, 'Spring Boot 官方', 'https://spring.io/projects/spring-boot', 'https://spring.io/images/spring-logo-9146a4d3298760c2e7e49595184e1975.svg', 'Spring Boot 官方项目主页', 1, 'admin@spring.io', '2025-09-29 11:36:10', 0);
INSERT INTO `link` VALUES (8, 1, 'GitHub', 'https://github.com', 'https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png', '全球最大的代码托管平台', 1, 'support@github.com', '2025-09-29 11:36:10', 0);
INSERT INTO `link` VALUES (9, 1, 'MDN Web 文档', 'https://developer.mozilla.org/', 'https://developer.mozilla.org/mdn-social-share.cd6c4a5a.png', 'Mozilla 开发者网络，Web 技术权威文档', 1, 'mdn-feedback@mozilla.com', '2025-09-29 11:36:10', 0);
INSERT INTO `link` VALUES (10, 1, 'Stack Overflow', 'https://stackoverflow.com', 'https://cdn.sstatic.net/Sites/stackoverflow/Img/apple-touch-icon.png', '程序员问答社区', 1, 'team@stackoverflow.com', '2025-09-29 11:36:10', 0);
INSERT INTO `link` VALUES (11, 1, 'Runoob 菜鸟教程', 'https://www.runoob.com', 'https://static.runoob.com/images/favicon.ico', '提供各种编程语言的基础教程', 1, 'webmaster@runoob.com', '2025-09-29 11:36:10', 0);
INSERT INTO `link` VALUES (12, 1, 'Element Plus', 'https://element-plus.org/', 'https://element-plus.org/images/element-plus-logo.svg', 'Vue 3 的桌面端组件库', 1, 'hello@element-plus.org', '2025-09-29 11:36:10', 0);
INSERT INTO `link` VALUES (13, 1, 'Ant Design', 'https://ant.design/', 'https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg', '企业级 UI 设计语言和组件库', 1, 'afc163@gmail.com', '2025-09-29 11:36:10', 0);
INSERT INTO `link` VALUES (14, 1, 'TypeScript', 'https://www.typescriptlang.org/', 'https://www.typescriptlang.org/icons/icon-48x48.png', 'JavaScript 的超集，添加了类型系统', 1, 'typescript@microsoft.com', '2025-09-29 11:36:10', 0);
INSERT INTO `link` VALUES (15, 1, 'Node.js', 'https://nodejs.org/', 'https://nodejs.org/static/images/logo.svg', 'JavaScript 运行时环境', 1, 'hello@nodejs.org', '2025-09-29 11:36:10', 0);
INSERT INTO `link` VALUES (16, 1, 'Redis 官方', 'https://redis.io/', 'https://redis.io/images/redis-white.png', '内存数据结构存储系统', 1, 'info@redis.io', '2025-09-29 11:36:23', 0);
INSERT INTO `link` VALUES (17, 1, 'MySQL 官方', 'https://www.mysql.com/', 'https://labs.mysql.com/common/logos/mysql-logo.svg', '世界上最流行的开源数据库', 1, 'mysql-info@oracle.com', '2025-09-29 11:36:23', 0);
INSERT INTO `link` VALUES (18, 1, 'Docker Hub', 'https://hub.docker.com/', 'https://www.docker.com/sites/default/files/d8/2019-07/Moby-logo.png', '容器镜像仓库', 1, 'support@docker.com', '2025-09-29 11:36:23', 0);
INSERT INTO `link` VALUES (19, 1, 'Nginx 官方', 'https://nginx.org/', 'https://nginx.org/nginx.png', '高性能的 Web 服务器和反向代理', 1, 'nginx@nginx.org', '2025-09-29 11:36:23', 0);
INSERT INTO `link` VALUES (20, 1, 'Postman', 'https://www.postman.com/', 'https://www.postman.com/web-assets/icons/icon-96x96.png', 'API 开发和测试平台', 1, 'help@postman.com', '2025-09-29 11:36:23', 0);
INSERT INTO `link` VALUES (21, 1, 'JetBrains', 'https://www.jetbrains.com/', 'https://www.jetbrains.com/apple-touch-icon.png', '专业开发工具厂商', 1, 'info@jetbrains.com', '2025-09-29 11:36:23', 0);
INSERT INTO `link` VALUES (22, 1, 'VS Code', 'https://code.visualstudio.com/', 'https://code.visualstudio.com/favicon.ico', '微软开发的代码编辑器微软开发的代码编辑器微软开发的代码编辑器微软开发的代码编', 1, 'vscode@microsoft.com', '2025-09-29 11:36:23', 0);
INSERT INTO `link` VALUES (23, 1, 'Webpack', 'https://webpack.js.org/', 'https://webpack.js.org/icon-square-small.85ba630cf0c5f29ae3e3.svg', '现代 JavaScript 应用程序的静态模块打包器', 1, 'webpack@js.foundation', '2025-09-29 11:36:23', 0);
INSERT INTO `link` VALUES (24, 1, 'Vite', 'https://vitejs.dev/', 'https://vitejs.dev/logo.svg', '下一代前端构建工具', 1, 'hello@vitejs.dev', '2025-09-29 11:36:23', 0);
INSERT INTO `link` VALUES (25, 1, 'Tailwind CSS', 'https://tailwindcss.com/', 'https://tailwindcss.com/favicons/favicon-32x32.png', '实用优先的 CSS 框架', 1, 'hello@tailwindcss.com', '2025-09-29 11:36:23', 0);
INSERT INTO `link` VALUES (26, 1, '友链123', 'http://localhost:7000/', 'https://image.sidifensen.com/sidifensen-blog/article/1/117f9f2a16e34e19849800dbe99bbea0.webp', '友链友链友链友链友链友链友链友链友链友链友链', 1, '1848221808@qq.com', '2025-09-29 13:56:59', 0);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '消息类型 0-系统 1-评论 2-点赞 3-收藏 4-关注',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `sender_id` int NOT NULL COMMENT '发送消息的用户id',
  `receiver_id` int NOT NULL COMMENT '接收消息的用户id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sender_id_type`(`sender_id` ASC, `type` ASC) USING BTREE,
  INDEX `idx_receiver_id_type`(`receiver_id` ASC, `type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 301 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 1, 0, '{\"text\":\"图片 1018 需要人工审核\"}', 1, 1, '2025-08-21 15:14:07', '2025-08-21 00:14:07', 0);
INSERT INTO `message` VALUES (2, 1, 0, '{\"text\":\"图片 1019 需要人工审核\"}', 1, 1, '2025-08-20 00:19:39', '2025-08-21 00:19:39', 0);
INSERT INTO `message` VALUES (3, 1, 0, '{\"text\":\"图片 1021 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (4, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (5, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (6, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (7, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (8, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (9, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (10, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (11, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (12, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (13, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (14, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (15, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (16, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (17, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (18, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (19, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (20, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (21, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (22, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (23, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (24, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (25, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (26, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (27, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (28, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (29, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (30, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (31, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (32, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (33, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (34, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (35, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (36, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (37, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (38, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (39, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (40, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (41, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (42, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (43, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (44, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (45, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (46, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (47, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (48, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (49, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (50, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (51, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (52, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (53, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (54, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (55, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (56, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (57, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (58, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (59, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (60, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (61, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (62, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (63, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (64, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (65, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (66, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (67, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (68, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (69, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (70, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (71, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (72, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (73, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (74, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (75, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (76, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (77, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (78, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (79, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (80, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (81, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (82, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (83, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (84, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (85, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (86, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (87, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (88, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (89, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (90, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (91, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (92, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (93, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (94, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (95, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (96, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (97, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (98, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-20 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (99, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (100, 1, 0, '{\"text\":\"图片 1020 需要人工审核\"}', 1, 1, '2025-08-06 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `message` VALUES (101, 1, 0, '{\"text\":\"图片 1022 需要人工审核\"}', 1, 1, '2025-08-23 22:18:38', '2025-08-23 22:18:38', 0);
INSERT INTO `message` VALUES (102, 1, 0, '{\"text\":\"图片 1023 需要人工审核\"}', 1, 1, '2025-08-23 22:27:07', '2025-08-23 22:27:07', 0);
INSERT INTO `message` VALUES (103, 1, 0, '{\"text\":\"图片 1025 需要人工审核\"}', 1, 1, '2025-08-26 21:41:04', '2025-08-26 21:41:04', 0);
INSERT INTO `message` VALUES (104, 1, 0, '{\"text\":\"图片 1026 需要人工审核\"}', 1, 1, '2025-08-26 21:41:17', '2025-08-26 21:41:17', 0);
INSERT INTO `message` VALUES (105, 1, 0, '{\"text\":\"图片 1027 需要人工审核\"}', 1, 1, '2025-08-26 21:46:49', '2025-08-26 21:46:49', 0);
INSERT INTO `message` VALUES (106, 1, 0, '{\"text\":\"图片 1028 需要人工审核\"}', 1, 1, '2025-08-26 21:47:25', '2025-08-26 21:47:25', 0);
INSERT INTO `message` VALUES (107, 1, 0, '{\"text\":\"图片 1029 需要人工审核\"}', 1, 1, '2025-08-26 21:47:36', '2025-08-26 21:47:36', 0);
INSERT INTO `message` VALUES (108, 1, 0, '{\"text\":\"图片 1030 需要人工审核\"}', 1, 1, '2025-08-26 21:47:56', '2025-08-26 21:47:56', 0);
INSERT INTO `message` VALUES (109, 1, 0, '{\"text\":\"图片 1031 需要人工审核\"}', 1, 1, '2025-08-26 21:48:17', '2025-08-26 21:48:17', 0);
INSERT INTO `message` VALUES (110, 1, 0, '{\"text\":\"图片 1032 需要人工审核\"}', 1, 1, '2025-08-29 11:25:10', '2025-08-29 11:25:10', 0);
INSERT INTO `message` VALUES (111, 1, 0, '{\"text\":\"图片 1033 需要人工审核\"}', 1, 1, '2025-08-29 14:15:31', '2025-08-29 14:15:31', 0);
INSERT INTO `message` VALUES (112, 1, 0, '{\"text\":\"图片 1034 需要人工审核\"}', 1, 1, '2025-08-29 14:16:14', '2025-08-29 14:16:14', 0);
INSERT INTO `message` VALUES (113, 1, 0, '{\"text\":\"图片 1035 需要人工审核\"}', 1, 1, '2025-08-29 14:21:14', '2025-08-29 14:21:14', 0);
INSERT INTO `message` VALUES (114, 1, 0, '{\"text\":\"图片 1036 需要人工审核\"}', 1, 1, '2025-08-29 14:27:22', '2025-08-29 14:27:22', 0);
INSERT INTO `message` VALUES (115, 1, 0, '{\"text\":\"图片 1037 需要人工审核\"}', 1, 1, '2025-08-29 14:33:17', '2025-08-29 14:33:17', 0);
INSERT INTO `message` VALUES (116, 1, 0, '{\"text\":\"图片 1038 需要人工审核\"}', 1, 1, '2025-08-29 14:36:20', '2025-08-29 14:36:20', 0);
INSERT INTO `message` VALUES (117, 1, 0, '{\"text\":\"图片 1039 需要人工审核\"}', 1, 1, '2025-08-29 14:37:47', '2025-08-29 14:37:47', 0);
INSERT INTO `message` VALUES (118, 1, 0, '{\"text\":\"图片 1040 需要人工审核\"}', 1, 1, '2025-08-29 14:43:35', '2025-08-29 14:43:35', 0);
INSERT INTO `message` VALUES (119, 1, 0, '{\"text\":\"图片 1041 需要人工审核\"}', 1, 1, '2025-08-29 15:07:39', '2025-08-29 15:07:39', 0);
INSERT INTO `message` VALUES (120, 1, 0, '{\"text\":\"图片 1042 需要人工审核\"}', 1, 1, '2025-08-29 15:25:57', '2025-08-29 15:25:57', 0);
INSERT INTO `message` VALUES (121, 1, 0, '{\"text\":\"图片 1043 需要人工审核\"}', 1, 1, '2025-08-29 15:40:12', '2025-08-29 15:40:12', 0);
INSERT INTO `message` VALUES (122, 1, 0, '{\"text\":\"图片 1044 需要人工审核\"}', 1, 1, '2025-08-29 15:48:54', '2025-08-29 15:48:54', 0);
INSERT INTO `message` VALUES (123, 1, 0, '{\"text\":\"图片 1045 需要人工审核\"}', 1, 1, '2025-08-30 15:06:27', '2025-08-30 15:06:27', 0);
INSERT INTO `message` VALUES (124, 1, 0, '{\"text\":\"图片 1046 需要人工审核\"}', 1, 1, '2025-08-30 15:07:15', '2025-08-30 15:07:15', 0);
INSERT INTO `message` VALUES (125, 1, 0, '{\"text\":\"图片 1047 需要人工审核\"}', 1, 1, '2025-08-30 15:09:09', '2025-08-30 15:09:09', 0);
INSERT INTO `message` VALUES (126, 1, 0, '{\"text\":\"图片 1048 需要人工审核\"}', 1, 1, '2025-08-30 17:39:29', '2025-08-30 17:39:29', 0);
INSERT INTO `message` VALUES (127, 1, 0, '{\"text\":\"图片 1049 需要人工审核\"}', 1, 1, '2025-08-30 23:54:24', '2025-08-30 23:54:24', 0);
INSERT INTO `message` VALUES (128, 1, 0, '{\"text\":\"文章 12 需要人工审核, 原因: null\"}', 1, 1, '2025-08-30 23:55:05', '2025-08-30 23:55:05', 0);
INSERT INTO `message` VALUES (129, 1, 0, '{\"text\":\"图片 1050 需要人工审核\"}', 1, 1, '2025-08-30 23:57:15', '2025-08-30 23:57:15', 0);
INSERT INTO `message` VALUES (130, 1, 0, '{\"text\":\"图片 1051 需要人工审核\"}', 1, 1, '2025-08-31 01:54:44', '2025-08-31 01:54:44', 0);
INSERT INTO `message` VALUES (131, 1, 0, '{\"text\":\"图片 1052 需要人工审核\"}', 1, 1, '2025-08-31 02:25:07', '2025-08-31 02:25:07', 0);
INSERT INTO `message` VALUES (132, 1, 0, '{\"text\":\"图片 1053 需要人工审核\"}', 1, 1, '2025-08-31 16:09:01', '2025-08-31 16:09:01', 0);
INSERT INTO `message` VALUES (133, 1, 0, '{\"text\":\"图片 1054 需要人工审核\"}', 1, 1, '2025-08-31 17:39:04', '2025-08-31 17:39:04', 0);
INSERT INTO `message` VALUES (134, 1, 0, '{\"text\":\"文章 22 需要人工审核, 原因: \"}', 1, 1, '2025-08-31 22:58:54', '2025-08-31 22:58:54', 0);
INSERT INTO `message` VALUES (135, 1, 0, '{\"text\":\"图片 1055 需要人工审核\"}', 1, 1, '2025-08-31 23:18:32', '2025-08-31 23:18:32', 0);
INSERT INTO `message` VALUES (136, 1, 0, '{\"text\":\"文章 2 需要人工审核, 原因: \"}', 1, 1, '2025-08-31 23:19:49', '2025-08-31 23:19:49', 0);
INSERT INTO `message` VALUES (137, 1, 0, '{\"text\":\"图片 1056 需要人工审核\"}', 1, 1, '2025-08-31 23:25:33', '2025-08-31 23:25:33', 0);
INSERT INTO `message` VALUES (138, 1, 0, '{\"text\":\"文章 2 需要人工审核, 原因: null\"}', 1, 1, '2025-08-31 23:52:31', '2025-08-31 23:52:31', 0);
INSERT INTO `message` VALUES (139, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-01 01:27:27', '2025-09-01 01:27:27', 0);
INSERT INTO `message` VALUES (140, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-01 02:13:31', '2025-09-01 02:13:31', 0);
INSERT INTO `message` VALUES (141, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-01 02:14:56', '2025-09-01 02:14:56', 0);
INSERT INTO `message` VALUES (142, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-01 03:28:08', '2025-09-01 03:28:08', 0);
INSERT INTO `message` VALUES (143, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-01 03:29:34', '2025-09-01 03:29:34', 0);
INSERT INTO `message` VALUES (144, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-01 03:31:52', '2025-09-01 03:31:52', 0);
INSERT INTO `message` VALUES (145, 1, 0, '{\"text\":\"图片 1057 需要人工审核\"}', 1, 1, '2025-09-01 03:34:16', '2025-09-01 03:34:16', 0);
INSERT INTO `message` VALUES (146, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-01 03:39:00', '2025-09-01 03:39:00', 0);
INSERT INTO `message` VALUES (147, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-01 17:50:40', '2025-09-01 17:50:40', 0);
INSERT INTO `message` VALUES (148, 1, 0, '{\"text\":\"图片 1058 需要人工审核\"}', 1, 1, '2025-09-01 21:10:06', '2025-09-01 21:10:06', 0);
INSERT INTO `message` VALUES (149, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-01 21:10:11', '2025-09-01 21:10:11', 0);
INSERT INTO `message` VALUES (150, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-01 21:50:11', '2025-09-01 21:50:11', 0);
INSERT INTO `message` VALUES (151, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-02 14:12:12', '2025-09-02 14:12:12', 0);
INSERT INTO `message` VALUES (152, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: null\"}', 1, 1, '2025-09-02 14:50:06', '2025-09-02 14:50:06', 0);
INSERT INTO `message` VALUES (153, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因: Sidifensen Blog 个人博客系统\"}', 1, 1, '2025-09-02 16:20:15', '2025-09-02 16:20:15', 0);
INSERT INTO `message` VALUES (154, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因: Sidifensen Blog 个人博客系统\"}', 1, 1, '2025-09-02 16:20:18', '2025-09-02 16:20:18', 0);
INSERT INTO `message` VALUES (155, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因: Sidifensen Blog 个人博客系统\"}', 1, 1, '2025-09-02 16:20:19', '2025-09-02 16:20:19', 0);
INSERT INTO `message` VALUES (156, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因: Sidifensen Blog 个人博客系统\"}', 1, 1, '2025-09-02 16:20:20', '2025-09-02 16:20:20', 0);
INSERT INTO `message` VALUES (157, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因: Sidifensen Blog 个人博客系统\"}', 1, 1, '2025-09-02 16:20:20', '2025-09-02 16:20:20', 0);
INSERT INTO `message` VALUES (158, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因: Sidifensen Blog 个人博客系统\"}', 1, 1, '2025-09-02 16:20:21', '2025-09-02 16:20:21', 0);
INSERT INTO `message` VALUES (159, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因: Sidifensen Blog 个人博客系统\"}', 1, 1, '2025-09-02 16:20:21', '2025-09-02 16:20:21', 0);
INSERT INTO `message` VALUES (160, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因: Sidifensen Blog 个人博客系统\"}', 1, 1, '2025-09-02 16:20:21', '2025-09-02 16:20:21', 0);
INSERT INTO `message` VALUES (161, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因: Sidifensen Blog 个人博客系统\"}', 1, 1, '2025-09-02 16:20:22', '2025-09-02 16:20:22', 0);
INSERT INTO `message` VALUES (162, 1, 0, '{\"text\":\"图片 1059 需要人工审核\"}', 1, 1, '2025-09-03 17:05:41', '2025-09-03 17:05:41', 0);
INSERT INTO `message` VALUES (163, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因: Sidifensen Blog\"}', 1, 1, '2025-09-03 17:05:48', '2025-09-03 17:05:48', 0);
INSERT INTO `message` VALUES (164, 1, 0, '{\"text\":\"文章 null 需要人工审核, 原因:  Sidifensen Blog\"}', 1, 1, '2025-09-03 17:07:31', '2025-09-03 17:07:31', 0);
INSERT INTO `message` VALUES (165, 1, 0, '{\"text\":\"图片 1060 需要人工审核\"}', 1, 1, '2025-09-03 17:14:04', '2025-09-03 17:14:04', 0);
INSERT INTO `message` VALUES (166, 1, 0, '{\"text\":\"文章 43 需要人工审核, 原因:  Sidifensen Blog2\"}', 1, 1, '2025-09-03 17:41:20', '2025-09-03 17:41:20', 0);
INSERT INTO `message` VALUES (167, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 内容审核不通过, 原因: null\"}', 1, 1, '2025-09-04 18:01:58', '2025-09-04 18:01:58', 0);
INSERT INTO `message` VALUES (168, 1, 0, '{\"text\":\"文章id 43 标题  Sidifensen Blog2 审核通过\"}', 1, 1, '2025-09-04 18:02:03', '2025-09-04 18:02:03', 0);
INSERT INTO `message` VALUES (169, 1, 0, '{\"text\":\"文章id 43 标题  Sidifensen Blog2 内容审核不通过, 原因: null\"}', 1, 1, '2025-09-04 18:02:05', '2025-09-04 18:02:05', 0);
INSERT INTO `message` VALUES (170, 1, 0, '{\"text\":\"文章id 43 标题  Sidifensen Blog2 审核通过\"}', 1, 1, '2025-09-04 18:02:07', '2025-09-04 18:02:07', 0);
INSERT INTO `message` VALUES (171, 1, 0, '{\"text\":\"文章id 38 标题  Sidifensen Blog 审核通过\"}', 1, 1, '2025-09-04 18:02:18', '2025-09-04 18:02:18', 0);
INSERT INTO `message` VALUES (172, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 审核通过\"}', 1, 1, '2025-09-04 18:03:10', '2025-09-04 18:03:10', 0);
INSERT INTO `message` VALUES (173, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 内容审核不通过, 原因: null\"}', 1, 1, '2025-09-04 18:06:04', '2025-09-04 18:06:04', 0);
INSERT INTO `message` VALUES (174, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 审核通过\"}', 1, 1, '2025-09-04 18:06:04', '2025-09-04 18:06:04', 0);
INSERT INTO `message` VALUES (175, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 审核通过\"}', 1, 1, '2025-09-04 18:06:16', '2025-09-04 18:06:16', 0);
INSERT INTO `message` VALUES (176, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 内容审核不通过, 原因: null\"}', 1, 1, '2025-09-04 18:06:36', '2025-09-04 18:06:36', 0);
INSERT INTO `message` VALUES (177, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 审核通过\"}', 1, 1, '2025-09-04 18:07:17', '2025-09-04 18:07:17', 0);
INSERT INTO `message` VALUES (178, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 审核通过\"}', 1, 1, '2025-09-04 21:12:32', '2025-09-04 21:12:32', 0);
INSERT INTO `message` VALUES (179, 1, 0, '{\"text\":\"文章id 39 标题  Sidifensen Blog 审核通过\"}', 1, 1, '2025-09-04 21:12:34', '2025-09-04 21:12:34', 0);
INSERT INTO `message` VALUES (180, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 审核通过\"}', 1, 1, '2025-09-04 22:02:46', '2025-09-04 22:02:46', 0);
INSERT INTO `message` VALUES (181, 1, 0, '{\"text\":\"文章id 35 标题  Sidifensen Blog 审核通过\"}', 1, 1, '2025-09-04 22:03:00', '2025-09-04 22:03:00', 0);
INSERT INTO `message` VALUES (182, 1, 0, '{\"text\":\"图片 1061 需要人工审核\"}', 1, 1, '2025-09-04 22:43:28', '2025-09-04 22:43:28', 0);
INSERT INTO `message` VALUES (183, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 fuck 1 内容审核不通过, 原因: 第 1 段文本审核不通过: 检测到违规内容: 辱骂内容(abuse), 关键词: fuck, 置信度: 99.91%; \"}', 1, 1, '2025-09-04 22:59:05', '2025-09-04 22:59:05', 0);
INSERT INTO `message` VALUES (184, 1, 0, '{\"text\":\"图片 1062 需要人工审核\"}', 1, 1, '2025-09-04 23:04:01', '2025-09-04 23:04:01', 0);
INSERT INTO `message` VALUES (185, 1, 0, '{\"text\":\"文章id 15 标题 15 内容审核不通过, 原因: null\"}', 1, 1, '2025-09-04 23:10:45', '2025-09-04 23:10:45', 0);
INSERT INTO `message` VALUES (186, 1, 0, '{\"text\":\"文章id 15 标题 15 审核通过\"}', 1, 1, '2025-09-04 23:27:30', '2025-09-04 23:27:30', 0);
INSERT INTO `message` VALUES (187, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 审核通过\"}', 1, 1, '2025-09-04 23:27:30', '2025-09-04 23:27:30', 0);
INSERT INTO `message` VALUES (188, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 内容审核不通过, 原因: null\"}', 1, 1, '2025-09-04 23:27:45', '2025-09-04 23:27:45', 0);
INSERT INTO `message` VALUES (189, 1, 0, '{\"text\":\"文章id 15 标题 15 内容审核不通过, 原因: null\"}', 1, 1, '2025-09-04 23:27:45', '2025-09-04 23:27:45', 0);
INSERT INTO `message` VALUES (190, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 内容审核不通过, 原因: null\"}', 1, 1, '2025-09-04 23:28:12', '2025-09-04 23:28:12', 0);
INSERT INTO `message` VALUES (191, 1, 0, '{\"text\":\"文章id 43 标题  Sidifensen Blog2 内容审核不通过, 原因: null\"}', 1, 1, '2025-09-04 23:28:12', '2025-09-04 23:28:12', 0);
INSERT INTO `message` VALUES (192, 1, 0, '{\"text\":\"文章id 42 标题  Sidifensen Blog1 内容审核不通过, 原因: null\"}', 1, 1, '2025-09-05 01:11:14', '2025-09-05 01:11:14', 0);
INSERT INTO `message` VALUES (193, 1, 0, '{\"text\":\"文章id 42 标题  Sidifensen Blog1 审核通过\"}', 1, 1, '2025-09-05 01:11:18', '2025-09-05 01:11:18', 0);
INSERT INTO `message` VALUES (194, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 审核通过\"}', 1, 1, '2025-09-05 01:11:19', '2025-09-05 01:11:19', 0);
INSERT INTO `message` VALUES (195, 1, 0, '{\"text\":\"图片 1063 需要人工审核\"}', 1, 1, '2025-09-10 15:04:35', '2025-09-10 15:04:35', 0);
INSERT INTO `message` VALUES (196, 1, 0, '{\"text\":\"图片 1064 需要人工审核\"}', 1, 1, '2025-09-10 15:11:59', '2025-09-10 15:11:59', 0);
INSERT INTO `message` VALUES (197, 1, 0, '{\"text\":\"文章 44 需要人工审核, 原因:  Sidifensen Blog3\"}', 1, 1, '2025-09-11 19:46:59', '2025-09-11 19:46:59', 0);
INSERT INTO `message` VALUES (198, 1, 0, '{\"text\":\"文章 44 需要人工审核, 原因:  Sidifensen Blog3\"}', 1, 1, '2025-09-11 19:47:12', '2025-09-11 19:47:12', 0);
INSERT INTO `message` VALUES (199, 1, 0, '{\"text\":\"文章id 15 标题 15 审核通过\"}', 1, 1, '2025-09-11 19:57:13', '2025-09-11 19:57:13', 0);
INSERT INTO `message` VALUES (200, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: 15\"}', 1, 1, '2025-09-11 21:10:55', '2025-09-11 21:10:55', 0);
INSERT INTO `message` VALUES (201, 1, 0, '{\"text\":\"文章id 15 标题 15 审核通过\"}', 1, 1, '2025-09-11 21:11:02', '2025-09-11 21:11:02', 0);
INSERT INTO `message` VALUES (202, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: 15\"}', 1, 1, '2025-09-11 21:12:34', '2025-09-11 21:12:34', 0);
INSERT INTO `message` VALUES (203, 1, 0, '{\"text\":\"文章id 15 标题 15 审核通过\"}', 1, 1, '2025-09-11 21:12:40', '2025-09-11 21:12:40', 0);
INSERT INTO `message` VALUES (204, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: 15\"}', 1, 1, '2025-09-11 21:13:52', '2025-09-11 21:13:52', 0);
INSERT INTO `message` VALUES (205, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: 15\"}', 1, 1, '2025-09-11 21:15:38', '2025-09-11 21:15:38', 0);
INSERT INTO `message` VALUES (206, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: 15\"}', 1, 1, '2025-09-11 21:17:09', '2025-09-11 21:17:09', 0);
INSERT INTO `message` VALUES (207, 1, 0, '{\"text\":\"文章id 15 标题 15 审核通过\"}', 1, 1, '2025-09-11 21:17:33', '2025-09-11 21:17:33', 0);
INSERT INTO `message` VALUES (208, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: 15\"}', 1, 1, '2025-09-11 21:23:17', '2025-09-11 21:23:17', 0);
INSERT INTO `message` VALUES (209, 1, 0, '{\"text\":\"文章id 15 标题 15 审核通过\"}', 1, 1, '2025-09-11 21:23:27', '2025-09-11 21:23:27', 0);
INSERT INTO `message` VALUES (210, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: 15\"}', 1, 1, '2025-09-11 21:31:20', '2025-09-11 21:31:20', 0);
INSERT INTO `message` VALUES (211, 1, 0, '{\"text\":\"文章id 15 标题 15 审核通过\"}', 1, 1, '2025-09-11 21:31:23', '2025-09-11 21:31:23', 0);
INSERT INTO `message` VALUES (212, 1, 0, '{\"text\":\"文章 15 需要人工审核, 原因: 15\"}', 1, 1, '2025-09-11 23:22:36', '2025-09-11 23:22:36', 0);
INSERT INTO `message` VALUES (213, 1, 0, '{\"text\":\"文章id 15 标题 15 审核通过\"}', 1, 1, '2025-09-11 23:22:59', '2025-09-11 23:22:59', 0);
INSERT INTO `message` VALUES (214, 1, 0, '{\"text\":\"文章 44 需要人工审核, 原因:  Sidifensen Blog3\"}', 1, 1, '2025-09-14 10:42:16', '2025-09-14 10:42:16', 0);
INSERT INTO `message` VALUES (215, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 审核通过\"}', 1, 1, '2025-09-14 10:42:26', '2025-09-14 10:42:26', 0);
INSERT INTO `message` VALUES (216, 1, 0, '{\"text\":\"文章 45 需要人工审核, 原因: 45\"}', 1, 1, '2025-09-14 20:43:25', '2025-09-14 20:43:25', 0);
INSERT INTO `message` VALUES (217, 1, 0, '{\"text\":\"文章 46 需要人工审核, 原因: 46\"}', 1, 1, '2025-09-14 20:43:32', '2025-09-14 20:43:32', 0);
INSERT INTO `message` VALUES (218, 1, 0, '{\"text\":\"文章 47 需要人工审核, 原因: 47\"}', 1, 1, '2025-09-14 20:43:36', '2025-09-14 20:43:36', 0);
INSERT INTO `message` VALUES (219, 1, 0, '{\"text\":\"文章 48 需要人工审核, 原因: 84\"}', 1, 1, '2025-09-14 20:43:38', '2025-09-14 20:43:38', 0);
INSERT INTO `message` VALUES (220, 1, 0, '{\"text\":\"文章 49 需要人工审核, 原因: 49\"}', 1, 1, '2025-09-14 20:43:44', '2025-09-14 20:43:44', 0);
INSERT INTO `message` VALUES (221, 1, 0, '{\"text\":\"文章 50 需要人工审核, 原因: 50\"}', 1, 1, '2025-09-14 20:43:50', '2025-09-14 20:43:50', 0);
INSERT INTO `message` VALUES (222, 1, 0, '{\"text\":\"文章 51 需要人工审核, 原因: 51\"}', 1, 1, '2025-09-14 20:43:53', '2025-09-14 20:43:53', 0);
INSERT INTO `message` VALUES (223, 1, 0, '{\"text\":\"文章 52 需要人工审核, 原因: 52\"}', 1, 1, '2025-09-14 20:43:57', '2025-09-14 20:43:57', 0);
INSERT INTO `message` VALUES (224, 1, 0, '{\"text\":\"文章 53 需要人工审核, 原因: 53\"}', 1, 1, '2025-09-14 20:44:02', '2025-09-14 20:44:02', 0);
INSERT INTO `message` VALUES (225, 1, 0, '{\"text\":\"文章 54 需要人工审核, 原因: 54\"}', 1, 1, '2025-09-14 20:44:06', '2025-09-14 20:44:06', 0);
INSERT INTO `message` VALUES (226, 1, 0, '{\"text\":\"文章 55 需要人工审核, 原因: 55\"}', 1, 1, '2025-09-14 20:44:32', '2025-09-14 20:44:32', 0);
INSERT INTO `message` VALUES (227, 1, 0, '{\"text\":\"文章 56 需要人工审核, 原因: 56\"}', 1, 1, '2025-09-14 20:44:35', '2025-09-14 20:44:35', 0);
INSERT INTO `message` VALUES (228, 1, 0, '{\"text\":\"文章 57 需要人工审核, 原因: 57\"}', 1, 1, '2025-09-14 20:44:38', '2025-09-14 20:44:38', 0);
INSERT INTO `message` VALUES (229, 1, 0, '{\"text\":\"文章 58 需要人工审核, 原因: 58\"}', 1, 1, '2025-09-14 20:44:41', '2025-09-14 20:44:41', 0);
INSERT INTO `message` VALUES (230, 1, 0, '{\"text\":\"文章 59 需要人工审核, 原因: 59\"}', 1, 1, '2025-09-14 20:44:44', '2025-09-14 20:44:44', 0);
INSERT INTO `message` VALUES (231, 1, 0, '{\"text\":\"文章 60 需要人工审核, 原因: 60\"}', 1, 1, '2025-09-14 20:44:47', '2025-09-14 20:44:47', 0);
INSERT INTO `message` VALUES (232, 1, 0, '{\"text\":\"文章 61 需要人工审核, 原因: 61\"}', 1, 1, '2025-09-14 20:44:53', '2025-09-14 20:44:53', 0);
INSERT INTO `message` VALUES (233, 1, 0, '{\"text\":\"文章 62 需要人工审核, 原因: 62\"}', 1, 1, '2025-09-14 20:44:55', '2025-09-14 20:44:55', 0);
INSERT INTO `message` VALUES (234, 1, 0, '{\"text\":\"文章 63 需要人工审核, 原因: 63\"}', 1, 1, '2025-09-14 20:44:58', '2025-09-14 20:44:58', 0);
INSERT INTO `message` VALUES (235, 1, 0, '{\"text\":\"文章 64 需要人工审核, 原因: 64\"}', 1, 1, '2025-09-14 20:45:01', '2025-09-14 20:45:01', 0);
INSERT INTO `message` VALUES (236, 1, 0, '{\"text\":\"文章 65 需要人工审核, 原因: 65\"}', 1, 1, '2025-09-14 20:45:04', '2025-09-14 20:45:04', 0);
INSERT INTO `message` VALUES (237, 1, 0, '{\"text\":\"文章 66 需要人工审核, 原因: 66\"}', 1, 1, '2025-09-14 20:45:06', '2025-09-14 20:45:06', 0);
INSERT INTO `message` VALUES (238, 1, 0, '{\"text\":\"文章 67 需要人工审核, 原因: 67\"}', 1, 1, '2025-09-14 20:45:09', '2025-09-14 20:45:09', 0);
INSERT INTO `message` VALUES (239, 1, 0, '{\"text\":\"文章 68 需要人工审核, 原因: 68\"}', 1, 1, '2025-09-14 20:45:12', '2025-09-14 20:45:12', 0);
INSERT INTO `message` VALUES (240, 1, 0, '{\"text\":\"文章 69 需要人工审核, 原因: 69\"}', 1, 1, '2025-09-14 20:45:14', '2025-09-14 20:45:14', 0);
INSERT INTO `message` VALUES (241, 1, 0, '{\"text\":\"文章 70 需要人工审核, 原因: 70\"}', 1, 1, '2025-09-14 20:45:18', '2025-09-14 20:45:18', 0);
INSERT INTO `message` VALUES (242, 1, 0, '{\"text\":\"文章id 70 标题 70 审核通过\"}', 1, 1, '2025-09-14 20:45:55', '2025-09-14 20:45:55', 0);
INSERT INTO `message` VALUES (243, 1, 0, '{\"text\":\"文章id 69 标题 69 审核通过\"}', 1, 1, '2025-09-14 20:45:55', '2025-09-14 20:45:55', 0);
INSERT INTO `message` VALUES (244, 1, 0, '{\"text\":\"文章id 68 标题 68 审核通过\"}', 1, 1, '2025-09-14 20:45:55', '2025-09-14 20:45:55', 0);
INSERT INTO `message` VALUES (245, 1, 0, '{\"text\":\"文章id 67 标题 67 审核通过\"}', 1, 1, '2025-09-14 20:45:55', '2025-09-14 20:45:55', 0);
INSERT INTO `message` VALUES (246, 1, 0, '{\"text\":\"文章id 66 标题 66 审核通过\"}', 1, 1, '2025-09-14 20:45:55', '2025-09-14 20:45:55', 0);
INSERT INTO `message` VALUES (247, 1, 0, '{\"text\":\"文章id 65 标题 65 审核通过\"}', 1, 1, '2025-09-14 20:45:55', '2025-09-14 20:45:55', 0);
INSERT INTO `message` VALUES (248, 1, 0, '{\"text\":\"文章id 64 标题 64 审核通过\"}', 1, 1, '2025-09-14 20:45:55', '2025-09-14 20:45:55', 0);
INSERT INTO `message` VALUES (249, 1, 0, '{\"text\":\"文章id 63 标题 63 审核通过\"}', 1, 1, '2025-09-14 20:45:55', '2025-09-14 20:45:55', 0);
INSERT INTO `message` VALUES (250, 1, 0, '{\"text\":\"文章id 62 标题 62 审核通过\"}', 1, 1, '2025-09-14 20:45:55', '2025-09-14 20:45:55', 0);
INSERT INTO `message` VALUES (251, 1, 0, '{\"text\":\"文章id 61 标题 61 审核通过\"}', 1, 1, '2025-09-14 20:45:55', '2025-09-14 20:45:55', 0);
INSERT INTO `message` VALUES (252, 1, 0, '{\"text\":\"文章id 60 标题 60 审核通过\"}', 1, 1, '2025-09-14 20:46:04', '2025-09-14 20:46:04', 0);
INSERT INTO `message` VALUES (253, 1, 0, '{\"text\":\"文章id 59 标题 59 审核通过\"}', 1, 1, '2025-09-14 20:46:04', '2025-09-14 20:46:04', 0);
INSERT INTO `message` VALUES (254, 1, 0, '{\"text\":\"文章id 58 标题 58 审核通过\"}', 1, 1, '2025-09-14 20:46:04', '2025-09-14 20:46:04', 0);
INSERT INTO `message` VALUES (255, 1, 0, '{\"text\":\"文章id 57 标题 57 审核通过\"}', 1, 1, '2025-09-14 20:46:04', '2025-09-14 20:46:04', 0);
INSERT INTO `message` VALUES (256, 1, 0, '{\"text\":\"文章id 56 标题 56 审核通过\"}', 1, 1, '2025-09-14 20:46:04', '2025-09-14 20:46:04', 0);
INSERT INTO `message` VALUES (257, 1, 0, '{\"text\":\"文章id 55 标题 55 审核通过\"}', 1, 1, '2025-09-14 20:46:04', '2025-09-14 20:46:04', 0);
INSERT INTO `message` VALUES (258, 1, 0, '{\"text\":\"文章id 54 标题 54 审核通过\"}', 1, 1, '2025-09-14 20:46:04', '2025-09-14 20:46:04', 0);
INSERT INTO `message` VALUES (259, 1, 0, '{\"text\":\"文章id 53 标题 53 审核通过\"}', 1, 1, '2025-09-14 20:46:04', '2025-09-14 20:46:04', 0);
INSERT INTO `message` VALUES (260, 1, 0, '{\"text\":\"文章id 52 标题 52 审核通过\"}', 1, 1, '2025-09-14 20:46:04', '2025-09-14 20:46:04', 0);
INSERT INTO `message` VALUES (261, 1, 0, '{\"text\":\"文章id 51 标题 51 审核通过\"}', 1, 1, '2025-09-14 20:46:04', '2025-09-14 20:46:04', 0);
INSERT INTO `message` VALUES (262, 1, 0, '{\"text\":\"文章id 50 标题 50 审核通过\"}', 1, 1, '2025-09-14 20:46:11', '2025-09-14 20:46:11', 0);
INSERT INTO `message` VALUES (263, 1, 0, '{\"text\":\"文章id 49 标题 49 审核通过\"}', 1, 1, '2025-09-14 20:46:11', '2025-09-14 20:46:11', 0);
INSERT INTO `message` VALUES (264, 1, 0, '{\"text\":\"文章id 48 标题 84 审核通过\"}', 1, 1, '2025-09-14 20:46:11', '2025-09-14 20:46:11', 0);
INSERT INTO `message` VALUES (265, 1, 0, '{\"text\":\"文章id 47 标题 47 审核通过\"}', 1, 1, '2025-09-14 20:46:11', '2025-09-14 20:46:11', 0);
INSERT INTO `message` VALUES (266, 1, 0, '{\"text\":\"文章id 46 标题 46 审核通过\"}', 1, 1, '2025-09-14 20:46:11', '2025-09-14 20:46:11', 0);
INSERT INTO `message` VALUES (267, 1, 0, '{\"text\":\"文章id 45 标题 45 审核通过\"}', 1, 1, '2025-09-14 20:46:11', '2025-09-14 20:46:11', 0);
INSERT INTO `message` VALUES (268, 1, 0, '{\"text\":\"文章id 44 标题  Sidifensen Blog3 审核通过\"}', 1, 1, '2025-09-14 20:46:11', '2025-09-14 20:46:11', 0);
INSERT INTO `message` VALUES (269, 1, 0, '{\"text\":\"文章id 15 标题 15 审核通过\"}', 1, 1, '2025-09-14 20:46:11', '2025-09-14 20:46:11', 0);
INSERT INTO `message` VALUES (270, 1, 0, '{\"text\":\"文章id 42 标题  Sidifensen Blog1 审核通过\"}', 1, 1, '2025-09-14 20:46:11', '2025-09-14 20:46:11', 0);
INSERT INTO `message` VALUES (271, 1, 0, '{\"text\":\"文章id 43 标题  Sidifensen Blog2 审核通过\"}', 1, 1, '2025-09-14 20:46:11', '2025-09-14 20:46:11', 0);
INSERT INTO `message` VALUES (272, 1, 0, '{\"text\":\"评论 45 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-16 13:53:10', '2025-09-16 13:53:10', 0);
INSERT INTO `message` VALUES (273, 1, 0, '{\"text\":\"评论 46 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-16 14:02:41', '2025-09-16 14:02:41', 0);
INSERT INTO `message` VALUES (274, 1, 0, '{\"text\":\"评论 47 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-16 14:04:12', '2025-09-16 14:04:12', 0);
INSERT INTO `message` VALUES (275, 1, 0, '{\"text\":\"评论 48 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-16 14:04:30', '2025-09-16 14:04:30', 0);
INSERT INTO `message` VALUES (276, 1, 0, '{\"text\":\"图片 1065 需要人工审核\"}', 1, 1, '2025-09-17 08:32:02', '2025-09-17 08:32:02', 0);
INSERT INTO `message` VALUES (277, 1, 0, '{\"text\":\"图片 1066 需要人工审核\"}', 1, 1, '2025-09-17 08:34:34', '2025-09-17 08:34:34', 0);
INSERT INTO `message` VALUES (278, 1, 0, '{\"text\":\"文章 71 需要人工审核, 原因: 测试文章\"}', 1, 1, '2025-09-17 08:34:39', '2025-09-17 08:34:39', 0);
INSERT INTO `message` VALUES (279, 1, 0, '{\"text\":\"文章id 71 标题 测试文章 审核通过\"}', 1, 1, '2025-09-17 08:40:22', '2025-09-17 08:40:22', 0);
INSERT INTO `message` VALUES (280, 1, 0, '{\"text\":\"评论 86 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-19 23:45:07', '2025-09-19 23:45:07', 0);
INSERT INTO `message` VALUES (281, 1, 0, '{\"text\":\"评论 87 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-20 00:49:39', '2025-09-20 00:49:39', 0);
INSERT INTO `message` VALUES (282, 1, 0, '{\"text\":\"评论 88 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-20 00:49:42', '2025-09-20 00:49:42', 0);
INSERT INTO `message` VALUES (283, 1, 0, '{\"text\":\"评论 89 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-20 10:42:39', '2025-09-20 10:42:39', 0);
INSERT INTO `message` VALUES (284, 1, 0, '{\"text\":\"评论 90 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-20 10:43:17', '2025-09-20 10:43:17', 0);
INSERT INTO `message` VALUES (285, 1, 0, '{\"text\":\"评论 91 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-20 10:45:05', '2025-09-20 10:45:05', 0);
INSERT INTO `message` VALUES (286, 1, 0, '{\"text\":\"评论 92 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-20 10:53:16', '2025-09-20 10:53:16', 0);
INSERT INTO `message` VALUES (287, 1, 0, '{\"text\":\"评论 93 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-20 10:53:59', '2025-09-20 10:53:59', 0);
INSERT INTO `message` VALUES (288, 1, 0, '{\"text\":\"评论 94 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-20 14:30:17', '2025-09-20 14:30:17', 0);
INSERT INTO `message` VALUES (289, 1, 0, '{\"text\":\"评论 95 需要人工审核, 原因: 未开启自动审核\"}', 1, 1, '2025-09-20 14:30:27', '2025-09-20 14:30:27', 0);
INSERT INTO `message` VALUES (290, 1, 0, '{\"text\":\"图片 1067 需要人工审核\"}', 1, 1, '2025-09-21 16:08:47', '2025-09-21 16:08:47', 0);
INSERT INTO `message` VALUES (291, 1, 0, '{\"text\":\"图片 1068 需要人工审核\"}', 1, 1, '2025-09-21 22:11:26', '2025-09-21 22:11:26', 0);
INSERT INTO `message` VALUES (292, 1, 0, '{\"text\":\"图片 1069 需要人工审核\"}', 1, 1, '2025-09-22 23:50:05', '2025-09-22 23:50:05', 0);
INSERT INTO `message` VALUES (293, 1, 0, '{\"text\":\"图片 1070 需要人工审核\"}', 1, 1, '2025-09-26 00:22:31', '2025-09-26 00:22:31', 0);
INSERT INTO `message` VALUES (294, 1, 0, '{\"text\":\"友链 3 名称 友链2 需要审核\"}', 1, 1, '2025-09-28 23:29:00', '2025-09-28 23:29:00', 0);
INSERT INTO `message` VALUES (295, 1, 0, '{\"text\":\"友链id: 4 , 名称: 友链友链友链友链友链友链友链友链友链友链 需要审核\"}', 1, 1, '2025-09-28 23:34:05', '2025-09-28 23:34:05', 0);
INSERT INTO `message` VALUES (296, 1, 0, '{\"text\":\"友链id: 5 , 名称: 友链友链友链友链友链友链友链友链友链友链 需要审核\"}', 1, 1, '2025-09-29 00:03:42', '2025-09-29 00:03:42', 0);
INSERT INTO `message` VALUES (297, 1, 0, '{\"text\":\"友链id: 26 , 名称: 友链123 需要审核\"}', 1, 1, '2025-09-29 13:56:59', '2025-09-29 13:56:59', 0);
INSERT INTO `message` VALUES (298, 1, 0, '{\"text\":\"图片id: 1071 需要人工审核\"}', 1, 1, '2025-10-01 15:35:26', '2025-10-01 15:35:26', 0);
INSERT INTO `message` VALUES (299, 0, 0, '{\"text\":\"图片id: 1072 需要人工审核\"}', 1, 1, '2025-10-09 09:06:02', '2025-10-09 09:06:02', 0);
INSERT INTO `message` VALUES (300, 0, 0, '{\"text\":\"图片id: 1073 需要人工审核\"}', 1, 1, '2025-10-09 09:14:50', '2025-10-09 09:14:50', 0);

-- ----------------------------
-- Table structure for photo
-- ----------------------------
DROP TABLE IF EXISTS `photo`;
CREATE TABLE `photo`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '图片id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片url',
  `examine_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0-待审核 1-审核通过 2-审核未通过',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_deleted_id_examine_status`(`is_deleted` ASC, `id` ASC, `examine_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of photo
-- ----------------------------
INSERT INTO `photo` VALUES (1, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/13/fd760317ef5745968a71200c65033c8b.webp', 1, '2025-08-15 21:35:34', '2025-08-15 21:35:36', 0);
INSERT INTO `photo` VALUES (2, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/2/d93fa7e492d542bf834cd08a553f9955.jpg', 1, '2025-08-15 23:38:47', '2025-08-15 23:38:48', 0);
INSERT INTO `photo` VALUES (3, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/2/91a312e5917142069d18b13e3e507ab7.webp', 1, '2025-08-15 23:47:18', '2025-08-15 23:47:20', 0);
INSERT INTO `photo` VALUES (4, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/2/e9ef1601e7fd477da8a8819fa7ab25f9.webp', 1, '2025-08-15 23:50:14', '2025-08-15 23:50:15', 0);
INSERT INTO `photo` VALUES (5, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/2/80e6bcc62397422bb9b6749d03ff5ab7.webp', 1, '2025-08-16 02:02:00', '2025-08-16 02:02:02', 0);
INSERT INTO `photo` VALUES (6, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/3/1d29f054b0f34070a6889dc49bf35ae3.webp', 1, '2025-08-17 00:24:04', '2025-08-17 00:24:05', 0);
INSERT INTO `photo` VALUES (7, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/2/444ce751eebc480e96bc2bd6e61254bb.webp', 1, '2025-08-17 01:37:44', '2025-08-17 01:37:45', 0);
INSERT INTO `photo` VALUES (8, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/4/4bed147d7d1e48e2a5cffe66e2c31a24.webp', 1, '2025-08-17 20:44:19', '2025-08-17 20:44:24', 0);
INSERT INTO `photo` VALUES (9, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/6/61d91658e974434186b382cd10955d82.webp', 1, '2025-08-17 21:53:44', '2025-08-17 21:53:46', 0);
INSERT INTO `photo` VALUES (10, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/7/651c690cbca74ecda4b794b2d8d21f81.webp', 1, '2025-08-18 00:40:50', '2025-08-18 00:40:52', 0);
INSERT INTO `photo` VALUES (11, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/8/253c190cf34e493096297e258fc25c42.webp', 1, '2025-08-18 00:42:15', '2025-08-18 00:42:16', 0);
INSERT INTO `photo` VALUES (12, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/9/7eb75aa3fe134277829afa0861bfe6f7.webp', 1, '2025-08-18 00:44:07', '2025-08-18 00:44:07', 0);
INSERT INTO `photo` VALUES (13, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/10/6df7d4da9241446a8471c4ddaee6860d.webp', 1, '2025-08-18 00:45:42', '2025-08-18 00:45:42', 0);
INSERT INTO `photo` VALUES (14, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/11/e1a22085ec6d4c2a8e9020c4766920e6.webp', 1, '2025-08-18 01:01:53', '2025-08-18 01:01:53', 0);
INSERT INTO `photo` VALUES (15, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/12/5636972df0e04b37b68c3268969e1d00.webp', 1, '2025-08-18 01:06:35', '2025-08-18 01:06:35', 0);
INSERT INTO `photo` VALUES (16, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/13/fd760317ef5745968a71200c65033c8b.webp', 1, '2025-08-18 01:32:10', '2025-08-18 01:32:10', 0);
INSERT INTO `photo` VALUES (17, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/2/658334308098465fbe66b5d4b3266ef9.webp', 1, '2025-08-18 01:37:47', '2025-08-18 01:37:47', 0);
INSERT INTO `photo` VALUES (18, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/1/c70f0f15ce8541a9820df4829e1274ed.webp', 1, '2025-08-21 00:14:07', '2025-08-21 00:14:07', 0);
INSERT INTO `photo` VALUES (19, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/1/e6f9dadfca504dd2b505734ad59c9e9b.webp', 1, '2025-08-21 00:19:39', '2025-08-21 00:19:39', 0);
INSERT INTO `photo` VALUES (20, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/1/75af8b4e0d5a40cfa4b3cbbb896c4160.webp', 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `photo` VALUES (21, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/1/d888b05f9a4345e7b49c59d569e851d3.webp', 1, '2025-08-21 00:30:12', '2025-08-21 00:30:12', 0);
INSERT INTO `photo` VALUES (22, 2, 'https://image.sidifensen.com/sidifensen-blog/album/2/14/8bde0bb1ec584c778bbf9bc230a0ceda.webp', 1, '2025-08-23 22:18:38', '2025-08-23 22:18:38', 0);
INSERT INTO `photo` VALUES (23, 2, 'https://image.sidifensen.com/sidifensen-blog/album/2/14/3ad10d18a3ff47d59e049211981b4ac9.webp', 1, '2025-08-23 22:27:07', '2025-08-23 22:27:07', 0);
INSERT INTO `photo` VALUES (24, 2, 'https://image.sidifensen.com/sidifensen-blog/album/2/14/cd176cf2d6864167abd79760ef223333.webp', 1, '2025-08-24 16:05:18', '2025-08-24 16:05:18', 0);
INSERT INTO `photo` VALUES (25, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/15/20987aa177e443d3b53aee3d9fc0d55d.webp', 1, '2025-08-26 21:41:04', '2025-08-26 21:41:04', 0);
INSERT INTO `photo` VALUES (26, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/16/41af469a92b94636a822e049b5bb4913.webp', 1, '2025-08-26 21:41:17', '2025-08-26 21:41:17', 0);
INSERT INTO `photo` VALUES (27, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/16/fbf2a30cea1b4a2fb5c519420abb9374.webp', 1, '2025-08-26 21:46:49', '2025-08-26 21:46:49', 0);
INSERT INTO `photo` VALUES (28, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/17/d8d5cca07d634a7580d76aa3195822ca.webp', 1, '2025-08-26 21:47:25', '2025-08-26 21:47:25', 0);
INSERT INTO `photo` VALUES (29, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/17/8077a7764d464ccdbeb18a4883ebb8ee.webp', 1, '2025-08-26 21:47:36', '2025-08-26 21:47:36', 0);
INSERT INTO `photo` VALUES (30, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/18/34ceaf19b2c14f088fe644e69b610812.webp', 1, '2025-08-26 21:47:56', '2025-08-26 21:47:56', 0);
INSERT INTO `photo` VALUES (31, 1, 'https://image.sidifensen.com/sidifensen-blog/album/1/19/267a024586df4aa18593c5099221e544.webp', 1, '2025-08-26 21:48:17', '2025-08-26 21:48:17', 0);
INSERT INTO `photo` VALUES (32, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/653b9d45916c477e965b5232d0264bd0.jpg', 1, '2025-08-29 11:25:10', '2025-08-29 11:25:10', 0);
INSERT INTO `photo` VALUES (33, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/a718f9f6a06246f29cde4b2591a19634.jpg', 1, '2025-08-29 14:15:31', '2025-08-29 14:15:31', 0);
INSERT INTO `photo` VALUES (34, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/df3dac165d99472c8df89b5df33a54f8.jpg', 1, '2025-08-29 14:16:14', '2025-08-29 14:16:14', 0);
INSERT INTO `photo` VALUES (35, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/16cfd6a49634443c896a3241030a3169.jpg', 1, '2025-08-29 14:21:14', '2025-08-29 14:21:14', 0);
INSERT INTO `photo` VALUES (36, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/3da96b67814842d3bd8cae0e517ac458.jpg', 1, '2025-08-29 14:27:22', '2025-08-29 14:27:22', 0);
INSERT INTO `photo` VALUES (37, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/8dd5d4a2b0a74e7ebd9001fa26675bd6.jpg', 1, '2025-08-29 14:33:17', '2025-08-29 14:33:17', 0);
INSERT INTO `photo` VALUES (38, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/7cb89614b735483b9644fa307e9ccbc9.jpg', 1, '2025-08-29 14:36:20', '2025-08-29 14:36:20', 0);
INSERT INTO `photo` VALUES (39, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/ba47b23e794d46cea0a767f28af41f3b.jpg', 1, '2025-08-29 14:37:47', '2025-08-29 14:37:47', 0);
INSERT INTO `photo` VALUES (40, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/4b9729fb121e43ceaf6c522330b3f565.jpg', 1, '2025-08-29 14:43:35', '2025-08-29 14:43:35', 0);
INSERT INTO `photo` VALUES (41, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/84467f00772d41dba9db3204caf675b1.jpg', 1, '2025-08-29 15:07:39', '2025-08-29 15:07:39', 0);
INSERT INTO `photo` VALUES (42, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/92b887c0b0974d05b2e733347f0355a1.jpg', 1, '2025-08-29 15:25:57', '2025-08-29 15:25:57', 0);
INSERT INTO `photo` VALUES (43, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/3c2bfc0c40f4428489c3df9cdabd1a3b.webp', 1, '2025-08-29 15:40:12', '2025-08-29 15:40:12', 0);
INSERT INTO `photo` VALUES (44, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/5c02be27899a47b9aa21283148eacfaf.webp', 1, '2025-08-29 15:48:53', '2025-08-29 15:48:53', 0);
INSERT INTO `photo` VALUES (45, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/e03e6f972ad94635957b743ebda2d389.webp', 1, '2025-08-30 15:06:27', '2025-08-30 15:06:27', 0);
INSERT INTO `photo` VALUES (46, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/10daaee589334204863eebcd3213c3a6.webp', 1, '2025-08-30 15:07:15', '2025-08-30 15:07:15', 0);
INSERT INTO `photo` VALUES (47, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/117f9f2a16e34e19849800dbe99bbea0.webp', 1, '2025-08-30 15:09:09', '2025-08-30 15:09:09', 0);
INSERT INTO `photo` VALUES (48, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/5b407324dfb2441794d88b39026ff33f.webp', 1, '2025-08-30 17:39:29', '2025-08-30 17:39:29', 0);
INSERT INTO `photo` VALUES (49, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/25c2f8fb52524838bc25963e3345907b.webp', 1, '2025-08-30 23:54:24', '2025-08-30 23:54:24', 0);
INSERT INTO `photo` VALUES (50, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/d1d46de1f84a48809bd4e76dd32b51d2.webp', 1, '2025-08-30 23:57:15', '2025-08-30 23:57:15', 0);
INSERT INTO `photo` VALUES (51, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/b086412fe72c448291fc16eedb251fd6.jpg', 1, '2025-08-31 01:54:44', '2025-08-31 01:54:44', 0);
INSERT INTO `photo` VALUES (52, 2, 'https://image.sidifensen.com/sidifensen-blog/article/2/add4b5d841924e8499b1230010ffeea9.jpg', 1, '2025-08-31 02:25:07', '2025-08-31 02:25:07', 0);
INSERT INTO `photo` VALUES (53, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/cb6db250feac40de8f43d34ed846f658.jpg', 1, '2025-08-31 16:09:01', '2025-08-31 16:09:01', 0);
INSERT INTO `photo` VALUES (54, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/e922e46e96ba41f3baca346e212b6ba6.jpg', 1, '2025-08-31 17:39:04', '2025-08-31 17:39:04', 0);
INSERT INTO `photo` VALUES (55, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/3dfb410027984c7eb61ba8e94e49290c.jpg', 1, '2025-08-31 23:18:32', '2025-08-31 23:18:32', 0);
INSERT INTO `photo` VALUES (56, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/f815a6ac779e4a45a1a9a72b375c59c8.jpg', 1, '2025-08-31 23:25:33', '2025-08-31 23:25:33', 0);
INSERT INTO `photo` VALUES (57, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/082048343f0442fcb3a515ad8048100e.jpg', 1, '2025-09-01 03:34:16', '2025-09-01 03:34:16', 0);
INSERT INTO `photo` VALUES (58, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/3e5f6e3d8c7b4c108ff5fc345c2d7473.webp', 1, '2025-09-01 21:10:06', '2025-09-01 21:10:06', 0);
INSERT INTO `photo` VALUES (59, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/94b9019155c74ba396dbbdc3d5afc68e.webp', 1, '2025-09-03 17:05:41', '2025-09-03 17:05:41', 0);
INSERT INTO `photo` VALUES (60, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/bba0dc7011694deca58e8c227ee2c213.webp', 1, '2025-09-03 17:14:04', '2025-09-03 17:14:04', 0);
INSERT INTO `photo` VALUES (61, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/b058688e7b7949f79e69c37b4c2d19bb.jpg', 1, '2025-09-04 22:43:28', '2025-09-04 22:43:28', 0);
INSERT INTO `photo` VALUES (62, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/cb7baa9e5628490b80c4f019a52c51ea.jpg', 1, '2025-09-04 23:04:01', '2025-09-04 23:04:01', 0);
INSERT INTO `photo` VALUES (63, 1, 'http://localhost:9000/sidifensen-blog/article/1/e609a624d54340179101b5a172d3d738.png', 1, '2025-09-10 15:04:35', '2025-09-10 15:04:35', 0);
INSERT INTO `photo` VALUES (64, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/97b0a3c6abca401591776ec349792d7c.png', 1, '2025-09-10 15:11:59', '2025-09-10 15:11:59', 0);
INSERT INTO `photo` VALUES (65, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/35e091cda96446dbb65f37075d6a6981.png', 1, '2025-09-17 08:32:02', '2025-09-17 08:32:02', 0);
INSERT INTO `photo` VALUES (66, 1, 'https://image.sidifensen.com/sidifensen-blog/article/1/e29abf1f62bb4004ae9f798e21c18c32.webp', 2, '2025-09-17 08:34:34', '2025-09-17 08:34:34', 0);
INSERT INTO `photo` VALUES (67, 1, 'http://115.190.116.72:40000/sidifensen-blog/article/1/9864b2af7658453f913598e19d6e8f10.jpg', 0, '2025-09-21 16:08:47', '2025-09-21 16:08:47', 0);
INSERT INTO `photo` VALUES (68, 1, 'http://115.190.116.72:40000/sidifensen-blog/column/1/8/481cad47d11f472a8af58fe86ac98154.jpg', 0, '2025-09-21 22:11:26', '2025-09-21 22:11:26', 0);
INSERT INTO `photo` VALUES (69, 1, 'http://115.190.116.72:40000/sidifensen-blog/column/1/84499e14d67f49d88435c026a8d81e6d.jpg', 0, '2025-09-22 23:50:05', '2025-09-22 23:50:05', 0);
INSERT INTO `photo` VALUES (70, 1, 'http://115.190.116.72:40000/sidifensen-blog/album/1/1/40243394761a40e6b40295eda2b55e66.webp', 1, '2025-09-26 00:22:31', '2025-09-26 00:22:31', 0);
INSERT INTO `photo` VALUES (71, 1, 'http://115.190.116.72:40000/sidifensen-blog/article/1/70f5c7d805e94de6a70807a02230604d.webp', 0, '2025-10-01 15:35:25', '2025-10-01 15:35:25', 0);
INSERT INTO `photo` VALUES (72, 2, 'http://115.190.116.72:40000/sidifensen-blog/article/2/79aed56ad3af4b05861d58240ebf1e2f.webp', 0, '2025-10-09 09:06:02', '2025-10-09 09:06:02', 0);
INSERT INTO `photo` VALUES (73, 2, 'https://image.sidifensen.com/sidifensen-blog/user/avatar/2/c514f8c7a08d4ef3a86557374ec3f8b0.webp', 0, '2025-10-09 09:14:50', '2025-10-09 09:14:50', 0);

-- ----------------------------
-- Table structure for sys_blacklist
-- ----------------------------
DROP TABLE IF EXISTS `sys_blacklist`;
CREATE TABLE `sys_blacklist`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '黑名单id',
  `type` tinyint NOT NULL COMMENT '黑名单类型 0-用户 1-ip地址',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip地址',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '拉黑原因',
  `ban_time` datetime NOT NULL COMMENT '拉黑时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '到期时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_expire`(`user_id` ASC, `expire_time` ASC) USING BTREE,
  INDEX `idx_ip_expire`(`ip` ASC, `expire_time` ASC) USING BTREE,
  INDEX `idx_type_ban_time`(`type` ASC, `ban_time` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_blacklist
-- ----------------------------
INSERT INTO `sys_blacklist` VALUES (6, 0, 2, NULL, '访问接口: ArticleController:getAllArticleList, 访问次数: 30次, 轻度违规,封禁1小时', '2025-10-03 15:37:18', '2025-10-03 16:37:18', '2025-10-03 15:37:18', '2025-10-03 15:37:18', 0);
INSERT INTO `sys_blacklist` VALUES (7, 0, 3, NULL, '违禁', '2025-10-03 22:30:12', '2025-10-04 00:00:00', '2025-10-03 22:30:12', '2025-10-03 22:41:35', 1);
INSERT INTO `sys_blacklist` VALUES (8, 0, 4, NULL, '违禁', '2025-10-03 22:30:12', '2025-10-04 00:00:00', '2025-10-03 22:30:12', '2025-10-03 22:41:37', 1);
INSERT INTO `sys_blacklist` VALUES (9, 0, 3, NULL, '封禁', '2025-10-03 22:42:09', '2025-10-03 00:00:00', '2025-10-03 22:42:09', '2025-10-03 22:42:20', 1);
INSERT INTO `sys_blacklist` VALUES (10, 0, 4, NULL, '封禁', '2025-10-03 22:42:09', '2025-10-03 00:00:00', '2025-10-03 22:42:09', '2025-10-03 22:42:20', 1);
INSERT INTO `sys_blacklist` VALUES (11, 0, 5, NULL, '封禁', '2025-10-03 22:42:09', '2025-10-03 00:00:00', '2025-10-03 22:42:09', '2025-10-03 22:42:20', 1);
INSERT INTO `sys_blacklist` VALUES (12, 0, 3, NULL, '拉黑', '2025-10-03 22:42:44', '2025-10-04 00:00:00', '2025-10-03 22:42:44', '2025-10-03 22:42:44', 0);
INSERT INTO `sys_blacklist` VALUES (13, 0, 4, NULL, '拉黑', '2025-10-03 22:42:44', '2025-10-04 00:00:00', '2025-10-03 22:42:44', '2025-10-03 22:42:44', 0);
INSERT INTO `sys_blacklist` VALUES (14, 0, 5, NULL, '拉黑', '2025-10-03 22:42:44', '2025-10-04 00:00:00', '2025-10-03 22:42:44', '2025-10-03 22:42:44', 0);
INSERT INTO `sys_blacklist` VALUES (15, 0, 2, NULL, '访问接口: SysUserController:info, 访问次数: 30次, 轻度违规,封禁1小时', '2025-10-06 15:12:19', '2025-10-06 16:12:19', '2025-10-06 15:12:19', '2025-10-06 15:12:19', 0);
INSERT INTO `sys_blacklist` VALUES (16, 0, 2, NULL, '访问接口: ArticleController:getAllArticleList, 访问次数: 30次, 轻度违规,封禁1小时', '2025-10-06 15:12:19', '2025-10-06 16:12:19', '2025-10-06 15:12:19', '2025-10-06 15:12:19', 0);

-- ----------------------------
-- Table structure for sys_loginlog
-- ----------------------------
DROP TABLE IF EXISTS `sys_loginlog`;
CREATE TABLE `sys_loginlog`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '登录日志id',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `login_type` tinyint NULL DEFAULT 0 COMMENT '登录方式 0-用户名/邮箱 1-gitee 2-github 3-QQ',
  `login_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录ip',
  `login_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录地址',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '登录状态 0-成功 1-失败',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_loginlog
-- ----------------------------
INSERT INTO `sys_loginlog` VALUES (1, 2, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 0, '2025-10-06 19:34:41');
INSERT INTO `sys_loginlog` VALUES (2, 2, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 0, '2025-10-06 19:35:04');
INSERT INTO `sys_loginlog` VALUES (3, 2, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 0, '2025-10-06 19:35:12');
INSERT INTO `sys_loginlog` VALUES (4, NULL, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 1, '2025-10-06 19:35:14');
INSERT INTO `sys_loginlog` VALUES (5, NULL, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 1, '2025-10-06 19:35:25');
INSERT INTO `sys_loginlog` VALUES (6, NULL, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 1, '2025-10-06 19:35:30');
INSERT INTO `sys_loginlog` VALUES (7, NULL, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 1, '2025-10-06 19:35:42');
INSERT INTO `sys_loginlog` VALUES (8, 2, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 0, '2025-10-06 19:35:48');
INSERT INTO `sys_loginlog` VALUES (9, NULL, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 1, '2025-10-06 19:35:50');
INSERT INTO `sys_loginlog` VALUES (10, 1, 'sidifensen', 0, '127.0.0.1', '中国-江西-九江-濂溪', 0, '2025-10-06 19:36:05');
INSERT INTO `sys_loginlog` VALUES (11, 2, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 0, '2025-10-06 19:36:26');
INSERT INTO `sys_loginlog` VALUES (12, 1, 'sidifensen', 0, '127.0.0.1', '中国-江西-九江-濂溪', 0, '2025-10-07 11:13:14');
INSERT INTO `sys_loginlog` VALUES (13, 2, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 0, '2025-10-07 14:05:30');
INSERT INTO `sys_loginlog` VALUES (14, 1, 'sidifensen', 0, '192.168.31.229', '中国-江西-九江-濂溪', 0, '2025-10-07 14:20:53');
INSERT INTO `sys_loginlog` VALUES (15, 1, 'sidifensen', 0, '192.168.31.220', '中国-江西-九江-濂溪', 0, '2025-10-07 15:21:59');
INSERT INTO `sys_loginlog` VALUES (16, NULL, 'sidifensen', 0, '192.168.31.220', '中国-江西-九江-濂溪', 1, '2025-10-08 15:37:53');
INSERT INTO `sys_loginlog` VALUES (17, 1, 'sidifensen', 0, '192.168.31.220', '中国-江西-九江-濂溪', 0, '2025-10-08 15:37:55');
INSERT INTO `sys_loginlog` VALUES (18, 2, 'youyu', 0, '127.0.0.1', '中国-江西-九江-濂溪', 0, '2025-10-09 08:52:31');
INSERT INTO `sys_loginlog` VALUES (19, 2, 'test', 0, '127.0.0.1', '中国-江西-九江-濂溪', 0, '2025-10-09 09:05:49');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '菜单id',
  `parent_id` int NULL DEFAULT 0 COMMENT '父级id',
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '首页', 0, '/home', '/home', 'House', 0, '2025-06-28 22:31:52', '2025-08-12 00:34:19', 0);
INSERT INTO `sys_menu` VALUES (2, 0, '系统管理', 1, '/system', ' ', 'Setting', 0, '2025-08-06 15:26:06', '2025-08-11 01:21:58', 0);
INSERT INTO `sys_menu` VALUES (3, 2, '菜单管理', 0, '/system/menu', '/system/menu', 'Menu', 0, '2025-08-06 15:40:22', '2025-08-11 01:22:00', 0);
INSERT INTO `sys_menu` VALUES (4, 2, '角色管理', 2, '/system/role', '/system/role', 'Avatar', 0, '2025-08-06 15:41:35', '2025-08-11 01:21:52', 0);
INSERT INTO `sys_menu` VALUES (5, 2, '权限管理', 1, '/system/permission', '/system/permission', 'Lock', 0, '2025-08-06 15:42:21', '2025-08-11 01:22:03', 0);
INSERT INTO `sys_menu` VALUES (6, 2, '用户管理', 3, '/system/user', '/system/user', 'User', 0, '2025-08-06 15:43:41', '2025-08-11 01:21:50', 0);
INSERT INTO `sys_menu` VALUES (7, 0, '图片管理', 3, '/photo', ' ', 'Camera', 0, '2025-08-08 16:17:18', '2025-08-15 00:24:54', 0);
INSERT INTO `sys_menu` VALUES (8, 7, '用户相册', 0, '/photo/album', '/photo/album', 'Picture', 0, '2025-08-14 11:41:12', '2025-08-15 00:25:18', 0);
INSERT INTO `sys_menu` VALUES (9, 7, '图片审核', 1, '/photo/examine', '/photo/examine', 'PictureRounded', 0, '2025-08-14 11:42:12', '2025-08-15 00:25:35', 0);
INSERT INTO `sys_menu` VALUES (10, 0, '文章管理', 4, '/article', '/article', 'Document', 0, '2025-08-08 16:17:43', '2025-08-29 11:42:39', 0);
INSERT INTO `sys_menu` VALUES (11, 10, '用户文章', 0, '/article/user', '/article/user', 'DocumentCopy', 0, '2025-08-29 14:02:23', '2025-08-29 14:05:12', 0);
INSERT INTO `sys_menu` VALUES (12, 10, '文章审核', 1, '/article/examine', '/article/examine', 'DocumentChecked', 0, '2025-08-29 14:04:49', '2025-08-29 14:05:25', 0);
INSERT INTO `sys_menu` VALUES (13, 0, '评论管理', 4, '/comment', '/comment', 'ChatDotRound', 0, '2025-09-16 23:36:25', '2025-09-16 23:36:50', 0);
INSERT INTO `sys_menu` VALUES (14, 13, '用户评论', 0, '/comment/user', '/comment/user', 'UserFilled', 0, '2025-09-16 23:37:50', '2025-09-16 23:37:50', 0);
INSERT INTO `sys_menu` VALUES (15, 13, '评论审核', 1, '/comment/examine', '/comment/examine', 'Comment', 0, '2025-09-16 23:38:31', '2025-09-16 23:38:31', 0);
INSERT INTO `sys_menu` VALUES (16, 0, '专栏管理', 5, '/column', '/column', 'Collection', 0, '2025-09-21 20:15:57', '2025-09-21 20:15:57', 0);
INSERT INTO `sys_menu` VALUES (17, 16, '用户专栏', 0, '/column/user', '/column/user', 'Folder', 0, '2025-09-21 20:16:01', '2025-09-21 20:16:01', 0);
INSERT INTO `sys_menu` VALUES (18, 16, '专栏审核', 1, '/column/examine', '/column/examine', 'FolderChecked', 0, '2025-09-21 20:16:04', '2025-09-21 20:16:04', 0);
INSERT INTO `sys_menu` VALUES (19, 0, '友链管理', 6, '/link', '/link', 'Link', 0, '2025-09-28 16:14:52', '2025-09-28 16:14:52', 0);
INSERT INTO `sys_menu` VALUES (20, 2, '黑名单管理', 4, '/system/backlist', '/system/backlist', 'CircleClose', 0, '2025-10-03 15:07:23', '2025-10-08 18:09:02', 0);
INSERT INTO `sys_menu` VALUES (21, 0, '标签管理', 8, '/tag', '/tag', 'CollectionTag', 0, '2025-10-05 13:20:54', '2025-10-05 13:20:54', 0);
INSERT INTO `sys_menu` VALUES (22, 2, '登录日志', 5, '/system/loginLog', '/system/loginLog', 'Calendar', 0, '2025-10-06 19:31:12', '2025-10-08 18:09:05', 0);
INSERT INTO `sys_menu` VALUES (23, 2, '访客日志', 6, '/system/visitorLog', '/system/visitorLog', 'DataAnalysis', 0, '2025-10-07 11:15:51', '2025-10-08 18:09:08', 0);

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限id',
  `description` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限描述',
  `permission` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限',
  `menu_id` int NOT NULL COMMENT '菜单id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, '获取用户菜单', 'system:menu:list', 3, '2025-08-06 15:57:43', '2025-08-06 15:57:45', 0);
INSERT INTO `sys_permission` VALUES (2, '获取菜单列表', 'system:menu:listAll', 3, '2025-08-08 20:27:02', '2025-08-08 20:27:06', 0);
INSERT INTO `sys_permission` VALUES (3, '新增菜单', 'system:menu:add', 3, '2025-08-06 16:20:13', '2025-08-06 16:20:16', 0);
INSERT INTO `sys_permission` VALUES (4, '修改菜单', 'system:menu:update', 3, '2025-08-06 16:20:13', '2025-08-06 16:20:16', 0);
INSERT INTO `sys_permission` VALUES (5, '删除菜单', 'system:menu:delete', 3, '2025-08-06 16:20:13', '2025-08-06 16:20:16', 0);
INSERT INTO `sys_permission` VALUES (6, '搜索菜单', 'system:menu:search', 3, '2025-08-08 03:06:41', '2025-08-08 03:06:43', 0);
INSERT INTO `sys_permission` VALUES (7, '获取角色列表', 'system:role:list', 4, '2025-08-06 15:57:43', '2025-08-06 15:57:45', 0);
INSERT INTO `sys_permission` VALUES (8, '新增角色', 'system:role:add', 4, '2025-08-06 16:20:13', '2025-08-06 16:20:16', 0);
INSERT INTO `sys_permission` VALUES (9, '修改角色', 'system:role:update', 4, '2025-08-06 16:20:13', '2025-08-06 16:20:16', 0);
INSERT INTO `sys_permission` VALUES (10, '删除角色', 'system:role:delete', 4, '2025-08-06 16:20:13', '2025-08-06 16:20:16', 0);
INSERT INTO `sys_permission` VALUES (11, '搜索角色', 'system:role:search', 4, '2025-08-08 03:06:41', '2025-08-08 03:06:43', 0);
INSERT INTO `sys_permission` VALUES (12, '菜单分配角色', 'system:role:menu:add', 3, '2025-08-09 23:42:07', '2025-08-09 23:42:09', 0);
INSERT INTO `sys_permission` VALUES (13, '获取菜单所属角色', 'system:role:menu:get', 3, '2025-08-10 00:01:37', '2025-08-10 00:01:40', 0);
INSERT INTO `sys_permission` VALUES (14, '角色分配用户', 'system:user:role:addUser', 4, '2025-08-10 11:36:31', '2025-08-10 11:36:34', 0);
INSERT INTO `sys_permission` VALUES (15, '获取角色所属用户', 'system:user:role:getUsers', 4, '2025-08-10 11:38:00', '2025-08-10 11:38:02', 0);
INSERT INTO `sys_permission` VALUES (16, '获取用户列表', 'system:user:list', 6, '2025-08-10 12:32:02', '2025-08-10 12:32:06', 0);
INSERT INTO `sys_permission` VALUES (17, '用户添加角色', 'system:user:role:addRole', 6, '2025-08-10 15:33:16', '2025-08-10 15:33:19', 0);
INSERT INTO `sys_permission` VALUES (18, '获取用户所属角色', 'system:user:role:getRoles', 6, '2025-08-10 15:33:58', '2025-08-10 15:34:01', 0);
INSERT INTO `sys_permission` VALUES (19, '获取权限列表', 'system:permission:list', 5, '2025-08-10 16:56:44', '2025-08-10 16:56:47', 0);
INSERT INTO `sys_permission` VALUES (20, '新增权限', 'system:permission:add', 5, '2025-08-10 16:57:55', '2025-08-10 16:57:57', 0);
INSERT INTO `sys_permission` VALUES (21, '修改权限', 'system:permission:update', 5, '2025-08-10 16:58:13', '2025-08-10 16:58:15', 0);
INSERT INTO `sys_permission` VALUES (22, '删除权限', 'system:permission:delete', 5, '2025-08-10 16:58:26', '2025-08-10 16:58:29', 0);
INSERT INTO `sys_permission` VALUES (23, '搜索权限', 'system:permission:search', 5, '2025-08-10 16:58:55', '2025-08-10 16:58:57', 0);
INSERT INTO `sys_permission` VALUES (24, '权限授权角色', 'system:role:permission:add', 5, '2025-08-10 17:10:12', '2025-08-10 17:10:15', 0);
INSERT INTO `sys_permission` VALUES (25, '获取权限所属角色', 'system:role:permission:get', 5, '2025-08-10 17:10:55', '2025-08-10 17:10:57', 0);
INSERT INTO `sys_permission` VALUES (26, '权限批量授权角色', 'system:role:permission:addBatch', 5, '2025-08-11 02:22:50', '2025-08-11 02:22:52', 0);
INSERT INTO `sys_permission` VALUES (27, '获取用户列表', 'system:user:list', 6, '2025-08-11 10:59:07', '2025-08-11 10:59:07', 0);
INSERT INTO `sys_permission` VALUES (28, '修改用户', 'system:user:update', 6, '2025-08-11 13:27:07', '2025-08-11 13:27:07', 0);
INSERT INTO `sys_permission` VALUES (29, '删除用户', 'system:user:delete', 6, '2025-08-11 13:27:29', '2025-08-11 13:27:29', 0);
INSERT INTO `sys_permission` VALUES (30, '搜索用户', 'system:user:search', 6, '2025-08-11 17:29:26', '2025-08-11 17:29:26', 0);
INSERT INTO `sys_permission` VALUES (31, '获取用户详情', 'system:user:info', 6, '2025-08-11 18:18:41', '2025-08-11 18:18:41', 0);
INSERT INTO `sys_permission` VALUES (32, '获取相册列表', 'album:list', 8, '2025-08-15 23:42:33', '2025-08-15 23:42:33', 0);
INSERT INTO `sys_permission` VALUES (33, '更新相册', 'album:update', 8, '2025-08-15 23:45:13', '2025-08-15 23:45:13', 0);
INSERT INTO `sys_permission` VALUES (34, '删除相册', 'album:delete', 8, '2025-08-15 23:45:46', '2025-08-15 23:45:46', 0);
INSERT INTO `sys_permission` VALUES (35, '搜索相册', 'album:search', 8, '2025-08-16 16:58:29', '2025-08-16 16:58:29', 0);
INSERT INTO `sys_permission` VALUES (36, '获取相册详情', 'album:detail', 8, '2025-08-16 23:49:45', '2025-08-16 23:49:45', 0);
INSERT INTO `sys_permission` VALUES (37, '删除图片', 'photo:delete', 8, '2025-08-17 00:30:04', '2025-08-17 00:30:04', 0);
INSERT INTO `sys_permission` VALUES (38, '批量删除图片', 'photo:deleteBatch', 8, '2025-08-17 00:30:15', '2025-08-17 00:30:15', 0);
INSERT INTO `sys_permission` VALUES (39, '审核图片', 'photo:audit', 9, '2025-08-17 00:39:51', '2025-08-17 00:39:51', 0);
INSERT INTO `sys_permission` VALUES (40, '批量审核图片', 'photo:auditBatch', 9, '2025-08-17 01:45:58', '2025-08-17 01:45:58', 0);
INSERT INTO `sys_permission` VALUES (41, '获取图片列表', 'photo:list', 9, '2025-08-17 03:10:00', '2025-08-17 03:10:00', 0);
INSERT INTO `sys_permission` VALUES (42, '搜索图片', 'photo:search', 9, '2025-08-17 15:19:06', '2025-08-17 15:19:06', 0);
INSERT INTO `sys_permission` VALUES (43, '获取未读消息数量', 'message:count', 1, '2025-08-18 02:01:25', '2025-08-21 00:17:46', 0);
INSERT INTO `sys_permission` VALUES (44, '获取消息列表', 'message:list', 1, '2025-08-18 02:01:40', '2025-08-18 02:01:40', 0);
INSERT INTO `sys_permission` VALUES (45, '读取消息', 'message:read', 1, '2025-08-21 00:15:34', '2025-08-21 00:17:28', 0);
INSERT INTO `sys_permission` VALUES (46, '删除消息', 'message:delete', 1, '2025-08-21 02:36:08', '2025-08-21 02:36:08', 0);
INSERT INTO `sys_permission` VALUES (47, '获取文章列表', 'article:list', 11, '2025-09-03 19:40:08', '2025-09-03 19:40:08', 0);
INSERT INTO `sys_permission` VALUES (48, '获取文章详情', 'article:get', 11, '2025-09-03 19:43:16', '2025-09-03 19:43:16', 0);
INSERT INTO `sys_permission` VALUES (49, '修改文章', 'article:update', 11, '2025-09-03 19:44:41', '2025-09-03 19:44:41', 0);
INSERT INTO `sys_permission` VALUES (50, '删除文章', 'article:delete', 11, '2025-09-03 19:45:01', '2025-09-03 19:45:01', 0);
INSERT INTO `sys_permission` VALUES (51, '审核文章', 'article:examine', 12, '2025-09-03 19:45:25', '2025-09-03 19:45:25', 0);
INSERT INTO `sys_permission` VALUES (52, '搜索文章', 'article:search', 12, '2025-09-04 14:09:16', '2025-09-04 14:09:16', 0);
INSERT INTO `sys_permission` VALUES (53, '获取用户文章列表', 'article:user:list', 11, '2025-09-04 15:47:30', '2025-09-04 15:47:30', 0);
INSERT INTO `sys_permission` VALUES (54, '获取评论列表', 'comment:list', 15, '2025-09-19 22:20:08', '2025-09-19 22:20:08', 0);
INSERT INTO `sys_permission` VALUES (55, '搜索评论', 'comment:search', 15, '2025-09-19 22:20:52', '2025-09-19 22:20:52', 0);
INSERT INTO `sys_permission` VALUES (56, '审核评论', 'comment:examine', 15, '2025-09-19 22:21:20', '2025-09-19 22:50:25', 0);
INSERT INTO `sys_permission` VALUES (57, '删除评论', 'comment:delete', 15, '2025-09-19 22:21:37', '2025-09-19 22:21:37', 0);
INSERT INTO `sys_permission` VALUES (58, '获取用户评论列表', 'comment:user:list', 14, '2025-09-19 22:22:09', '2025-09-19 22:22:09', 0);
INSERT INTO `sys_permission` VALUES (59, '获取专栏列表', 'column:list', 17, '2025-09-21 20:16:17', '2025-09-21 20:16:17', 0);
INSERT INTO `sys_permission` VALUES (60, '获取用户专栏列表', 'column:user:list', 17, '2025-09-21 20:16:21', '2025-09-21 20:16:21', 0);
INSERT INTO `sys_permission` VALUES (61, '搜索专栏', 'column:search', 17, '2025-09-21 20:16:25', '2025-09-21 20:16:25', 0);
INSERT INTO `sys_permission` VALUES (62, '审核专栏', 'column:examine', 18, '2025-09-21 20:16:29', '2025-09-21 20:16:29', 0);
INSERT INTO `sys_permission` VALUES (63, '删除专栏', 'column:delete', 17, '2025-09-21 20:16:33', '2025-09-21 20:16:33', 0);
INSERT INTO `sys_permission` VALUES (64, '更新专栏', 'column:update', 17, '2025-09-23 18:47:16', '2025-09-23 18:47:16', 0);
INSERT INTO `sys_permission` VALUES (65, '获取专栏详情', 'column:detail', 17, '2025-09-24 08:40:20', '2025-09-24 08:40:20', 0);
INSERT INTO `sys_permission` VALUES (66, '获取友链列表', 'link:list', 19, '2025-09-29 10:26:43', '2025-09-29 10:26:43', 0);
INSERT INTO `sys_permission` VALUES (67, '搜索友链', 'link:search', 19, '2025-09-29 10:29:44', '2025-09-29 10:29:44', 0);
INSERT INTO `sys_permission` VALUES (68, '审核友链', 'link:examine', 19, '2025-09-29 10:30:16', '2025-09-29 10:30:16', 0);
INSERT INTO `sys_permission` VALUES (69, '删除友链', 'link:delete', 19, '2025-09-29 10:30:55', '2025-09-29 10:30:55', 0);
INSERT INTO `sys_permission` VALUES (70, '获取黑名单列表', 'system:blacklist:list', 20, '2025-10-03 15:32:08', '2025-10-03 15:32:08', 0);
INSERT INTO `sys_permission` VALUES (71, '新增黑名单', 'system:blacklist:add', 20, '2025-10-03 15:27:03', '2025-10-03 15:27:03', 0);
INSERT INTO `sys_permission` VALUES (72, '搜索黑名单', 'system:blacklist:search', 20, '2025-10-03 15:27:25', '2025-10-03 15:27:25', 0);
INSERT INTO `sys_permission` VALUES (73, '更新黑名单', 'system:blacklist:update', 20, '2025-10-03 15:27:43', '2025-10-03 15:27:43', 0);
INSERT INTO `sys_permission` VALUES (74, '删除黑名单', 'system:blacklist:delete', 20, '2025-10-03 15:27:58', '2025-10-03 15:27:58', 0);
INSERT INTO `sys_permission` VALUES (75, '新增标签', 'tag:add', 21, '2025-10-06 14:03:48', '2025-10-06 14:03:48', 0);
INSERT INTO `sys_permission` VALUES (76, '修改标签', 'tag:update', 21, '2025-10-06 14:05:59', '2025-10-06 14:42:21', 0);
INSERT INTO `sys_permission` VALUES (77, '删除标签', 'tag:delete', 21, '2025-10-06 14:42:31', '2025-10-06 14:42:31', 0);
INSERT INTO `sys_permission` VALUES (78, '获取登录日志', 'system:loginLog:list', 22, '2025-10-06 19:32:18', '2025-10-06 19:33:24', 0);
INSERT INTO `sys_permission` VALUES (79, '搜索日志', 'system:loginLog:search', 22, '2025-10-06 19:32:39', '2025-10-06 19:32:39', 0);
INSERT INTO `sys_permission` VALUES (80, '删除登录日志', 'system:loginLog:delete', 22, '2025-10-06 19:33:39', '2025-10-06 19:33:39', 0);
INSERT INTO `sys_permission` VALUES (81, '获取访客列表', 'system:visitorLog:list', 23, '2025-10-07 11:14:35', '2025-10-07 11:16:07', 0);
INSERT INTO `sys_permission` VALUES (82, '搜索访客日志', 'system:visitorLog:search', 23, '2025-10-07 15:46:53', '2025-10-07 15:47:16', 0);
INSERT INTO `sys_permission` VALUES (83, '删除访客日志', 'system:visitorLog:delete', 23, '2025-10-07 15:47:11', '2025-10-07 15:47:11', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色标识',
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `description` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'admin', '超级管理员', '拥有最高管理权限', 0, '2025-06-28 22:31:00', '2025-08-10 00:21:12', 0);
INSERT INTO `sys_role` VALUES (2, 'viewer', '管理后台查看者', '可以查看部分后台信息,但是不能进行操作', 0, '2025-08-08 15:51:55', '2025-08-10 03:06:55', 0);
INSERT INTO `sys_role` VALUES (3, 'user', '网站普通用户', '博客网站普通用户', 0, '2025-08-08 15:52:58', '2025-08-09 22:22:07', 0);
INSERT INTO `sys_role` VALUES (4, 'vip', '用户', '用户', 0, '2025-08-09 13:18:12', '2025-08-09 22:22:10', 0);
INSERT INTO `sys_role` VALUES (5, 'vip', '用户', '用户', 0, '2025-08-09 13:19:25', '2025-08-09 22:22:11', 0);
INSERT INTO `sys_role` VALUES (6, 'vip', '用户', '用户', 0, '2025-08-09 13:19:26', '2025-08-09 22:22:12', 0);
INSERT INTO `sys_role` VALUES (7, 'vip', '用户', '用户', 0, '2025-08-09 13:19:26', '2025-08-09 13:19:26', 0);
INSERT INTO `sys_role` VALUES (8, 'vip', '用户', '用户', 0, '2025-08-09 13:19:26', '2025-08-09 13:19:26', 0);
INSERT INTO `sys_role` VALUES (9, 'vip', '用户', '用户', 0, '2025-08-09 13:19:26', '2025-08-09 13:19:26', 0);
INSERT INTO `sys_role` VALUES (10, 'vip1', '用户1', '用户1', 0, '2025-08-09 13:19:26', '2025-08-10 00:40:40', 0);
INSERT INTO `sys_role` VALUES (11, 'vip', '用户', '用户', 0, '2025-08-09 13:19:26', '2025-08-09 13:19:26', 0);
INSERT INTO `sys_role` VALUES (12, 'vip', '用户', '用户', 0, '2025-08-09 13:19:26', '2025-08-09 13:19:26', 0);
INSERT INTO `sys_role` VALUES (13, 'vip', '用户', '用户', 0, '2025-08-09 18:47:40', '2025-08-10 00:42:58', 0);
INSERT INTO `sys_role` VALUES (14, 'vip', '用户', '用户', 0, '2025-08-10 00:40:32', '2025-08-10 15:55:39', 1);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_id` int NOT NULL COMMENT '角色id',
  `menu_id` int NOT NULL COMMENT '菜单id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 1);
INSERT INTO `sys_role_menu` VALUES (2, 1, 2);
INSERT INTO `sys_role_menu` VALUES (3, 1, 3);
INSERT INTO `sys_role_menu` VALUES (4, 1, 4);
INSERT INTO `sys_role_menu` VALUES (5, 1, 5);
INSERT INTO `sys_role_menu` VALUES (7, 2, 1);
INSERT INTO `sys_role_menu` VALUES (8, 2, 2);
INSERT INTO `sys_role_menu` VALUES (9, 2, 3);
INSERT INTO `sys_role_menu` VALUES (20, 1, 6);
INSERT INTO `sys_role_menu` VALUES (21, 2, 5);
INSERT INTO `sys_role_menu` VALUES (22, 2, 6);
INSERT INTO `sys_role_menu` VALUES (23, 2, 4);
INSERT INTO `sys_role_menu` VALUES (26, 1, 7);
INSERT INTO `sys_role_menu` VALUES (29, 1, 8);
INSERT INTO `sys_role_menu` VALUES (30, 1, 9);
INSERT INTO `sys_role_menu` VALUES (31, 1, 10);
INSERT INTO `sys_role_menu` VALUES (32, 1, 11);
INSERT INTO `sys_role_menu` VALUES (33, 1, 12);
INSERT INTO `sys_role_menu` VALUES (35, 1, 13);
INSERT INTO `sys_role_menu` VALUES (36, 1, 14);
INSERT INTO `sys_role_menu` VALUES (37, 1, 15);
INSERT INTO `sys_role_menu` VALUES (38, 1, 16);
INSERT INTO `sys_role_menu` VALUES (39, 1, 17);
INSERT INTO `sys_role_menu` VALUES (40, 1, 18);
INSERT INTO `sys_role_menu` VALUES (41, 2, 7);
INSERT INTO `sys_role_menu` VALUES (42, 2, 8);
INSERT INTO `sys_role_menu` VALUES (43, 2, 9);
INSERT INTO `sys_role_menu` VALUES (44, 2, 10);
INSERT INTO `sys_role_menu` VALUES (45, 2, 11);
INSERT INTO `sys_role_menu` VALUES (46, 2, 13);
INSERT INTO `sys_role_menu` VALUES (47, 2, 14);
INSERT INTO `sys_role_menu` VALUES (48, 2, 15);
INSERT INTO `sys_role_menu` VALUES (49, 2, 16);
INSERT INTO `sys_role_menu` VALUES (50, 2, 17);
INSERT INTO `sys_role_menu` VALUES (51, 2, 18);
INSERT INTO `sys_role_menu` VALUES (52, 2, 12);
INSERT INTO `sys_role_menu` VALUES (53, 1, 19);
INSERT INTO `sys_role_menu` VALUES (54, 1, 20);
INSERT INTO `sys_role_menu` VALUES (55, 1, 21);
INSERT INTO `sys_role_menu` VALUES (56, 1, 22);
INSERT INTO `sys_role_menu` VALUES (57, 1, 23);
INSERT INTO `sys_role_menu` VALUES (58, 2, 19);
INSERT INTO `sys_role_menu` VALUES (59, 2, 21);
INSERT INTO `sys_role_menu` VALUES (60, 2, 20);
INSERT INTO `sys_role_menu` VALUES (61, 2, 22);
INSERT INTO `sys_role_menu` VALUES (62, 2, 23);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_id` int NOT NULL COMMENT '角色id',
  `permission_id` int NOT NULL COMMENT '权限id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 142 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, 1);
INSERT INTO `sys_role_permission` VALUES (2, 1, 2);
INSERT INTO `sys_role_permission` VALUES (3, 1, 3);
INSERT INTO `sys_role_permission` VALUES (4, 1, 4);
INSERT INTO `sys_role_permission` VALUES (5, 1, 5);
INSERT INTO `sys_role_permission` VALUES (6, 1, 6);
INSERT INTO `sys_role_permission` VALUES (7, 2, 1);
INSERT INTO `sys_role_permission` VALUES (8, 2, 2);
INSERT INTO `sys_role_permission` VALUES (9, 2, 6);
INSERT INTO `sys_role_permission` VALUES (10, 1, 7);
INSERT INTO `sys_role_permission` VALUES (11, 1, 8);
INSERT INTO `sys_role_permission` VALUES (12, 1, 9);
INSERT INTO `sys_role_permission` VALUES (14, 1, 11);
INSERT INTO `sys_role_permission` VALUES (15, 2, 7);
INSERT INTO `sys_role_permission` VALUES (16, 2, 11);
INSERT INTO `sys_role_permission` VALUES (17, 1, 12);
INSERT INTO `sys_role_permission` VALUES (18, 1, 13);
INSERT INTO `sys_role_permission` VALUES (19, 1, 14);
INSERT INTO `sys_role_permission` VALUES (20, 1, 15);
INSERT INTO `sys_role_permission` VALUES (21, 1, 16);
INSERT INTO `sys_role_permission` VALUES (22, 1, 17);
INSERT INTO `sys_role_permission` VALUES (23, 1, 18);
INSERT INTO `sys_role_permission` VALUES (24, 1, 19);
INSERT INTO `sys_role_permission` VALUES (25, 1, 20);
INSERT INTO `sys_role_permission` VALUES (26, 1, 21);
INSERT INTO `sys_role_permission` VALUES (27, 1, 22);
INSERT INTO `sys_role_permission` VALUES (28, 1, 23);
INSERT INTO `sys_role_permission` VALUES (29, 1, 24);
INSERT INTO `sys_role_permission` VALUES (30, 1, 25);
INSERT INTO `sys_role_permission` VALUES (31, 1, 26);
INSERT INTO `sys_role_permission` VALUES (47, 2, 26);
INSERT INTO `sys_role_permission` VALUES (48, 2, 25);
INSERT INTO `sys_role_permission` VALUES (49, 3, 26);
INSERT INTO `sys_role_permission` VALUES (51, 4, 26);
INSERT INTO `sys_role_permission` VALUES (53, 1, 27);
INSERT INTO `sys_role_permission` VALUES (54, 1, 28);
INSERT INTO `sys_role_permission` VALUES (55, 1, 29);
INSERT INTO `sys_role_permission` VALUES (56, 1, 30);
INSERT INTO `sys_role_permission` VALUES (57, 1, 31);
INSERT INTO `sys_role_permission` VALUES (58, 1, 32);
INSERT INTO `sys_role_permission` VALUES (59, 1, 33);
INSERT INTO `sys_role_permission` VALUES (60, 1, 34);
INSERT INTO `sys_role_permission` VALUES (61, 1, 35);
INSERT INTO `sys_role_permission` VALUES (62, 1, 36);
INSERT INTO `sys_role_permission` VALUES (63, 1, 39);
INSERT INTO `sys_role_permission` VALUES (64, 1, 38);
INSERT INTO `sys_role_permission` VALUES (65, 1, 37);
INSERT INTO `sys_role_permission` VALUES (66, 1, 40);
INSERT INTO `sys_role_permission` VALUES (67, 1, 41);
INSERT INTO `sys_role_permission` VALUES (68, 1, 42);
INSERT INTO `sys_role_permission` VALUES (69, 1, 43);
INSERT INTO `sys_role_permission` VALUES (70, 1, 44);
INSERT INTO `sys_role_permission` VALUES (71, 1, 45);
INSERT INTO `sys_role_permission` VALUES (72, 1, 46);
INSERT INTO `sys_role_permission` VALUES (73, 1, 47);
INSERT INTO `sys_role_permission` VALUES (74, 1, 49);
INSERT INTO `sys_role_permission` VALUES (75, 1, 51);
INSERT INTO `sys_role_permission` VALUES (76, 1, 50);
INSERT INTO `sys_role_permission` VALUES (77, 1, 48);
INSERT INTO `sys_role_permission` VALUES (78, 1, 52);
INSERT INTO `sys_role_permission` VALUES (79, 1, 53);
INSERT INTO `sys_role_permission` VALUES (80, 1, 54);
INSERT INTO `sys_role_permission` VALUES (81, 1, 55);
INSERT INTO `sys_role_permission` VALUES (82, 1, 56);
INSERT INTO `sys_role_permission` VALUES (83, 1, 57);
INSERT INTO `sys_role_permission` VALUES (84, 1, 58);
INSERT INTO `sys_role_permission` VALUES (85, 1, 59);
INSERT INTO `sys_role_permission` VALUES (86, 1, 60);
INSERT INTO `sys_role_permission` VALUES (87, 1, 61);
INSERT INTO `sys_role_permission` VALUES (88, 1, 62);
INSERT INTO `sys_role_permission` VALUES (89, 1, 63);
INSERT INTO `sys_role_permission` VALUES (90, 2, 16);
INSERT INTO `sys_role_permission` VALUES (91, 2, 15);
INSERT INTO `sys_role_permission` VALUES (92, 2, 18);
INSERT INTO `sys_role_permission` VALUES (93, 2, 19);
INSERT INTO `sys_role_permission` VALUES (94, 2, 23);
INSERT INTO `sys_role_permission` VALUES (95, 2, 27);
INSERT INTO `sys_role_permission` VALUES (96, 2, 30);
INSERT INTO `sys_role_permission` VALUES (97, 2, 31);
INSERT INTO `sys_role_permission` VALUES (98, 2, 32);
INSERT INTO `sys_role_permission` VALUES (99, 2, 35);
INSERT INTO `sys_role_permission` VALUES (100, 2, 36);
INSERT INTO `sys_role_permission` VALUES (101, 2, 41);
INSERT INTO `sys_role_permission` VALUES (102, 2, 47);
INSERT INTO `sys_role_permission` VALUES (103, 2, 48);
INSERT INTO `sys_role_permission` VALUES (104, 2, 53);
INSERT INTO `sys_role_permission` VALUES (105, 2, 54);
INSERT INTO `sys_role_permission` VALUES (106, 2, 55);
INSERT INTO `sys_role_permission` VALUES (107, 2, 58);
INSERT INTO `sys_role_permission` VALUES (108, 2, 59);
INSERT INTO `sys_role_permission` VALUES (109, 2, 60);
INSERT INTO `sys_role_permission` VALUES (110, 2, 61);
INSERT INTO `sys_role_permission` VALUES (111, 1, 64);
INSERT INTO `sys_role_permission` VALUES (112, 1, 65);
INSERT INTO `sys_role_permission` VALUES (113, 2, 65);
INSERT INTO `sys_role_permission` VALUES (114, 1, 66);
INSERT INTO `sys_role_permission` VALUES (115, 1, 67);
INSERT INTO `sys_role_permission` VALUES (116, 1, 68);
INSERT INTO `sys_role_permission` VALUES (117, 1, 69);
INSERT INTO `sys_role_permission` VALUES (118, 1, 70);
INSERT INTO `sys_role_permission` VALUES (119, 1, 71);
INSERT INTO `sys_role_permission` VALUES (120, 1, 72);
INSERT INTO `sys_role_permission` VALUES (121, 1, 73);
INSERT INTO `sys_role_permission` VALUES (122, 1, 74);
INSERT INTO `sys_role_permission` VALUES (123, 1, 75);
INSERT INTO `sys_role_permission` VALUES (124, 1, 76);
INSERT INTO `sys_role_permission` VALUES (125, 1, 77);
INSERT INTO `sys_role_permission` VALUES (126, 1, 78);
INSERT INTO `sys_role_permission` VALUES (127, 1, 79);
INSERT INTO `sys_role_permission` VALUES (128, 1, 80);
INSERT INTO `sys_role_permission` VALUES (129, 1, 81);
INSERT INTO `sys_role_permission` VALUES (130, 1, 82);
INSERT INTO `sys_role_permission` VALUES (131, 1, 83);
INSERT INTO `sys_role_permission` VALUES (132, 2, 66);
INSERT INTO `sys_role_permission` VALUES (133, 2, 67);
INSERT INTO `sys_role_permission` VALUES (134, 2, 70);
INSERT INTO `sys_role_permission` VALUES (135, 2, 72);
INSERT INTO `sys_role_permission` VALUES (136, 2, 78);
INSERT INTO `sys_role_permission` VALUES (137, 2, 79);
INSERT INTO `sys_role_permission` VALUES (138, 2, 81);
INSERT INTO `sys_role_permission` VALUES (139, 2, 82);
INSERT INTO `sys_role_permission` VALUES (140, 2, 43);
INSERT INTO `sys_role_permission` VALUES (141, 2, 44);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `sex` tinyint NULL DEFAULT 0 COMMENT '性别 0-男 1-女',
  `introduction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `avatar` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `fans_count` int NULL DEFAULT NULL COMMENT '粉丝数',
  `follow_count` int NULL DEFAULT NULL COMMENT '关注数',
  `register_type` tinyint NULL DEFAULT 0 COMMENT '注册方式 0-用户名/邮箱 1-gitee 2-github 3-QQ',
  `register_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '注册ip',
  `register_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '注册地址',
  `login_type` tinyint NULL DEFAULT 0 COMMENT '登录方式 0-用户名/邮箱 1-gitee 2-github 3-QQ',
  `login_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录ip',
  `login_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录地址',
  `login_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'sidifensen', '$2a$10$CAVzdDmq3V.OYuJx3TLZeeAS2RsP6LnxzxlCux7BAX4UoeoI0yfxm', '斯蒂芬森斯蒂芬森大神', '1848221808@qq.com', 0, '强者', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar.jpg', 0, 5, 35, 0, '127.0.0.1', '内网地址', 0, '192.168.31.220', '内网地址', '2025-10-08 15:37:55', '2025-06-28 22:30:22', '2025-10-09 08:58:45', 0);
INSERT INTO `sys_user` VALUES (2, 'test', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', 'test用户', 'test@qq.com', 0, 'helloworld', 'https://image.sidifensen.com/sidifensen-blog/user/avatar/2/c514f8c7a08d4ef3a86557374ec3f8b0.webp', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-10-09 09:05:49', '2025-07-31 17:38:46', '2025-10-09 09:00:07', 0);
INSERT INTO `sys_user` VALUES (3, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '1@qq.com', 0, 'helloworld', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.5.4', '内网地址', '2025-08-18 01:46:21', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (4, 'WebUser1', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户1', '12@qq.com', 0, 'helloworld', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (5, 'WebUser2', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户2', '123@qq.com', 0, 'helloworld', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (6, 'WebUser3', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户3', '1234@qq.com', 0, 'helloworld', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (7, 'WebUser4', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户4', '12345@qq.com', 0, 'helloworld', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (8, 'WebUser5', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户5', '123456@qq.com', 0, 'helloworld', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (9, 'WebUser6', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户6', '1234567@qq.com', 0, 'helloworld', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (10, 'WebUser7', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户7', '12345678@qq.com', 0, 'helloworld', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (11, 'WebUser8', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户8', '123456789@qq.com', 0, 'helloworld', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (12, 'WebUser9', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户9', '12345678910@qq.com', 0, 'helloworld', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (16, 'gitee_14919133', '$2a$10$7t3OrRk8j8NTDb7KxkouNOnY8T1ExAUFefKRGl0WzCFyexxNJ1IIC', '斯蒂芬森', NULL, 0, '梦想是成为全栈高手', 'https://foruda.gitee.com/avatar/1752080025136627357/14919133_sidifensen_1752080025.png', 0, 0, 0, 1, '127.0.0.1', '内网地址', 1, '192.168.31.220', '内网地址', '2025-09-18 10:57:43', '2025-08-20 15:12:12', '2025-08-20 15:12:12', 0);
INSERT INTO `sys_user` VALUES (17, 'github_186551034', '$2a$10$6.VSdM9z.TuAhIqXd7pPjuhwGtWxaUj0BdVgSnMh4wgbqB7GYaEw.', '斯蒂芬森', NULL, 0, '梦想是成为全栈高手', 'https://avatars.githubusercontent.com/u/186551034?v=4', 0, 0, 0, 2, '127.0.0.1', '内网地址', 2, '192.168.31.220', '内网地址', '2025-09-18 10:59:54', '2025-08-20 15:16:05', '2025-09-18 10:59:54', 0);
INSERT INTO `sys_user` VALUES (18, 'tech_master', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '科技大师', 'techmaster@example.com', 0, '专注于前沿科技研究，分享技术干货和行业趋势', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (19, 'code_warrior', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '代码战士', 'codewarrior@example.com', 1, '全栈开发工程师，热爱编程，追求代码的艺术与美感', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (20, 'ai_researcher', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', 'AI研究员', 'airesearcher@example.com', 0, '人工智能领域研究者，专注机器学习和深度学习技术', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (21, 'travel_blogger', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '旅行博主', 'travelblogger@example.com', 1, '环游世界，记录美好瞬间，分享旅行攻略和心得体验', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (22, 'food_explorer', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '美食探索家', 'foodexplorer@example.com', 0, '寻找天下美食，分享烹饪技巧，品味人生百味', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (23, 'book_lover', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '书虫读者', 'booklover@example.com', 1, '阅读爱好者，分享读书心得，推荐优质书籍', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (24, 'artist_soul', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '艺术灵魂', 'artistsoul@example.com', 1, '画家兼设计师，用画笔记录生活的美好，传递艺术的力量', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (25, 'music_composer', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '音乐作曲家', 'musiccomposer@example.com', 0, '独立音乐人，创作原创音乐，分享音乐创作心得', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (26, 'entrepreneur', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '创业者', 'entrepreneur@example.com', 0, '连续创业者，分享创业经验和商业思考', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (27, 'market_analyst', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '市场分析师', 'marketanalyst@example.com', 1, '金融市场分析师，关注经济动态，分享投资理念', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (28, 'fitness_coach', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '健身教练', 'fitnesscoach@example.com', 0, '专业健身教练，分享健身知识和健康生活方式', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (29, 'yoga_master', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '瑜伽大师', 'yogamaster@example.com', 1, '瑜伽导师，倡导身心平衡，分享瑜伽练习心得', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (30, 'teacher_wang', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '王老师', 'teacherwang@example.com', 1, '高中数学老师，热爱教育事业，分享教学经验', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (31, 'language_tutor', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '语言导师', 'languagetutor@example.com', 0, '多语言学习专家，分享语言学习技巧和方法', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (32, 'game_streamer', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '游戏主播', 'gamestreamer@example.com', 0, '电竞选手兼游戏主播，分享游戏攻略和电竞资讯', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (33, 'indie_developer', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '独立开发者', 'indiedeveloper@example.com', 1, '独立游戏开发者，记录游戏开发历程，分享创作心得', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (34, 'space_explorer', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '太空探索者', 'spaceexplorer@example.com', 0, '天文学爱好者，关注宇宙奥秘，分享天文科普知识', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (35, 'bio_scientist', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '生物学家', 'bioscientist@example.com', 1, '生物学研究者，探索生命奥秘，科普生物学知识', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);
INSERT INTO `sys_user` VALUES (36, 'fashion_guru', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '时尚达人', 'fashionguru@example.com', 1, '时尚博主，分享穿搭技巧和时尚趋势', ' https://image.sidifensen.com/sidifensen-blog/avatar/avatar1.png', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-09-26 14:28:58', '2025-09-26 14:28:58', '2025-09-26 14:28:58', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int NOT NULL COMMENT '用户id',
  `role_id` int NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 1, 1);
INSERT INTO `sys_user_role` VALUES (17, 2, 2);
INSERT INTO `sys_user_role` VALUES (48, 13, 3);
INSERT INTO `sys_user_role` VALUES (49, 14, 3);
INSERT INTO `sys_user_role` VALUES (50, 3, 3);
INSERT INTO `sys_user_role` VALUES (51, 15, 3);
INSERT INTO `sys_user_role` VALUES (52, 16, 3);
INSERT INTO `sys_user_role` VALUES (53, 17, 3);

-- ----------------------------
-- Table structure for sys_visitorlog
-- ----------------------------
DROP TABLE IF EXISTS `sys_visitorlog`;
CREATE TABLE `sys_visitorlog`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '访客记录id',
  `user_id` int NULL DEFAULT NULL COMMENT '访客用户id',
  `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '访客ip',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '访客地址',
  `device` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型（PC/Mobile）',
  `visit_time` datetime NOT NULL COMMENT '访问时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_visit_time`(`visit_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_visitorlog
-- ----------------------------
INSERT INTO `sys_visitorlog` VALUES (23, 1, '127.0.0.1', '内网地址', 'PC', '2025-10-06 14:55:33');
INSERT INTO `sys_visitorlog` VALUES (24, NULL, '192.168.31.220', '内网地址', 'PC', '2025-10-06 14:56:20');
INSERT INTO `sys_visitorlog` VALUES (25, NULL, '192.168.31.229', '内网地址', 'Mobile', '2025-10-07 14:56:43');
INSERT INTO `sys_visitorlog` VALUES (26, 1, '127.0.0.1', '内网地址', 'PC', '2025-10-07 15:01:33');
INSERT INTO `sys_visitorlog` VALUES (27, NULL, '192.168.31.220', '内网地址', 'PC', '2025-10-07 15:21:55');
INSERT INTO `sys_visitorlog` VALUES (28, 1, '192.168.31.220', '内网地址', 'PC', '2025-10-07 15:22:01');
INSERT INTO `sys_visitorlog` VALUES (29, NULL, '192.168.31.229', '内网地址', 'Mobile', '2025-10-07 15:22:42');
INSERT INTO `sys_visitorlog` VALUES (30, 1, '192.168.31.229', '内网地址', 'Mobile', '2025-10-07 18:59:24');
INSERT INTO `sys_visitorlog` VALUES (31, NULL, '127.0.0.1', '内网地址', 'PC', '2025-10-07 21:07:14');
INSERT INTO `sys_visitorlog` VALUES (32, 1, '127.0.0.1', '内网地址', 'PC', '2025-10-08 00:00:26');
INSERT INTO `sys_visitorlog` VALUES (33, 2, '192.168.31.220', '内网地址', 'PC', '2025-10-08 00:05:42');
INSERT INTO `sys_visitorlog` VALUES (34, 2, '127.0.0.1', '内网地址', 'PC', '2025-10-08 00:40:09');
INSERT INTO `sys_visitorlog` VALUES (35, 1, '192.168.31.220', '内网地址', 'PC', '2025-10-08 01:06:48');
INSERT INTO `sys_visitorlog` VALUES (36, NULL, '192.168.31.220', '内网地址', 'PC', '2025-10-08 14:13:58');
INSERT INTO `sys_visitorlog` VALUES (37, NULL, '127.0.0.1', '内网地址', 'PC', '2025-10-08 17:06:31');
INSERT INTO `sys_visitorlog` VALUES (38, 1, '127.0.0.1', '内网地址', 'PC', '2025-10-09 07:55:37');
INSERT INTO `sys_visitorlog` VALUES (39, NULL, '127.0.0.1', '内网地址', 'PC', '2025-10-09 08:52:30');
INSERT INTO `sys_visitorlog` VALUES (40, 2, '127.0.0.1', '内网地址', 'PC', '2025-10-09 08:52:33');

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `category` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签分类',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, 'Java', 'idea', 4);
INSERT INTO `tag` VALUES (2, 'Java', 'spring', 4);
INSERT INTO `tag` VALUES (3, 'Java', 'springboot', 4);
INSERT INTO `tag` VALUES (4, 'Java', 'redis', 4);
INSERT INTO `tag` VALUES (5, 'Java', 'mysql', 4);
INSERT INTO `tag` VALUES (6, '前端', 'vue', 11);
INSERT INTO `tag` VALUES (7, '前端', 'html', 11);
INSERT INTO `tag` VALUES (8, '前端', 'js', 11);
INSERT INTO `tag` VALUES (9, '前端', 'css', 11);
INSERT INTO `tag` VALUES (10, '数据库', 'elasticsearch', 25);
INSERT INTO `tag` VALUES (11, '数据库', 'mongodb', 25);
INSERT INTO `tag` VALUES (12, '后端', 'python', 13);
INSERT INTO `tag` VALUES (13, '后端', 'go', 13);
INSERT INTO `tag` VALUES (14, 'Python', 'django', 5);
INSERT INTO `tag` VALUES (15, 'Python', 'pygame', 5);
INSERT INTO `tag` VALUES (16, 'Python', 'virtualenv', 5);
INSERT INTO `tag` VALUES (17, 'Python', 'tornado', 5);
INSERT INTO `tag` VALUES (18, 'Python', 'flask', 5);
INSERT INTO `tag` VALUES (19, 'Python', 'scikit-learn', 5);
INSERT INTO `tag` VALUES (20, 'Python', 'plotly', 5);
INSERT INTO `tag` VALUES (21, 'Python', 'dash', 5);
INSERT INTO `tag` VALUES (22, 'Python', 'fastapi', 5);
INSERT INTO `tag` VALUES (23, 'Python', 'pyqt', 5);
INSERT INTO `tag` VALUES (24, 'Python', 'scrapy', 5);
INSERT INTO `tag` VALUES (25, 'Python', 'beautifulsoup', 5);
INSERT INTO `tag` VALUES (26, 'Python', 'numpy', 5);
INSERT INTO `tag` VALUES (27, 'Python', 'scipy', 5);
INSERT INTO `tag` VALUES (28, 'Python', 'pandas', 5);
INSERT INTO `tag` VALUES (29, 'Python', 'matplotlib', 5);
INSERT INTO `tag` VALUES (30, 'Python', 'httpx', 5);
INSERT INTO `tag` VALUES (31, 'Python', 'web3.py', 5);
INSERT INTO `tag` VALUES (32, 'Python', 'pytest', 5);
INSERT INTO `tag` VALUES (33, 'Python', 'pillow', 5);
INSERT INTO `tag` VALUES (34, 'Python', 'gunicorn', 5);
INSERT INTO `tag` VALUES (35, 'Python', 'pip', 5);
INSERT INTO `tag` VALUES (36, 'Python', 'conda', 5);
INSERT INTO `tag` VALUES (37, 'Python', 'ipython', 5);
INSERT INTO `tag` VALUES (39, 'Java', 'eclipse', 4);
INSERT INTO `tag` VALUES (40, 'Java', 'java', 4);
INSERT INTO `tag` VALUES (41, 'Java', 'tomcat', 4);
INSERT INTO `tag` VALUES (42, 'Java', 'hibernate', 4);
INSERT INTO `tag` VALUES (43, 'Java', 'maven', 4);
INSERT INTO `tag` VALUES (44, 'Java', 'struts', 4);
INSERT INTO `tag` VALUES (45, 'Java', 'kafka', 4);
INSERT INTO `tag` VALUES (46, 'Java', 'intellij-idea', 4);
INSERT INTO `tag` VALUES (47, 'Java', 'java-ee', 4);
INSERT INTO `tag` VALUES (48, 'Java', 'spring boot', 4);
INSERT INTO `tag` VALUES (49, 'Java', 'spring cloud', 4);
INSERT INTO `tag` VALUES (50, 'Java', 'jvm', 4);
INSERT INTO `tag` VALUES (51, 'Java', 'jetty', 4);
INSERT INTO `tag` VALUES (52, 'Java', 'junit', 4);
INSERT INTO `tag` VALUES (53, 'Java', 'log4j', 4);
INSERT INTO `tag` VALUES (54, 'Java', 'servlet', 4);
INSERT INTO `tag` VALUES (55, 'Java', 'mybatis', 4);
INSERT INTO `tag` VALUES (56, 'Java', 'nio', 4);
INSERT INTO `tag` VALUES (57, 'Java', 'dubbo', 4);
INSERT INTO `tag` VALUES (58, 'Java', 'sentinel', 4);
INSERT INTO `tag` VALUES (59, 'Java', 'java-consul', 4);
INSERT INTO `tag` VALUES (60, 'Java', 'java-zookeeper', 4);
INSERT INTO `tag` VALUES (61, 'Java', 'java-rabbitmq', 4);
INSERT INTO `tag` VALUES (62, 'Java', 'java-activemq', 4);
INSERT INTO `tag` VALUES (63, 'Java', 'java-rocketmq', 4);
INSERT INTO `tag` VALUES (64, 'Java', 'sdkman', 4);
INSERT INTO `tag` VALUES (65, 'Java', 'guava', 4);
INSERT INTO `tag` VALUES (67, '编程语言', 'php', 33);
INSERT INTO `tag` VALUES (68, '编程语言', 'c++', 33);
INSERT INTO `tag` VALUES (69, '编程语言', 'c语言', 33);
INSERT INTO `tag` VALUES (70, '编程语言', 'javascript', 33);
INSERT INTO `tag` VALUES (71, '编程语言', 'c#', 33);
INSERT INTO `tag` VALUES (72, '编程语言', 'ruby', 33);
INSERT INTO `tag` VALUES (73, '编程语言', 'qt', 33);
INSERT INTO `tag` VALUES (74, '编程语言', 'actionscript', 33);
INSERT INTO `tag` VALUES (75, '编程语言', 'lua', 33);
INSERT INTO `tag` VALUES (76, '编程语言', 'perl', 33);
INSERT INTO `tag` VALUES (77, '编程语言', 'symfony', 33);
INSERT INTO `tag` VALUES (78, '编程语言', 'r语言', 33);
INSERT INTO `tag` VALUES (79, '编程语言', 'swift', 33);
INSERT INTO `tag` VALUES (80, '编程语言', 'laravel', 33);
INSERT INTO `tag` VALUES (81, '编程语言', 'scala', 33);
INSERT INTO `tag` VALUES (82, '编程语言', 'bash', 33);
INSERT INTO `tag` VALUES (83, '编程语言', 'batch', 33);
INSERT INTO `tag` VALUES (84, '编程语言', 'lisp', 33);
INSERT INTO `tag` VALUES (85, '编程语言', 'typescript', 33);
INSERT INTO `tag` VALUES (86, '编程语言', 'erlang', 33);
INSERT INTO `tag` VALUES (87, '编程语言', 'composer', 33);
INSERT INTO `tag` VALUES (88, '编程语言', 'objective-c', 33);
INSERT INTO `tag` VALUES (89, '编程语言', 'julia', 33);
INSERT INTO `tag` VALUES (90, '编程语言', '开发语言', 33);
INSERT INTO `tag` VALUES (91, '编程语言', 'kotlin', 33);
INSERT INTO `tag` VALUES (92, '编程语言', 'golang', 33);
INSERT INTO `tag` VALUES (93, '编程语言', 'matlab', 33);
INSERT INTO `tag` VALUES (94, '编程语言', 'rust', 33);
INSERT INTO `tag` VALUES (95, '编程语言', '青少年编程', 33);
INSERT INTO `tag` VALUES (96, '编程语言', 'rescript', 33);
INSERT INTO `tag` VALUES (97, '编程语言', '汇编', 33);
INSERT INTO `tag` VALUES (98, '编程语言', 'mojo', 33);
INSERT INTO `tag` VALUES (99, '编程语言', 'carbon', 33);
INSERT INTO `tag` VALUES (102, '开发工具', 'github', 17);
INSERT INTO `tag` VALUES (103, '开发工具', 'git', 17);
INSERT INTO `tag` VALUES (104, '开发工具', 'windows', 17);
INSERT INTO `tag` VALUES (105, '开发工具', 'svn', 17);
INSERT INTO `tag` VALUES (106, '开发工具', 'ide', 17);
INSERT INTO `tag` VALUES (107, '开发工具', 'visual studio', 17);
INSERT INTO `tag` VALUES (108, '开发工具', 'ci/cd', 17);
INSERT INTO `tag` VALUES (109, '开发工具', 'pycharm', 17);
INSERT INTO `tag` VALUES (110, '开发工具', 'emacs', 17);
INSERT INTO `tag` VALUES (111, '开发工具', 'vim', 17);
INSERT INTO `tag` VALUES (112, '开发工具', 'docker', 17);
INSERT INTO `tag` VALUES (113, '开发工具', 'vscode', 17);
INSERT INTO `tag` VALUES (114, '开发工具', 'postman', 17);
INSERT INTO `tag` VALUES (115, '开发工具', 'jupyter', 17);
INSERT INTO `tag` VALUES (116, '开发工具', 'macos', 17);
INSERT INTO `tag` VALUES (117, '开发工具', 'myeclipse', 17);
INSERT INTO `tag` VALUES (118, '开发工具', 'phpstorm', 17);
INSERT INTO `tag` VALUES (119, '开发工具', 'visualstudio', 17);
INSERT INTO `tag` VALUES (120, '开发工具', 'visual studio code', 17);
INSERT INTO `tag` VALUES (121, '开发工具', 'sublime text', 17);
INSERT INTO `tag` VALUES (122, '开发工具', 'intellij idea', 17);
INSERT INTO `tag` VALUES (123, '开发工具', 'webstorm', 17);
INSERT INTO `tag` VALUES (124, '开发工具', '编辑器', 17);
INSERT INTO `tag` VALUES (125, '开发工具', 'arcgis', 17);
INSERT INTO `tag` VALUES (126, '开发工具', 'gitlab', 17);
INSERT INTO `tag` VALUES (127, '开发工具', 'yapi', 17);
INSERT INTO `tag` VALUES (128, '开发工具', 'labview', 17);
INSERT INTO `tag` VALUES (132, '数据结构与算法', '算法', 26);
INSERT INTO `tag` VALUES (133, '数据结构与算法', '数据结构', 26);
INSERT INTO `tag` VALUES (134, '数据结构与算法', '线性回归', 26);
INSERT INTO `tag` VALUES (135, '数据结构与算法', '链表', 26);
INSERT INTO `tag` VALUES (136, '数据结构与算法', '贪心算法', 26);
INSERT INTO `tag` VALUES (137, '数据结构与算法', '动态规划', 26);
INSERT INTO `tag` VALUES (138, '数据结构与算法', '排序算法', 26);
INSERT INTO `tag` VALUES (139, '数据结构与算法', 'kmeans', 26);
INSERT INTO `tag` VALUES (140, '数据结构与算法', 'leetcode', 26);
INSERT INTO `tag` VALUES (141, '数据结构与算法', '决策树', 26);
INSERT INTO `tag` VALUES (142, '数据结构与算法', '最小二乘法', 26);
INSERT INTO `tag` VALUES (143, '数据结构与算法', 'b树', 26);
INSERT INTO `tag` VALUES (144, '数据结构与算法', '模拟退火算法', 26);
INSERT INTO `tag` VALUES (145, '数据结构与算法', '散列表', 26);
INSERT INTO `tag` VALUES (146, '数据结构与算法', '随机森林', 26);
INSERT INTO `tag` VALUES (147, '数据结构与算法', '支持向量机', 26);
INSERT INTO `tag` VALUES (148, '数据结构与算法', '启发式算法', 26);
INSERT INTO `tag` VALUES (149, '数据结构与算法', '逻辑回归', 26);
INSERT INTO `tag` VALUES (150, '数据结构与算法', '推荐算法', 26);
INSERT INTO `tag` VALUES (151, '数据结构与算法', '宽度优先', 26);
INSERT INTO `tag` VALUES (152, '数据结构与算法', '广度优先', 26);
INSERT INTO `tag` VALUES (153, '数据结构与算法', '深度优先', 26);
INSERT INTO `tag` VALUES (154, '数据结构与算法', '迭代加深', 26);
INSERT INTO `tag` VALUES (155, '数据结构与算法', '图搜索算法', 26);
INSERT INTO `tag` VALUES (156, '数据结构与算法', '爬山算法', 26);
INSERT INTO `tag` VALUES (157, '数据结构与算法', '近邻算法', 26);
INSERT INTO `tag` VALUES (158, '数据结构与算法', '均值算法', 26);
INSERT INTO `tag` VALUES (159, '数据结构与算法', '预编码算法', 26);
INSERT INTO `tag` VALUES (160, '数据结构与算法', '霍夫曼树', 26);
INSERT INTO `tag` VALUES (161, '数据结构与算法', '剪枝', 26);
INSERT INTO `tag` VALUES (162, '数据结构与算法', '哈希算法', 26);
INSERT INTO `tag` VALUES (163, '数据结构与算法', '柔性数组', 26);
INSERT INTO `tag` VALUES (164, '数据结构与算法', 'skiplist', 26);
INSERT INTO `tag` VALUES (165, '数据结构与算法', 'hash-index', 26);
INSERT INTO `tag` VALUES (166, '数据结构与算法', 'sstable', 26);
INSERT INTO `tag` VALUES (167, '数据结构与算法', 'lsm-tree', 26);
INSERT INTO `tag` VALUES (168, '数据结构与算法', 'inverted-index', 26);
INSERT INTO `tag` VALUES (169, '数据结构与算法', 'suffix-tree', 26);
INSERT INTO `tag` VALUES (170, '数据结构与算法', 'r-tree', 26);
INSERT INTO `tag` VALUES (171, '大数据', 'sqlite', 14);
INSERT INTO `tag` VALUES (172, '大数据', 'oracle', 14);
INSERT INTO `tag` VALUES (173, '大数据', 'json', 14);
INSERT INTO `tag` VALUES (174, '大数据', 'sql', 14);
INSERT INTO `tag` VALUES (175, '大数据', 'database', 14);
INSERT INTO `tag` VALUES (176, '大数据', 'hbase', 14);
INSERT INTO `tag` VALUES (177, '大数据', 'hadoop', 14);
INSERT INTO `tag` VALUES (178, '大数据', 'hive', 14);
INSERT INTO `tag` VALUES (179, '大数据', 'storm', 14);
INSERT INTO `tag` VALUES (180, '大数据', 'zookeeper', 14);
INSERT INTO `tag` VALUES (181, '大数据', 'spark', 14);
INSERT INTO `tag` VALUES (182, '大数据', 'memcached', 14);
INSERT INTO `tag` VALUES (183, '大数据', 'flume', 14);
INSERT INTO `tag` VALUES (184, '大数据', 'rabbitmq', 14);
INSERT INTO `tag` VALUES (185, '大数据', 'memcache', 14);
INSERT INTO `tag` VALUES (186, '大数据', 'big data', 14);
INSERT INTO `tag` VALUES (187, '大数据', 'eureka', 14);
INSERT INTO `tag` VALUES (188, '大数据', 'etcd', 14);
INSERT INTO `tag` VALUES (189, '大数据', 'flink', 14);
INSERT INTO `tag` VALUES (190, '大数据', 'consul', 14);
INSERT INTO `tag` VALUES (191, '大数据', 'postgresql', 14);
INSERT INTO `tag` VALUES (192, '大数据', 'nosql', 14);
INSERT INTO `tag` VALUES (193, '大数据', 'sqlserver', 14);
INSERT INTO `tag` VALUES (194, '大数据', '时序数据库', 14);
INSERT INTO `tag` VALUES (195, '大数据', 'tdengine', 14);
INSERT INTO `tag` VALUES (196, '大数据', '数据库', 14);
INSERT INTO `tag` VALUES (197, '大数据', 'mariadb', 14);
INSERT INTO `tag` VALUES (198, '大数据', 'talkingdata', 14);
INSERT INTO `tag` VALUES (199, '大数据', '涛思数据', 14);
INSERT INTO `tag` VALUES (200, '大数据', 'kylin', 14);
INSERT INTO `tag` VALUES (201, '大数据', 'hdfs', 14);
INSERT INTO `tag` VALUES (202, '大数据', 'mapreduce', 14);
INSERT INTO `tag` VALUES (203, '大数据', 'cloudera', 14);
INSERT INTO `tag` VALUES (204, '大数据', 'ambari', 14);
INSERT INTO `tag` VALUES (205, '大数据', 'sqoop', 14);
INSERT INTO `tag` VALUES (206, '大数据', 'odps', 14);
INSERT INTO `tag` VALUES (207, '大数据', '大数据', 14);
INSERT INTO `tag` VALUES (208, '大数据', '数据仓库', 14);
INSERT INTO `tag` VALUES (209, '大数据', 'etl', 14);
INSERT INTO `tag` VALUES (210, '大数据', '数据库架构', 14);
INSERT INTO `tag` VALUES (211, '大数据', 'dba', 14);
INSERT INTO `tag` VALUES (212, '大数据', 'etl工程师', 14);
INSERT INTO `tag` VALUES (213, '大数据', '数据库开发', 14);
INSERT INTO `tag` VALUES (214, '大数据', 'activemq', 14);
INSERT INTO `tag` VALUES (215, '大数据', 'rocketmq', 14);
INSERT INTO `tag` VALUES (216, '大数据', 'finebi', 14);
INSERT INTO `tag` VALUES (217, '大数据', 'powerbi', 14);
INSERT INTO `tag` VALUES (218, '大数据', 'sequoiadb', 14);
INSERT INTO `tag` VALUES (219, '大数据', 'oceanbase', 14);
INSERT INTO `tag` VALUES (220, '大数据', 'tidb', 14);
INSERT INTO `tag` VALUES (221, '大数据', 'couchdb', 14);
INSERT INTO `tag` VALUES (222, '大数据', 'opentsdb', 14);
INSERT INTO `tag` VALUES (223, '大数据', 'iotdb', 14);
INSERT INTO `tag` VALUES (224, '大数据', 'milvus', 14);
INSERT INTO `tag` VALUES (225, '大数据', 'faiss', 14);
INSERT INTO `tag` VALUES (226, '大数据', 'jina', 14);
INSERT INTO `tag` VALUES (227, '大数据', 'neo4j', 14);
INSERT INTO `tag` VALUES (228, '大数据', 'clickhouse', 14);
INSERT INTO `tag` VALUES (229, '大数据', 'ceph', 14);
INSERT INTO `tag` VALUES (230, '大数据', 'gaussdb', 14);
INSERT INTO `tag` VALUES (231, '大数据', 'fusioninsight', 14);
INSERT INTO `tag` VALUES (237, '前端', 'html5', 11);
INSERT INTO `tag` VALUES (238, '前端', 'firefox', 11);
INSERT INTO `tag` VALUES (239, '前端', 'jquery', 11);
INSERT INTO `tag` VALUES (240, '前端', 'ajax', 11);
INSERT INTO `tag` VALUES (241, '前端', '正则表达式', 11);
INSERT INTO `tag` VALUES (242, '前端', 'chrome', 11);
INSERT INTO `tag` VALUES (243, '前端', 'safari', 11);
INSERT INTO `tag` VALUES (244, '前端', 'easyui', 11);
INSERT INTO `tag` VALUES (245, '前端', 'bootstrap', 11);
INSERT INTO `tag` VALUES (246, '前端', 'ecmascript', 11);
INSERT INTO `tag` VALUES (247, '前端', 'css3', 11);
INSERT INTO `tag` VALUES (248, '前端', 'echarts', 11);
INSERT INTO `tag` VALUES (249, '前端', 'less', 11);
INSERT INTO `tag` VALUES (250, '前端', 'node.js', 11);
INSERT INTO `tag` VALUES (251, '前端', 'gulp', 11);
INSERT INTO `tag` VALUES (252, '前端', 'vue.js', 11);
INSERT INTO `tag` VALUES (253, '前端', 'electron', 11);
INSERT INTO `tag` VALUES (254, '前端', 'angular.js', 11);
INSERT INTO `tag` VALUES (255, '前端', 'layui', 11);
INSERT INTO `tag` VALUES (256, '前端', 'react.js', 11);
INSERT INTO `tag` VALUES (257, '前端', 'stylus', 11);
INSERT INTO `tag` VALUES (258, '前端', 'elementui', 11);
INSERT INTO `tag` VALUES (259, '前端', 'scss', 11);
INSERT INTO `tag` VALUES (260, '前端', 'reactjs', 11);
INSERT INTO `tag` VALUES (261, '前端', 'es6', 11);
INSERT INTO `tag` VALUES (262, '前端', 'npm', 11);
INSERT INTO `tag` VALUES (263, '前端', 'sass', 11);
INSERT INTO `tag` VALUES (264, '前端', 'chrome devtools', 11);
INSERT INTO `tag` VALUES (265, '前端', 'angular', 11);
INSERT INTO `tag` VALUES (266, '前端', 'coffeescript', 11);
INSERT INTO `tag` VALUES (267, '前端', 'postcss', 11);
INSERT INTO `tag` VALUES (268, '前端', 'fiddler', 11);
INSERT INTO `tag` VALUES (269, '前端', 'webkit', 11);
INSERT INTO `tag` VALUES (270, '前端', 'yarn', 11);
INSERT INTO `tag` VALUES (271, '前端', 'firebug', 11);
INSERT INTO `tag` VALUES (272, '前端', 'edge', 11);
INSERT INTO `tag` VALUES (273, '前端', 'webpack', 11);
INSERT INTO `tag` VALUES (274, '前端', '前端', 11);
INSERT INTO `tag` VALUES (275, '前端', 'xss', 11);
INSERT INTO `tag` VALUES (276, '前端', 'csrf', 11);
INSERT INTO `tag` VALUES (277, '前端', 'xhtml', 11);
INSERT INTO `tag` VALUES (278, '前端', '前端框架', 11);
INSERT INTO `tag` VALUES (279, '前端', 'view design', 11);
INSERT INTO `tag` VALUES (280, '前端', 'tdesign', 11);
INSERT INTO `tag` VALUES (281, '前端', 'arco design', 11);
INSERT INTO `tag` VALUES (282, '前端', 'anti-design-vue', 11);
INSERT INTO `tag` VALUES (283, '前端', 'express', 11);
INSERT INTO `tag` VALUES (284, '前端', 'turbopack', 11);
INSERT INTO `tag` VALUES (291, '后端', 'mvc', 13);
INSERT INTO `tag` VALUES (292, '后端', 'nginx', 13);
INSERT INTO `tag` VALUES (293, '后端', 'asp.net', 13);
INSERT INTO `tag` VALUES (294, '后端', 'swoole', 13);
INSERT INTO `tag` VALUES (295, '后端', 'ruby on rails', 13);
INSERT INTO `tag` VALUES (296, '后端', 'lavarel', 13);
INSERT INTO `tag` VALUES (297, '后端', '爬虫', 13);
INSERT INTO `tag` VALUES (298, '后端', '后端', 13);
INSERT INTO `tag` VALUES (299, '后端', 'restful', 13);
INSERT INTO `tag` VALUES (300, '后端', 'graphql', 13);
INSERT INTO `tag` VALUES (301, '后端', '架构', 13);
INSERT INTO `tag` VALUES (302, '后端', '分布式', 13);
INSERT INTO `tag` VALUES (303, '后端', '中间件', 13);
INSERT INTO `tag` VALUES (304, '后端', 'gateway', 13);
INSERT INTO `tag` VALUES (305, '后端', 'ribbon', 13);
INSERT INTO `tag` VALUES (306, '后端', 'gin', 13);
INSERT INTO `tag` VALUES (307, '后端', 'beego', 13);
INSERT INTO `tag` VALUES (308, '后端', 'hystrix', 13);
INSERT INTO `tag` VALUES (309, '后端', 'logback', 13);
INSERT INTO `tag` VALUES (326, '云原生', '容器', 6);
INSERT INTO `tag` VALUES (327, '云原生', 'jenkins', 6);
INSERT INTO `tag` VALUES (328, '云原生', 'devops', 6);
INSERT INTO `tag` VALUES (329, '云原生', 'kubernetes', 6);
INSERT INTO `tag` VALUES (330, '云原生', '云原生', 6);
INSERT INTO `tag` VALUES (331, '云原生', '微服务', 6);
INSERT INTO `tag` VALUES (332, '云原生', '服务发现', 6);
INSERT INTO `tag` VALUES (333, '云原生', 'paas', 6);
INSERT INTO `tag` VALUES (334, '云原生', 'serverless', 6);
INSERT INTO `tag` VALUES (335, '云原生', 'kubeless', 6);
INSERT INTO `tag` VALUES (336, '云原生', 'kubelet', 6);
INSERT INTO `tag` VALUES (337, '云原生', 'kind', 6);
INSERT INTO `tag` VALUES (338, '云原生', 'knative', 6);
INSERT INTO `tag` VALUES (339, '云原生', 'istio', 6);
INSERT INTO `tag` VALUES (340, '云原生', 'service_mesh', 6);
INSERT INTO `tag` VALUES (341, '云原生', 'terraform', 6);
INSERT INTO `tag` VALUES (342, '云原生', 'argocd', 6);
INSERT INTO `tag` VALUES (343, '云原生', 'rancher', 6);
INSERT INTO `tag` VALUES (344, '云原生', 'openshift', 6);
INSERT INTO `tag` VALUES (345, '云原生', 'openstack', 6);
INSERT INTO `tag` VALUES (346, '云原生', 'harvester', 6);
INSERT INTO `tag` VALUES (347, '云原生', 'podman', 6);
INSERT INTO `tag` VALUES (354, '移动开发', 'android', 32);
INSERT INTO `tag` VALUES (355, '移动开发', 'ios', 32);
INSERT INTO `tag` VALUES (356, '移动开发', 'iphone', 32);
INSERT INTO `tag` VALUES (357, '移动开发', 'webview', 32);
INSERT INTO `tag` VALUES (358, '移动开发', 'xml', 32);
INSERT INTO `tag` VALUES (359, '移动开发', 'xcode', 32);
INSERT INTO `tag` VALUES (360, '移动开发', 'phonegap', 32);
INSERT INTO `tag` VALUES (361, '移动开发', 'apache', 32);
INSERT INTO `tag` VALUES (362, '移动开发', 'ipad', 32);
INSERT INTO `tag` VALUES (363, '移动开发', 'cocoa', 32);
INSERT INTO `tag` VALUES (364, '移动开发', '小程序', 32);
INSERT INTO `tag` VALUES (365, '移动开发', 'xamarin', 32);
INSERT INTO `tag` VALUES (366, '移动开发', '微信小程序', 32);
INSERT INTO `tag` VALUES (367, '移动开发', 'reactnative', 32);
INSERT INTO `tag` VALUES (368, '移动开发', 'flutter', 32);
INSERT INTO `tag` VALUES (369, '移动开发', 'android-studio', 32);
INSERT INTO `tag` VALUES (370, '移动开发', '百度小程序', 32);
INSERT INTO `tag` VALUES (371, '移动开发', 'react native', 32);
INSERT INTO `tag` VALUES (372, '移动开发', 'android studio', 32);
INSERT INTO `tag` VALUES (373, '移动开发', 'web app', 32);
INSERT INTO `tag` VALUES (374, '移动开发', 'gradle', 32);
INSERT INTO `tag` VALUES (375, '移动开发', 'android jetpack', 32);
INSERT INTO `tag` VALUES (376, '移动开发', 'rxjava', 32);
INSERT INTO `tag` VALUES (377, '移动开发', 'swiftui', 32);
INSERT INTO `tag` VALUES (378, '移动开发', 'cocoapods', 32);
INSERT INTO `tag` VALUES (379, '移动开发', 'wwdc', 32);
INSERT INTO `tag` VALUES (380, '移动开发', 'rxswift', 32);
INSERT INTO `tag` VALUES (381, '移动开发', 'dalvik', 32);
INSERT INTO `tag` VALUES (382, '移动开发', 'okhttp', 32);
INSERT INTO `tag` VALUES (383, '移动开发', 'retrofit', 32);
INSERT INTO `tag` VALUES (384, '移动开发', 'glide', 32);
INSERT INTO `tag` VALUES (385, '移动开发', 'binder', 32);
INSERT INTO `tag` VALUES (386, '移动开发', 'android runtime', 32);
INSERT INTO `tag` VALUES (387, '移动开发', 'zygote', 32);
INSERT INTO `tag` VALUES (388, '移动开发', 'appcompat', 32);
INSERT INTO `tag` VALUES (389, '移动开发', 'androidx', 32);
INSERT INTO `tag` VALUES (390, '移动开发', 'adb', 32);
INSERT INTO `tag` VALUES (391, '移动开发', 'uni-app', 32);
INSERT INTO `tag` VALUES (392, '移动开发', 'taro', 32);
INSERT INTO `tag` VALUES (398, '人工智能', 'opencv', 8);
INSERT INTO `tag` VALUES (399, '人工智能', '数据挖掘', 8);
INSERT INTO `tag` VALUES (400, '人工智能', '语音识别', 8);
INSERT INTO `tag` VALUES (401, '人工智能', '计算机视觉', 8);
INSERT INTO `tag` VALUES (402, '人工智能', '目标检测', 8);
INSERT INTO `tag` VALUES (403, '人工智能', '机器学习', 8);
INSERT INTO `tag` VALUES (404, '人工智能', '人工智能', 8);
INSERT INTO `tag` VALUES (405, '人工智能', 'caffe', 8);
INSERT INTO `tag` VALUES (406, '人工智能', '深度学习', 8);
INSERT INTO `tag` VALUES (407, '人工智能', '神经网络', 8);
INSERT INTO `tag` VALUES (408, '人工智能', '自然语言处理', 8);
INSERT INTO `tag` VALUES (409, '人工智能', 'sklearn', 8);
INSERT INTO `tag` VALUES (410, '人工智能', 'cnn', 8);
INSERT INTO `tag` VALUES (411, '人工智能', 'mllib', 8);
INSERT INTO `tag` VALUES (412, '人工智能', 'word2vec', 8);
INSERT INTO `tag` VALUES (413, '人工智能', 'tensorflow', 8);
INSERT INTO `tag` VALUES (414, '人工智能', '目标跟踪', 8);
INSERT INTO `tag` VALUES (415, '人工智能', 'keras', 8);
INSERT INTO `tag` VALUES (416, '人工智能', '知识图谱', 8);
INSERT INTO `tag` VALUES (417, '人工智能', 'rnn', 8);
INSERT INTO `tag` VALUES (418, '人工智能', 'lstm', 8);
INSERT INTO `tag` VALUES (419, '人工智能', '自动驾驶', 8);
INSERT INTO `tag` VALUES (420, '人工智能', 'dnn', 8);
INSERT INTO `tag` VALUES (421, '人工智能', '生成对抗网络', 8);
INSERT INTO `tag` VALUES (422, '人工智能', 'mxnet', 8);
INSERT INTO `tag` VALUES (423, '人工智能', 'pytorch', 8);
INSERT INTO `tag` VALUES (424, '人工智能', '机器翻译', 8);
INSERT INTO `tag` VALUES (425, '人工智能', '语言模型', 8);
INSERT INTO `tag` VALUES (426, '人工智能', 'oneflow', 8);
INSERT INTO `tag` VALUES (427, '人工智能', 'mlnet', 8);
INSERT INTO `tag` VALUES (428, '人工智能', 'paddlepaddle', 8);
INSERT INTO `tag` VALUES (429, '人工智能', 'gru', 8);
INSERT INTO `tag` VALUES (430, '人工智能', 'mnn', 8);
INSERT INTO `tag` VALUES (431, '人工智能', 'boosting', 8);
INSERT INTO `tag` VALUES (432, '人工智能', 'transformer', 8);
INSERT INTO `tag` VALUES (433, '人工智能', 'xlnet', 8);
INSERT INTO `tag` VALUES (434, '人工智能', 'bert', 8);
INSERT INTO `tag` VALUES (435, '人工智能', 'openvino', 8);
INSERT INTO `tag` VALUES (436, '人工智能', '边缘计算', 8);
INSERT INTO `tag` VALUES (437, '人工智能', '超分辨率重建', 8);
INSERT INTO `tag` VALUES (438, '人工智能', '智慧城市', 8);
INSERT INTO `tag` VALUES (439, '人工智能', '视觉检测', 8);
INSERT INTO `tag` VALUES (440, '人工智能', '图像处理', 8);
INSERT INTO `tag` VALUES (441, '人工智能', 'nlp', 8);
INSERT INTO `tag` VALUES (442, '人工智能', '数据分析', 8);
INSERT INTO `tag` VALUES (443, '人工智能', '聚类', 8);
INSERT INTO `tag` VALUES (444, '人工智能', '集成学习', 8);
INSERT INTO `tag` VALUES (445, '人工智能', '迁移学习', 8);
INSERT INTO `tag` VALUES (446, '人工智能', '分类', 8);
INSERT INTO `tag` VALUES (447, '人工智能', '回归', 8);
INSERT INTO `tag` VALUES (448, '人工智能', 'gpt-3', 8);
INSERT INTO `tag` VALUES (449, '人工智能', 'spark-ml', 8);
INSERT INTO `tag` VALUES (450, '人工智能', 'AI作画', 8);
INSERT INTO `tag` VALUES (451, '人工智能', 'tf-idf', 8);
INSERT INTO `tag` VALUES (452, '人工智能', 'stable diffusion', 8);
INSERT INTO `tag` VALUES (453, '人工智能', 'chatgpt', 8);
INSERT INTO `tag` VALUES (454, '人工智能', 'DALL·E 2', 8);
INSERT INTO `tag` VALUES (455, '人工智能', 'craiyon', 8);
INSERT INTO `tag` VALUES (456, '人工智能', 'Imagen', 8);
INSERT INTO `tag` VALUES (457, '人工智能', 'DreamFusion', 8);
INSERT INTO `tag` VALUES (458, '人工智能', 'AudioLM', 8);
INSERT INTO `tag` VALUES (459, '人工智能', 'YOLO', 8);
INSERT INTO `tag` VALUES (460, '人工智能', 'bard', 8);
INSERT INTO `tag` VALUES (461, '人工智能', '文心一言', 8);
INSERT INTO `tag` VALUES (462, '人工智能', 'ocr', 8);
INSERT INTO `tag` VALUES (463, '人工智能', '腾讯云AI代码助手', 8);
INSERT INTO `tag` VALUES (465, '网络与通信', 'http', 34);
INSERT INTO `tag` VALUES (466, '网络与通信', 'p2p', 34);
INSERT INTO `tag` VALUES (467, '网络与通信', 'udp', 34);
INSERT INTO `tag` VALUES (468, '网络与通信', 'ssl', 34);
INSERT INTO `tag` VALUES (469, '网络与通信', 'https', 34);
INSERT INTO `tag` VALUES (470, '网络与通信', 'wireshark', 34);
INSERT INTO `tag` VALUES (471, '网络与通信', 'websocket', 34);
INSERT INTO `tag` VALUES (472, '网络与通信', '网络安全', 34);
INSERT INTO `tag` VALUES (473, '网络与通信', 'tcpdump', 34);
INSERT INTO `tag` VALUES (474, '网络与通信', '网络协议', 34);
INSERT INTO `tag` VALUES (475, '网络与通信', 'tcp/ip', 34);
INSERT INTO `tag` VALUES (476, '网络与通信', 'rpc', 34);
INSERT INTO `tag` VALUES (477, '网络与通信', '5G', 34);
INSERT INTO `tag` VALUES (478, '网络与通信', '信号处理', 34);
INSERT INTO `tag` VALUES (479, '网络与通信', '信息与通信', 34);
INSERT INTO `tag` VALUES (480, '嵌入式', '单片机', 16);
INSERT INTO `tag` VALUES (481, '嵌入式', 'stm32', 16);
INSERT INTO `tag` VALUES (482, '嵌入式', '51单片机', 16);
INSERT INTO `tag` VALUES (483, '嵌入式', 'proteus', 16);
INSERT INTO `tag` VALUES (484, '嵌入式', 'mcu', 16);
INSERT INTO `tag` VALUES (485, '嵌入式', '物联网', 16);
INSERT INTO `tag` VALUES (486, '嵌入式', '嵌入式硬件', 16);
INSERT INTO `tag` VALUES (487, '嵌入式', 'iot', 16);
INSERT INTO `tag` VALUES (488, '嵌入式', '嵌入式实时数据库', 16);
INSERT INTO `tag` VALUES (489, '嵌入式', 'rtdbs', 16);
INSERT INTO `tag` VALUES (490, '硬件开发', '硬件工程', 31);
INSERT INTO `tag` VALUES (491, '硬件开发', '驱动开发', 31);
INSERT INTO `tag` VALUES (492, '硬件开发', 'fpga开发', 31);
INSERT INTO `tag` VALUES (493, '硬件开发', 'dsp开发', 31);
INSERT INTO `tag` VALUES (494, '硬件开发', 'arm开发', 31);
INSERT INTO `tag` VALUES (495, '硬件开发', '材料工程', 31);
INSERT INTO `tag` VALUES (496, '硬件开发', '精益工程', 31);
INSERT INTO `tag` VALUES (497, '硬件开发', '射频工程', 31);
INSERT INTO `tag` VALUES (498, '硬件开发', '基带工程', 31);
INSERT INTO `tag` VALUES (499, '硬件开发', '硬件架构', 31);
INSERT INTO `tag` VALUES (500, '硬件开发', 'pcb工艺', 31);
INSERT INTO `tag` VALUES (501, '游戏', 'cocos2d', 29);
INSERT INTO `tag` VALUES (502, '游戏', '动画', 29);
INSERT INTO `tag` VALUES (503, '游戏', 'ogre', 29);
INSERT INTO `tag` VALUES (504, '游戏', 'unity', 29);
INSERT INTO `tag` VALUES (505, '游戏', '游戏引擎', 29);
INSERT INTO `tag` VALUES (506, '游戏', 'ar', 29);
INSERT INTO `tag` VALUES (507, '游戏', '3dsmax', 29);
INSERT INTO `tag` VALUES (508, '游戏', 'maya', 29);
INSERT INTO `tag` VALUES (509, '游戏', '贴图', 29);
INSERT INTO `tag` VALUES (510, '游戏', 'uv', 29);
INSERT INTO `tag` VALUES (511, '游戏', 'vr', 29);
INSERT INTO `tag` VALUES (512, '游戏', 'ue4', 29);
INSERT INTO `tag` VALUES (513, '游戏', 'houdini', 29);
INSERT INTO `tag` VALUES (514, '游戏', '着色器', 29);
INSERT INTO `tag` VALUES (515, '游戏', '材质', 29);
INSERT INTO `tag` VALUES (516, '游戏', '技术美术', 29);
INSERT INTO `tag` VALUES (517, '游戏', 'blender', 29);
INSERT INTO `tag` VALUES (518, '游戏', 'spine', 29);
INSERT INTO `tag` VALUES (519, '游戏', '图形渲染', 29);
INSERT INTO `tag` VALUES (520, '游戏', '虚幻', 29);
INSERT INTO `tag` VALUES (521, '游戏', 'ue5', 29);
INSERT INTO `tag` VALUES (522, '游戏', 'godot', 29);
INSERT INTO `tag` VALUES (523, '游戏', 'cryengine', 29);
INSERT INTO `tag` VALUES (524, '游戏', 'lumberyard', 29);
INSERT INTO `tag` VALUES (525, '游戏', 'mr', 29);
INSERT INTO `tag` VALUES (526, '游戏', 'xr', 29);
INSERT INTO `tag` VALUES (527, '游戏', 'cinema4d', 29);
INSERT INTO `tag` VALUES (528, '游戏', 'zbrush', 29);
INSERT INTO `tag` VALUES (529, '游戏', '3dcoat', 29);
INSERT INTO `tag` VALUES (530, '游戏', 'topogun', 29);
INSERT INTO `tag` VALUES (531, '游戏', 'rizomuv', 29);
INSERT INTO `tag` VALUES (532, '游戏', 'substance designer', 29);
INSERT INTO `tag` VALUES (533, '游戏', 'substance painter', 29);
INSERT INTO `tag` VALUES (534, '游戏', 'quixel', 29);
INSERT INTO `tag` VALUES (535, '游戏', '数字雕刻', 29);
INSERT INTO `tag` VALUES (536, '游戏', '重拓扑', 29);
INSERT INTO `tag` VALUES (537, '游戏', '骨骼绑定', 29);
INSERT INTO `tag` VALUES (538, '游戏', '关卡设计', 29);
INSERT INTO `tag` VALUES (539, '游戏', '游戏程序', 29);
INSERT INTO `tag` VALUES (540, '游戏', '游戏美术', 29);
INSERT INTO `tag` VALUES (541, '游戏', '游戏策划', 29);
INSERT INTO `tag` VALUES (542, '游戏', 'cascadeur', 29);
INSERT INTO `tag` VALUES (544, 'HarmonyOS', '华为', 1);
INSERT INTO `tag` VALUES (545, 'HarmonyOS', 'harmonyos', 1);
INSERT INTO `tag` VALUES (546, 'HarmonyOS', '华为云', 1);
INSERT INTO `tag` VALUES (547, 'HarmonyOS', '华为od', 1);
INSERT INTO `tag` VALUES (548, '微软技术', '.net', 20);
INSERT INTO `tag` VALUES (549, '微软技术', 'wpf', 20);
INSERT INTO `tag` VALUES (550, '微软技术', 'mfc', 20);
INSERT INTO `tag` VALUES (551, '微软技术', 'sharepoint', 20);
INSERT INTO `tag` VALUES (552, '微软技术', 'linq', 20);
INSERT INTO `tag` VALUES (553, '微软技术', 'microsoft', 20);
INSERT INTO `tag` VALUES (554, '微软技术', 'azure', 20);
INSERT INTO `tag` VALUES (555, '微软技术', 'hololens', 20);
INSERT INTO `tag` VALUES (556, '微软技术', 'mssql', 20);
INSERT INTO `tag` VALUES (557, '微软技术', '.netcore', 20);
INSERT INTO `tag` VALUES (558, '微软技术', 'xbox', 20);
INSERT INTO `tag` VALUES (569, '操作系统', 'linux', 22);
INSERT INTO `tag` VALUES (570, '操作系统', 'ubuntu', 22);
INSERT INTO `tag` VALUES (571, '操作系统', 'centos', 22);
INSERT INTO `tag` VALUES (572, '操作系统', 'gnu', 22);
INSERT INTO `tag` VALUES (573, '操作系统', 'risc-v', 22);
INSERT INTO `tag` VALUES (574, '操作系统', 'blackberry', 22);
INSERT INTO `tag` VALUES (578, '搜索', 'lucene', 21);
INSERT INTO `tag` VALUES (579, '搜索', 'solr', 21);
INSERT INTO `tag` VALUES (580, '搜索', 'sphinx', 21);
INSERT INTO `tag` VALUES (581, '搜索', '搜索引擎', 21);
INSERT INTO `tag` VALUES (582, '搜索', '全文检索', 21);
INSERT INTO `tag` VALUES (583, '搜索', '中文分词', 21);
INSERT INTO `tag` VALUES (585, '设计模式', 'uml', 37);
INSERT INTO `tag` VALUES (586, '设计模式', '单例模式', 37);
INSERT INTO `tag` VALUES (587, '设计模式', '开闭原则', 37);
INSERT INTO `tag` VALUES (588, '设计模式', '命令模式', 37);
INSERT INTO `tag` VALUES (589, '设计模式', '代理模式', 37);
INSERT INTO `tag` VALUES (590, '设计模式', '桥接模式', 37);
INSERT INTO `tag` VALUES (591, '设计模式', '观察者模式', 37);
INSERT INTO `tag` VALUES (592, '设计模式', '访问者模式', 37);
INSERT INTO `tag` VALUES (593, '设计模式', '迭代器模式', 37);
INSERT INTO `tag` VALUES (594, '设计模式', '简单工厂模式', 37);
INSERT INTO `tag` VALUES (595, '设计模式', '里氏替换原则', 37);
INSERT INTO `tag` VALUES (596, '设计模式', '依赖倒置原则', 37);
INSERT INTO `tag` VALUES (597, '设计模式', '单一职责原则', 37);
INSERT INTO `tag` VALUES (598, '设计模式', '接口隔离原则', 37);
INSERT INTO `tag` VALUES (599, '设计模式', '迪米特法则', 37);
INSERT INTO `tag` VALUES (600, '设计模式', '合成复用原则', 37);
INSERT INTO `tag` VALUES (601, '设计模式', '原型模式', 37);
INSERT INTO `tag` VALUES (602, '设计模式', '工厂方法模式', 37);
INSERT INTO `tag` VALUES (603, '设计模式', '抽象工厂模式', 37);
INSERT INTO `tag` VALUES (604, '设计模式', '建造者模式', 37);
INSERT INTO `tag` VALUES (605, '设计模式', '适配器模式', 37);
INSERT INTO `tag` VALUES (606, '设计模式', '装饰器模式', 37);
INSERT INTO `tag` VALUES (607, '设计模式', '外观模式', 37);
INSERT INTO `tag` VALUES (608, '设计模式', '享元模式', 37);
INSERT INTO `tag` VALUES (609, '设计模式', '组合模式', 37);
INSERT INTO `tag` VALUES (610, '设计模式', '模板方法模式', 37);
INSERT INTO `tag` VALUES (611, '设计模式', '策略模式', 37);
INSERT INTO `tag` VALUES (612, '设计模式', '责任链模式', 37);
INSERT INTO `tag` VALUES (613, '设计模式', '状态模式', 37);
INSERT INTO `tag` VALUES (614, '设计模式', '中介者模式', 37);
INSERT INTO `tag` VALUES (615, '设计模式', '备忘录模式', 37);
INSERT INTO `tag` VALUES (616, '设计模式', '解释器模式', 37);
INSERT INTO `tag` VALUES (617, '设计模式', '设计模式', 37);
INSERT INTO `tag` VALUES (618, '测试', '单元测试', 28);
INSERT INTO `tag` VALUES (619, '测试', 'selenium', 28);
INSERT INTO `tag` VALUES (620, '测试', 'jira', 28);
INSERT INTO `tag` VALUES (621, '测试', '测试工具', 28);
INSERT INTO `tag` VALUES (622, '测试', '压力测试', 28);
INSERT INTO `tag` VALUES (623, '测试', '测试用例', 28);
INSERT INTO `tag` VALUES (624, '测试', 'ab测试', 28);
INSERT INTO `tag` VALUES (625, '测试', '集成测试', 28);
INSERT INTO `tag` VALUES (626, '测试', '模块测试', 28);
INSERT INTO `tag` VALUES (627, '测试', '测试覆盖率', 28);
INSERT INTO `tag` VALUES (628, '测试', '安全性测试', 28);
INSERT INTO `tag` VALUES (629, '测试', '威胁分析', 28);
INSERT INTO `tag` VALUES (630, '测试', '可用性测试', 28);
INSERT INTO `tag` VALUES (631, '测试', '功能测试', 28);
INSERT INTO `tag` VALUES (632, '测试', 'metersphere', 28);
INSERT INTO `tag` VALUES (633, '测试', 'appium', 28);
INSERT INTO `tag` VALUES (634, '测试', 'jmeter', 28);
INSERT INTO `tag` VALUES (635, '测试', 'testlink', 28);
INSERT INTO `tag` VALUES (637, '云平台', '云计算', 7);
INSERT INTO `tag` VALUES (638, '云平台', '七牛云存储', 7);
INSERT INTO `tag` VALUES (639, '云平台', '百度云', 7);
INSERT INTO `tag` VALUES (640, '云平台', '腾讯云', 7);
INSERT INTO `tag` VALUES (641, '云平台', '阿里云', 7);
INSERT INTO `tag` VALUES (642, '云平台', 'aws', 7);
INSERT INTO `tag` VALUES (643, '云平台', '京东云', 7);
INSERT INTO `tag` VALUES (644, '云平台', '火山引擎', 7);
INSERT INTO `tag` VALUES (645, '云平台', 'CSDN开发云', 7);
INSERT INTO `tag` VALUES (646, '云平台', 'googlecloud', 7);
INSERT INTO `tag` VALUES (649, '软件工程', 'tfs', 38);
INSERT INTO `tag` VALUES (650, '软件工程', '需求分析', 38);
INSERT INTO `tag` VALUES (651, '软件工程', '结对编程', 38);
INSERT INTO `tag` VALUES (652, '软件工程', '团队开发', 38);
INSERT INTO `tag` VALUES (653, '软件工程', 'scrum', 38);
INSERT INTO `tag` VALUES (654, '软件工程', 'sprint', 38);
INSERT INTO `tag` VALUES (655, '软件工程', '个人开发', 38);
INSERT INTO `tag` VALUES (656, '软件工程', '规格说明书', 38);
INSERT INTO `tag` VALUES (657, '软件工程', '极限编程', 38);
INSERT INTO `tag` VALUES (658, '软件工程', '敏捷流程', 38);
INSERT INTO `tag` VALUES (659, '软件工程', '性能优化', 38);
INSERT INTO `tag` VALUES (660, '软件工程', '新媒体运营', 38);
INSERT INTO `tag` VALUES (661, '软件工程', '内容运营', 38);
INSERT INTO `tag` VALUES (662, '软件工程', '用户运营', 38);
INSERT INTO `tag` VALUES (663, '软件工程', '产品运营', 38);
INSERT INTO `tag` VALUES (664, '软件工程', 'axure', 38);
INSERT INTO `tag` VALUES (665, '软件工程', '墨刀', 38);
INSERT INTO `tag` VALUES (666, '软件工程', '流量运营', 38);
INSERT INTO `tag` VALUES (667, '软件工程', '交互', 38);
INSERT INTO `tag` VALUES (668, '软件工程', 'ux', 38);
INSERT INTO `tag` VALUES (669, '软件工程', 'ui', 38);
INSERT INTO `tag` VALUES (670, '软件工程', '开源', 38);
INSERT INTO `tag` VALUES (671, '软件工程', '软件工程', 38);
INSERT INTO `tag` VALUES (672, '软件工程', 'tdd', 38);
INSERT INTO `tag` VALUES (673, '软件工程', '代码复审', 38);
INSERT INTO `tag` VALUES (674, '软件工程', '重构', 38);
INSERT INTO `tag` VALUES (675, '软件工程', '源代码管理', 38);
INSERT INTO `tag` VALUES (676, '软件工程', '代码规范', 38);
INSERT INTO `tag` VALUES (677, '软件工程', '软件构建', 38);
INSERT INTO `tag` VALUES (678, '软件工程', 'cmmi', 38);
INSERT INTO `tag` VALUES (679, '软件工程', '甘特图', 38);
INSERT INTO `tag` VALUES (680, '软件工程', '流程图', 38);
INSERT INTO `tag` VALUES (681, '软件工程', '代码覆盖率', 38);
INSERT INTO `tag` VALUES (682, '软件工程', 'bug', 38);
INSERT INTO `tag` VALUES (683, '软件工程', '设计规范', 38);
INSERT INTO `tag` VALUES (684, '软件工程', 'issue', 38);
INSERT INTO `tag` VALUES (685, '软件工程', 'redmine', 38);
INSERT INTO `tag` VALUES (686, '软件工程', 'teambition', 38);
INSERT INTO `tag` VALUES (687, '软件工程', '产品经理', 38);
INSERT INTO `tag` VALUES (694, '区块链', '区块链', 12);
INSERT INTO `tag` VALUES (695, '区块链', '智能合约', 12);
INSERT INTO `tag` VALUES (696, '区块链', '信任链', 12);
INSERT INTO `tag` VALUES (697, '区块链', '去中心化', 12);
INSERT INTO `tag` VALUES (698, '区块链', '分布式账本', 12);
INSERT INTO `tag` VALUES (699, '区块链', '共识算法', 12);
INSERT INTO `tag` VALUES (700, '区块链', '同态加密', 12);
INSERT INTO `tag` VALUES (701, '区块链', '零知识证明', 12);
INSERT INTO `tag` VALUES (702, '区块链', 'web3', 12);
INSERT INTO `tag` VALUES (703, '数学', '线性代数', 24);
INSERT INTO `tag` VALUES (704, '数学', '矩阵', 24);
INSERT INTO `tag` VALUES (705, '数学', '概率论', 24);
INSERT INTO `tag` VALUES (706, '数学', '拓扑学', 24);
INSERT INTO `tag` VALUES (707, '数学', '抽象代数', 24);
INSERT INTO `tag` VALUES (708, '数学', '几何学', 24);
INSERT INTO `tag` VALUES (709, '数学', '图论', 24);
INSERT INTO `tag` VALUES (710, '数学', '傅立叶分析', 24);
INSERT INTO `tag` VALUES (711, '数学', '数学建模', 24);
INSERT INTO `tag` VALUES (713, '运维', '负载均衡', 39);
INSERT INTO `tag` VALUES (714, '运维', '服务器', 39);
INSERT INTO `tag` VALUES (715, '运维', '运维', 39);
INSERT INTO `tag` VALUES (716, '运维', 'ssh', 39);
INSERT INTO `tag` VALUES (717, '运维', 'vagrant', 39);
INSERT INTO `tag` VALUES (718, '运维', 'debian', 39);
INSERT INTO `tag` VALUES (719, '运维', 'fabric', 39);
INSERT INTO `tag` VALUES (720, '运维', '自动化', 39);
INSERT INTO `tag` VALUES (721, '运维', '系统架构', 39);
INSERT INTO `tag` VALUES (722, '运维', '网络', 39);
INSERT INTO `tag` VALUES (723, '运维', '运维开发', 39);
INSERT INTO `tag` VALUES (724, '运维', 'graylog', 39);
INSERT INTO `tag` VALUES (725, '运维', 'elk', 39);
INSERT INTO `tag` VALUES (726, '运维', 'ezone', 39);
INSERT INTO `tag` VALUES (727, '运维', 'gitea', 39);
INSERT INTO `tag` VALUES (728, '运维', 'tekton', 39);
INSERT INTO `tag` VALUES (729, '运维', 'puppet', 39);
INSERT INTO `tag` VALUES (730, '运维', 'saltstack', 39);
INSERT INTO `tag` VALUES (731, '运维', 'ansible', 39);
INSERT INTO `tag` VALUES (732, '运维', 'prometheus', 39);
INSERT INTO `tag` VALUES (733, '运维', 'skywalking', 39);
INSERT INTO `tag` VALUES (734, '运维', 'sentry', 39);
INSERT INTO `tag` VALUES (735, '运维', 'zabbix', 39);
INSERT INTO `tag` VALUES (736, '运维', 'grafana', 39);
INSERT INTO `tag` VALUES (737, '运维', 'kong', 39);
INSERT INTO `tag` VALUES (738, '运维', 'openresty', 39);
INSERT INTO `tag` VALUES (739, '运维', 'lvs', 39);
INSERT INTO `tag` VALUES (750, '网络空间安全', '安全', 35);
INSERT INTO `tag` VALUES (751, '网络空间安全', '系统安全', 35);
INSERT INTO `tag` VALUES (752, '网络空间安全', 'web安全', 35);
INSERT INTO `tag` VALUES (753, '网络空间安全', '安全架构', 35);
INSERT INTO `tag` VALUES (754, '网络空间安全', '密码学', 35);
INSERT INTO `tag` VALUES (755, '网络空间安全', '可信计算技术', 35);
INSERT INTO `tag` VALUES (756, '网络空间安全', '网络攻击模型', 35);
INSERT INTO `tag` VALUES (757, '网络空间安全', 'ddos', 35);
INSERT INTO `tag` VALUES (758, '网络空间安全', '安全威胁分析', 35);
INSERT INTO `tag` VALUES (759, '网络空间安全', '计算机网络', 35);
INSERT INTO `tag` VALUES (761, '服务器', '缓存', 27);
INSERT INTO `tag` VALUES (762, '服务器', 'unix', 27);
INSERT INTO `tag` VALUES (767, '学习和成长', '蓝桥杯', 15);
INSERT INTO `tag` VALUES (768, '学习和成长', 'pat考试', 15);
INSERT INTO `tag` VALUES (769, '学习和成长', '职场和发展', 15);
INSERT INTO `tag` VALUES (770, '学习和成长', '面试', 15);
INSERT INTO `tag` VALUES (771, '学习和成长', '程序人生', 15);
INSERT INTO `tag` VALUES (772, '学习和成长', '业界资讯', 15);
INSERT INTO `tag` VALUES (773, '学习和成长', '学习方法', 15);
INSERT INTO `tag` VALUES (774, '学习和成长', '跳槽', 15);
INSERT INTO `tag` VALUES (775, '学习和成长', '考研', 15);
INSERT INTO `tag` VALUES (776, '学习和成长', '高考', 15);
INSERT INTO `tag` VALUES (777, '学习和成长', '改行学it', 15);
INSERT INTO `tag` VALUES (778, '学习和成长', '创业创新', 15);
INSERT INTO `tag` VALUES (779, '学习和成长', '远程工作', 15);
INSERT INTO `tag` VALUES (780, '学习和成长', '程序员创富', 15);
INSERT INTO `tag` VALUES (782, '教育培训', 'c1认证', 23);
INSERT INTO `tag` VALUES (783, '教育培训', 'c4java', 23);
INSERT INTO `tag` VALUES (784, '教育培训', 'c4python', 23);
INSERT INTO `tag` VALUES (785, '教育培训', 'c4前端', 23);
INSERT INTO `tag` VALUES (786, '教育培训', 'c5底层', 23);
INSERT INTO `tag` VALUES (787, '教育培训', 'c5交付', 23);
INSERT INTO `tag` VALUES (788, '教育培训', 'c5全栈', 23);
INSERT INTO `tag` VALUES (789, '用户体验设计', 'illustrator', 30);
INSERT INTO `tag` VALUES (790, '用户体验设计', '平面', 30);
INSERT INTO `tag` VALUES (791, '用户体验设计', 'photoshop', 30);
INSERT INTO `tag` VALUES (792, '用户体验设计', 'sketch', 30);
INSERT INTO `tag` VALUES (793, '用户体验设计', '3d', 30);
INSERT INTO `tag` VALUES (794, '用户体验设计', '人机交互', 30);
INSERT INTO `tag` VALUES (795, '用户体验设计', '设计语言', 30);
INSERT INTO `tag` VALUES (796, '用户体验设计', '信息可视化', 30);
INSERT INTO `tag` VALUES (797, '用户体验设计', 'figma', 30);
INSERT INTO `tag` VALUES (798, '用户体验设计', 'adobe', 30);
INSERT INTO `tag` VALUES (803, '音视频', '音视频', 41);
INSERT INTO `tag` VALUES (804, '音视频', '视频编解码', 41);
INSERT INTO `tag` VALUES (805, '音视频', '实时音视频', 41);
INSERT INTO `tag` VALUES (806, '音视频', 'webrtc', 41);
INSERT INTO `tag` VALUES (807, '音视频', '实时互动', 41);
INSERT INTO `tag` VALUES (808, '音视频', 'mpeg-1', 41);
INSERT INTO `tag` VALUES (809, '音视频', 'mpeg-2', 41);
INSERT INTO `tag` VALUES (810, '音视频', 'vc-1', 41);
INSERT INTO `tag` VALUES (811, '音视频', 'vp8', 41);
INSERT INTO `tag` VALUES (812, '音视频', 'vp9', 41);
INSERT INTO `tag` VALUES (813, '音视频', 'h.264', 41);
INSERT INTO `tag` VALUES (814, '音视频', 'h.265', 41);
INSERT INTO `tag` VALUES (815, '音视频', 'av1', 41);
INSERT INTO `tag` VALUES (816, '音视频', 'h.266', 41);
INSERT INTO `tag` VALUES (817, '音视频', 'avs3', 41);
INSERT INTO `tag` VALUES (818, '音视频', 'pcm', 41);
INSERT INTO `tag` VALUES (819, '音视频', 'g726', 41);
INSERT INTO `tag` VALUES (820, '音视频', 'adpcm', 41);
INSERT INTO `tag` VALUES (821, '音视频', 'lpcm', 41);
INSERT INTO `tag` VALUES (822, '音视频', 'g711', 41);
INSERT INTO `tag` VALUES (823, '音视频', 'aac', 41);
INSERT INTO `tag` VALUES (828, '行业数字化', '金融', 36);
INSERT INTO `tag` VALUES (829, '行业数字化', '教育电商', 36);
INSERT INTO `tag` VALUES (830, '行业数字化', '传媒', 36);
INSERT INTO `tag` VALUES (831, '行业数字化', '健康医疗', 36);
INSERT INTO `tag` VALUES (832, '行业数字化', '游戏', 36);
INSERT INTO `tag` VALUES (833, '行业数字化', '娱乐', 36);
INSERT INTO `tag` VALUES (834, '行业数字化', '社交电子', 36);
INSERT INTO `tag` VALUES (835, '行业数字化', '媒体', 36);
INSERT INTO `tag` VALUES (836, '行业数字化', '零售', 36);
INSERT INTO `tag` VALUES (837, '行业数字化', '交通物流', 36);
INSERT INTO `tag` VALUES (838, '行业数字化', '制造', 36);
INSERT INTO `tag` VALUES (839, '行业数字化', '能源', 36);
INSERT INTO `tag` VALUES (840, '行业数字化', '旅游', 36);
INSERT INTO `tag` VALUES (841, '行业数字化', '政务', 36);
INSERT INTO `tag` VALUES (842, '非IT技术', '交友', 40);
INSERT INTO `tag` VALUES (843, '非IT技术', '求职招聘', 40);
INSERT INTO `tag` VALUES (844, '非IT技术', '科技', 40);
INSERT INTO `tag` VALUES (845, '非IT技术', '玩游戏', 40);
INSERT INTO `tag` VALUES (846, '非IT技术', '节日', 40);
INSERT INTO `tag` VALUES (847, '非IT技术', '学习', 40);
INSERT INTO `tag` VALUES (848, '非IT技术', '宠物', 40);
INSERT INTO `tag` VALUES (849, '非IT技术', '帅哥', 40);
INSERT INTO `tag` VALUES (850, '非IT技术', '汽车', 40);
INSERT INTO `tag` VALUES (851, '非IT技术', '美女', 40);
INSERT INTO `tag` VALUES (852, '非IT技术', '美食', 40);
INSERT INTO `tag` VALUES (853, '非IT技术', '风景', 40);
INSERT INTO `tag` VALUES (854, '非IT技术', '生活', 40);
INSERT INTO `tag` VALUES (857, '前沿技术', '低代码', 10);
INSERT INTO `tag` VALUES (858, '前沿技术', '智能家居', 10);
INSERT INTO `tag` VALUES (859, '前沿技术', '无人机', 10);
INSERT INTO `tag` VALUES (860, '前沿技术', '车载系统', 10);
INSERT INTO `tag` VALUES (861, '前沿技术', '量子计算', 10);
INSERT INTO `tag` VALUES (862, '前沿技术', '智能硬件', 10);
INSERT INTO `tag` VALUES (863, '前沿技术', 'rpa', 10);
INSERT INTO `tag` VALUES (864, '前沿技术', 'wasm', 10);
INSERT INTO `tag` VALUES (865, '前沿技术', '机器人', 10);
INSERT INTO `tag` VALUES (866, '前沿技术', 'c++20', 10);
INSERT INTO `tag` VALUES (867, '前沿技术', 'python3.11', 10);
INSERT INTO `tag` VALUES (868, '前沿技术', 'java18', 10);
INSERT INTO `tag` VALUES (869, '前沿技术', '论文阅读', 10);
INSERT INTO `tag` VALUES (870, '前沿技术', 'c++23', 10);
INSERT INTO `tag` VALUES (871, '前沿技术', 'es13', 10);
INSERT INTO `tag` VALUES (872, '前沿技术', 'c#11.0', 10);
INSERT INTO `tag` VALUES (873, '前沿技术', 'ruby3.1.2', 10);
INSERT INTO `tag` VALUES (874, '前沿技术', 'qt6.3', 10);
INSERT INTO `tag` VALUES (875, '前沿技术', 'lua5.4', 10);
INSERT INTO `tag` VALUES (876, '前沿技术', 'perl5.36.0', 10);
INSERT INTO `tag` VALUES (877, '前沿技术', 'r语言-4.2.1', 10);
INSERT INTO `tag` VALUES (878, '前沿技术', 'scala3.1.2', 10);
INSERT INTO `tag` VALUES (879, '前沿技术', 'swift5.6.3', 10);
INSERT INTO `tag` VALUES (880, '前沿技术', 'go1.19', 10);
INSERT INTO `tag` VALUES (881, '前沿技术', 'webgl', 10);
INSERT INTO `tag` VALUES (882, '前沿技术', 'AIGC', 10);
INSERT INTO `tag` VALUES (883, '前沿技术', 'AI-native', 10);
INSERT INTO `tag` VALUES (884, '前沿技术', 'inscode', 10);
INSERT INTO `tag` VALUES (885, '前沿技术', 'apple vision pro', 10);
INSERT INTO `tag` VALUES (886, '前沿技术', '空间计算', 10);
INSERT INTO `tag` VALUES (900, 'IT工具', 'wps', 3);
INSERT INTO `tag` VALUES (901, 'IT工具', 'foxmail', 3);
INSERT INTO `tag` VALUES (902, 'IT工具', 'notion', 3);
INSERT INTO `tag` VALUES (903, 'IT工具', 'word', 3);
INSERT INTO `tag` VALUES (904, 'IT工具', 'excel', 3);
INSERT INTO `tag` VALUES (905, 'IT工具', 'powerpoint', 3);
INSERT INTO `tag` VALUES (906, 'IT工具', 'outlook', 3);
INSERT INTO `tag` VALUES (907, 'IT工具', 'onenote', 3);
INSERT INTO `tag` VALUES (908, 'IT工具', 'xmind', 3);
INSERT INTO `tag` VALUES (909, 'IT工具', 'teamviewer', 3);
INSERT INTO `tag` VALUES (910, 'IT工具', '亿图图示', 3);
INSERT INTO `tag` VALUES (911, 'IT工具', '企业微信', 3);
INSERT INTO `tag` VALUES (912, 'IT工具', '钉钉', 3);
INSERT INTO `tag` VALUES (913, 'IT工具', '腾讯会议', 3);
INSERT INTO `tag` VALUES (914, 'IT工具', '福昕阅读器', 3);
INSERT INTO `tag` VALUES (915, 'IT工具', 'draw.io', 3);
INSERT INTO `tag` VALUES (916, 'IT工具', '石墨文档', 3);
INSERT INTO `tag` VALUES (917, 'IT工具', 'worktile', 3);
INSERT INTO `tag` VALUES (918, 'IT工具', 'asana', 3);
INSERT INTO `tag` VALUES (919, 'IT工具', 'pingcode', 3);
INSERT INTO `tag` VALUES (920, 'IT工具', 'atlassian', 3);
INSERT INTO `tag` VALUES (921, 'IT工具', 'zoom', 3);
INSERT INTO `tag` VALUES (922, 'IT工具', 'canva可画', 3);
INSERT INTO `tag` VALUES (923, 'IT工具', 'processon', 3);
INSERT INTO `tag` VALUES (924, 'IT工具', '蓝湖', 3);
INSERT INTO `tag` VALUES (925, 'IT工具', '飞书', 3);
INSERT INTO `tag` VALUES (926, 'IT工具', 'winrar', 3);
INSERT INTO `tag` VALUES (927, 'IT工具', '7-zip', 3);
INSERT INTO `tag` VALUES (928, 'IT工具', '火绒安全', 3);
INSERT INTO `tag` VALUES (929, 'IT工具', 'faststone capture', 3);
INSERT INTO `tag` VALUES (930, 'IT工具', '格式工厂', 3);
INSERT INTO `tag` VALUES (931, 'IT工具', 'ffmpeg', 3);
INSERT INTO `tag` VALUES (932, 'IT工具', 'adobe acrobat reader', 3);
INSERT INTO `tag` VALUES (933, 'IT工具', 'dreamweaver', 3);
INSERT INTO `tag` VALUES (934, 'IT工具', 'notepad++', 3);
INSERT INTO `tag` VALUES (935, 'IT工具', 'editplus', 3);
INSERT INTO `tag` VALUES (936, 'IT工具', 'onedrive', 3);
INSERT INTO `tag` VALUES (937, 'IT工具', 'icloud', 3);
INSERT INTO `tag` VALUES (938, 'IT工具', 'everything', 3);
INSERT INTO `tag` VALUES (939, 'IT工具', 'diskgenius', 3);
INSERT INTO `tag` VALUES (940, 'IT工具', '有道云笔记', 3);
INSERT INTO `tag` VALUES (941, 'IT工具', '印象笔记', 3);
INSERT INTO `tag` VALUES (942, 'IT工具', '网易邮箱大师', 3);
INSERT INTO `tag` VALUES (943, 'IT工具', 'idm', 3);
INSERT INTO `tag` VALUES (950, '开发组件', 'pdf', 18);
INSERT INTO `tag` VALUES (951, '开源', '开源软件', 19);
INSERT INTO `tag` VALUES (952, '开源', '开源协议', 19);
INSERT INTO `tag` VALUES (953, '开源', 'gitcode', 19);
INSERT INTO `tag` VALUES (954, '开源', 'gitee', 19);
INSERT INTO `tag` VALUES (955, '开源', '开放原子', 19);
INSERT INTO `tag` VALUES (956, '开源', 'ossinsight', 19);
INSERT INTO `tag` VALUES (961, '其他', '微信', 9);
INSERT INTO `tag` VALUES (962, '其他', '新浪微博', 9);
INSERT INTO `tag` VALUES (963, '其他', 'facebook', 9);
INSERT INTO `tag` VALUES (964, '其他', '微信公众平台', 9);
INSERT INTO `tag` VALUES (965, '其他', '百度', 9);
INSERT INTO `tag` VALUES (966, '其他', 'twitter', 9);
INSERT INTO `tag` VALUES (967, '其他', 'paddle', 9);
INSERT INTO `tag` VALUES (968, '其他', '微信开放平台', 9);
INSERT INTO `tag` VALUES (969, '其他', '其他', 9);
INSERT INTO `tag` VALUES (970, '其他', 'oneapi', 9);
INSERT INTO `tag` VALUES (971, '其他', 'segmentfault', 9);
INSERT INTO `tag` VALUES (972, '其他', '经验分享', 9);
INSERT INTO `tag` VALUES (973, '其他', '课程设计', 9);
INSERT INTO `tag` VALUES (974, '其他', '笔记', 9);
INSERT INTO `tag` VALUES (976, '3C硬件', '电脑', 0);
INSERT INTO `tag` VALUES (977, '3C硬件', '智能手机', 0);
INSERT INTO `tag` VALUES (978, '3C硬件', '智能路由器', 0);
INSERT INTO `tag` VALUES (979, '3C硬件', '计算机外设', 0);
INSERT INTO `tag` VALUES (980, '3C硬件', '数码相机', 0);
INSERT INTO `tag` VALUES (981, '3C硬件', '智能手表', 0);
INSERT INTO `tag` VALUES (982, '3C硬件', '智能电视', 0);
INSERT INTO `tag` VALUES (983, '3C硬件', '游戏机', 0);
INSERT INTO `tag` VALUES (984, '3C硬件', '电视盒子', 0);
INSERT INTO `tag` VALUES (985, '3C硬件', '智能音箱', 0);
INSERT INTO `tag` VALUES (986, 'AIGC', 'gpt', 2);
INSERT INTO `tag` VALUES (987, 'AIGC', 'llama', 2);
INSERT INTO `tag` VALUES (988, 'AIGC', 'midjourney', 2);
INSERT INTO `tag` VALUES (989, 'AIGC', 'whisper', 2);
INSERT INTO `tag` VALUES (990, 'AIGC', 'copilot', 2);
INSERT INTO `tag` VALUES (991, 'AIGC', '华为snap', 2);
INSERT INTO `tag` VALUES (992, 'AIGC', 'AI写作', 2);
INSERT INTO `tag` VALUES (993, 'AIGC', 'AI编程', 2);
INSERT INTO `tag` VALUES (994, 'AIGC', 'mlir', 2);
INSERT INTO `tag` VALUES (995, 'AIGC', 'gpu算力', 2);
INSERT INTO `tag` VALUES (996, 'AIGC', 'langchain', 2);
INSERT INTO `tag` VALUES (997, 'AIGC', 'prompt', 2);
INSERT INTO `tag` VALUES (998, 'AIGC', 'palm', 2);
INSERT INTO `tag` VALUES (999, 'AIGC', 'agi', 2);
INSERT INTO `tag` VALUES (1000, 'AIGC', 'embedding', 2);
INSERT INTO `tag` VALUES (1001, 'AIGC', 'mcp', 2);

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int NOT NULL COMMENT '用户id',
  `message_id` int NOT NULL COMMENT '消息id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_message
-- ----------------------------
INSERT INTO `user_message` VALUES (1, 1, 1);
INSERT INTO `user_message` VALUES (2, 1, 2);
INSERT INTO `user_message` VALUES (3, 1, 3);
INSERT INTO `user_message` VALUES (4, 1, 4);
INSERT INTO `user_message` VALUES (5, 1, 5);
INSERT INTO `user_message` VALUES (6, 1, 6);
INSERT INTO `user_message` VALUES (7, 1, 7);
INSERT INTO `user_message` VALUES (8, 1, 8);

SET FOREIGN_KEY_CHECKS = 1;
