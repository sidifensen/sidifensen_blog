/**
 * 用户相关 API
 */
import { get, post } from './index'

/**
 * 获取登录验证码
 */
export function getCheckCode() {
  return get('/user/checkCode')
}

/**
 * 获取用户信息
 * @param {number} userId 用户ID
 */
export function getUserInfo(userId) {
  return get(`/user/${userId}`)
}

/**
 * 获取我的信息
 */
export function getMyInfo() {
  return get('/user/info')
}

/**
 * 更新个人资料
 * @param {Object} data 更新数据
 */
export function updateProfile(data) {
  return post('/user/profile', data)
}

/**
 * 获取用户文章列表
 * @param {number} userId 用户ID
 * @param {Object} params 查询参数
 */
export function getUserArticles(userId, params) {
  return get(`/user/${userId}/articles`, params)
}

/**
 * 获取用户相册列表
 * @param {number} userId 用户ID
 * @param {Object} params 查询参数
 */
export function getUserAlbums(userId, params) {
  return get(`/album/list/${userId}`, params)
}

/**
 * 获取用户收藏列表
 * @param {number} userId 用户ID
 * @param {Object} params 查询参数
 */
export function getUserFavorites(userId, params) {
  return get(`/user/${userId}/favorites`, params)
}

/**
 * 获取我的文章列表
 * @param {Object} params 查询参数
 */
export function getMyArticles(params) {
  const { pageNum, pageSize, ...body } = params
  let queryStr = ''
  if (pageNum) queryStr += (queryStr ? '&' : '?') + 'pageNum=' + pageNum
  if (pageSize) queryStr += (queryStr ? '&' : '?') + 'pageSize=' + pageSize
  return post(`/article/user/list${queryStr}`, { editStatus: 0, ...body })
}

/**
 * 获取关注列表
 * @param {number} userId 用户ID
 * @param {Object} params 查询参数
 */
export function getFollowings(userId, params) {
  return get(`/user/${userId}/followings`, params)
}

/**
 * 获取粉丝列表
 * @param {number} userId 用户ID
 * @param {Object} params 查询参数
 */
export function getFollowers(userId, params) {
  return get(`/user/${userId}/followers`, params)
}

/**
 * 关注用户
 * @param {number} userId 用户ID
 */
export function followUser(userId) {
  return post('/user/follow', { userId })
}

/**
 * 取消关注
 * @param {number} userId 用户ID
 */
export function unfollowUser(userId) {
  return post('/user/unfollow', { userId })
}
