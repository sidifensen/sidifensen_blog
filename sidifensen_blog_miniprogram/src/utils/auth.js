/**
 * 认证工具函数
 */

/**
 * 检查是否已登录
 */
export function isLoggedIn() {
  return !!uni.getStorageSync('token')
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  const userInfo = uni.getStorageSync('userInfo')
  return userInfo ? JSON.parse(userInfo) : null
}

/**
 * 保存用户信息
 */
export function saveUserInfo(userInfo) {
  uni.setStorageSync('userInfo', JSON.stringify(userInfo))
}

/**
 * 保存 Token
 */
export function saveToken(token) {
  uni.setStorageSync('token', token)
}

/**
 * 清除登录信息
 */
export function clearAuth() {
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')
}

/**
 * 获取微信登录凭证
 */
export function getWxLoginCode() {
  return new Promise((resolve, reject) => {
    uni.login({
      provider: 'weixin',
      success: (res) => {
        if (res.code) {
          resolve(res.code)
        } else {
          reject(new Error('获取微信登录凭证失败'))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

/**
 * 检查登录状态，跳转登录页
 */
export function checkLoginAndNavigate(to = '/pages/login/login') {
  if (!isLoggedIn()) {
    uni.navigateTo({ url: to })
    return false
  }
  return true
}
