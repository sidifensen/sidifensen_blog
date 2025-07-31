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

 Date: 01/08/2025 01:21:38
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
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '相册名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相册描述',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相册封面',
  `show_status` tinyint NOT NULL DEFAULT 0 COMMENT '展示状态 0-公开 1-私有',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_show_status`(`show_status` ASC) USING BTREE,
  INDEX `idx_user_status_create_time`(`user_id` ASC, `show_status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of album
-- ----------------------------
INSERT INTO `album` VALUES (1, 1, '相册1', '123', 'http://localhost:9000/sidifensen-blog/album/1/1/9c572780bf984a2ea164fb1839d8af19.jpg', 0, '2025-07-31 17:44:39', '2025-07-31 17:44:39', 0);
INSERT INTO `album` VALUES (2, 1, '相册2', '666', 'http://localhost:9000/sidifensen-blog/album/1/2/174b731ae4a7475db099ab49ed995754.jpg', 0, '2025-07-31 19:57:12', '2025-07-31 19:57:12', 0);
INSERT INTO `album` VALUES (3, 1, '相册3', '111', 'http://localhost:9000/sidifensen-blog/album/1/3/4f11703b962f432b89e2f839b3b26a5e.jpg', 0, '2025-07-31 20:44:13', '2025-07-31 20:44:13', 0);
INSERT INTO `album` VALUES (4, 1, '相册4', '12', 'http://localhost:9000/sidifensen-blog/album/1/4/3087004dab7b4d51925553a66ff7c9b2.jpg', 0, '2025-07-31 20:44:20', '2025-07-31 20:44:20', 0);
INSERT INTO `album` VALUES (5, 1, '相册5', '1232131', 'http://localhost:9000/sidifensen-blog/album/1/5/02d59846d9784884905f21782f4d49da.jpg', 0, '2025-07-31 20:44:28', '2025-07-31 20:44:28', 0);
INSERT INTO `album` VALUES (6, 1, '相册6', '123', 'http://localhost:9000/sidifensen-blog/album/1/6/d81c8d3b69ee4aa1a25fb59b2277c163.jpg', 1, '2025-07-31 20:44:40', '2025-07-31 20:44:40', 0);
INSERT INTO `album` VALUES (7, 1, '相册7', '6', 'http://localhost:9000/sidifensen-blog/album/1/7/89b3af6ad3f140bf8179db329a4aef55.jpg', 1, '2025-07-31 20:44:47', '2025-07-31 20:44:47', 0);
INSERT INTO `album` VALUES (8, 2, '相册11', '11', 'http://localhost:9000/sidifensen-blog/album/2/8/155268c3f32048f0abd621be7d8095e9.jpg', 0, '2025-07-31 20:57:24', '2025-07-31 20:57:24', 0);

