import request from "@/utils/Request";

// 将权限授权给角色
export function addRolePermission(data) {
  return request({
    url: "/role-permission/add",
    method: "post",
    data
  });
}

// 获取拥有当前菜单的角色列表
export function getRolesByPermission(permissionId) {
  return request({
    url: `/role-permission/${permissionId}`,
    method: "get"
  });
}

// 将权限批量授权给角色
export function addBatchRolePermission(data) {
  return request({
    url: "/role-permission/addBatch",
    method: "post",
    data
  });
}