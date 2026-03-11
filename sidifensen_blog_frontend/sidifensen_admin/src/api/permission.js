import request from "@/utils/Request";

// 获取权限列表
export function getPermissionList() {
  return request({
    url: "/permission/list",
    method: "get",
  });
}

// 分页获取权限列表
export function getPermissionPage(params) {
  return request({
    url: "/permission/page",
    method: "get",
    params,
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

// 分页查询权限
export function queryPermissionPage(data) {
  return request({
    url: "/permission/page/search",
    method: "post",
    data,
  });
}
