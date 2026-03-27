/**
 * 收藏相关 API
 */
import { get, post } from './index'

/**
 * 获取我的收藏列表
 * @param {Object} params 查询参数
 */
export function getMyFavorites(params) {
  return get('/favorite/list', params)
}

/**
 * 收藏文章
 * @param {number} articleId 文章ID
 */
export function addFavorite(articleId) {
  return post('/favorite/add', { articleId })
}

/**
 * 取消收藏
 * @param {number} articleId 文章ID
 */
export function removeFavorite(articleId) {
  return post('/favorite/remove', { articleId })
}

/**
 * 检查是否收藏
 * @param {number} articleId 文章ID
 */
export function checkFavorite(articleId) {
  return get('/favorite/check', { articleId })
}
