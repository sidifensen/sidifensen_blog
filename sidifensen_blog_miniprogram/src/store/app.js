import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    theme: 'light',
    isDark: false
  }),

  actions: {
    /**
     * 初始化主题
     */
    initTheme() {
      const theme = uni.getStorageSync('theme') || 'light'
      this.setTheme(theme)
    },

    /**
     * 设置主题
     */
    setTheme(theme) {
      this.theme = theme
      this.isDark = theme === 'dark'
      uni.setStorageSync('theme', theme)

      // 通过切换 CSS 类来应用主题
      if (this.isDark) {
        uni.$setTheme?.('dark')
      } else {
        uni.$setTheme?.('light')
      }
    },

    /**
     * 切换主题
     */
    toggleTheme() {
      const newTheme = this.theme === 'light' ? 'dark' : 'light'
      this.setTheme(newTheme)
    }
  }
})
