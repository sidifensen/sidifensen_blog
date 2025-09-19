import request from "@/utils/Request";

// 管理员获取所有评论列表
export function adminGetCommentList() {
  return request({
    url: "/comment/admin/list",
    method: "get",
  });
}

// 管理员根据用户ID获取评论列表
export function adminGetCommentsByUserId(userId) {
  return request({
    url: `/comment/admin/user/${userId}`,
    method: "get",
  });
}

// 管理员搜索评论
export function adminSearchComment(data) {
  return request({
    url: "/comment/admin/search",
    method: "post",
    data: data,
  });
}

// 管理员审核评论
export function adminExamineComment(data) {
  return request({
    url: "/comment/admin/examine",
    method: "put",
    data: data,
  });
}

// 管理员批量审核评论
export function adminExamineBatchComment(data) {
  return request({
    url: "/comment/admin/examine/batch",
    method: "put",
    data: data,
  });
}

// 管理员删除评论
export function adminDeleteComment(commentId) {
  return request({
    url: `/comment/admin/${commentId}`,
    method: "delete",
  });
}

// 管理员批量删除评论
export function adminDeleteBatchComment(commentIds) {
  return request({
    url: "/comment/admin/delete/batch",
    method: "delete",
    data: commentIds,
  });
}

// 获取用户列表（包含评论数量）- 需要后端添加此接口
export function getUserListWithCommentCount() {
  return request({
    url: "/user/admin/listWithCommentCount",
    method: "get",
  });
}
