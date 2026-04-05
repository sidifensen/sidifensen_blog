import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useDarkStore = defineStore(
  'dark',
  () => {
    // 每次访问都先检测系统主题作为默认
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    // 已有保存的主题设置则优先使用保存的值
    const storedDarkMode = localStorage.getItem('darkMode')
    const isDark = ref(storedDarkMode !== null ? JSON.parse(storedDarkMode) : prefersDark)

    const toggleDark = () => {
      isDark.value = !isDark.value
      updateHtmlClass()
    }

    const updateHtmlClass = () => {
      const html = document.querySelector('html')
      if (html) {
        if (!isDark.value) {
          html.classList.remove('dark')
          html.classList.add('light')
        } else {
          html.classList.remove('light')
          html.classList.add('dark')
        }
      }
    }

    // 监听系统主题变化，实时响应
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    const handleSystemThemeChange = (e) => {
      isDark.value = e.matches
      updateHtmlClass()
    }
    mediaQuery.addEventListener('change', handleSystemThemeChange)

    // 初始化时更新 html 类名
    const initDarkMode = () => {
      updateHtmlClass()
    }

    return { isDark, toggleDark, initDarkMode }
  },
  {
    persist: {
      enabled: true,
      strategies: [
        {
          key: 'darkMode',
          storage: localStorage,
        },
      ],
    },
  },
)
