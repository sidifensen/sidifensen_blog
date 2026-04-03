/**
 * 深色模式主题管理
 * 微信小程序通过 wx.getWindowInfo().colorScheme 获取系统主题
 */

// 更新导航栏颜色（原生导航栏才有效）
function updateNavigationBarColor(isDark) {
  const navStyle = isDark ? {
    backgroundColor: '#111827',
    frontColor: '#ffffff'
  } : {
    backgroundColor: '#ffffff',
    frontColor: '#000000'
  }
  uni.setNavigationBarColor(navStyle)
}

// 获取当前深色模式状态
function getCurrentDarkMode() {
  // #ifdef MP-WEIXIN
  try {
    const info = wx.getWindowInfo()
    return info.colorScheme === 'dark'
  } catch (e) {
    return false
  }
  // #endif
  // #ifndef MP-WEIXIN
  return false
  // #endif
}

// 初始化主题监听
export function initThemeListener() {
  // 获取当前主题并设置
  const isDark = getCurrentDarkMode()
  updateNavigationBarColor(isDark)
  uni.$emit('themeChange', isDark ? 'dark' : 'light')

  // 监听系统主题变化
  uni.onThemeChange((res) => {
    const themeIsDark = res.theme === 'dark'
    updateNavigationBarColor(themeIsDark)
    uni.$emit('themeChange', themeIsDark ? 'dark' : 'light')
  })
}
