import request from "@/utils/Request";

// 给角色分配用户
export function addUser(data) {
  return request({
    url: "/user-role/addUser",
    method: "post",
    data
  });
}

//获取拥有当前角色的用户列表
export function getUsersByRole(roleId) {
  return request({
    url: `/user-role/getUsers/${roleId}`,
    method: "get"
  });
}