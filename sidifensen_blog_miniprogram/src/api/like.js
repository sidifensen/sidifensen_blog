/**
 * 点赞相关 API
 */
import { get, post } from './index'

/**
 * 点赞或取消点赞（通用）
 * @param {number} type 点赞类型 0-文章 1-评论
 * @param {number} typeId 类型ID
 */
export function toggleLike(type, typeId) {
  return post('/like/toggle', { type, typeId })
}

/**
 * 点赞文章
 * @param {number} articleId 文章ID
 */
export function likeArticle(articleId) {
  return toggleLike(0, articleId)
}

/**
 * 取消点赞文章
 * @param {number} articleId 文章ID
 */
export function unlikeArticle(articleId) {
  return toggleLike(0, articleId)
}

/**
 * 点赞评论
 * @param {number} commentId 评论ID
 */
export function likeComment(commentId) {
  return toggleLike(1, commentId)
}

/**
 * 取消点赞评论
 * @param {number} commentId 评论ID
 */
export function unlikeComment(commentId) {
  return toggleLike(1, commentId)
}

/**
 * 获取我的点赞列表
 * @param {Object} params 查询参数
 */
export function getMyLikes(params) {
  return get('/like/my', params)
}

/**
 * 检查是否点赞
 * @param {number} type 点赞类型 0-文章 1-评论
 * @param {number} typeId 类型ID
 */
export function checkLike(type, typeId) {
  return get('/like/isLiked', { type, typeId })
}
