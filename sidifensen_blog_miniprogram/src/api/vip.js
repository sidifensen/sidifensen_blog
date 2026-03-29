/**
 * VIP 相关 API
 */
import { get, post } from './index'

/**
 * 获取 VIP 套餐列表
 */
export function getVipPlans() {
  return get('/vip/plans')
}

/**
 * 获取当前用户 VIP 状态
 */
export function getVipStatus() {
  return get('/vip/me')
}

/**
 * 创建 VIP 订单
 * @param {Object} data 订单数据
 */
export function createVipOrder(data) {
  return post('/vip/orders', data)
}

/**
 * 查询当前用户订单
 * @param {string} orderNo 订单号
 */
export function getVipOrder(orderNo) {
  return get(`/vip/orders/${orderNo}`)
}

/**
 * 分页查询当前用户订单
 */
export function listVipOrders(pageNum = 1, pageSize = 10) {
  return get('/vip/orders', { pageNum, pageSize })
}

/**
 * 取消 VIP 订单
 * @param {string} orderNo 订单号
 */
export function cancelVipOrder(orderNo) {
  return post(`/vip/orders/${orderNo}/cancel`)
}

/**
 * 重新支付订单
 * @param {string} orderNo 订单号
 */
export function repayVipOrder(orderNo) {
  return post(`/vip/orders/${orderNo}/pay`)
}
