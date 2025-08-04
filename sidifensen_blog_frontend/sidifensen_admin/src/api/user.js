import request from "@/utils/Request";

// 登录
export function login(data) {
  return request({
    url: "/user/adminLogin",
    method: "post",
    data: data,
  });
}

// 获取用户信息
export function info() {
  return request({
    url: "/user/info",
    method: "get",
  });
}
