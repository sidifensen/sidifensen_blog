/**
 * 评论相关 API
 */
import { get, post } from './index'

/**
 * 获取文章评论列表
 * @param {number} articleId 文章ID
 * @param {Object} params 查询参数
 */
export function getArticleComments(articleId, params) {
  return get('/comment/list', { articleId, ...params })
}

/**
 * 获取我的评论
 * @param {Object} params 查询参数
 */
export function getMyComments(params) {
  return get('/comment/my', params)
}

/**
 * 发布评论
 * @param {Object} data 评论数据
 */
export function addComment(data) {
  return post('/comment/add', data)
}

/**
 * 删除评论
 * @param {number} commentId 评论ID
 */
export function deleteComment(commentId) {
  return post('/comment/delete', { commentId })
}

/**
 * 回复评论
 * @param {Object} data 回复数据
 */
export function replyComment(data) {
  return post('/comment/reply', data)
}
