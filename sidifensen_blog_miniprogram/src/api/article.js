/**
 * 文章相关 API
 */
import { get, post, del } from './index'

/**
 * 获取文章列表
 * @param {Object} params 查询参数
 */
export function getArticleList(params) {
  return get('/article/listAll', params)
}

/**
 * 获取文章详情
 * @param {number} id 文章ID
 */
export function getArticleDetail(id) {
  return get(`/article/get/${id}`)
}

/**
 * 获取推荐文章
 * @param {number} limit 数量
 */
export function getRecommendArticles(limit = 5) {
  return get('/article/recommend', { limit })
}

/**
 * 搜索文章
 * @param {Object} params 搜索参数
 */
export function searchArticles(params) {
  return get('/article/search', params)
}

/**
 * 点赞或取消点赞文章
 * @param {number} articleId 文章ID
 */
export function likeArticle(articleId) {
  return post('/like/toggle', { type: 0, typeId: articleId })
}

/**
 * 取消点赞
 * @param {number} articleId 文章ID
 */
export function unlikeArticle(articleId) {
  return post('/like/toggle', { type: 0, typeId: articleId })
}

/**
 * 收藏文章
 * @param {number} articleId 文章ID
 */
export function favoriteArticle(articleId) {
  return post('/article/favorite', { articleId })
}

/**
 * 取消收藏
 * @param {number} articleId 文章ID
 */
export function unfavoriteArticle(articleId) {
  return del('/article/unfavorite', { articleId })
}

/**
 * 获取我的收藏
 * @param {Object} params 查询参数
 */
export function getMyFavorites(params) {
  return get('/article/favorites', params)
}

/**
 * 获取用户文章统计
 */
export function getUserArticleStatistics() {
  return get('/article/user/statistics')
}
