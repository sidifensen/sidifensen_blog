import request from "@/utils/Request";

// 给角色分配用户
export function addUser(data) {
  return request({
    url: "/user-role/addUser",
    method: "post",
    data
  });
}

// 获取拥有当前角色的用户列表
export function getUsersByRole(roleId) {
  return request({
    url: `/user-role/getUsers/${roleId}`,
    method: "get"
  });
}

// 给用户添加角色
export function addRole(data){
  return request({
    url: "/user-role/addRole",
    method: "post",
    data
  })
}

// 获取当前用户拥有的角色列表
export function getRolesByUser(userId) {
  return request({
    url: `/user-role/getRoles/${userId}`,
    method: "get"
  });
}