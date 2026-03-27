/**
 * 请求封装工具
 * 基于 UniApp 的 uni.request 封装
 */

// 开发环境后端地址（微信小程序需要使用实际 IP 或域名）
const BASE_URL = 'http://192.168.31.220:5000'

/**
 * 获取 Token
 */
function getToken() {
  return uni.getStorageSync('token') || ''
}

/**
 * 请求拦截器
 */
function requestInterceptor(config) {
  // 添加 Token
  const token = getToken()
  if (token) {
    config.header = config.header || {}
    config.header['Authorization'] = `Bearer ${token}`
  }

  // 显示加载中
  if (config.showLoading !== false) {
    uni.showLoading({
      title: '加载中...',
      mask: true
    })
  }

  return config
}

/**
 * 响应拦截器
 */
function responseInterceptor(response) {
  uni.hideLoading()

  const { statusCode, data } = response

  if (statusCode === 200) {
    // 业务错误处理
    if (data.code && data.code !== 200 && data.code !== 0) {
      // Token 失效
      if (data.code === 401 || data.code === 403) {
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
        uni.reLaunch({ url: '/pages/login/login' })
      }
      return Promise.reject(data)
    }
    return data
  } else if (statusCode === 401) {
    // Token 失效
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.reLaunch({ url: '/pages/login/login' })
    return Promise.reject(new Error('登录已过期'))
  } else {
    uni.showToast({
      title: '网络请求失败',
      icon: 'none'
    })
    return Promise.reject(new Error('网络请求失败'))
  }
}

/**
 * 请求封装
 * @param {Object} options 请求配置
 */
function request(options) {
  const {
    url,
    method = 'GET',
    data,
    header = {},
    showLoading = true,
    ...rest
  } = options

  const config = {
    url: url.startsWith('http') ? url : BASE_URL + url,
    method,
    data,
    header,
    showLoading,
    ...rest
  }

  return new Promise((resolve, reject) => {
    uni.request({
      ...requestInterceptor(config),
      success: (res) => {
        resolve(responseInterceptor(res))
      },
      fail: (err) => {
        uni.hideLoading()
        uni.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

/**
 * GET 请求
 */
export function get(url, params, options = {}) {
  return request({
    url,
    method: 'GET',
    data: params,
    ...options
  })
}

/**
 * POST 请求
 */
export function post(url, data, options = {}) {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  })
}

/**
 * PUT 请求
 */
export function put(url, data, options = {}) {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  })
}

/**
 * DELETE 请求
 */
export function del(url, data, options = {}) {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  })
}

export default {
  request,
  get,
  post,
  put,
  del,
  BASE_URL
}
