/**
 * VIP 相关 API
 */
import { get, post } from './index'

/**
 * 获取 VIP 权益信息
 */
export function getVipInfo() {
  return get('/vip/info')
}

/**
 * 获取 VIP 会员状态
 */
export function getVipStatus() {
  return get('/vip/status')
}

/**
 * 创建 VIP 订单
 * @param {Object} data 订单数据
 */
export function createVipOrder(data) {
  return post('/vip/order/create', data)
}

/**
 * 获取 VIP 订单状态
 * @param {string} orderId 订单ID
 */
export function getVipOrderStatus(orderId) {
  return get('/vip/order/status', { orderId })
}

/**
 * 取消 VIP 订单
 * @param {string} orderId 订单ID
 */
export function cancelVipOrder(orderId) {
  return post('/vip/order/cancel', { orderId })
}

/**
 * 获取支付宝支付链接
 * @param {string} orderId 订单ID
 */
export function getAlipayUrl(orderId) {
  return get('/vip/pay/alipay', { orderId })
}
