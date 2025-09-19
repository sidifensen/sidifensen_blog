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

// 获取用户列表（包含文章数量）
export function getUserListWithArticleCount() {
  return request({
    url: "/user/admin/listWithArticleCount",
    method: "get",
  });
}

// 获取用户列表（包含评论数量）
export function getUserListWithCommentCount() {
  return request({
    url: "/user/admin/listWithCommentCount",
    method: "get",
  });
}

// 修改用户
export function updateUser(data) {
  return request({
    url: "/user/admin/update",
    method: "post",
    data: data,
  });
}

// 删除用户
export function deleteUser(userId) {
  return request({
    url: `/user/admin/${userId}`,
    method: "delete",
  });
}

// 搜索用户
export function queryUser(data) {
  return request({
    url: "/user/admin/search",
    method: "post",
    data: data,
  });
}

// 获取用户详情
export function getUserDetail(userId) {
  return request({
    url: `/user/admin/${userId}`,
    method: "get",
  });
}

// 获取用户总数统计
export function getUserTotalCount() {
  return request({
    url: "/user/admin/count",
    method: "get",
  });
}
