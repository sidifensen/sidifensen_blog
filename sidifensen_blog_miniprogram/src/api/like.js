/**
 * 点赞相关 API
 */
import { get, post } from './index'

/**
 * 点赞文章
 * @param {number} articleId 文章ID
 */
export function likeArticle(articleId) {
  return post('/like/article', { articleId })
}

/**
 * 取消点赞文章
 * @param {number} articleId 文章ID
 */
export function unlikeArticle(articleId) {
  return post('/like/article/cancel', { articleId })
}

/**
 * 点赞评论
 * @param {number} commentId 评论ID
 */
export function likeComment(commentId) {
  return post('/like/comment', { commentId })
}

/**
 * 取消点赞评论
 * @param {number} commentId 评论ID
 */
export function unlikeComment(commentId) {
  return post('/like/comment/cancel', { commentId })
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
 * @param {number} articleId 文章ID
 */
export function checkLike(articleId) {
  return get('/like/check', { articleId })
}
