import request from "@/utils/Request";

// 新增文章
export function addArticle(data) {
  return request({
    url: "/article/add",
    method: "post",
    data,
  });
}

// 保存草稿
export function saveDraft(data) {
  return request({
    url: "/article/saveDraft",
    method: "post",
    data,
  });
}

// 获取文章详情
export function getArticleDetail(articleId) {
  return request({
    url: `/article/get/${articleId}`,
    method: "get",
  });
}

// 获取用户文章列表
export function getUserArticleList(pageNum, pageSize, articleStatusDto) {
  return request({
    url: `/article/user/list?pageNum=${pageNum}&pageSize=${pageSize}`,
    method: "post",
    data: articleStatusDto,
  });
}

// 更新文章状态
export function updateArticle(data) {
  return request({
    url: "/article/update",
    method: "put",
    data,
  });
}

// 删除文章
export function deleteArticle(articleId) {
  return request({
    url: `/article/delete/${articleId}`,
    method: "delete",
  });
}

// 获取用户文章状态统计
export function getUserArticleStatistics() {
  return request({
    url: "/article/user/statistics",
    method: "get",
  });
}

// 获取指定用户的文章统计
export function getUserArticleStatisticsById(userId) {
  return request({
    url: `/article/user/${userId}/statistics`,
    method: "get",
  });
}

// 获取所有文章列表
export function getAllArticleList(pageNum, pageSize) {
  return request({
    url: `/article/listAll?pageNum=${pageNum}&pageSize=${pageSize}`,
    method: "get",
  });
}

// 获取文章列表（通用）
export function getArticleList(pageNum, pageSize) {
  return request({
    url: `/article/list?pageNum=${pageNum}&pageSize=${pageSize}`,
    method: "get",
  });
}

// 增加文章阅读量
export function increaseReadCount(articleId) {
  return request({
    url: `/article/incrReadCount/${articleId}`,
    method: "post",
  });
}
