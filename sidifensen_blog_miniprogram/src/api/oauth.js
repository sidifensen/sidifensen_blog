/**
 * 第三方登录相关 API
 */
import { post } from './index'

/**
 * 微信登录
 * @param {string} code 微信登录凭证
 */
export function wxLogin(code) {
  return post('/oauth/wechat/login', { code })
}

/**
 * 微信手机号登录
 * @param {string} code 手机号凭证
 */
export function wxPhoneLogin(code) {
  return post('/oauth/wx/phone', { code })
}

/**
 * 兑换 OAuth 一次性票据，获取 JWT token
 * @param {string} ticket 一次性票据
 */
export function exchangeTicket(ticket) {
  return post('/user/oauth/exchange', { ticket })
}

/**
 * 账号密码登录
 * @param {Object} data 登录数据
 */
export function passwordLogin(data) {
  return post('/user/login', data)
}

/**
 * 注册
 * @param {Object} data 注册数据
 */
export function register(data) {
  return post('/oauth/register', data)
}

/**
 * 发送验证码
 * @param {string} phone 手机号
 */
export function sendSmsCode(phone) {
  return post('/oauth/sms/send', { phone })
}

/**
 * 验证验证码
 * @param {string} phone 手机号
 * @param {string} code 验证码
 */
export function verifySmsCode(phone, code) {
  return post('/oauth/sms/verify', { phone, code })
}

/**
 * 重置密码
 * @param {Object} data 重置数据
 */
export function resetPassword(data) {
  return post('/oauth/password/reset', data)
}
