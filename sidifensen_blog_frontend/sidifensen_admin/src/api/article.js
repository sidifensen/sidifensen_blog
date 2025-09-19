import request from "@/utils/Request";

// 管理端获取文章列表
export function adminGetArticleList() {
  return request({
    url: `/article/admin/list`,
    method: "get",
  });
}

// 管理端获取文章详情
export function adminGetArticle(articleId) {
  return request({
    url: `/article/admin/${articleId}`,
    method: "get",
  });
}

// 管理端更新文章
export function adminUpdateArticle(data) {
  return request({
    url: `/article/admin/update`,
    method: "put",
    data,
  });
}

// 管理端删除文章
export function adminDeleteArticle(articleId) {
  return request({
    url: `/article/admin/${articleId}`,
    method: "delete",
  });
}

// 管理端批量删除文章
export function adminDeleteBatchArticle(data) {
  return request({
    url: `/article/admin/delete/batch`,
    method: "delete",
    data,
  });
}

// 管理端审核文章
export function adminExamineArticle(data) {
  return request({
    url: `/article/admin/examine`,
    method: "put",
    data,
  });
}

// 管理端批量审核文章
export function adminExamineBatchArticle(data) {
  return request({
    url: `/article/admin/examine/batch`,
    method: "put",
    data,
  });
}

// 管理端搜索文章
export function adminSearchArticle(data) {
  return request({
    url: `/article/admin/search`,
    method: "post",
    data,
  });
}

// 管理端根据用户ID获取文章列表
export function adminGetArticlesByUserId(userId) {
  return request({
    url: `/article/admin/user/${userId}`,
    method: "get",
  });
}

// 获取文章统计数据
export function getArticleStatistics() {
  return request({
    url: "/article/admin/statistics",
    method: "get",
  });
}
