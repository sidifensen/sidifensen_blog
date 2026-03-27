/**
 * 消息相关 API
 */
import { get, post } from './index'

/**
 * 获取消息列表
 * @param {Object} params 查询参数
 */
export function getMessageList(params) {
  return get('/message/notifications', params)
}

/**
 * 获取未读消息数量
 */
export function getUnreadCount() {
  return get('/message/notifications/unread/count')
}

/**
 * 标记消息已读
 * @param {number} messageId 消息ID
 */
export function markAsRead(messageId) {
  return put('/message/notifications/read', [messageId])
}

/**
 * 标记所有消息已读
 */
export function markAllAsRead() {
  return put('/message/notifications/read', [])
}

/**
 * 删除消息
 * @param {number} messageId 消息ID
 */
export function deleteMessage(messageId) {
  return post('/message/delete', { messageId })
}

/**
 * 获取系统通知
 * @param {Object} params 查询参数
 */
export function getSystemNotifications(params) {
  return get('/message/system', params)
}

/**
 * 获取评论通知
 * @param {Object} params 查询参数
 */
export function getCommentNotifications(params) {
  return get('/message/comment', params)
}

/**
 * 获取点赞通知
 * @param {Object} params 查询参数
 */
export function getLikeNotifications(params) {
  return get('/message/like', params)
}

/**
 * 获取关注通知
 * @param {Object} params 查询参数
 */
export function getFollowNotifications(params) {
  return get('/message/follow', params)
}
