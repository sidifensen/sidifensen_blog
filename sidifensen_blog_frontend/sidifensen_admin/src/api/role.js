import request from '@/utils/Request'

// 获取角色
export function getRoleList() {
  return request({
    url: '/role/list',
    method: 'get',
  })
}

// 分页获取角色
export function getRolePage(params) {
  return request({
    url: '/role/page',
    method: 'get',
    params,
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/role/add',
    method: 'post',
    data,
  })
}
// 更新角色
export function updateRole(data) {
  return request({
    url: '/role/update',
    method: 'put',
    data,
  })
}

// 删除角色
export function deleteRole(roleId) {
  return request({
    url: `/role/${roleId}`,
    method: 'delete',
  })
}

// 查询角色
export function queryRole(name) {
  return request({
    url: '/role/search',
    method: 'get',
    params: {
      name,
    },
  })
}

// 分页查询角色
export function queryRolePage(params) {
  return request({
    url: '/role/page/search',
    method: 'get',
    params,
  })
}
