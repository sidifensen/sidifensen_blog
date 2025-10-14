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

 Date: 14/10/2025 09:49:45
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
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;


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
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


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
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


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
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of link
-- ----------------------------
INSERT INTO `link` VALUES (1, 1, '通义千问', 'https://www.tongyi.com/', '\r\nhttps://img.alicdn.com/imgextra/i4/O1CN01EfJVFQ1uZPd7W4W6i_!!6000000006051-2-tps-112-112.png', '阿里云推出的超大规模语言模型，支持多轮对话', 1, 'tongyi@aliyun.com', '2025-10-13 11:36:10', 0);
INSERT INTO `link` VALUES (2, 1, 'Kimi', 'https://kimi.moonshot.cn/', 'https://statics.moonshot.cn/kimi-chat/favicon.ico', 'Moonshot AI 推出的智能助手，支持超长文本对话', 1, 'contact@moonshot.cn', '2025-10-14 11:36:10', 0);
INSERT INTO `link` VALUES (3, 1, '智谱清言', 'https://chatglm.cn/', 'https://chatglm.cn/favicon.ico', '智谱 AI 推出的对话式 AI 助手，基于 ChatGLM 模型', 1, 'contact@zhipuai.cn', '2025-10-14 11:36:10', 0);
INSERT INTO `link` VALUES (4, 1, 'DeepSeek', 'https://www.deepseek.com/', 'https://www.deepseek.com/favicon.ico', '深度求索推出的 AI 大模型，强大的推理能力', 1, 'contact@deepseek.com', '2025-10-14 11:36:10', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 308 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;


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
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_role` VALUES (4, 'vip', 'vip用户', 'vip用户', 0, '2025-08-09 13:18:12', '2025-08-09 22:22:10', 0);

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
  `nickname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `sex` tinyint NULL DEFAULT 0 COMMENT '性别 0-男 1-女',
  `introduction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `avatar` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `fans_count` int NULL DEFAULT 0 COMMENT '粉丝数',
  `follow_count` int NULL DEFAULT 0 COMMENT '关注数',
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
INSERT INTO `sys_user` VALUES (1, 'sidifensen', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '斯蒂芬森', '123456@qq.com', 0, '强者', '', 0, 5, 35, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-10-14 09:46:58', '2025-06-28 22:30:22', '2025-10-09 16:14:20', 0);
INSERT INTO `sys_user` VALUES (2, 'test', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', 'test用户', 'test@qq.com', 0, 'helloworld', '', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '127.0.0.1', '内网地址', '2025-10-12 23:39:50', '2025-07-31 17:38:46', '2025-10-09 09:00:07', 0);
INSERT INTO `sys_user` VALUES (3, 'WebUser', '$2a$10$uTyUKEl.mc24YCN44QSjOukVgnc1hPGm6OFdZ0oQo6i5G5Yd.gIqS', '网站用户', '1@qq.com', 0, 'helloworld', '', 0, 0, 0, 0, '127.0.0.1', '内网地址', 0, '192.168.5.4', '内网地址', '2025-08-18 01:46:21', '2025-07-31 17:38:46', '2025-09-26 14:28:58', 0);
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
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
INSERT INTO `tag` VALUES (1, 'Java', 'idea', 0);
INSERT INTO `tag` VALUES (2, 'Java', 'spring', 0);
INSERT INTO `tag` VALUES (3, 'Java', 'springboot', 0);
INSERT INTO `tag` VALUES (4, 'Java', 'redis', 0);
INSERT INTO `tag` VALUES (5, 'Java', 'mysql', 0);
INSERT INTO `tag` VALUES (6, '前端', 'vue', 2);
INSERT INTO `tag` VALUES (7, '前端', 'html', 2);
INSERT INTO `tag` VALUES (8, '前端', 'js', 2);
INSERT INTO `tag` VALUES (9, '前端', 'css', 2);
INSERT INTO `tag` VALUES (10, '数据库', 'elasticsearch', 15);
INSERT INTO `tag` VALUES (11, '数据库', 'mongodb', 15);
INSERT INTO `tag` VALUES (12, '后端', 'python', 1);
INSERT INTO `tag` VALUES (13, '后端', 'go', 1);
INSERT INTO `tag` VALUES (14, 'Python', 'django', 8);
INSERT INTO `tag` VALUES (15, 'Python', 'pygame', 8);
INSERT INTO `tag` VALUES (16, 'Python', 'virtualenv', 8);
INSERT INTO `tag` VALUES (17, 'Python', 'tornado', 8);
INSERT INTO `tag` VALUES (18, 'Python', 'flask', 8);
INSERT INTO `tag` VALUES (19, 'Python', 'scikit-learn', 8);
INSERT INTO `tag` VALUES (20, 'Python', 'plotly', 8);
INSERT INTO `tag` VALUES (21, 'Python', 'dash', 8);
INSERT INTO `tag` VALUES (22, 'Python', 'fastapi', 8);
INSERT INTO `tag` VALUES (23, 'Python', 'pyqt', 8);
INSERT INTO `tag` VALUES (24, 'Python', 'scrapy', 8);
INSERT INTO `tag` VALUES (25, 'Python', 'beautifulsoup', 8);
INSERT INTO `tag` VALUES (26, 'Python', 'numpy', 8);
INSERT INTO `tag` VALUES (27, 'Python', 'scipy', 8);
INSERT INTO `tag` VALUES (28, 'Python', 'pandas', 8);
INSERT INTO `tag` VALUES (29, 'Python', 'matplotlib', 8);
INSERT INTO `tag` VALUES (30, 'Python', 'httpx', 8);
INSERT INTO `tag` VALUES (31, 'Python', 'web3.py', 8);
INSERT INTO `tag` VALUES (32, 'Python', 'pytest', 8);
INSERT INTO `tag` VALUES (33, 'Python', 'pillow', 8);
INSERT INTO `tag` VALUES (34, 'Python', 'gunicorn', 8);
INSERT INTO `tag` VALUES (35, 'Python', 'pip', 8);
INSERT INTO `tag` VALUES (36, 'Python', 'conda', 8);
INSERT INTO `tag` VALUES (37, 'Python', 'ipython', 8);
INSERT INTO `tag` VALUES (39, 'Java', 'eclipse', 0);
INSERT INTO `tag` VALUES (40, 'Java', 'java', 0);
INSERT INTO `tag` VALUES (41, 'Java', 'tomcat', 0);
INSERT INTO `tag` VALUES (42, 'Java', 'hibernate', 0);
INSERT INTO `tag` VALUES (43, 'Java', 'maven', 0);
INSERT INTO `tag` VALUES (44, 'Java', 'struts', 0);
INSERT INTO `tag` VALUES (45, 'Java', 'kafka', 0);
INSERT INTO `tag` VALUES (46, 'Java', 'intellij-idea', 0);
INSERT INTO `tag` VALUES (47, 'Java', 'java-ee', 0);
INSERT INTO `tag` VALUES (48, 'Java', 'spring boot', 0);
INSERT INTO `tag` VALUES (49, 'Java', 'spring cloud', 0);
INSERT INTO `tag` VALUES (50, 'Java', 'jvm', 0);
INSERT INTO `tag` VALUES (51, 'Java', 'jetty', 0);
INSERT INTO `tag` VALUES (52, 'Java', 'junit', 0);
INSERT INTO `tag` VALUES (53, 'Java', 'log4j', 0);
INSERT INTO `tag` VALUES (54, 'Java', 'servlet', 0);
INSERT INTO `tag` VALUES (55, 'Java', 'mybatis', 0);
INSERT INTO `tag` VALUES (56, 'Java', 'nio', 0);
INSERT INTO `tag` VALUES (57, 'Java', 'dubbo', 0);
INSERT INTO `tag` VALUES (58, 'Java', 'sentinel', 0);
INSERT INTO `tag` VALUES (59, 'Java', 'java-consul', 0);
INSERT INTO `tag` VALUES (60, 'Java', 'java-zookeeper', 0);
INSERT INTO `tag` VALUES (61, 'Java', 'java-rabbitmq', 0);
INSERT INTO `tag` VALUES (62, 'Java', 'java-activemq', 0);
INSERT INTO `tag` VALUES (63, 'Java', 'java-rocketmq', 0);
INSERT INTO `tag` VALUES (64, 'Java', 'sdkman', 0);
INSERT INTO `tag` VALUES (65, 'Java', 'guava', 0);
INSERT INTO `tag` VALUES (67, '编程语言', 'php', 3);
INSERT INTO `tag` VALUES (68, '编程语言', 'c++', 3);
INSERT INTO `tag` VALUES (69, '编程语言', 'c语言', 3);
INSERT INTO `tag` VALUES (70, '编程语言', 'javascript', 3);
INSERT INTO `tag` VALUES (71, '编程语言', 'c#', 3);
INSERT INTO `tag` VALUES (72, '编程语言', 'ruby', 3);
INSERT INTO `tag` VALUES (73, '编程语言', 'qt', 3);
INSERT INTO `tag` VALUES (74, '编程语言', 'actionscript', 3);
INSERT INTO `tag` VALUES (75, '编程语言', 'lua', 3);
INSERT INTO `tag` VALUES (76, '编程语言', 'perl', 3);
INSERT INTO `tag` VALUES (77, '编程语言', 'symfony', 3);
INSERT INTO `tag` VALUES (78, '编程语言', 'r语言', 3);
INSERT INTO `tag` VALUES (79, '编程语言', 'swift', 3);
INSERT INTO `tag` VALUES (80, '编程语言', 'laravel', 3);
INSERT INTO `tag` VALUES (81, '编程语言', 'scala', 3);
INSERT INTO `tag` VALUES (82, '编程语言', 'bash', 3);
INSERT INTO `tag` VALUES (83, '编程语言', 'batch', 3);
INSERT INTO `tag` VALUES (84, '编程语言', 'lisp', 3);
INSERT INTO `tag` VALUES (85, '编程语言', 'typescript', 3);
INSERT INTO `tag` VALUES (86, '编程语言', 'erlang', 3);
INSERT INTO `tag` VALUES (87, '编程语言', 'composer', 3);
INSERT INTO `tag` VALUES (88, '编程语言', 'objective-c', 3);
INSERT INTO `tag` VALUES (89, '编程语言', 'julia', 3);
INSERT INTO `tag` VALUES (90, '编程语言', '开发语言', 3);
INSERT INTO `tag` VALUES (91, '编程语言', 'kotlin', 3);
INSERT INTO `tag` VALUES (92, '编程语言', 'golang', 3);
INSERT INTO `tag` VALUES (93, '编程语言', 'matlab', 3);
INSERT INTO `tag` VALUES (94, '编程语言', 'rust', 3);
INSERT INTO `tag` VALUES (95, '编程语言', '青少年编程', 3);
INSERT INTO `tag` VALUES (96, '编程语言', 'rescript', 3);
INSERT INTO `tag` VALUES (97, '编程语言', '汇编', 3);
INSERT INTO `tag` VALUES (98, '编程语言', 'mojo', 3);
INSERT INTO `tag` VALUES (99, '编程语言', 'carbon', 3);
INSERT INTO `tag` VALUES (102, '开发工具', 'github', 9);
INSERT INTO `tag` VALUES (103, '开发工具', 'git', 9);
INSERT INTO `tag` VALUES (104, '开发工具', 'windows', 9);
INSERT INTO `tag` VALUES (105, '开发工具', 'svn', 9);
INSERT INTO `tag` VALUES (106, '开发工具', 'ide', 9);
INSERT INTO `tag` VALUES (107, '开发工具', 'visual studio', 9);
INSERT INTO `tag` VALUES (108, '开发工具', 'ci/cd', 9);
INSERT INTO `tag` VALUES (109, '开发工具', 'pycharm', 9);
INSERT INTO `tag` VALUES (110, '开发工具', 'emacs', 9);
INSERT INTO `tag` VALUES (111, '开发工具', 'vim', 9);
INSERT INTO `tag` VALUES (112, '开发工具', 'docker', 9);
INSERT INTO `tag` VALUES (113, '开发工具', 'vscode', 9);
INSERT INTO `tag` VALUES (114, '开发工具', 'postman', 9);
INSERT INTO `tag` VALUES (115, '开发工具', 'jupyter', 9);
INSERT INTO `tag` VALUES (116, '开发工具', 'macos', 9);
INSERT INTO `tag` VALUES (117, '开发工具', 'myeclipse', 9);
INSERT INTO `tag` VALUES (118, '开发工具', 'phpstorm', 9);
INSERT INTO `tag` VALUES (119, '开发工具', 'visualstudio', 9);
INSERT INTO `tag` VALUES (120, '开发工具', 'visual studio code', 9);
INSERT INTO `tag` VALUES (121, '开发工具', 'sublime text', 9);
INSERT INTO `tag` VALUES (122, '开发工具', 'intellij idea', 9);
INSERT INTO `tag` VALUES (123, '开发工具', 'webstorm', 9);
INSERT INTO `tag` VALUES (124, '开发工具', '编辑器', 9);
INSERT INTO `tag` VALUES (125, '开发工具', 'arcgis', 9);
INSERT INTO `tag` VALUES (126, '开发工具', 'gitlab', 9);
INSERT INTO `tag` VALUES (127, '开发工具', 'yapi', 9);
INSERT INTO `tag` VALUES (128, '开发工具', 'labview', 9);
INSERT INTO `tag` VALUES (132, '数据结构与算法', '算法', 10);
INSERT INTO `tag` VALUES (133, '数据结构与算法', '数据结构', 10);
INSERT INTO `tag` VALUES (134, '数据结构与算法', '线性回归', 10);
INSERT INTO `tag` VALUES (135, '数据结构与算法', '链表', 10);
INSERT INTO `tag` VALUES (136, '数据结构与算法', '贪心算法', 10);
INSERT INTO `tag` VALUES (137, '数据结构与算法', '动态规划', 10);
INSERT INTO `tag` VALUES (138, '数据结构与算法', '排序算法', 10);
INSERT INTO `tag` VALUES (139, '数据结构与算法', 'kmeans', 10);
INSERT INTO `tag` VALUES (140, '数据结构与算法', 'leetcode', 10);
INSERT INTO `tag` VALUES (141, '数据结构与算法', '决策树', 10);
INSERT INTO `tag` VALUES (142, '数据结构与算法', '最小二乘法', 10);
INSERT INTO `tag` VALUES (143, '数据结构与算法', 'b树', 10);
INSERT INTO `tag` VALUES (144, '数据结构与算法', '模拟退火算法', 10);
INSERT INTO `tag` VALUES (145, '数据结构与算法', '散列表', 10);
INSERT INTO `tag` VALUES (146, '数据结构与算法', '随机森林', 10);
INSERT INTO `tag` VALUES (147, '数据结构与算法', '支持向量机', 10);
INSERT INTO `tag` VALUES (148, '数据结构与算法', '启发式算法', 10);
INSERT INTO `tag` VALUES (149, '数据结构与算法', '逻辑回归', 10);
INSERT INTO `tag` VALUES (150, '数据结构与算法', '推荐算法', 10);
INSERT INTO `tag` VALUES (151, '数据结构与算法', '宽度优先', 10);
INSERT INTO `tag` VALUES (152, '数据结构与算法', '广度优先', 10);
INSERT INTO `tag` VALUES (153, '数据结构与算法', '深度优先', 10);
INSERT INTO `tag` VALUES (154, '数据结构与算法', '迭代加深', 10);
INSERT INTO `tag` VALUES (155, '数据结构与算法', '图搜索算法', 10);
INSERT INTO `tag` VALUES (156, '数据结构与算法', '爬山算法', 10);
INSERT INTO `tag` VALUES (157, '数据结构与算法', '近邻算法', 10);
INSERT INTO `tag` VALUES (158, '数据结构与算法', '均值算法', 10);
INSERT INTO `tag` VALUES (159, '数据结构与算法', '预编码算法', 10);
INSERT INTO `tag` VALUES (160, '数据结构与算法', '霍夫曼树', 10);
INSERT INTO `tag` VALUES (161, '数据结构与算法', '剪枝', 10);
INSERT INTO `tag` VALUES (162, '数据结构与算法', '哈希算法', 10);
INSERT INTO `tag` VALUES (163, '数据结构与算法', '柔性数组', 10);
INSERT INTO `tag` VALUES (164, '数据结构与算法', 'skiplist', 10);
INSERT INTO `tag` VALUES (165, '数据结构与算法', 'hash-index', 10);
INSERT INTO `tag` VALUES (166, '数据结构与算法', 'sstable', 10);
INSERT INTO `tag` VALUES (167, '数据结构与算法', 'lsm-tree', 10);
INSERT INTO `tag` VALUES (168, '数据结构与算法', 'inverted-index', 10);
INSERT INTO `tag` VALUES (169, '数据结构与算法', 'suffix-tree', 10);
INSERT INTO `tag` VALUES (170, '数据结构与算法', 'r-tree', 10);
INSERT INTO `tag` VALUES (171, '大数据', 'sqlite', 4);
INSERT INTO `tag` VALUES (172, '大数据', 'oracle', 4);
INSERT INTO `tag` VALUES (173, '大数据', 'json', 4);
INSERT INTO `tag` VALUES (174, '大数据', 'sql', 4);
INSERT INTO `tag` VALUES (175, '大数据', 'database', 4);
INSERT INTO `tag` VALUES (176, '大数据', 'hbase', 4);
INSERT INTO `tag` VALUES (177, '大数据', 'hadoop', 4);
INSERT INTO `tag` VALUES (178, '大数据', 'hive', 4);
INSERT INTO `tag` VALUES (179, '大数据', 'storm', 4);
INSERT INTO `tag` VALUES (180, '大数据', 'zookeeper', 4);
INSERT INTO `tag` VALUES (181, '大数据', 'spark', 4);
INSERT INTO `tag` VALUES (182, '大数据', 'memcached', 4);
INSERT INTO `tag` VALUES (183, '大数据', 'flume', 4);
INSERT INTO `tag` VALUES (184, '大数据', 'rabbitmq', 4);
INSERT INTO `tag` VALUES (185, '大数据', 'memcache', 4);
INSERT INTO `tag` VALUES (186, '大数据', 'big data', 4);
INSERT INTO `tag` VALUES (187, '大数据', 'eureka', 4);
INSERT INTO `tag` VALUES (188, '大数据', 'etcd', 4);
INSERT INTO `tag` VALUES (189, '大数据', 'flink', 4);
INSERT INTO `tag` VALUES (190, '大数据', 'consul', 4);
INSERT INTO `tag` VALUES (191, '大数据', 'postgresql', 4);
INSERT INTO `tag` VALUES (192, '大数据', 'nosql', 4);
INSERT INTO `tag` VALUES (193, '大数据', 'sqlserver', 4);
INSERT INTO `tag` VALUES (194, '大数据', '时序数据库', 4);
INSERT INTO `tag` VALUES (195, '大数据', 'tdengine', 4);
INSERT INTO `tag` VALUES (196, '大数据', '数据库', 4);
INSERT INTO `tag` VALUES (197, '大数据', 'mariadb', 4);
INSERT INTO `tag` VALUES (198, '大数据', 'talkingdata', 4);
INSERT INTO `tag` VALUES (199, '大数据', '涛思数据', 4);
INSERT INTO `tag` VALUES (200, '大数据', 'kylin', 4);
INSERT INTO `tag` VALUES (201, '大数据', 'hdfs', 4);
INSERT INTO `tag` VALUES (202, '大数据', 'mapreduce', 4);
INSERT INTO `tag` VALUES (203, '大数据', 'cloudera', 4);
INSERT INTO `tag` VALUES (204, '大数据', 'ambari', 4);
INSERT INTO `tag` VALUES (205, '大数据', 'sqoop', 4);
INSERT INTO `tag` VALUES (206, '大数据', 'odps', 4);
INSERT INTO `tag` VALUES (207, '大数据', '大数据', 4);
INSERT INTO `tag` VALUES (208, '大数据', '数据仓库', 4);
INSERT INTO `tag` VALUES (209, '大数据', 'etl', 4);
INSERT INTO `tag` VALUES (210, '大数据', '数据库架构', 4);
INSERT INTO `tag` VALUES (211, '大数据', 'dba', 4);
INSERT INTO `tag` VALUES (212, '大数据', 'etl工程师', 4);
INSERT INTO `tag` VALUES (213, '大数据', '数据库开发', 4);
INSERT INTO `tag` VALUES (214, '大数据', 'activemq', 4);
INSERT INTO `tag` VALUES (215, '大数据', 'rocketmq', 4);
INSERT INTO `tag` VALUES (216, '大数据', 'finebi', 4);
INSERT INTO `tag` VALUES (217, '大数据', 'powerbi', 4);
INSERT INTO `tag` VALUES (218, '大数据', 'sequoiadb', 4);
INSERT INTO `tag` VALUES (219, '大数据', 'oceanbase', 4);
INSERT INTO `tag` VALUES (220, '大数据', 'tidb', 4);
INSERT INTO `tag` VALUES (221, '大数据', 'couchdb', 4);
INSERT INTO `tag` VALUES (222, '大数据', 'opentsdb', 4);
INSERT INTO `tag` VALUES (223, '大数据', 'iotdb', 4);
INSERT INTO `tag` VALUES (224, '大数据', 'milvus', 4);
INSERT INTO `tag` VALUES (225, '大数据', 'faiss', 4);
INSERT INTO `tag` VALUES (226, '大数据', 'jina', 4);
INSERT INTO `tag` VALUES (227, '大数据', 'neo4j', 4);
INSERT INTO `tag` VALUES (228, '大数据', 'clickhouse', 4);
INSERT INTO `tag` VALUES (229, '大数据', 'ceph', 4);
INSERT INTO `tag` VALUES (230, '大数据', 'gaussdb', 4);
INSERT INTO `tag` VALUES (231, '大数据', 'fusioninsight', 4);
INSERT INTO `tag` VALUES (237, '前端', 'html5', 2);
INSERT INTO `tag` VALUES (238, '前端', 'firefox', 2);
INSERT INTO `tag` VALUES (239, '前端', 'jquery', 2);
INSERT INTO `tag` VALUES (240, '前端', 'ajax', 2);
INSERT INTO `tag` VALUES (241, '前端', '正则表达式', 2);
INSERT INTO `tag` VALUES (242, '前端', 'chrome', 2);
INSERT INTO `tag` VALUES (243, '前端', 'safari', 2);
INSERT INTO `tag` VALUES (244, '前端', 'easyui', 2);
INSERT INTO `tag` VALUES (245, '前端', 'bootstrap', 2);
INSERT INTO `tag` VALUES (246, '前端', 'ecmascript', 2);
INSERT INTO `tag` VALUES (247, '前端', 'css3', 2);
INSERT INTO `tag` VALUES (248, '前端', 'echarts', 2);
INSERT INTO `tag` VALUES (249, '前端', 'less', 2);
INSERT INTO `tag` VALUES (250, '前端', 'node.js', 2);
INSERT INTO `tag` VALUES (251, '前端', 'gulp', 2);
INSERT INTO `tag` VALUES (252, '前端', 'vue.js', 2);
INSERT INTO `tag` VALUES (253, '前端', 'electron', 2);
INSERT INTO `tag` VALUES (254, '前端', 'angular.js', 2);
INSERT INTO `tag` VALUES (255, '前端', 'layui', 2);
INSERT INTO `tag` VALUES (256, '前端', 'react.js', 2);
INSERT INTO `tag` VALUES (257, '前端', 'stylus', 2);
INSERT INTO `tag` VALUES (258, '前端', 'elementui', 2);
INSERT INTO `tag` VALUES (259, '前端', 'scss', 2);
INSERT INTO `tag` VALUES (260, '前端', 'reactjs', 2);
INSERT INTO `tag` VALUES (261, '前端', 'es6', 2);
INSERT INTO `tag` VALUES (262, '前端', 'npm', 2);
INSERT INTO `tag` VALUES (263, '前端', 'sass', 2);
INSERT INTO `tag` VALUES (264, '前端', 'chrome devtools', 2);
INSERT INTO `tag` VALUES (265, '前端', 'angular', 2);
INSERT INTO `tag` VALUES (266, '前端', 'coffeescript', 2);
INSERT INTO `tag` VALUES (267, '前端', 'postcss', 2);
INSERT INTO `tag` VALUES (268, '前端', 'fiddler', 2);
INSERT INTO `tag` VALUES (269, '前端', 'webkit', 2);
INSERT INTO `tag` VALUES (270, '前端', 'yarn', 2);
INSERT INTO `tag` VALUES (271, '前端', 'firebug', 2);
INSERT INTO `tag` VALUES (272, '前端', 'edge', 2);
INSERT INTO `tag` VALUES (273, '前端', 'webpack', 2);
INSERT INTO `tag` VALUES (274, '前端', '前端', 2);
INSERT INTO `tag` VALUES (275, '前端', 'xss', 2);
INSERT INTO `tag` VALUES (276, '前端', 'csrf', 2);
INSERT INTO `tag` VALUES (277, '前端', 'xhtml', 2);
INSERT INTO `tag` VALUES (278, '前端', '前端框架', 2);
INSERT INTO `tag` VALUES (279, '前端', 'view design', 2);
INSERT INTO `tag` VALUES (280, '前端', 'tdesign', 2);
INSERT INTO `tag` VALUES (281, '前端', 'arco design', 2);
INSERT INTO `tag` VALUES (282, '前端', 'anti-design-vue', 2);
INSERT INTO `tag` VALUES (283, '前端', 'express', 2);
INSERT INTO `tag` VALUES (284, '前端', 'turbopack', 2);
INSERT INTO `tag` VALUES (291, '后端', 'mvc', 1);
INSERT INTO `tag` VALUES (292, '后端', 'nginx', 1);
INSERT INTO `tag` VALUES (293, '后端', 'asp.net', 1);
INSERT INTO `tag` VALUES (294, '后端', 'swoole', 1);
INSERT INTO `tag` VALUES (295, '后端', 'ruby on rails', 1);
INSERT INTO `tag` VALUES (296, '后端', 'lavarel', 1);
INSERT INTO `tag` VALUES (297, '后端', '爬虫', 1);
INSERT INTO `tag` VALUES (298, '后端', '后端', 1);
INSERT INTO `tag` VALUES (299, '后端', 'restful', 1);
INSERT INTO `tag` VALUES (300, '后端', 'graphql', 1);
INSERT INTO `tag` VALUES (301, '后端', '架构', 1);
INSERT INTO `tag` VALUES (302, '后端', '分布式', 1);
INSERT INTO `tag` VALUES (303, '后端', '中间件', 1);
INSERT INTO `tag` VALUES (304, '后端', 'gateway', 1);
INSERT INTO `tag` VALUES (305, '后端', 'ribbon', 1);
INSERT INTO `tag` VALUES (306, '后端', 'gin', 1);
INSERT INTO `tag` VALUES (307, '后端', 'beego', 1);
INSERT INTO `tag` VALUES (308, '后端', 'hystrix', 1);
INSERT INTO `tag` VALUES (309, '后端', 'logback', 1);
INSERT INTO `tag` VALUES (326, '云原生', '容器', 12);
INSERT INTO `tag` VALUES (327, '云原生', 'jenkins', 12);
INSERT INTO `tag` VALUES (328, '云原生', 'devops', 12);
INSERT INTO `tag` VALUES (329, '云原生', 'kubernetes', 12);
INSERT INTO `tag` VALUES (330, '云原生', '云原生', 12);
INSERT INTO `tag` VALUES (331, '云原生', '微服务', 12);
INSERT INTO `tag` VALUES (332, '云原生', '服务发现', 12);
INSERT INTO `tag` VALUES (333, '云原生', 'paas', 12);
INSERT INTO `tag` VALUES (334, '云原生', 'serverless', 12);
INSERT INTO `tag` VALUES (335, '云原生', 'kubeless', 12);
INSERT INTO `tag` VALUES (336, '云原生', 'kubelet', 12);
INSERT INTO `tag` VALUES (337, '云原生', 'kind', 12);
INSERT INTO `tag` VALUES (338, '云原生', 'knative', 12);
INSERT INTO `tag` VALUES (339, '云原生', 'istio', 12);
INSERT INTO `tag` VALUES (340, '云原生', 'service_mesh', 12);
INSERT INTO `tag` VALUES (341, '云原生', 'terraform', 12);
INSERT INTO `tag` VALUES (342, '云原生', 'argocd', 12);
INSERT INTO `tag` VALUES (343, '云原生', 'rancher', 12);
INSERT INTO `tag` VALUES (344, '云原生', 'openshift', 12);
INSERT INTO `tag` VALUES (345, '云原生', 'openstack', 12);
INSERT INTO `tag` VALUES (346, '云原生', 'harvester', 12);
INSERT INTO `tag` VALUES (347, '云原生', 'podman', 12);
INSERT INTO `tag` VALUES (354, '移动开发', 'android', 22);
INSERT INTO `tag` VALUES (355, '移动开发', 'ios', 22);
INSERT INTO `tag` VALUES (356, '移动开发', 'iphone', 22);
INSERT INTO `tag` VALUES (357, '移动开发', 'webview', 22);
INSERT INTO `tag` VALUES (358, '移动开发', 'xml', 22);
INSERT INTO `tag` VALUES (359, '移动开发', 'xcode', 22);
INSERT INTO `tag` VALUES (360, '移动开发', 'phonegap', 22);
INSERT INTO `tag` VALUES (361, '移动开发', 'apache', 22);
INSERT INTO `tag` VALUES (362, '移动开发', 'ipad', 22);
INSERT INTO `tag` VALUES (363, '移动开发', 'cocoa', 22);
INSERT INTO `tag` VALUES (364, '移动开发', '小程序', 22);
INSERT INTO `tag` VALUES (365, '移动开发', 'xamarin', 22);
INSERT INTO `tag` VALUES (366, '移动开发', '微信小程序', 22);
INSERT INTO `tag` VALUES (367, '移动开发', 'reactnative', 22);
INSERT INTO `tag` VALUES (368, '移动开发', 'flutter', 22);
INSERT INTO `tag` VALUES (369, '移动开发', 'android-studio', 22);
INSERT INTO `tag` VALUES (370, '移动开发', '百度小程序', 22);
INSERT INTO `tag` VALUES (371, '移动开发', 'react native', 22);
INSERT INTO `tag` VALUES (372, '移动开发', 'android studio', 22);
INSERT INTO `tag` VALUES (373, '移动开发', 'web app', 22);
INSERT INTO `tag` VALUES (374, '移动开发', 'gradle', 22);
INSERT INTO `tag` VALUES (375, '移动开发', 'android jetpack', 22);
INSERT INTO `tag` VALUES (376, '移动开发', 'rxjava', 22);
INSERT INTO `tag` VALUES (377, '移动开发', 'swiftui', 22);
INSERT INTO `tag` VALUES (378, '移动开发', 'cocoapods', 22);
INSERT INTO `tag` VALUES (379, '移动开发', 'wwdc', 22);
INSERT INTO `tag` VALUES (380, '移动开发', 'rxswift', 22);
INSERT INTO `tag` VALUES (381, '移动开发', 'dalvik', 22);
INSERT INTO `tag` VALUES (382, '移动开发', 'okhttp', 22);
INSERT INTO `tag` VALUES (383, '移动开发', 'retrofit', 22);
INSERT INTO `tag` VALUES (384, '移动开发', 'glide', 22);
INSERT INTO `tag` VALUES (385, '移动开发', 'binder', 22);
INSERT INTO `tag` VALUES (386, '移动开发', 'android runtime', 22);
INSERT INTO `tag` VALUES (387, '移动开发', 'zygote', 22);
INSERT INTO `tag` VALUES (388, '移动开发', 'appcompat', 22);
INSERT INTO `tag` VALUES (389, '移动开发', 'androidx', 22);
INSERT INTO `tag` VALUES (390, '移动开发', 'adb', 22);
INSERT INTO `tag` VALUES (391, '移动开发', 'uni-app', 22);
INSERT INTO `tag` VALUES (392, '移动开发', 'taro', 22);
INSERT INTO `tag` VALUES (398, '人工智能', 'opencv', 5);
INSERT INTO `tag` VALUES (399, '人工智能', '数据挖掘', 5);
INSERT INTO `tag` VALUES (400, '人工智能', '语音识别', 5);
INSERT INTO `tag` VALUES (401, '人工智能', '计算机视觉', 5);
INSERT INTO `tag` VALUES (402, '人工智能', '目标检测', 5);
INSERT INTO `tag` VALUES (403, '人工智能', '机器学习', 5);
INSERT INTO `tag` VALUES (404, '人工智能', '人工智能', 5);
INSERT INTO `tag` VALUES (405, '人工智能', 'caffe', 5);
INSERT INTO `tag` VALUES (406, '人工智能', '深度学习', 5);
INSERT INTO `tag` VALUES (407, '人工智能', '神经网络', 5);
INSERT INTO `tag` VALUES (408, '人工智能', '自然语言处理', 5);
INSERT INTO `tag` VALUES (409, '人工智能', 'sklearn', 5);
INSERT INTO `tag` VALUES (410, '人工智能', 'cnn', 5);
INSERT INTO `tag` VALUES (411, '人工智能', 'mllib', 5);
INSERT INTO `tag` VALUES (412, '人工智能', 'word2vec', 5);
INSERT INTO `tag` VALUES (413, '人工智能', 'tensorflow', 5);
INSERT INTO `tag` VALUES (414, '人工智能', '目标跟踪', 5);
INSERT INTO `tag` VALUES (415, '人工智能', 'keras', 5);
INSERT INTO `tag` VALUES (416, '人工智能', '知识图谱', 5);
INSERT INTO `tag` VALUES (417, '人工智能', 'rnn', 5);
INSERT INTO `tag` VALUES (418, '人工智能', 'lstm', 5);
INSERT INTO `tag` VALUES (419, '人工智能', '自动驾驶', 5);
INSERT INTO `tag` VALUES (420, '人工智能', 'dnn', 5);
INSERT INTO `tag` VALUES (421, '人工智能', '生成对抗网络', 5);
INSERT INTO `tag` VALUES (422, '人工智能', 'mxnet', 5);
INSERT INTO `tag` VALUES (423, '人工智能', 'pytorch', 5);
INSERT INTO `tag` VALUES (424, '人工智能', '机器翻译', 5);
INSERT INTO `tag` VALUES (425, '人工智能', '语言模型', 5);
INSERT INTO `tag` VALUES (426, '人工智能', 'oneflow', 5);
INSERT INTO `tag` VALUES (427, '人工智能', 'mlnet', 5);
INSERT INTO `tag` VALUES (428, '人工智能', 'paddlepaddle', 5);
INSERT INTO `tag` VALUES (429, '人工智能', 'gru', 5);
INSERT INTO `tag` VALUES (430, '人工智能', 'mnn', 5);
INSERT INTO `tag` VALUES (431, '人工智能', 'boosting', 5);
INSERT INTO `tag` VALUES (432, '人工智能', 'transformer', 5);
INSERT INTO `tag` VALUES (433, '人工智能', 'xlnet', 5);
INSERT INTO `tag` VALUES (434, '人工智能', 'bert', 5);
INSERT INTO `tag` VALUES (435, '人工智能', 'openvino', 5);
INSERT INTO `tag` VALUES (436, '人工智能', '边缘计算', 5);
INSERT INTO `tag` VALUES (437, '人工智能', '超分辨率重建', 5);
INSERT INTO `tag` VALUES (438, '人工智能', '智慧城市', 5);
INSERT INTO `tag` VALUES (439, '人工智能', '视觉检测', 5);
INSERT INTO `tag` VALUES (440, '人工智能', '图像处理', 5);
INSERT INTO `tag` VALUES (441, '人工智能', 'nlp', 5);
INSERT INTO `tag` VALUES (442, '人工智能', '数据分析', 5);
INSERT INTO `tag` VALUES (443, '人工智能', '聚类', 5);
INSERT INTO `tag` VALUES (444, '人工智能', '集成学习', 5);
INSERT INTO `tag` VALUES (445, '人工智能', '迁移学习', 5);
INSERT INTO `tag` VALUES (446, '人工智能', '分类', 5);
INSERT INTO `tag` VALUES (447, '人工智能', '回归', 5);
INSERT INTO `tag` VALUES (448, '人工智能', 'gpt-3', 5);
INSERT INTO `tag` VALUES (449, '人工智能', 'spark-ml', 5);
INSERT INTO `tag` VALUES (450, '人工智能', 'AI作画', 5);
INSERT INTO `tag` VALUES (451, '人工智能', 'tf-idf', 5);
INSERT INTO `tag` VALUES (452, '人工智能', 'stable diffusion', 5);
INSERT INTO `tag` VALUES (453, '人工智能', 'chatgpt', 5);
INSERT INTO `tag` VALUES (454, '人工智能', 'DALL·E 2', 5);
INSERT INTO `tag` VALUES (455, '人工智能', 'craiyon', 5);
INSERT INTO `tag` VALUES (456, '人工智能', 'Imagen', 5);
INSERT INTO `tag` VALUES (457, '人工智能', 'DreamFusion', 5);
INSERT INTO `tag` VALUES (458, '人工智能', 'AudioLM', 5);
INSERT INTO `tag` VALUES (459, '人工智能', 'YOLO', 5);
INSERT INTO `tag` VALUES (460, '人工智能', 'bard', 5);
INSERT INTO `tag` VALUES (461, '人工智能', '文心一言', 5);
INSERT INTO `tag` VALUES (462, '人工智能', 'ocr', 5);
INSERT INTO `tag` VALUES (463, '人工智能', '腾讯云AI代码助手', 5);
INSERT INTO `tag` VALUES (465, '网络与通信', 'http', 37);
INSERT INTO `tag` VALUES (466, '网络与通信', 'p2p', 37);
INSERT INTO `tag` VALUES (467, '网络与通信', 'udp', 37);
INSERT INTO `tag` VALUES (468, '网络与通信', 'ssl', 37);
INSERT INTO `tag` VALUES (469, '网络与通信', 'https', 37);
INSERT INTO `tag` VALUES (470, '网络与通信', 'wireshark', 37);
INSERT INTO `tag` VALUES (471, '网络与通信', 'websocket', 37);
INSERT INTO `tag` VALUES (472, '网络与通信', '网络安全', 37);
INSERT INTO `tag` VALUES (473, '网络与通信', 'tcpdump', 37);
INSERT INTO `tag` VALUES (474, '网络与通信', '网络协议', 37);
INSERT INTO `tag` VALUES (475, '网络与通信', 'tcp/ip', 37);
INSERT INTO `tag` VALUES (476, '网络与通信', 'rpc', 37);
INSERT INTO `tag` VALUES (477, '网络与通信', '5G', 37);
INSERT INTO `tag` VALUES (478, '网络与通信', '信号处理', 37);
INSERT INTO `tag` VALUES (479, '网络与通信', '信息与通信', 37);
INSERT INTO `tag` VALUES (480, '嵌入式', '单片机', 24);
INSERT INTO `tag` VALUES (481, '嵌入式', 'stm32', 24);
INSERT INTO `tag` VALUES (482, '嵌入式', '51单片机', 24);
INSERT INTO `tag` VALUES (483, '嵌入式', 'proteus', 24);
INSERT INTO `tag` VALUES (484, '嵌入式', 'mcu', 24);
INSERT INTO `tag` VALUES (485, '嵌入式', '物联网', 24);
INSERT INTO `tag` VALUES (486, '嵌入式', '嵌入式硬件', 24);
INSERT INTO `tag` VALUES (487, '嵌入式', 'iot', 24);
INSERT INTO `tag` VALUES (488, '嵌入式', '嵌入式实时数据库', 24);
INSERT INTO `tag` VALUES (489, '嵌入式', 'rtdbs', 24);
INSERT INTO `tag` VALUES (490, '硬件开发', '硬件工程', 36);
INSERT INTO `tag` VALUES (491, '硬件开发', '驱动开发', 36);
INSERT INTO `tag` VALUES (492, '硬件开发', 'fpga开发', 36);
INSERT INTO `tag` VALUES (493, '硬件开发', 'dsp开发', 36);
INSERT INTO `tag` VALUES (494, '硬件开发', 'arm开发', 36);
INSERT INTO `tag` VALUES (495, '硬件开发', '材料工程', 36);
INSERT INTO `tag` VALUES (496, '硬件开发', '精益工程', 36);
INSERT INTO `tag` VALUES (497, '硬件开发', '射频工程', 36);
INSERT INTO `tag` VALUES (498, '硬件开发', '基带工程', 36);
INSERT INTO `tag` VALUES (499, '硬件开发', '硬件架构', 36);
INSERT INTO `tag` VALUES (500, '硬件开发', 'pcb工艺', 36);
INSERT INTO `tag` VALUES (501, '游戏', 'cocos2d', 34);
INSERT INTO `tag` VALUES (502, '游戏', '动画', 34);
INSERT INTO `tag` VALUES (503, '游戏', 'ogre', 34);
INSERT INTO `tag` VALUES (504, '游戏', 'unity', 34);
INSERT INTO `tag` VALUES (505, '游戏', '游戏引擎', 34);
INSERT INTO `tag` VALUES (506, '游戏', 'ar', 34);
INSERT INTO `tag` VALUES (507, '游戏', '3dsmax', 34);
INSERT INTO `tag` VALUES (508, '游戏', 'maya', 34);
INSERT INTO `tag` VALUES (509, '游戏', '贴图', 34);
INSERT INTO `tag` VALUES (510, '游戏', 'uv', 34);
INSERT INTO `tag` VALUES (511, '游戏', 'vr', 34);
INSERT INTO `tag` VALUES (512, '游戏', 'ue4', 34);
INSERT INTO `tag` VALUES (513, '游戏', 'houdini', 34);
INSERT INTO `tag` VALUES (514, '游戏', '着色器', 34);
INSERT INTO `tag` VALUES (515, '游戏', '材质', 34);
INSERT INTO `tag` VALUES (516, '游戏', '技术美术', 34);
INSERT INTO `tag` VALUES (517, '游戏', 'blender', 34);
INSERT INTO `tag` VALUES (518, '游戏', 'spine', 34);
INSERT INTO `tag` VALUES (519, '游戏', '图形渲染', 34);
INSERT INTO `tag` VALUES (520, '游戏', '虚幻', 34);
INSERT INTO `tag` VALUES (521, '游戏', 'ue5', 34);
INSERT INTO `tag` VALUES (522, '游戏', 'godot', 34);
INSERT INTO `tag` VALUES (523, '游戏', 'cryengine', 34);
INSERT INTO `tag` VALUES (524, '游戏', 'lumberyard', 34);
INSERT INTO `tag` VALUES (525, '游戏', 'mr', 34);
INSERT INTO `tag` VALUES (526, '游戏', 'xr', 34);
INSERT INTO `tag` VALUES (527, '游戏', 'cinema4d', 34);
INSERT INTO `tag` VALUES (528, '游戏', 'zbrush', 34);
INSERT INTO `tag` VALUES (529, '游戏', '3dcoat', 34);
INSERT INTO `tag` VALUES (530, '游戏', 'topogun', 34);
INSERT INTO `tag` VALUES (531, '游戏', 'rizomuv', 34);
INSERT INTO `tag` VALUES (532, '游戏', 'substance designer', 34);
INSERT INTO `tag` VALUES (533, '游戏', 'substance painter', 34);
INSERT INTO `tag` VALUES (534, '游戏', 'quixel', 34);
INSERT INTO `tag` VALUES (535, '游戏', '数字雕刻', 34);
INSERT INTO `tag` VALUES (536, '游戏', '重拓扑', 34);
INSERT INTO `tag` VALUES (537, '游戏', '骨骼绑定', 34);
INSERT INTO `tag` VALUES (538, '游戏', '关卡设计', 34);
INSERT INTO `tag` VALUES (539, '游戏', '游戏程序', 34);
INSERT INTO `tag` VALUES (540, '游戏', '游戏美术', 34);
INSERT INTO `tag` VALUES (541, '游戏', '游戏策划', 34);
INSERT INTO `tag` VALUES (542, '游戏', 'cascadeur', 34);
INSERT INTO `tag` VALUES (544, 'HarmonyOS', '华为', 7);
INSERT INTO `tag` VALUES (545, 'HarmonyOS', 'harmonyos', 7);
INSERT INTO `tag` VALUES (546, 'HarmonyOS', '华为云', 7);
INSERT INTO `tag` VALUES (547, 'HarmonyOS', '华为od', 7);
INSERT INTO `tag` VALUES (548, '微软技术', '.net', 27);
INSERT INTO `tag` VALUES (549, '微软技术', 'wpf', 27);
INSERT INTO `tag` VALUES (550, '微软技术', 'mfc', 27);
INSERT INTO `tag` VALUES (551, '微软技术', 'sharepoint', 27);
INSERT INTO `tag` VALUES (552, '微软技术', 'linq', 27);
INSERT INTO `tag` VALUES (553, '微软技术', 'microsoft', 27);
INSERT INTO `tag` VALUES (554, '微软技术', 'azure', 27);
INSERT INTO `tag` VALUES (555, '微软技术', 'hololens', 27);
INSERT INTO `tag` VALUES (556, '微软技术', 'mssql', 27);
INSERT INTO `tag` VALUES (557, '微软技术', '.netcore', 27);
INSERT INTO `tag` VALUES (558, '微软技术', 'xbox', 27);
INSERT INTO `tag` VALUES (569, '操作系统', 'linux', 29);
INSERT INTO `tag` VALUES (570, '操作系统', 'ubuntu', 29);
INSERT INTO `tag` VALUES (571, '操作系统', 'centos', 29);
INSERT INTO `tag` VALUES (572, '操作系统', 'gnu', 29);
INSERT INTO `tag` VALUES (573, '操作系统', 'risc-v', 29);
INSERT INTO `tag` VALUES (574, '操作系统', 'blackberry', 29);
INSERT INTO `tag` VALUES (578, '搜索', 'lucene', 28);
INSERT INTO `tag` VALUES (579, '搜索', 'solr', 28);
INSERT INTO `tag` VALUES (580, '搜索', 'sphinx', 28);
INSERT INTO `tag` VALUES (581, '搜索', '搜索引擎', 28);
INSERT INTO `tag` VALUES (582, '搜索', '全文检索', 28);
INSERT INTO `tag` VALUES (583, '搜索', '中文分词', 28);
INSERT INTO `tag` VALUES (585, '设计模式', 'uml', 21);
INSERT INTO `tag` VALUES (586, '设计模式', '单例模式', 21);
INSERT INTO `tag` VALUES (587, '设计模式', '开闭原则', 21);
INSERT INTO `tag` VALUES (588, '设计模式', '命令模式', 21);
INSERT INTO `tag` VALUES (589, '设计模式', '代理模式', 21);
INSERT INTO `tag` VALUES (590, '设计模式', '桥接模式', 21);
INSERT INTO `tag` VALUES (591, '设计模式', '观察者模式', 21);
INSERT INTO `tag` VALUES (592, '设计模式', '访问者模式', 21);
INSERT INTO `tag` VALUES (593, '设计模式', '迭代器模式', 21);
INSERT INTO `tag` VALUES (594, '设计模式', '简单工厂模式', 21);
INSERT INTO `tag` VALUES (595, '设计模式', '里氏替换原则', 21);
INSERT INTO `tag` VALUES (596, '设计模式', '依赖倒置原则', 21);
INSERT INTO `tag` VALUES (597, '设计模式', '单一职责原则', 21);
INSERT INTO `tag` VALUES (598, '设计模式', '接口隔离原则', 21);
INSERT INTO `tag` VALUES (599, '设计模式', '迪米特法则', 21);
INSERT INTO `tag` VALUES (600, '设计模式', '合成复用原则', 21);
INSERT INTO `tag` VALUES (601, '设计模式', '原型模式', 21);
INSERT INTO `tag` VALUES (602, '设计模式', '工厂方法模式', 21);
INSERT INTO `tag` VALUES (603, '设计模式', '抽象工厂模式', 21);
INSERT INTO `tag` VALUES (604, '设计模式', '建造者模式', 21);
INSERT INTO `tag` VALUES (605, '设计模式', '适配器模式', 21);
INSERT INTO `tag` VALUES (606, '设计模式', '装饰器模式', 21);
INSERT INTO `tag` VALUES (607, '设计模式', '外观模式', 21);
INSERT INTO `tag` VALUES (608, '设计模式', '享元模式', 21);
INSERT INTO `tag` VALUES (609, '设计模式', '组合模式', 21);
INSERT INTO `tag` VALUES (610, '设计模式', '模板方法模式', 21);
INSERT INTO `tag` VALUES (611, '设计模式', '策略模式', 21);
INSERT INTO `tag` VALUES (612, '设计模式', '责任链模式', 21);
INSERT INTO `tag` VALUES (613, '设计模式', '状态模式', 21);
INSERT INTO `tag` VALUES (614, '设计模式', '中介者模式', 21);
INSERT INTO `tag` VALUES (615, '设计模式', '备忘录模式', 21);
INSERT INTO `tag` VALUES (616, '设计模式', '解释器模式', 21);
INSERT INTO `tag` VALUES (617, '设计模式', '设计模式', 21);
INSERT INTO `tag` VALUES (618, '测试', '单元测试', 33);
INSERT INTO `tag` VALUES (619, '测试', 'selenium', 33);
INSERT INTO `tag` VALUES (620, '测试', 'jira', 33);
INSERT INTO `tag` VALUES (621, '测试', '测试工具', 33);
INSERT INTO `tag` VALUES (622, '测试', '压力测试', 33);
INSERT INTO `tag` VALUES (623, '测试', '测试用例', 33);
INSERT INTO `tag` VALUES (624, '测试', 'ab测试', 33);
INSERT INTO `tag` VALUES (625, '测试', '集成测试', 33);
INSERT INTO `tag` VALUES (626, '测试', '模块测试', 33);
INSERT INTO `tag` VALUES (627, '测试', '测试覆盖率', 33);
INSERT INTO `tag` VALUES (628, '测试', '安全性测试', 33);
INSERT INTO `tag` VALUES (629, '测试', '威胁分析', 33);
INSERT INTO `tag` VALUES (630, '测试', '可用性测试', 33);
INSERT INTO `tag` VALUES (631, '测试', '功能测试', 33);
INSERT INTO `tag` VALUES (632, '测试', 'metersphere', 33);
INSERT INTO `tag` VALUES (633, '测试', 'appium', 33);
INSERT INTO `tag` VALUES (634, '测试', 'jmeter', 33);
INSERT INTO `tag` VALUES (635, '测试', 'testlink', 33);
INSERT INTO `tag` VALUES (637, '云平台', '云计算', 17);
INSERT INTO `tag` VALUES (638, '云平台', '七牛云存储', 17);
INSERT INTO `tag` VALUES (639, '云平台', '百度云', 17);
INSERT INTO `tag` VALUES (640, '云平台', '腾讯云', 17);
INSERT INTO `tag` VALUES (641, '云平台', '阿里云', 17);
INSERT INTO `tag` VALUES (642, '云平台', 'aws', 17);
INSERT INTO `tag` VALUES (643, '云平台', '京东云', 17);
INSERT INTO `tag` VALUES (644, '云平台', '火山引擎', 17);
INSERT INTO `tag` VALUES (645, '云平台', 'CSDN开发云', 17);
INSERT INTO `tag` VALUES (646, '云平台', 'googlecloud', 17);
INSERT INTO `tag` VALUES (649, '软件工程', 'tfs', 11);
INSERT INTO `tag` VALUES (650, '软件工程', '需求分析', 11);
INSERT INTO `tag` VALUES (651, '软件工程', '结对编程', 11);
INSERT INTO `tag` VALUES (652, '软件工程', '团队开发', 11);
INSERT INTO `tag` VALUES (653, '软件工程', 'scrum', 11);
INSERT INTO `tag` VALUES (654, '软件工程', 'sprint', 11);
INSERT INTO `tag` VALUES (655, '软件工程', '个人开发', 11);
INSERT INTO `tag` VALUES (656, '软件工程', '规格说明书', 11);
INSERT INTO `tag` VALUES (657, '软件工程', '极限编程', 11);
INSERT INTO `tag` VALUES (658, '软件工程', '敏捷流程', 11);
INSERT INTO `tag` VALUES (659, '软件工程', '性能优化', 11);
INSERT INTO `tag` VALUES (660, '软件工程', '新媒体运营', 11);
INSERT INTO `tag` VALUES (661, '软件工程', '内容运营', 11);
INSERT INTO `tag` VALUES (662, '软件工程', '用户运营', 11);
INSERT INTO `tag` VALUES (663, '软件工程', '产品运营', 11);
INSERT INTO `tag` VALUES (664, '软件工程', 'axure', 11);
INSERT INTO `tag` VALUES (665, '软件工程', '墨刀', 11);
INSERT INTO `tag` VALUES (666, '软件工程', '流量运营', 11);
INSERT INTO `tag` VALUES (667, '软件工程', '交互', 11);
INSERT INTO `tag` VALUES (668, '软件工程', 'ux', 11);
INSERT INTO `tag` VALUES (669, '软件工程', 'ui', 11);
INSERT INTO `tag` VALUES (670, '软件工程', '开源', 11);
INSERT INTO `tag` VALUES (671, '软件工程', '软件工程', 11);
INSERT INTO `tag` VALUES (672, '软件工程', 'tdd', 11);
INSERT INTO `tag` VALUES (673, '软件工程', '代码复审', 11);
INSERT INTO `tag` VALUES (674, '软件工程', '重构', 11);
INSERT INTO `tag` VALUES (675, '软件工程', '源代码管理', 11);
INSERT INTO `tag` VALUES (676, '软件工程', '代码规范', 11);
INSERT INTO `tag` VALUES (677, '软件工程', '软件构建', 11);
INSERT INTO `tag` VALUES (678, '软件工程', 'cmmi', 11);
INSERT INTO `tag` VALUES (679, '软件工程', '甘特图', 11);
INSERT INTO `tag` VALUES (680, '软件工程', '流程图', 11);
INSERT INTO `tag` VALUES (681, '软件工程', '代码覆盖率', 11);
INSERT INTO `tag` VALUES (682, '软件工程', 'bug', 11);
INSERT INTO `tag` VALUES (683, '软件工程', '设计规范', 11);
INSERT INTO `tag` VALUES (684, '软件工程', 'issue', 11);
INSERT INTO `tag` VALUES (685, '软件工程', 'redmine', 11);
INSERT INTO `tag` VALUES (686, '软件工程', 'teambition', 11);
INSERT INTO `tag` VALUES (687, '软件工程', '产品经理', 11);
INSERT INTO `tag` VALUES (694, '区块链', '区块链', 20);
INSERT INTO `tag` VALUES (695, '区块链', '智能合约', 20);
INSERT INTO `tag` VALUES (696, '区块链', '信任链', 20);
INSERT INTO `tag` VALUES (697, '区块链', '去中心化', 20);
INSERT INTO `tag` VALUES (698, '区块链', '分布式账本', 20);
INSERT INTO `tag` VALUES (699, '区块链', '共识算法', 20);
INSERT INTO `tag` VALUES (700, '区块链', '同态加密', 20);
INSERT INTO `tag` VALUES (701, '区块链', '零知识证明', 20);
INSERT INTO `tag` VALUES (702, '区块链', 'web3', 20);
INSERT INTO `tag` VALUES (703, '数学', '线性代数', 31);
INSERT INTO `tag` VALUES (704, '数学', '矩阵', 31);
INSERT INTO `tag` VALUES (705, '数学', '概率论', 31);
INSERT INTO `tag` VALUES (706, '数学', '拓扑学', 31);
INSERT INTO `tag` VALUES (707, '数学', '抽象代数', 31);
INSERT INTO `tag` VALUES (708, '数学', '几何学', 31);
INSERT INTO `tag` VALUES (709, '数学', '图论', 31);
INSERT INTO `tag` VALUES (710, '数学', '傅立叶分析', 31);
INSERT INTO `tag` VALUES (711, '数学', '数学建模', 31);
INSERT INTO `tag` VALUES (713, '运维', '负载均衡', 19);
INSERT INTO `tag` VALUES (714, '运维', '服务器', 19);
INSERT INTO `tag` VALUES (715, '运维', '运维', 19);
INSERT INTO `tag` VALUES (716, '运维', 'ssh', 19);
INSERT INTO `tag` VALUES (717, '运维', 'vagrant', 19);
INSERT INTO `tag` VALUES (718, '运维', 'debian', 19);
INSERT INTO `tag` VALUES (719, '运维', 'fabric', 19);
INSERT INTO `tag` VALUES (720, '运维', '自动化', 19);
INSERT INTO `tag` VALUES (721, '运维', '系统架构', 19);
INSERT INTO `tag` VALUES (722, '运维', '网络', 19);
INSERT INTO `tag` VALUES (723, '运维', '运维开发', 19);
INSERT INTO `tag` VALUES (724, '运维', 'graylog', 19);
INSERT INTO `tag` VALUES (725, '运维', 'elk', 19);
INSERT INTO `tag` VALUES (726, '运维', 'ezone', 19);
INSERT INTO `tag` VALUES (727, '运维', 'gitea', 19);
INSERT INTO `tag` VALUES (728, '运维', 'tekton', 19);
INSERT INTO `tag` VALUES (729, '运维', 'puppet', 19);
INSERT INTO `tag` VALUES (730, '运维', 'saltstack', 19);
INSERT INTO `tag` VALUES (731, '运维', 'ansible', 19);
INSERT INTO `tag` VALUES (732, '运维', 'prometheus', 19);
INSERT INTO `tag` VALUES (733, '运维', 'skywalking', 19);
INSERT INTO `tag` VALUES (734, '运维', 'sentry', 19);
INSERT INTO `tag` VALUES (735, '运维', 'zabbix', 19);
INSERT INTO `tag` VALUES (736, '运维', 'grafana', 19);
INSERT INTO `tag` VALUES (737, '运维', 'kong', 19);
INSERT INTO `tag` VALUES (738, '运维', 'openresty', 19);
INSERT INTO `tag` VALUES (739, '运维', 'lvs', 19);
INSERT INTO `tag` VALUES (750, '网络空间安全', '安全', 38);
INSERT INTO `tag` VALUES (751, '网络空间安全', '系统安全', 38);
INSERT INTO `tag` VALUES (752, '网络空间安全', 'web安全', 38);
INSERT INTO `tag` VALUES (753, '网络空间安全', '安全架构', 38);
INSERT INTO `tag` VALUES (754, '网络空间安全', '密码学', 38);
INSERT INTO `tag` VALUES (755, '网络空间安全', '可信计算技术', 38);
INSERT INTO `tag` VALUES (756, '网络空间安全', '网络攻击模型', 38);
INSERT INTO `tag` VALUES (757, '网络空间安全', 'ddos', 38);
INSERT INTO `tag` VALUES (758, '网络空间安全', '安全威胁分析', 38);
INSERT INTO `tag` VALUES (759, '网络空间安全', '计算机网络', 38);
INSERT INTO `tag` VALUES (761, '服务器', '缓存', 32);
INSERT INTO `tag` VALUES (762, '服务器', 'unix', 32);
INSERT INTO `tag` VALUES (767, '学习和成长', '蓝桥杯', 23);
INSERT INTO `tag` VALUES (768, '学习和成长', 'pat考试', 23);
INSERT INTO `tag` VALUES (769, '学习和成长', '职场和发展', 23);
INSERT INTO `tag` VALUES (770, '学习和成长', '面试', 23);
INSERT INTO `tag` VALUES (771, '学习和成长', '程序人生', 23);
INSERT INTO `tag` VALUES (772, '学习和成长', '业界资讯', 23);
INSERT INTO `tag` VALUES (773, '学习和成长', '学习方法', 23);
INSERT INTO `tag` VALUES (774, '学习和成长', '跳槽', 23);
INSERT INTO `tag` VALUES (775, '学习和成长', '考研', 23);
INSERT INTO `tag` VALUES (776, '学习和成长', '高考', 23);
INSERT INTO `tag` VALUES (777, '学习和成长', '改行学it', 23);
INSERT INTO `tag` VALUES (778, '学习和成长', '创业创新', 23);
INSERT INTO `tag` VALUES (779, '学习和成长', '远程工作', 23);
INSERT INTO `tag` VALUES (780, '学习和成长', '程序员创富', 23);
INSERT INTO `tag` VALUES (782, '教育培训', 'c1认证', 30);
INSERT INTO `tag` VALUES (783, '教育培训', 'c4java', 30);
INSERT INTO `tag` VALUES (784, '教育培训', 'c4python', 30);
INSERT INTO `tag` VALUES (785, '教育培训', 'c4前端', 30);
INSERT INTO `tag` VALUES (786, '教育培训', 'c5底层', 30);
INSERT INTO `tag` VALUES (787, '教育培训', 'c5交付', 30);
INSERT INTO `tag` VALUES (788, '教育培训', 'c5全栈', 30);
INSERT INTO `tag` VALUES (789, '用户体验设计', 'illustrator', 35);
INSERT INTO `tag` VALUES (790, '用户体验设计', '平面', 35);
INSERT INTO `tag` VALUES (791, '用户体验设计', 'photoshop', 35);
INSERT INTO `tag` VALUES (792, '用户体验设计', 'sketch', 35);
INSERT INTO `tag` VALUES (793, '用户体验设计', '3d', 35);
INSERT INTO `tag` VALUES (794, '用户体验设计', '人机交互', 35);
INSERT INTO `tag` VALUES (795, '用户体验设计', '设计语言', 35);
INSERT INTO `tag` VALUES (796, '用户体验设计', '信息可视化', 35);
INSERT INTO `tag` VALUES (797, '用户体验设计', 'figma', 35);
INSERT INTO `tag` VALUES (798, '用户体验设计', 'adobe', 35);
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
INSERT INTO `tag` VALUES (828, '行业数字化', '金融', 39);
INSERT INTO `tag` VALUES (829, '行业数字化', '教育电商', 39);
INSERT INTO `tag` VALUES (830, '行业数字化', '传媒', 39);
INSERT INTO `tag` VALUES (831, '行业数字化', '健康医疗', 39);
INSERT INTO `tag` VALUES (832, '行业数字化', '游戏', 39);
INSERT INTO `tag` VALUES (833, '行业数字化', '娱乐', 39);
INSERT INTO `tag` VALUES (834, '行业数字化', '社交电子', 39);
INSERT INTO `tag` VALUES (835, '行业数字化', '媒体', 39);
INSERT INTO `tag` VALUES (836, '行业数字化', '零售', 39);
INSERT INTO `tag` VALUES (837, '行业数字化', '交通物流', 39);
INSERT INTO `tag` VALUES (838, '行业数字化', '制造', 39);
INSERT INTO `tag` VALUES (839, '行业数字化', '能源', 39);
INSERT INTO `tag` VALUES (840, '行业数字化', '旅游', 39);
INSERT INTO `tag` VALUES (841, '行业数字化', '政务', 39);
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
INSERT INTO `tag` VALUES (857, '前沿技术', '低代码', 13);
INSERT INTO `tag` VALUES (858, '前沿技术', '智能家居', 13);
INSERT INTO `tag` VALUES (859, '前沿技术', '无人机', 13);
INSERT INTO `tag` VALUES (860, '前沿技术', '车载系统', 13);
INSERT INTO `tag` VALUES (861, '前沿技术', '量子计算', 13);
INSERT INTO `tag` VALUES (862, '前沿技术', '智能硬件', 13);
INSERT INTO `tag` VALUES (863, '前沿技术', 'rpa', 13);
INSERT INTO `tag` VALUES (864, '前沿技术', 'wasm', 13);
INSERT INTO `tag` VALUES (865, '前沿技术', '机器人', 13);
INSERT INTO `tag` VALUES (866, '前沿技术', 'c++20', 13);
INSERT INTO `tag` VALUES (867, '前沿技术', 'python3.11', 13);
INSERT INTO `tag` VALUES (868, '前沿技术', 'java18', 13);
INSERT INTO `tag` VALUES (869, '前沿技术', '论文阅读', 13);
INSERT INTO `tag` VALUES (870, '前沿技术', 'c++23', 13);
INSERT INTO `tag` VALUES (871, '前沿技术', 'es13', 13);
INSERT INTO `tag` VALUES (872, '前沿技术', 'c#11.0', 13);
INSERT INTO `tag` VALUES (873, '前沿技术', 'ruby3.1.2', 13);
INSERT INTO `tag` VALUES (874, '前沿技术', 'qt6.3', 13);
INSERT INTO `tag` VALUES (875, '前沿技术', 'lua5.4', 13);
INSERT INTO `tag` VALUES (876, '前沿技术', 'perl5.36.0', 13);
INSERT INTO `tag` VALUES (877, '前沿技术', 'r语言-4.2.1', 13);
INSERT INTO `tag` VALUES (878, '前沿技术', 'scala3.1.2', 13);
INSERT INTO `tag` VALUES (879, '前沿技术', 'swift5.6.3', 13);
INSERT INTO `tag` VALUES (880, '前沿技术', 'go1.19', 13);
INSERT INTO `tag` VALUES (881, '前沿技术', 'webgl', 13);
INSERT INTO `tag` VALUES (882, '前沿技术', 'AIGC', 13);
INSERT INTO `tag` VALUES (883, '前沿技术', 'AI-native', 13);
INSERT INTO `tag` VALUES (884, '前沿技术', 'inscode', 13);
INSERT INTO `tag` VALUES (885, '前沿技术', 'apple vision pro', 13);
INSERT INTO `tag` VALUES (886, '前沿技术', '空间计算', 13);
INSERT INTO `tag` VALUES (900, 'IT工具', 'wps', 6);
INSERT INTO `tag` VALUES (901, 'IT工具', 'foxmail', 6);
INSERT INTO `tag` VALUES (902, 'IT工具', 'notion', 6);
INSERT INTO `tag` VALUES (903, 'IT工具', 'word', 6);
INSERT INTO `tag` VALUES (904, 'IT工具', 'excel', 6);
INSERT INTO `tag` VALUES (905, 'IT工具', 'powerpoint', 6);
INSERT INTO `tag` VALUES (906, 'IT工具', 'outlook', 6);
INSERT INTO `tag` VALUES (907, 'IT工具', 'onenote', 6);
INSERT INTO `tag` VALUES (908, 'IT工具', 'xmind', 6);
INSERT INTO `tag` VALUES (909, 'IT工具', 'teamviewer', 6);
INSERT INTO `tag` VALUES (910, 'IT工具', '亿图图示', 6);
INSERT INTO `tag` VALUES (911, 'IT工具', '企业微信', 6);
INSERT INTO `tag` VALUES (912, 'IT工具', '钉钉', 6);
INSERT INTO `tag` VALUES (913, 'IT工具', '腾讯会议', 6);
INSERT INTO `tag` VALUES (914, 'IT工具', '福昕阅读器', 6);
INSERT INTO `tag` VALUES (915, 'IT工具', 'draw.io', 6);
INSERT INTO `tag` VALUES (916, 'IT工具', '石墨文档', 6);
INSERT INTO `tag` VALUES (917, 'IT工具', 'worktile', 6);
INSERT INTO `tag` VALUES (918, 'IT工具', 'asana', 6);
INSERT INTO `tag` VALUES (919, 'IT工具', 'pingcode', 6);
INSERT INTO `tag` VALUES (920, 'IT工具', 'atlassian', 6);
INSERT INTO `tag` VALUES (921, 'IT工具', 'zoom', 6);
INSERT INTO `tag` VALUES (922, 'IT工具', 'canva可画', 6);
INSERT INTO `tag` VALUES (923, 'IT工具', 'processon', 6);
INSERT INTO `tag` VALUES (924, 'IT工具', '蓝湖', 6);
INSERT INTO `tag` VALUES (925, 'IT工具', '飞书', 6);
INSERT INTO `tag` VALUES (926, 'IT工具', 'winrar', 6);
INSERT INTO `tag` VALUES (927, 'IT工具', '7-zip', 6);
INSERT INTO `tag` VALUES (928, 'IT工具', '火绒安全', 6);
INSERT INTO `tag` VALUES (929, 'IT工具', 'faststone capture', 6);
INSERT INTO `tag` VALUES (930, 'IT工具', '格式工厂', 6);
INSERT INTO `tag` VALUES (931, 'IT工具', 'ffmpeg', 6);
INSERT INTO `tag` VALUES (932, 'IT工具', 'adobe acrobat reader', 6);
INSERT INTO `tag` VALUES (933, 'IT工具', 'dreamweaver', 6);
INSERT INTO `tag` VALUES (934, 'IT工具', 'notepad++', 6);
INSERT INTO `tag` VALUES (935, 'IT工具', 'editplus', 6);
INSERT INTO `tag` VALUES (936, 'IT工具', 'onedrive', 6);
INSERT INTO `tag` VALUES (937, 'IT工具', 'icloud', 6);
INSERT INTO `tag` VALUES (938, 'IT工具', 'everything', 6);
INSERT INTO `tag` VALUES (939, 'IT工具', 'diskgenius', 6);
INSERT INTO `tag` VALUES (940, 'IT工具', '有道云笔记', 6);
INSERT INTO `tag` VALUES (941, 'IT工具', '印象笔记', 6);
INSERT INTO `tag` VALUES (942, 'IT工具', '网易邮箱大师', 6);
INSERT INTO `tag` VALUES (943, 'IT工具', 'idm', 6);
INSERT INTO `tag` VALUES (950, '开发组件', 'pdf', 25);
INSERT INTO `tag` VALUES (951, '开源', '开源软件', 26);
INSERT INTO `tag` VALUES (952, '开源', '开源协议', 26);
INSERT INTO `tag` VALUES (953, '开源', 'gitcode', 26);
INSERT INTO `tag` VALUES (954, '开源', 'gitee', 26);
INSERT INTO `tag` VALUES (955, '开源', '开放原子', 26);
INSERT INTO `tag` VALUES (956, '开源', 'ossinsight', 26);
INSERT INTO `tag` VALUES (961, '其他', '微信', 18);
INSERT INTO `tag` VALUES (962, '其他', '新浪微博', 18);
INSERT INTO `tag` VALUES (963, '其他', 'facebook', 18);
INSERT INTO `tag` VALUES (964, '其他', '微信公众平台', 18);
INSERT INTO `tag` VALUES (965, '其他', '百度', 18);
INSERT INTO `tag` VALUES (966, '其他', 'twitter', 18);
INSERT INTO `tag` VALUES (967, '其他', 'paddle', 18);
INSERT INTO `tag` VALUES (968, '其他', '微信开放平台', 18);
INSERT INTO `tag` VALUES (969, '其他', '其他', 18);
INSERT INTO `tag` VALUES (970, '其他', 'oneapi', 18);
INSERT INTO `tag` VALUES (971, '其他', 'segmentfault', 18);
INSERT INTO `tag` VALUES (972, '其他', '经验分享', 18);
INSERT INTO `tag` VALUES (973, '其他', '课程设计', 18);
INSERT INTO `tag` VALUES (974, '其他', '笔记', 18);
INSERT INTO `tag` VALUES (976, '3C硬件', '电脑', 14);
INSERT INTO `tag` VALUES (977, '3C硬件', '智能手机', 14);
INSERT INTO `tag` VALUES (978, '3C硬件', '智能路由器', 14);
INSERT INTO `tag` VALUES (979, '3C硬件', '计算机外设', 14);
INSERT INTO `tag` VALUES (980, '3C硬件', '数码相机', 14);
INSERT INTO `tag` VALUES (981, '3C硬件', '智能手表', 14);
INSERT INTO `tag` VALUES (982, '3C硬件', '智能电视', 14);
INSERT INTO `tag` VALUES (983, '3C硬件', '游戏机', 14);
INSERT INTO `tag` VALUES (984, '3C硬件', '电视盒子', 14);
INSERT INTO `tag` VALUES (985, '3C硬件', '智能音箱', 14);
INSERT INTO `tag` VALUES (986, 'AIGC', 'gpt', 16);
INSERT INTO `tag` VALUES (987, 'AIGC', 'llama', 16);
INSERT INTO `tag` VALUES (988, 'AIGC', 'midjourney', 16);
INSERT INTO `tag` VALUES (989, 'AIGC', 'whisper', 16);
INSERT INTO `tag` VALUES (990, 'AIGC', 'copilot', 16);
INSERT INTO `tag` VALUES (991, 'AIGC', '华为snap', 16);
INSERT INTO `tag` VALUES (992, 'AIGC', 'AI写作', 16);
INSERT INTO `tag` VALUES (993, 'AIGC', 'AI编程', 16);
INSERT INTO `tag` VALUES (994, 'AIGC', 'mlir', 16);
INSERT INTO `tag` VALUES (995, 'AIGC', 'gpu算力', 16);
INSERT INTO `tag` VALUES (996, 'AIGC', 'langchain', 16);
INSERT INTO `tag` VALUES (997, 'AIGC', 'prompt', 16);
INSERT INTO `tag` VALUES (998, 'AIGC', 'palm', 16);
INSERT INTO `tag` VALUES (999, 'AIGC', 'agi', 16);
INSERT INTO `tag` VALUES (1000, 'AIGC', 'embedding', 16);
INSERT INTO `tag` VALUES (1001, 'AIGC', 'mcp', 16);

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


SET FOREIGN_KEY_CHECKS = 1;
