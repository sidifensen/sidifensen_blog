import request from "@/utils/Request";

// 将菜单分配给角色
export function addRoleMenu(data) {
  return request({
    url: "/role-menu/add",
    method: "post",
    data
  });
}

// 获取拥有当前菜单的角色列表
export function getRolesByMenu(menuId) {
  return request({
    url: `/role-menu/${menuId}`,
    method: "get"
  });
}