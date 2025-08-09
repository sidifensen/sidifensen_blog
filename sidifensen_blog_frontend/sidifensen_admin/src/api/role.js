import request from "@/utils/Request";

// 获取角色
export function getRoles() {
  return request({
    url: "/role/list",
    method: "get",
  });
}

// 新增角色
export function addRole(data) {
  return request({
    url: "/role/add",
    method: "post",
    data,
  });
}
// 更新角色
export function updateRole(data) {
  return request({
    url: "/role/update",
    method: "put",
    data,
  });
}

// 删除角色
export function deleteRole(roleId) {
  return request({
    url: `/role/${roleId}`,
    method: "delete",
  });
}

// 查询角色
export function queryRole(name) {
  return request({
    url: "/role/search",
    method: "get",
    params: {
      name,
    },
  });
}
