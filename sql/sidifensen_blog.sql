/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : sidifensen_blog

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 18/08/2025 14:23:00
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
  INDEX `idx_is_deleted_show_status`(`is_deleted` ASC, `show_status` ASC) USING BTREE,
  INDEX `idx_is_deleted_user_id`(`is_deleted` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of album
-- ----------------------------
INSERT INTO `album` VALUES (1, 1, '风景1', 'http://14.103.112.11:9000/sidifensen-blog/album/1/1/14d56ca203964a3da8f47316d3f1e8d2.jpg', 0, '2025-08-15 01:12:18', '2025-08-15 01:12:18', 0);
INSERT INTO `album` VALUES (2, 1, '壁纸', 'http://14.103.112.11:9000/sidifensen-blog/album/1/2/658334308098465fbe66b5d4b3266ef9.webp', 0, '2025-08-15 01:16:51', '2025-08-15 01:16:51', 0);
INSERT INTO `album` VALUES (3, 1, '电脑壁纸', 'http://14.103.112.11:9000/sidifensen-blog/album/1/3/1d29f054b0f34070a6889dc49bf35ae3.webp', 0, '2025-08-15 01:17:16', '2025-08-15 01:17:16', 0);
INSERT INTO `album` VALUES (4, 1, '精美壁纸', 'http://14.103.112.11:9000/sidifensen-blog/album/1/4/4bed147d7d1e48e2a5cffe66e2c31a24.webp', 0, '2025-08-15 01:24:06', '2025-08-15 01:24:06', 0);
INSERT INTO `album` VALUES (5, 1, '1', NULL, 0, '2025-08-15 02:11:13', '2025-08-15 02:11:13', 1);
INSERT INTO `album` VALUES (6, 1, '优美壁纸', 'http://14.103.112.11:9000/sidifensen-blog/album/1/6/61d91658e974434186b382cd10955d82.webp', 0, '2025-08-17 00:23:38', '2025-08-17 00:23:38', 0);
INSERT INTO `album` VALUES (7, 1, '手机壁纸', 'http://14.103.112.11:9000/sidifensen-blog/album/1/7/651c690cbca74ecda4b794b2d8d21f81.webp', 0, '2025-08-17 00:24:19', '2025-08-17 00:24:19', 0);
INSERT INTO `album` VALUES (8, 1, '大自然壁纸', 'http://14.103.112.11:9000/sidifensen-blog/album/1/8/253c190cf34e493096297e258fc25c42.webp', 0, '2025-08-17 00:45:42', '2025-08-17 00:45:42', 0);
INSERT INTO `album` VALUES (9, 1, '人文壁纸', 'http://14.103.112.11:9000/sidifensen-blog/album/1/9/7eb75aa3fe134277829afa0861bfe6f7.webp', 0, '2025-08-17 00:45:47', '2025-08-17 00:45:47', 0);
INSERT INTO `album` VALUES (10, 1, '人像', 'http://14.103.112.11:9000/sidifensen-blog/album/1/10/6df7d4da9241446a8471c4ddaee6860d.webp', 0, '2025-08-17 00:45:52', '2025-08-17 00:45:52', 1);
INSERT INTO `album` VALUES (11, 1, '宠物', 'http://14.103.112.11:9000/sidifensen-blog/album/1/11/e1a22085ec6d4c2a8e9020c4766920e6.webp', 0, '2025-08-17 00:46:01', '2025-08-17 00:46:01', 1);
INSERT INTO `album` VALUES (12, 1, '科幻', 'http://14.103.112.11:9000/sidifensen-blog/album/1/12/5636972df0e04b37b68c3268969e1d00.webp', 0, '2025-08-17 00:46:17', '2025-08-17 00:46:17', 1);
INSERT INTO `album` VALUES (13, 1, '宇宙', 'http://14.103.112.11:9000/sidifensen-blog/album/1/13/fd760317ef5745968a71200c65033c8b.webp', 0, '2025-08-18 01:32:00', '2025-08-18 01:32:00', 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1018 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of album_photo
-- ----------------------------
INSERT INTO `album_photo` VALUES (1001, 2, 1001);
INSERT INTO `album_photo` VALUES (1002, 2, 1002);
INSERT INTO `album_photo` VALUES (1003, 2, 1003);
INSERT INTO `album_photo` VALUES (1004, 2, 1004);
INSERT INTO `album_photo` VALUES (1005, 3, 1005);
INSERT INTO `album_photo` VALUES (1006, 2, 1006);
INSERT INTO `album_photo` VALUES (1008, 4, 1008);
INSERT INTO `album_photo` VALUES (1009, 6, 1009);
INSERT INTO `album_photo` VALUES (1010, 7, 1010);
INSERT INTO `album_photo` VALUES (1011, 8, 1011);
INSERT INTO `album_photo` VALUES (1012, 9, 1012);
INSERT INTO `album_photo` VALUES (1013, 10, 1013);
INSERT INTO `album_photo` VALUES (1014, 11, 1014);
INSERT INTO `album_photo` VALUES (1015, 12, 1015);
INSERT INTO `album_photo` VALUES (1016, 13, 1016);
INSERT INTO `album_photo` VALUES (1017, 2, 1017);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '消息类型 0-系统 1-评论 2-点赞 3-收藏 4-关注',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 0, 0, '{\"text\":\"图片 1013 需要人工审核\"}', '2025-08-18 00:45:42', '2025-08-18 00:45:42', 0);
INSERT INTO `message` VALUES (2, 0, 0, '{\"text\":\"图片 1014 需要人工审核\"}', '2025-08-18 01:01:53', '2025-08-18 01:01:53', 0);
INSERT INTO `message` VALUES (3, 0, 0, '{\"text\":\"图片 1015 需要人工审核\"}', '2025-08-18 01:06:35', '2025-08-18 01:06:35', 0);
INSERT INTO `message` VALUES (4, 0, 0, '{\"text\":\"图片 1016 需要人工审核\"}', '2025-08-18 01:32:10', '2025-08-18 01:32:10', 0);
INSERT INTO `message` VALUES (5, 0, 0, '{\"text\":\"图片 1017 需要人工审核\"}', '2025-08-18 01:37:47', '2025-08-18 01:37:47', 0);

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
  INDEX `idx_photo_user_album_status_create_time`(`user_id` ASC, `examine_status` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_deleted_id_status`(`is_deleted` ASC, `id` ASC, `examine_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1018 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of photo
-- ----------------------------
INSERT INTO `photo` VALUES (1001, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/2/d93fa7e492d542bf834cd08a553f9955.jpg', 1, '2025-08-15 23:38:47', '2025-08-15 23:38:48', 0);
INSERT INTO `photo` VALUES (1002, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/2/91a312e5917142069d18b13e3e507ab7.webp', 1, '2025-08-15 23:47:18', '2025-08-15 23:47:20', 0);
INSERT INTO `photo` VALUES (1003, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/2/e9ef1601e7fd477da8a8819fa7ab25f9.webp', 1, '2025-08-15 23:50:14', '2025-08-15 23:50:15', 0);
INSERT INTO `photo` VALUES (1004, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/2/80e6bcc62397422bb9b6749d03ff5ab7.webp', 1, '2025-08-16 02:02:00', '2025-08-16 02:02:02', 0);
INSERT INTO `photo` VALUES (1005, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/3/1d29f054b0f34070a6889dc49bf35ae3.webp', 1, '2025-08-17 00:24:04', '2025-08-17 00:24:05', 0);
INSERT INTO `photo` VALUES (1006, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/2/444ce751eebc480e96bc2bd6e61254bb.webp', 1, '2025-08-17 01:37:44', '2025-08-17 01:37:45', 0);
INSERT INTO `photo` VALUES (1008, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/4/4bed147d7d1e48e2a5cffe66e2c31a24.webp', 1, '2025-08-17 20:44:19', '2025-08-17 20:44:24', 0);
INSERT INTO `photo` VALUES (1009, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/6/61d91658e974434186b382cd10955d82.webp', 1, '2025-08-17 21:53:44', '2025-08-17 21:53:46', 0);
INSERT INTO `photo` VALUES (1010, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/7/651c690cbca74ecda4b794b2d8d21f81.webp', 1, '2025-08-18 00:40:50', '2025-08-18 00:40:52', 0);
INSERT INTO `photo` VALUES (1011, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/8/253c190cf34e493096297e258fc25c42.webp', 1, '2025-08-18 00:42:15', '2025-08-18 00:42:16', 0);
INSERT INTO `photo` VALUES (1012, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/9/7eb75aa3fe134277829afa0861bfe6f7.webp', 1, '2025-08-18 00:44:07', '2025-08-18 00:44:07', 0);
INSERT INTO `photo` VALUES (1013, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/10/6df7d4da9241446a8471c4ddaee6860d.webp', 1, '2025-08-18 00:45:42', '2025-08-18 00:45:42', 0);
INSERT INTO `photo` VALUES (1014, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/11/e1a22085ec6d4c2a8e9020c4766920e6.webp', 1, '2025-08-18 01:01:53', '2025-08-18 01:01:53', 0);
INSERT INTO `photo` VALUES (1015, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/12/5636972df0e04b37b68c3268969e1d00.webp', 1, '2025-08-18 01:06:35', '2025-08-18 01:06:35', 0);
INSERT INTO `photo` VALUES (1016, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/13/fd760317ef5745968a71200c65033c8b.webp', 1, '2025-08-18 01:32:10', '2025-08-18 01:32:10', 0);
INSERT INTO `photo` VALUES (1017, 1, 'http://14.103.112.11:9000/sidifensen-blog/album/1/2/658334308098465fbe66b5d4b3266ef9.webp', 1, '2025-08-18 01:37:47', '2025-08-18 01:37:47', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
INSERT INTO `sys_menu` VALUES (8, 0, '1', 4, '/sys', '/sys', 'Menu', 0, '2025-08-08 16:17:43', '2025-08-10 03:06:59', 0);
INSERT INTO `sys_menu` VALUES (9, 0, '1', 5, '/sys', '/sys', 'Menu', 0, '2025-08-08 20:33:16', '2025-08-08 20:33:19', 0);
INSERT INTO `sys_menu` VALUES (10, 0, '1', 6, '/sys', '/sys', 'Menu', 0, '2025-08-08 20:34:20', '2025-08-11 01:15:12', 0);
INSERT INTO `sys_menu` VALUES (11, 0, '1', 7, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-08 21:26:18', 0);
INSERT INTO `sys_menu` VALUES (12, 0, '1', 8, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-08 21:26:18', 0);
INSERT INTO `sys_menu` VALUES (13, 0, '1', 9, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-08 21:26:18', 0);
INSERT INTO `sys_menu` VALUES (14, 0, '1', 10, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-08 21:26:18', 0);
INSERT INTO `sys_menu` VALUES (15, 0, '1', 11, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-08 21:26:18', 0);
INSERT INTO `sys_menu` VALUES (16, 0, '1', 12, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-08 21:26:18', 0);
INSERT INTO `sys_menu` VALUES (17, 0, '1', 13, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-08 21:26:18', 0);
INSERT INTO `sys_menu` VALUES (18, 0, '1', 14, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-08 21:26:18', 0);
INSERT INTO `sys_menu` VALUES (19, 0, '1', 15, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-08 21:26:18', 0);
INSERT INTO `sys_menu` VALUES (20, 0, '1', 16, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-08 21:26:18', 0);
INSERT INTO `sys_menu` VALUES (21, 0, '一', 17, '/sys', '/sys', 'Menu', 0, '2025-08-08 21:25:52', '2025-08-11 13:19:29', 0);
INSERT INTO `sys_menu` VALUES (22, 23, '用户管理用户管理管理', 0, '/system/user/add', '/system/user/add', 'User', 0, '2025-08-10 01:01:19', '2025-08-11 12:31:31', 0);
INSERT INTO `sys_menu` VALUES (23, 7, '系统管理系统管理管理', 1, '/sys/11', '/sys/11', 'AddLocation', 0, '2025-08-10 15:58:30', '2025-08-13 18:25:06', 1);
INSERT INTO `sys_menu` VALUES (24, 7, 'sys111', 2, '/sys111', '/sys111', 'Apple', 0, '2025-08-10 15:58:46', '2025-08-13 18:25:19', 1);
INSERT INTO `sys_menu` VALUES (25, 24, 'sys1111', 3, '/sys1111', '/sys1111', 'ArrowDown', 0, '2025-08-11 11:36:38', '2025-08-13 18:24:49', 1);
INSERT INTO `sys_menu` VALUES (26, 25, 'sys11111', 0, '/sys11111', '/sys11111', 'AddLocation', 0, '2025-08-11 11:40:14', '2025-08-13 18:24:47', 1);
INSERT INTO `sys_menu` VALUES (27, 8, 'sys111111', 0, '/sys111111', '/sys111111', 'Aim', 0, '2025-08-11 11:40:28', '2025-08-13 18:25:12', 1);
INSERT INTO `sys_menu` VALUES (28, 7, '用户相册', 0, '/photo/album', '/photo/album', 'Picture', 0, '2025-08-14 11:41:12', '2025-08-15 00:25:18', 0);
INSERT INTO `sys_menu` VALUES (29, 7, '图片审核', 1, '/photo/examine', '/photo/examine', 'PictureRounded', 0, '2025-08-14 11:42:12', '2025-08-15 00:25:35', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
INSERT INTO `sys_permission` VALUES (32, '获取相册列表', 'album:list', 28, '2025-08-15 23:42:33', '2025-08-15 23:42:33', 0);
INSERT INTO `sys_permission` VALUES (33, '更新相册', 'album:update', 28, '2025-08-15 23:45:13', '2025-08-15 23:45:13', 0);
INSERT INTO `sys_permission` VALUES (34, '删除相册', 'album:delete', 28, '2025-08-15 23:45:46', '2025-08-15 23:45:46', 0);
INSERT INTO `sys_permission` VALUES (35, '搜索相册', 'album:search', 28, '2025-08-16 16:58:29', '2025-08-16 16:58:29', 0);
INSERT INTO `sys_permission` VALUES (36, '获取相册详情', 'album:detail', 28, '2025-08-16 23:49:45', '2025-08-16 23:49:45', 0);
INSERT INTO `sys_permission` VALUES (37, '删除图片', 'photo:delete', 28, '2025-08-17 00:30:04', '2025-08-17 00:30:04', 0);
INSERT INTO `sys_permission` VALUES (38, '批量删除图片', 'photo:deleteBatch', 28, '2025-08-17 00:30:15', '2025-08-17 00:30:15', 0);
INSERT INTO `sys_permission` VALUES (39, '审核图片', 'photo:audit', 28, '2025-08-17 00:39:51', '2025-08-17 00:39:51', 0);
INSERT INTO `sys_permission` VALUES (40, '批量审核图片', 'photo:auditBatch', 28, '2025-08-17 01:45:58', '2025-08-17 01:45:58', 0);
INSERT INTO `sys_permission` VALUES (41, '获取图片列表', 'photo:list', 29, '2025-08-17 03:10:00', '2025-08-17 03:10:00', 0);
INSERT INTO `sys_permission` VALUES (42, '搜索图片', 'photo:search', 29, '2025-08-17 15:19:06', '2025-08-17 15:19:06', 0);
INSERT INTO `sys_permission` VALUES (43, '获取消息数量', 'message:count', 1, '2025-08-18 02:01:25', '2025-08-18 02:01:25', 0);
INSERT INTO `sys_permission` VALUES (44, '获取消息列表', 'message:list', 1, '2025-08-18 02:01:40', '2025-08-18 02:01:40', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
INSERT INTO `sys_role_menu` VALUES (29, 1, 22);
INSERT INTO `sys_role_menu` VALUES (30, 1, 28);
INSERT INTO `sys_role_menu` VALUES (31, 1, 29);

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
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_role_permission` VALUES (50, 3, 25);
INSERT INTO `sys_role_permission` VALUES (51, 4, 26);
INSERT INTO `sys_role_permission` VALUES (52, 4, 25);
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

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `sex` tinyint NULL DEFAULT 0 COMMENT '性别 0-男 1-女',
  `introduction` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
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
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'sidifensen', '$2a$10$GJwVW/wuZ/xFFiWNElXGNuaK3VKWfqIcCICrUyFEKz.cpFa7WtWNi', '斯蒂芬森', 'sidifensen@163.com', 0, 'helloworld1111', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-08-18 14:15:47', '2025-06-28 22:30:22', '2025-08-12 01:51:14', 0);
INSERT INTO `sys_user` VALUES (2, 'youyu', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '柚屿', '111111@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.5.4', '内网地址', '2025-08-18 13:08:35', '2025-07-31 17:38:46', '2025-08-14 11:52:09', 0);
INSERT INTO `sys_user` VALUES (3, '123', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.5.4', '内网地址', '2025-08-18 01:46:21', '2025-07-31 17:38:46', '2025-08-12 01:12:29', 0);
INSERT INTO `sys_user` VALUES (4, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (5, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (6, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (7, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 1);
INSERT INTO `sys_user` VALUES (8, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 1);
INSERT INTO `sys_user` VALUES (9, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 1);
INSERT INTO `sys_user` VALUES (10, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 1);
INSERT INTO `sys_user` VALUES (11, 'WebUser1', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-08-12 01:30:15', 1);
INSERT INTO `sys_user` VALUES (12, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://14.103.112.11:9000/sidifensen-blog/avatar/avatar1.png', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 1);
INSERT INTO `sys_user` VALUES (13, 'sidifensen_14919133', '$2a$10$9Ai7TJESMAuzQ7PLHsj.SuxmKA9IBYoJVPgBKsyyb/AqKbjYcKXOe', '斯蒂芬森2', NULL, 0, '梦想是成为全栈高手', 'https://foruda.gitee.com/avatar/1752080025136627357/14919133_sidifensen_1752080025.png', 0, 1, '127.0.0.1', '内网地址', 0, '192.168.5.4', '内网地址', '2025-08-13 23:09:06', '2025-08-13 23:09:05', '2025-08-13 23:09:05', 0);
INSERT INTO `sys_user` VALUES (14, 'sidifensen_186551034', '$2a$10$4j5DL6d8XUOjmYcRIScmluLz4DGFifrpy8fE4gfV/2KADu3R/vv6S', '斯蒂芬森3', NULL, 0, '梦想是成为全栈高手', 'https://avatars.githubusercontent.com/u/186551034?v=4', 0, 2, '127.0.0.1', '内网地址', 0, '192.168.5.4', '内网地址', '2025-08-13 23:30:34', '2025-08-13 23:30:33', '2025-08-13 23:30:33', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 1, 1);
INSERT INTO `sys_user_role` VALUES (17, 2, 2);
INSERT INTO `sys_user_role` VALUES (48, 13, 3);
INSERT INTO `sys_user_role` VALUES (49, 14, 3);
INSERT INTO `sys_user_role` VALUES (50, 3, 3);

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_message
-- ----------------------------
INSERT INTO `user_message` VALUES (1, 1, 1);
INSERT INTO `user_message` VALUES (2, 1, 2);
INSERT INTO `user_message` VALUES (3, 1, 3);
INSERT INTO `user_message` VALUES (4, 1, 4);
INSERT INTO `user_message` VALUES (5, 1, 5);

SET FOREIGN_KEY_CHECKS = 1;
