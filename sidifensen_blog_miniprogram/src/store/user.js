import { defineStore } from 'pinia'
import { getUserInfo, saveUserInfo, clearAuth } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: uni.getStorageSync('token') || '',
    isLoggedIn: false
  }),

  getters: {
    getUserInfo: (state) => state.userInfo,
    isVip: (state) => state.userInfo?.vipEndTime ? new Date(state.userInfo.vipEndTime) > new Date() : false
  },

  actions: {
    /**
     * 检查登录状态
     */
    checkLogin() {
      const token = uni.getStorageSync('token')
      const userInfoStr = uni.getStorageSync('userInfo')

      if (token && userInfoStr) {
        try {
          this.token = token
          this.userInfo = JSON.parse(userInfoStr)
          this.isLoggedIn = true
        } catch (e) {
          // JSON 解析失败，清除无效数据
          this.token = ''
          this.userInfo = null
          this.isLoggedIn = false
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
        }
      } else {
        this.token = ''
        this.userInfo = null
        this.isLoggedIn = false
      }
    },

    /**
     * 设置用户信息
     */
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      this.isLoggedIn = true
      saveUserInfo(userInfo)
    },

    /**
     * 设置 Token
     */
    setToken(token) {
      this.token = token
      uni.setStorageSync('token', token)
    },

    /**
     * 登录
     */
    login(token, userInfo) {
      this.token = token
      this.userInfo = userInfo
      this.isLoggedIn = true
      uni.setStorageSync('token', token)
      if (userInfo) {
        saveUserInfo(userInfo)
      }
    },

    /**
     * 退出登录
     */
    logout() {
      this.token = ''
      this.userInfo = null
      this.isLoggedIn = false
      clearAuth()
    },

    /**
     * 更新用户信息
     */
    updateUserInfo(partialInfo) {
      if (this.userInfo) {
        this.userInfo = { ...this.userInfo, ...partialInfo }
        saveUserInfo(this.userInfo)
      }
    }
  }
})
