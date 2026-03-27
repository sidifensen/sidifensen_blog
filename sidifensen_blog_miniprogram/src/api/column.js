/**
 * 专栏相关 API
 */
import { get } from './index'

/**
 * 获取专栏列表
 * @param {Object} params 查询参数
 */
export function getColumnList(params) {
  return get('/column/list', params)
}

/**
 * 获取专栏详情
 * @param {number} id 专栏ID
 */
export function getColumnDetail(id) {
  return get(`/column/${id}`)
}

/**
 * 获取专栏文章列表
 * @param {number} columnId 专栏ID
 * @param {Object} params 查询参数
 */
export function getColumnArticles(columnId, params) {
  return get(`/column/${columnId}/articles`, params)
}
