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

 Date: 13/08/2025 23:32:14
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
  INDEX `idx_user_status_create_time`(`user_id` ASC, `show_status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of album
-- ----------------------------
INSERT INTO `album` VALUES (1, 1, '相册相册相册相册相册', 'http://localhost:9000/sidifensen-blog/album/1/1/180a0d22b71f4475aaa8d879fe64cc12.jpg', 0, '2025-07-31 17:44:39', '2025-07-31 17:44:39', 0);
INSERT INTO `album` VALUES (2, 1, '相册2', 'http://localhost:9000/sidifensen-blog/album/1/2/174b731ae4a7475db099ab49ed995754.jpg', 0, '2025-07-31 19:57:12', '2025-07-31 19:57:12', 0);
INSERT INTO `album` VALUES (3, 1, '相册3', 'http://localhost:9000/sidifensen-blog/album/1/3/4f11703b962f432b89e2f839b3b26a5e.jpg', 0, '2025-07-31 20:44:13', '2025-07-31 20:44:13', 0);
INSERT INTO `album` VALUES (4, 1, '相册4', 'http://localhost:9000/sidifensen-blog/album/1/4/3087004dab7b4d51925553a66ff7c9b2.jpg', 0, '2025-07-31 20:44:20', '2025-07-31 20:44:20', 0);
INSERT INTO `album` VALUES (5, 1, '相册5', 'http://localhost:9000/sidifensen-blog/album/1/5/02d59846d9784884905f21782f4d49da.jpg', 0, '2025-07-31 20:44:28', '2025-07-31 20:44:28', 0);
INSERT INTO `album` VALUES (6, 1, '相册6', 'http://localhost:9000/sidifensen-blog/album/1/6/d81c8d3b69ee4aa1a25fb59b2277c163.jpg', 0, '2025-07-31 20:44:40', '2025-07-31 20:44:40', 0);
INSERT INTO `album` VALUES (7, 1, '相册7', 'http://localhost:9000/sidifensen-blog/album/1/7/89b3af6ad3f140bf8179db329a4aef55.jpg', 0, '2025-07-31 20:44:47', '2025-07-31 20:44:47', 0);
INSERT INTO `album` VALUES (8, 2, '相册1(用户2)', 'http://localhost:9000/sidifensen-blog/album/2/8/155268c3f32048f0abd621be7d8095e9.jpg', 0, '2025-07-31 20:57:24', '2025-07-31 20:57:24', 0);
INSERT INTO `album` VALUES (9, 1, '相册8', 'http://localhost:9000/sidifensen-blog/album/1/9/089a4b94f7434c12ada38ffd2a3b8b82.jpg', 0, '2025-08-01 02:19:38', '2025-08-01 02:19:38', 0);
INSERT INTO `album` VALUES (10, 1, '相册9', 'http://localhost:9000/sidifensen-blog/album/1/10/e888d72f6fa44818be6ad425eaeebd43.jpg', 0, '2025-08-01 02:19:57', '2025-08-01 02:19:57', 0);
INSERT INTO `album` VALUES (11, 1, '相册10', 'http://localhost:9000/sidifensen-blog/album/1/11/65b66f8c11234f1b951e056132271d04.jpg', 0, '2025-08-01 02:20:16', '2025-08-01 02:20:16', 0);
INSERT INTO `album` VALUES (12, 1, '相册11', 'http://localhost:9000/sidifensen-blog/album/1/12/d9586c1d84ba418ba36a6b54deb4f613.jpg', 0, '2025-08-01 02:20:47', '2025-08-01 02:20:47', 0);
INSERT INTO `album` VALUES (13, 1, '相册12', 'http://localhost:9000/sidifensen-blog/album/1/13/ae03c29e909440df9c323b0f9481238f.jpg', 0, '2025-08-01 02:21:07', '2025-08-01 02:21:07', 0);

-- ----------------------------
-- Table structure for photo
-- ----------------------------
DROP TABLE IF EXISTS `photo`;
CREATE TABLE `photo`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '图片id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片url',
  `album_id` bigint NOT NULL COMMENT '相册id',
  `examine_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0-待审核 1-审核通过 2-审核未通过',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_examine_status`(`examine_status` ASC) USING BTREE,
  INDEX `idx_photo_user_album_status_create_time`(`user_id` ASC, `album_id` ASC, `examine_status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of photo
-- ----------------------------
INSERT INTO `photo` VALUES (1, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/cd182c86e10a41e1949aeb61e47cdaee.jpg', 1, 0, '2025-07-31 17:45:10', '2025-07-31 20:53:11', 0);
INSERT INTO `photo` VALUES (2, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/374cc0a6432e42d08adeebb4d5407350.jpg', 1, 0, '2025-07-31 17:48:08', '2025-07-31 19:56:49', 0);
INSERT INTO `photo` VALUES (3, 1, 'http://localhost:9000/sidifensen-blog/album/1/2/0e0926b8b5da436d99e29374132a14fa.jpg', 2, 0, '2025-07-31 20:10:18', '2025-07-31 20:10:18', 0);
INSERT INTO `photo` VALUES (4, 1, 'http://localhost:9000/sidifensen-blog/album/1/2/174b731ae4a7475db099ab49ed995754.jpg', 2, 0, '2025-07-31 20:12:53', '2025-07-31 20:12:53', 0);
INSERT INTO `photo` VALUES (5, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/9ab1fc3fea28438ca5e6813134dca766.jpg', 1, 0, '2025-07-31 20:34:26', '2025-07-31 20:34:26', 0);
INSERT INTO `photo` VALUES (6, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/0846714350764244bf7af40972a4395b.jpg', 1, 0, '2025-07-31 20:45:09', '2025-07-31 20:45:09', 0);
INSERT INTO `photo` VALUES (7, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/99aea818bd3b4b05b486db536fc05808.jpg', 1, 0, '2025-07-31 20:45:14', '2025-07-31 20:45:14', 0);
INSERT INTO `photo` VALUES (8, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/eb18356e02404f259cf9a5c5561fc33d.jpg', 1, 0, '2025-07-31 20:45:20', '2025-07-31 20:45:20', 0);
INSERT INTO `photo` VALUES (9, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/90ab310ba35346678e9b3af6c359da8f.jpg', 1, 0, '2025-07-31 20:45:28', '2025-07-31 20:45:28', 0);
INSERT INTO `photo` VALUES (10, 1, 'http://localhost:9000/sidifensen-blog/album/1/3/4f11703b962f432b89e2f839b3b26a5e.jpg', 3, 0, '2025-07-31 20:51:22', '2025-07-31 20:51:22', 0);
INSERT INTO `photo` VALUES (11, 1, 'http://localhost:9000/sidifensen-blog/album/1/4/3087004dab7b4d51925553a66ff7c9b2.jpg', 4, 0, '2025-07-31 20:51:35', '2025-07-31 20:51:35', 0);
INSERT INTO `photo` VALUES (12, 1, 'http://localhost:9000/sidifensen-blog/album/1/5/02d59846d9784884905f21782f4d49da.jpg', 5, 0, '2025-07-31 20:51:56', '2025-07-31 20:51:56', 0);
INSERT INTO `photo` VALUES (13, 1, 'http://localhost:9000/sidifensen-blog/album/1/6/d81c8d3b69ee4aa1a25fb59b2277c163.jpg', 6, 0, '2025-07-31 20:52:05', '2025-07-31 20:52:05', 0);
INSERT INTO `photo` VALUES (14, 1, 'http://localhost:9000/sidifensen-blog/album/1/7/89b3af6ad3f140bf8179db329a4aef55.jpg', 7, 0, '2025-07-31 20:52:13', '2025-07-31 20:52:13', 0);
INSERT INTO `photo` VALUES (15, 2, 'http://localhost:9000/sidifensen-blog/album/2/8/155268c3f32048f0abd621be7d8095e9.jpg', 8, 0, '2025-07-31 20:57:51', '2025-07-31 20:57:51', 0);
INSERT INTO `photo` VALUES (16, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/d564740c0f654d75a618692ac4385c74.jpg', 1, 0, '2025-07-31 23:24:05', '2025-07-31 23:24:05', 0);
INSERT INTO `photo` VALUES (17, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/0f1083c777f240899eea98ed21bd0b25.jpg', 1, 0, '2025-07-31 23:24:12', '2025-07-31 23:24:12', 0);
INSERT INTO `photo` VALUES (18, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/9638a4c73b7b4af8a82e894c198b8e8a.jpg', 1, 0, '2025-07-31 23:25:33', '2025-07-31 23:25:33', 0);
INSERT INTO `photo` VALUES (19, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/45723bfe688843b0be37cdbab7a48ac9.jpg', 1, 0, '2025-07-31 23:25:41', '2025-07-31 23:25:41', 0);
INSERT INTO `photo` VALUES (20, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/6b345bb942bf4125b63d19dfd26ab1c1.jpg', 1, 0, '2025-07-31 23:25:46', '2025-07-31 23:25:46', 0);
INSERT INTO `photo` VALUES (21, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/0d4099dddcb24749a0ec6340ddbcb060.jpg', 1, 0, '2025-08-01 01:17:56', '2025-08-01 01:17:56', 0);
INSERT INTO `photo` VALUES (22, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/9c572780bf984a2ea164fb1839d8af19.jpg', 1, 0, '2025-08-01 01:17:56', '2025-08-01 01:17:56', 0);
INSERT INTO `photo` VALUES (23, 1, 'http://localhost:9000/sidifensen-blog/album/1/9/089a4b94f7434c12ada38ffd2a3b8b82.jpg', 9, 0, '2025-08-01 02:19:47', '2025-08-01 02:19:47', 0);
INSERT INTO `photo` VALUES (24, 1, 'http://localhost:9000/sidifensen-blog/album/1/10/e888d72f6fa44818be6ad425eaeebd43.jpg', 10, 0, '2025-08-01 02:20:07', '2025-08-01 02:20:07', 0);
INSERT INTO `photo` VALUES (25, 1, 'http://localhost:9000/sidifensen-blog/album/1/11/65b66f8c11234f1b951e056132271d04.jpg', 11, 0, '2025-08-01 02:20:31', '2025-08-01 02:20:31', 0);
INSERT INTO `photo` VALUES (26, 1, 'http://localhost:9000/sidifensen-blog/album/1/12/d9586c1d84ba418ba36a6b54deb4f613.jpg', 12, 0, '2025-08-01 02:20:57', '2025-08-01 02:20:57', 0);
INSERT INTO `photo` VALUES (27, 1, 'http://localhost:9000/sidifensen-blog/album/1/13/ae03c29e909440df9c323b0f9481238f.jpg', 13, 0, '2025-08-01 02:21:16', '2025-08-01 02:21:16', 0);
INSERT INTO `photo` VALUES (28, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/04682bd7f51b4143baff1999ba6cc322.jpg', 1, 0, '2025-08-01 22:56:13', '2025-08-01 22:56:13', 0);
INSERT INTO `photo` VALUES (29, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/c3704347ffe641ed9bf894899a03412b.jpg', 1, 2, '2025-08-01 22:56:18', '2025-08-01 22:56:18', 0);
INSERT INTO `photo` VALUES (30, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/fb3f86fcb45e4f3498bbd5c7c692ab63.jpg', 1, 1, '2025-08-01 22:56:31', '2025-08-01 22:56:31', 0);
INSERT INTO `photo` VALUES (31, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/d32749a37b82413eb198023c29b08482.jpg', 1, 0, '2025-08-04 11:57:41', '2025-08-04 11:57:41', 0);
INSERT INTO `photo` VALUES (32, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/76bde25645e54117b4ad6b354edd00bc.jpg', 1, 0, '2025-08-04 15:31:59', '2025-08-04 15:31:59', 0);
INSERT INTO `photo` VALUES (33, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/8a735dfe5b174a8db4d4eaaa17e5a3e0.jpg', 1, 0, '2025-08-04 15:34:54', '2025-08-04 15:34:54', 0);
INSERT INTO `photo` VALUES (34, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/8913cadb39104736afaa59aa4de7d2a5.jpg', 1, 0, '2025-08-04 15:42:49', '2025-08-04 15:42:49', 0);
INSERT INTO `photo` VALUES (35, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/180a0d22b71f4475aaa8d879fe64cc12.jpg', 1, 0, '2025-08-04 15:44:30', '2025-08-04 15:44:30', 0);
INSERT INTO `photo` VALUES (36, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/be307384960d450e8fd59db59676c600.jpg', 1, 0, '2025-08-04 15:44:45', '2025-08-04 15:44:45', 0);
INSERT INTO `photo` VALUES (37, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/5a9ba5099ca441648f82e5501bbf42ef.jpg', 1, 0, '2025-08-04 15:51:14', '2025-08-04 15:51:14', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '首页', 0, '/home', '/home', 'House', 0, '2025-06-28 22:31:52', '2025-08-12 00:34:19', 0);
INSERT INTO `sys_menu` VALUES (2, 0, '系统管理', 1, '/system', ' ', 'Setting', 0, '2025-08-06 15:26:06', '2025-08-11 01:21:58', 0);
INSERT INTO `sys_menu` VALUES (3, 2, '菜单管理', 0, '/system/menu', '/system/menu', 'Menu', 0, '2025-08-06 15:40:22', '2025-08-11 01:22:00', 0);
INSERT INTO `sys_menu` VALUES (4, 2, '角色管理', 2, '/system/role', '/system/role', 'Avatar', 0, '2025-08-06 15:41:35', '2025-08-11 01:21:52', 0);
INSERT INTO `sys_menu` VALUES (5, 2, '权限管理', 1, '/system/permission', '/system/permission', 'Lock', 0, '2025-08-06 15:42:21', '2025-08-11 01:22:03', 0);
INSERT INTO `sys_menu` VALUES (6, 2, '用户管理', 3, '/system/user', '/system/user', 'User', 0, '2025-08-06 15:43:41', '2025-08-11 01:21:50', 0);
INSERT INTO `sys_menu` VALUES (7, 0, '系统管理系统管理系统', 3, '/sys1', '/sys1', 'Menu', 0, '2025-08-08 16:17:18', '2025-08-11 01:21:56', 0);
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
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_id` int NOT NULL COMMENT '角色id',
  `permission_id` int NOT NULL COMMENT '权限id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `sex` tinyint NULL DEFAULT 0 COMMENT '性别 0-男 1-女',
  `introduction` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
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
INSERT INTO `sys_user` VALUES (1, 'sidifensen', '$2a$10$GJwVW/wuZ/xFFiWNElXGNuaK3VKWfqIcCICrUyFEKz.cpFa7WtWNi', '斯蒂芬森', 'sidifensen@163.com', 0, 'helloworld1111', 'http://localhost:9000/sidifensen-blog/avatar.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.5.4', '内网地址', '2025-08-13 18:40:05', '2025-06-28 22:30:22', '2025-08-12 01:51:14', 0);
INSERT INTO `sys_user` VALUES (2, '斯蒂芬森', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '斯蒂芬森', '111111@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 15:39:51', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (3, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-08-12 01:12:29', 0);
INSERT INTO `sys_user` VALUES (4, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (5, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (6, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (7, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (8, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (9, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (10, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);
INSERT INTO `sys_user` VALUES (11, 'WebUser1', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-08-12 01:30:15', 0);
INSERT INTO `sys_user` VALUES (12, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '123456@qq.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/xd.jpg', 0, 0, '127.0.0.1', '内网地址', 0, '192.168.10.149', '内网地址', '2025-08-10 02:00:15', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 1);
INSERT INTO `sys_user` VALUES (13, 'sidifensen_14919133', '$2a$10$9Ai7TJESMAuzQ7PLHsj.SuxmKA9IBYoJVPgBKsyyb/AqKbjYcKXOe', '斯蒂芬森', NULL, 0, '梦想是成为全栈高手', 'https://foruda.gitee.com/avatar/1752080025136627357/14919133_sidifensen_1752080025.png', 0, 1, '127.0.0.1', '内网地址', 0, '192.168.5.4', '内网地址', '2025-08-13 23:09:06', '2025-08-13 23:09:05', '2025-08-13 23:09:05', 0);
INSERT INTO `sys_user` VALUES (14, 'sidifensen_186551034', '$2a$10$4j5DL6d8XUOjmYcRIScmluLz4DGFifrpy8fE4gfV/2KADu3R/vv6S', '斯蒂芬森', NULL, 0, '梦想是成为全栈高手', 'https://avatars.githubusercontent.com/u/186551034?v=4', 0, 2, '127.0.0.1', '内网地址', 0, '192.168.5.4', '内网地址', '2025-08-13 23:30:34', '2025-08-13 23:30:33', '2025-08-13 23:30:33', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int NOT NULL COMMENT '用户id',
  `role_id` int NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 1, 1);
INSERT INTO `sys_user_role` VALUES (17, 2, 2);
INSERT INTO `sys_user_role` VALUES (38, 14919135, 3);
INSERT INTO `sys_user_role` VALUES (39, 14919133, 3);
INSERT INTO `sys_user_role` VALUES (40, 186551034, 3);
INSERT INTO `sys_user_role` VALUES (41, 14919133, 3);
INSERT INTO `sys_user_role` VALUES (42, 14919133, 3);
INSERT INTO `sys_user_role` VALUES (43, 14919133, 3);
INSERT INTO `sys_user_role` VALUES (44, 186551034, 3);
INSERT INTO `sys_user_role` VALUES (45, 14919133, 3);
INSERT INTO `sys_user_role` VALUES (46, 186551034, 3);
INSERT INTO `sys_user_role` VALUES (47, 14919133, 3);
INSERT INTO `sys_user_role` VALUES (48, 13, 3);
INSERT INTO `sys_user_role` VALUES (49, 14, 3);

SET FOREIGN_KEY_CHECKS = 1;
