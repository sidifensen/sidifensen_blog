-- 专栏权限管理SQL脚本
-- 作者: sidifensen
-- 日期: 2025-09-21
-- 说明: 为ColumnController中的管理员权限创建对应的权限记录并分配给管理员角色

-- ===========================
-- 1. 创建专栏管理菜单
-- ===========================

-- 插入专栏管理主菜单 (ID: 16)
INSERT INTO `sys_menu` VALUES (16, 0, '专栏管理', 5, '/column', '/column', 'Collection', 0, NOW(), NOW(), 0);

-- 插入用户专栏子菜单 (ID: 17)
INSERT INTO `sys_menu` VALUES (17, 16, '用户专栏', 0, '/column/user', '/column/user', 'Folder', 0, NOW(), NOW(), 0);

-- 插入专栏审核子菜单 (ID: 18)
INSERT INTO `sys_menu` VALUES (18, 16, '专栏审核', 1, '/column/examine', '/column/examine', 'FolderChecked', 0, NOW(), NOW(), 0);

-- ===========================
-- 2. 创建专栏相关权限
-- ===========================

-- 管理员获取专栏列表权限 (ID: 59)
INSERT INTO `sys_permission` VALUES (59, '获取专栏列表', 'column:list', 17, NOW(), NOW(), 0);

-- 管理员根据用户ID获取专栏列表权限 (ID: 60)
INSERT INTO `sys_permission` VALUES (60, '获取用户专栏列表', 'column:user:list', 17, NOW(), NOW(), 0);

-- 管理员搜索专栏权限 (ID: 61)
INSERT INTO `sys_permission` VALUES (61, '搜索专栏', 'column:search', 17, NOW(), NOW(), 0);

-- 管理员审核专栏权限 (ID: 62)
INSERT INTO `sys_permission` VALUES (62, '审核专栏', 'column:examine', 18, NOW(), NOW(), 0);

-- 管理员删除专栏权限 (ID: 63)
INSERT INTO `sys_permission` VALUES (63, '删除专栏', 'column:delete', 17, NOW(), NOW(), 0);

-- ===========================
-- 3. 将菜单分配给管理员角色
-- ===========================

-- 为管理员角色(role_id=1)分配专栏管理主菜单
INSERT INTO `sys_role_menu` VALUES (NULL, 1, 16);

-- 为管理员角色(role_id=1)分配用户专栏子菜单
INSERT INTO `sys_role_menu` VALUES (NULL, 1, 17);

-- 为管理员角色(role_id=1)分配专栏审核子菜单  
INSERT INTO `sys_role_menu` VALUES (NULL, 1, 18);

-- ===========================
-- 4. 将权限分配给管理员角色
-- ===========================

-- 分配"获取专栏列表"权限给管理员角色(role_id=1)
INSERT INTO `sys_role_permission` VALUES (NULL, 1, 59);

-- 分配"获取用户专栏列表"权限给管理员角色(role_id=1)
INSERT INTO `sys_role_permission` VALUES (NULL, 1, 60);

-- 分配"搜索专栏"权限给管理员角色(role_id=1)
INSERT INTO `sys_role_permission` VALUES (NULL, 1, 61);

-- 分配"审核专栏"权限给管理员角色(role_id=1)
INSERT INTO `sys_role_permission` VALUES (NULL, 1, 62);

-- 分配"删除专栏"权限给管理员角色(role_id=1)
INSERT INTO `sys_role_permission` VALUES (NULL, 1, 63);

-- ===========================
-- 5. 验证SQL
-- ===========================

-- 查询验证菜单是否创建成功
-- SELECT * FROM sys_menu WHERE name LIKE '%专栏%' ORDER BY id;

-- 查询验证权限是否创建成功
-- SELECT * FROM sys_permission WHERE permission LIKE 'column:%' ORDER BY id;

-- 查询验证管理员角色是否获得菜单权限
-- SELECT sm.name AS menu_name, sr.name AS role_name 
-- FROM sys_role_menu srm 
-- JOIN sys_menu sm ON srm.menu_id = sm.id 
-- JOIN sys_role sr ON srm.role_id = sr.id 
-- WHERE sr.role = 'admin' AND sm.name LIKE '%专栏%';

-- 查询验证管理员角色是否获得操作权限
-- SELECT sp.description AS permission_name, sp.permission, sr.name AS role_name 
-- FROM sys_role_permission srp 
-- JOIN sys_permission sp ON srp.permission_id = sp.id 
-- JOIN sys_role sr ON srp.role_id = sr.id 
-- WHERE sr.role = 'admin' AND sp.permission LIKE 'column:%';

COMMIT;
