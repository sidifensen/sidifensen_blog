import request from "@/utils/Request";

// 获取权限列表
export function getPermissionList() {
  return request({
    url: "/permission/list",
    method: "get",
  });
}

// 新增权限
export function addPermission(data) {
  return request({
    url: "/permission/add",
    method: "post",
    data,
  });
}

// 更新权限
export function updatePermission(data) {
  return request({
    url: "/permission/update",
    method: "put",
    data,
  });
}

// 删除权限
export function deletePermission(permissionId) {
  return request({
    url: `/permission/${permissionId}`,
    method: "delete",
  });
}

// 查询权限
export function queryPermission(data) {
  return request({
    url: "/permission/search",
    method: "post",
    data,
  });
}
