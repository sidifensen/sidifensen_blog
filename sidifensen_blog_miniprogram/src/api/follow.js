/**
 * 关注相关 API
 */
import { get, post } from './index'

/**
 * 关注用户
 * @param {number} userId 用户ID
 */
export function follow(userId) {
  return post('/follow/add', { userId })
}

/**
 * 取消关注
 * @param {number} userId 用户ID
 */
export function unfollow(userId) {
  return post('/follow/remove', { userId })
}

/**
 * 获取我的关注列表
 * @param {number} userId 用户ID
 * @param {Object} params 查询参数
 */
export function getMyFollowings(userId, params) {
  return get(`/follow/followList/${userId}`, params)
}

/**
 * 获取我的粉丝列表
 * @param {number} userId 用户ID
 * @param {Object} params 查询参数
 */
export function getMyFollowers(userId, params) {
  return get(`/follow/fansList/${userId}`, params)
}

/**
 * 检查是否关注
 * @param {number} userId 用户ID
 */
export function checkFollow(userId) {
  return get('/follow/check', { userId })
}
