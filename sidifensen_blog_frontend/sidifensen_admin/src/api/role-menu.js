import request from '@/utils/Request'

// 将菜单分配给角色
export function addRoleMenu(data) {
  return request({
    url: '/role-menu/add',
    method: 'post',
    data,
  })
}

// 获取拥有当前菜单的角色列表
export function getRolesByMenu(menuId) {
  return request({
    url: `/role-menu/${menuId}`,
    method: 'get',
  })
}

// 根据角色ID获取菜单ID列表
export function getMenusByRole(roleId) {
  return request({
    url: `/role-menu/role/${roleId}`,
    method: 'get',
  })
}

// 给角色分配菜单权限
export function assignMenus(data) {
  return request({
    url: '/role-menu/assign',
    method: 'post',
    data,
  })
}
