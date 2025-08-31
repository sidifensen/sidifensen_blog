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
    method: "get"
  });
}
