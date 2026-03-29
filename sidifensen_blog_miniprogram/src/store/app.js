import { defineStore } from 'pinia'
import { useTheme } from 'uview-pro'

export const useAppStore = defineStore('app', {
  state: () => ({
    isDark: false
  }),

  actions: {
    /**
     * 初始化主题（由 main.js 的 initTheme() 自动处理）
     */
    initTheme() {
      const { isInDarkMode } = useTheme()
      this.isDark = isInDarkMode()
    },

    /**
     * 切换主题
     */
    toggleTheme() {
      const { toggleDarkMode, isInDarkMode } = useTheme()
      toggleDarkMode()
      this.isDark = isInDarkMode()
    },

    /**
     * 设置深色模式
     */
    setDarkMode(dark) {
      const { setDarkMode, isInDarkMode } = useTheme()
      setDarkMode(dark ? 'dark' : 'light')
      this.isDark = isInDarkMode()
    }
  }
})
