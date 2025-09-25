import request from "@/utils/Request";

/**
 * 获取当前用户的收藏夹列表
 * @returns {Promise} 返回收藏夹列表
 */
export const getFavoriteList = () => {
  return request({
    url: "/favorite/list",
    method: "get",
  });
};

/**
 * 根据文章ID获取当前用户的收藏夹列表，并标识该文章在各收藏夹中的收藏状态
 * @param {number} articleId 文章ID
 * @returns {Promise} 返回收藏夹列表（包含收藏状态）
 */
export const getFavoriteListByArticle = (articleId) => {
  return request({
    url: "/favorite/listByArticle",
    method: "get",
    params: {
      articleId,
    },
  });
};

/**
 * 新增收藏夹
 * @param {Object} data 收藏夹信息
 * @param {string} data.name 收藏夹名称
 * @param {number} data.showStatus 展示状态 0-公开 1-私密
 * @returns {Promise} 返回操作结果
 */
export const addFavorite = (data) => {
  return request({
    url: "/favorite/add",
    method: "post",
    data,
  });
};

/**
 * 将文章添加到收藏夹
 * @param {number} articleId 文章ID
 * @param {number} favoriteId 收藏夹ID
 * @returns {Promise} 返回操作结果
 */
export const addArticleToFavorite = (articleId, favoriteId) => {
  return request({
    url: "/favorite/addArticle",
    method: "post",
    params: {
      articleId,
      favoriteId,
    },
  });
};

/**
 * 从收藏夹中移除文章
 * @param {number} articleId 文章ID
 * @param {number} favoriteId 收藏夹ID
 * @returns {Promise} 返回操作结果
 */
export const removeArticleFromFavorite = (articleId, favoriteId) => {
  return request({
    url: "/favorite/removeArticle",
    method: "delete",
    params: {
      articleId,
      favoriteId,
    },
  });
};

/**
 * 根据用户ID获取收藏夹列表
 * @param {number} userId 用户ID，可选参数
 * @returns {Promise} 返回收藏夹列表
 */
export const getFavoriteListByUserId = (userId) => {
  return request({
    url: "/favorite/listByUser",
    method: "get",
    params: {
      userId,
    },
  });
};

/**
 * 根据收藏夹ID获取收藏夹中的文章列表
 * @param {number} favoriteId 收藏夹ID
 * @returns {Promise} 返回文章列表
 */
export const getArticleListByFavoriteId = (favoriteId) => {
  return request({
    url: "/favorite/articles",
    method: "get",
    params: {
      favoriteId,
    },
  });
};