-- ----------------------------
-- Table structure for photo
-- ----------------------------
DROP TABLE IF EXISTS `photo`;
CREATE TABLE `photo`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '图片id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片url',
  `album_id` bigint NOT NULL COMMENT '相册id',
  `show_status` tinyint NOT NULL DEFAULT 0 COMMENT '展示状态 0-公开 1-私有',
  `examine_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0-待审核 1-审核通过 2-审核未通过',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_examine_status`(`examine_status` ASC) USING BTREE,
  INDEX `idx_photo_user_album_status_create_time`(`user_id` ASC, `album_id` ASC, `show_status` ASC, `examine_status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of photo
-- ----------------------------
INSERT INTO `photo` VALUES (1, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/cd182c86e10a41e1949aeb61e47cdaee.jpg', 1, 0, 0, '2025-07-31 17:45:10', '2025-07-31 20:53:11', 0);
INSERT INTO `photo` VALUES (2, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/374cc0a6432e42d08adeebb4d5407350.jpg', 1, 0, 0, '2025-07-31 17:48:08', '2025-07-31 19:56:49', 0);
INSERT INTO `photo` VALUES (3, 1, 'http://localhost:9000/sidifensen-blog/album/1/2/0e0926b8b5da436d99e29374132a14fa.jpg', 2, 0, 0, '2025-07-31 20:10:18', '2025-07-31 20:10:18', 0);
INSERT INTO `photo` VALUES (4, 1, 'http://localhost:9000/sidifensen-blog/album/1/2/174b731ae4a7475db099ab49ed995754.jpg', 2, 0, 0, '2025-07-31 20:12:53', '2025-07-31 20:12:53', 0);
INSERT INTO `photo` VALUES (5, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/9ab1fc3fea28438ca5e6813134dca766.jpg', 1, 0, 0, '2025-07-31 20:34:26', '2025-07-31 20:34:26', 0);
INSERT INTO `photo` VALUES (6, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/0846714350764244bf7af40972a4395b.jpg', 1, 0, 0, '2025-07-31 20:45:09', '2025-07-31 20:45:09', 0);
INSERT INTO `photo` VALUES (7, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/99aea818bd3b4b05b486db536fc05808.jpg', 1, 0, 0, '2025-07-31 20:45:14', '2025-07-31 20:45:14', 0);
INSERT INTO `photo` VALUES (8, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/eb18356e02404f259cf9a5c5561fc33d.jpg', 1, 0, 0, '2025-07-31 20:45:20', '2025-07-31 20:45:20', 0);
INSERT INTO `photo` VALUES (9, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/90ab310ba35346678e9b3af6c359da8f.jpg', 1, 0, 0, '2025-07-31 20:45:28', '2025-07-31 20:45:28', 0);
INSERT INTO `photo` VALUES (10, 1, 'http://localhost:9000/sidifensen-blog/album/1/3/4f11703b962f432b89e2f839b3b26a5e.jpg', 3, 0, 0, '2025-07-31 20:51:22', '2025-07-31 20:51:22', 0);
INSERT INTO `photo` VALUES (11, 1, 'http://localhost:9000/sidifensen-blog/album/1/4/3087004dab7b4d51925553a66ff7c9b2.jpg', 4, 0, 0, '2025-07-31 20:51:35', '2025-07-31 20:51:35', 0);
INSERT INTO `photo` VALUES (12, 1, 'http://localhost:9000/sidifensen-blog/album/1/5/02d59846d9784884905f21782f4d49da.jpg', 5, 0, 0, '2025-07-31 20:51:56', '2025-07-31 20:51:56', 0);
INSERT INTO `photo` VALUES (13, 1, 'http://localhost:9000/sidifensen-blog/album/1/6/d81c8d3b69ee4aa1a25fb59b2277c163.jpg', 6, 0, 0, '2025-07-31 20:52:05', '2025-07-31 20:52:05', 0);
INSERT INTO `photo` VALUES (14, 1, 'http://localhost:9000/sidifensen-blog/album/1/7/89b3af6ad3f140bf8179db329a4aef55.jpg', 7, 0, 0, '2025-07-31 20:52:13', '2025-07-31 20:52:13', 0);
INSERT INTO `photo` VALUES (15, 2, 'http://localhost:9000/sidifensen-blog/album/2/8/155268c3f32048f0abd621be7d8095e9.jpg', 8, 0, 0, '2025-07-31 20:57:51', '2025-07-31 20:57:51', 0);
INSERT INTO `photo` VALUES (16, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/d564740c0f654d75a618692ac4385c74.jpg', 1, 0, 0, '2025-07-31 23:24:05', '2025-07-31 23:24:05', 0);
INSERT INTO `photo` VALUES (17, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/0f1083c777f240899eea98ed21bd0b25.jpg', 1, 0, 0, '2025-07-31 23:24:12', '2025-07-31 23:24:12', 0);
INSERT INTO `photo` VALUES (18, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/9638a4c73b7b4af8a82e894c198b8e8a.jpg', 1, 0, 0, '2025-07-31 23:25:33', '2025-07-31 23:25:33', 0);
INSERT INTO `photo` VALUES (19, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/45723bfe688843b0be37cdbab7a48ac9.jpg', 1, 0, 0, '2025-07-31 23:25:41', '2025-07-31 23:25:41', 0);
INSERT INTO `photo` VALUES (20, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/6b345bb942bf4125b63d19dfd26ab1c1.jpg', 1, 0, 0, '2025-07-31 23:25:46', '2025-07-31 23:25:46', 0);
INSERT INTO `photo` VALUES (21, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/0d4099dddcb24749a0ec6340ddbcb060.jpg', 1, 0, 0, '2025-08-01 01:17:56', '2025-08-01 01:17:56', 0);
INSERT INTO `photo` VALUES (22, 1, 'http://localhost:9000/sidifensen-blog/album/1/1/9c572780bf984a2ea164fb1839d8af19.jpg', 1, 0, 0, '2025-08-01 01:17:56', '2025-08-01 01:17:56', 0);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '菜单id',
  `parent_id` int NULL DEFAULT 0 COMMENT '父级id',
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单标题',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `type` tinyint NULL DEFAULT 0 COMMENT '类型 0-目录 1-菜单 2-按钮',
  `path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路径',
  `component_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `permission` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '首页', 0, 0, '/index', 'index', 'index', 'home', 0, '2025-06-28 22:31:52', '2025-06-28 22:30:22', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `sort` int NULL DEFAULT 0 COMMENT '  排序',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'admin', '超级管理员', NULL, 0, 0, '2025-06-28 22:31:00', '2025-06-28 22:30:22', 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_id` int NOT NULL COMMENT '角色id',
  `menu_id` int NOT NULL COMMENT '菜单id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 1);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `sex` tinyint NULL DEFAULT 0 COMMENT '性别 0-男 1-女',
  `introduction` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `register_type` tinyint NULL DEFAULT 0 COMMENT '注册方式 0-用户名/邮箱注册 1-第三方注册',
  `register_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '注册ip',
  `login_type` tinyint NULL DEFAULT 0 COMMENT '登录方式 0-用户名/邮箱登录 1-第三方登录',
  `login_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录ip',
  `login_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'sidifensen', '$2a$10$GJwVW/wuZ/xFFiWNElXGNuaK3VKWfqIcCICrUyFEKz.cpFa7WtWNi', '斯蒂芬森', 'sidifensen@163.com', 0, 'helloworld', 'http://localhost:9000/sidifensen-blog/avatar.jpg', 0, 0, NULL, 0, '127.0.0.1', '2025-08-01 01:10:51', '2025-06-28 22:30:22', '2025-07-31 17:29:15', 0);
INSERT INTO `sys_user` VALUES (2, '斯蒂芬森', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '斯蒂芬森', '1848221808@qq.com', 0, NULL, NULL, 0, 0, NULL, 0, '127.0.0.1', '2025-07-31 20:57:11', '2025-07-31 17:38:46', '2025-07-31 17:38:46', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int NOT NULL COMMENT '用户id',
  `role_id` int NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
