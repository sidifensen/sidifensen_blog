import request from "@/utils/Request";

// 登录
export function login(data) {
  return request({
    url: "/user/login",
    method: "post",
    data: data,
  });
}

export function oauthLogin(data) {
  return request({
    url: "/user/oauthLogin",
    headers: {
      "Login-Type": data.type,
    },
    method: "post",
    data: data,
  });
}

// 发送验证码
export function checkCode() {
  return request({
    url: "/user/checkCode",
    method: "get",
  });
}

// 注册
export function register(data) {
  return request({
    url: "/user/register",
    method: "post",
    data: data,
  });
}

// 发送邮箱验证码
export function sendEmail(data) {
  return request({
    url: "/user/sendEmail",
    method: "post",
    data: data,
  });
}

// 重置密码时校验邮箱验证码
export function verifyReset(data) {
  return request({
    url: "/user/verifyReset",
    method: "post",
    data: data,
  });
}

// 重置密码时校验邮箱验证码
export function resetPassword(data) {
  return request({
    url: "/user/resetPassword",
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

// 根据用户ID获取用户详情信息
export function getUserInfo(userId) {
  return request({
    url: `/user/admin/${userId}`,
    method: "get",
  });
}