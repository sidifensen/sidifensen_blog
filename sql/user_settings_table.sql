-- ============================================
-- 用户个人设置表 - DDL
-- 说明：每个用户一条记录，每个设置项作为独立字段
--      新增设置类型时直接添加新字段即可
-- 创建时间：2026-03-18
-- ============================================

-- 创建用户个人设置表
DROP TABLE IF EXISTS `user_settings`;
CREATE TABLE `user_settings` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` INT NOT NULL COMMENT '用户 ID',
    `receive_private_message_email` TINYINT NOT NULL DEFAULT 0 COMMENT '是否接收私信邮件通知：0-关闭，1-开启',
    `receive_comment_email` TINYINT NOT NULL DEFAULT 1 COMMENT '是否接收评论邮件通知：0-关闭，1-开启',
    `receive_system_email` TINYINT NOT NULL DEFAULT 1 COMMENT '是否接收系统邮件通知：0-关闭，1-开启',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户个人设置表';

-- ============================================
-- 初始化数据 - 为所有现有用户添加默认设置
-- ============================================

INSERT INTO `user_settings` (`user_id`, `receive_private_message_email`, `receive_comment_email`, `receive_system_email`)
SELECT
    `id` AS user_id,
    0 AS receive_private_message_email,
    1 AS receive_comment_email,
    1 AS receive_system_email
FROM `sys_user`
WHERE `is_deleted` = 0;
