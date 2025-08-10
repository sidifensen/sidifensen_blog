import request from "@/utils/Request";

// 登录
export function login(data) {
  return request({
    url: "/user/admin/login",
    method: "post",
    data: data,
  });
}

// 获取用户信息
export function info() {
  return request({
    url: "/user/admin/info",
    method: "get",
  });
}

// 获取用户列表
export function getUserList() {
  return request({
    url: "/user/admin/list",
    method: "get",
  });
}
