SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 管理端会员管理菜单
INSERT INTO `sys_menu` (`parent_id`, `name`, `sort`, `path`, `component`, `icon`, `status`, `create_time`, `update_time`, `is_deleted`)
SELECT 2, '会员管理', 8, '/system/vip', '/system/vip', 'Medal', 0, NOW(), NOW(), 0
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `path` = '/system/vip' AND `is_deleted` = 0
);

-- 管理端会员管理权限
INSERT INTO `sys_permission` (`description`, `permission`, `menu_id`, `create_time`, `update_time`, `is_deleted`)
SELECT '会员总览', 'system:vip:dashboard', m.id, NOW(), NOW(), 0
FROM `sys_menu` m
WHERE m.path = '/system/vip'
  AND m.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_permission` WHERE `permission` = 'system:vip:dashboard' AND `is_deleted` = 0
  );

INSERT INTO `sys_permission` (`description`, `permission`, `menu_id`, `create_time`, `update_time`, `is_deleted`)
SELECT '会员列表', 'system:vip:member:list', m.id, NOW(), NOW(), 0
FROM `sys_menu` m
WHERE m.path = '/system/vip'
  AND m.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_permission` WHERE `permission` = 'system:vip:member:list' AND `is_deleted` = 0
  );

INSERT INTO `sys_permission` (`description`, `permission`, `menu_id`, `create_time`, `update_time`, `is_deleted`)
SELECT '调整会员', 'system:vip:member:update', m.id, NOW(), NOW(), 0
FROM `sys_menu` m
WHERE m.path = '/system/vip'
  AND m.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_permission` WHERE `permission` = 'system:vip:member:update' AND `is_deleted` = 0
  );

INSERT INTO `sys_permission` (`description`, `permission`, `menu_id`, `create_time`, `update_time`, `is_deleted`)
SELECT '会员订单', 'system:vip:order:list', m.id, NOW(), NOW(), 0
FROM `sys_menu` m
WHERE m.path = '/system/vip'
  AND m.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_permission` WHERE `permission` = 'system:vip:order:list' AND `is_deleted` = 0
  );

INSERT INTO `sys_permission` (`description`, `permission`, `menu_id`, `create_time`, `update_time`, `is_deleted`)
SELECT '套餐列表', 'system:vip:plan:list', m.id, NOW(), NOW(), 0
FROM `sys_menu` m
WHERE m.path = '/system/vip'
  AND m.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_permission` WHERE `permission` = 'system:vip:plan:list' AND `is_deleted` = 0
  );

INSERT INTO `sys_permission` (`description`, `permission`, `menu_id`, `create_time`, `update_time`, `is_deleted`)
SELECT '修改套餐', 'system:vip:plan:update', m.id, NOW(), NOW(), 0
FROM `sys_menu` m
WHERE m.path = '/system/vip'
  AND m.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_permission` WHERE `permission` = 'system:vip:plan:update' AND `is_deleted` = 0
  );

-- 管理端角色挂载会员菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `sys_role` r
JOIN `sys_menu` m ON m.path = '/system/vip' AND m.is_deleted = 0
WHERE r.role IN ('admin', 'viewer')
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_menu` rm WHERE rm.role_id = r.id AND rm.menu_id = m.id
  );

-- admin 拥有会员管理全部权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.permission = 'system:vip:dashboard' AND p.is_deleted = 0
WHERE r.role = 'admin'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.permission = 'system:vip:member:list' AND p.is_deleted = 0
WHERE r.role = 'admin'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.permission = 'system:vip:member:update' AND p.is_deleted = 0
WHERE r.role = 'admin'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.permission = 'system:vip:order:list' AND p.is_deleted = 0
WHERE r.role = 'admin'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.permission = 'system:vip:plan:list' AND p.is_deleted = 0
WHERE r.role = 'admin'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.permission = 'system:vip:plan:update' AND p.is_deleted = 0
WHERE r.role = 'admin'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- viewer 只开放会员管理读权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.permission = 'system:vip:dashboard' AND p.is_deleted = 0
WHERE r.role = 'viewer'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.permission = 'system:vip:member:list' AND p.is_deleted = 0
WHERE r.role = 'viewer'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.permission = 'system:vip:order:list' AND p.is_deleted = 0
WHERE r.role = 'viewer'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.permission = 'system:vip:plan:list' AND p.is_deleted = 0
WHERE r.role = 'viewer'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

SET FOREIGN_KEY_CHECKS = 1;
