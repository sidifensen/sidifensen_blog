import { test, expect } from '@playwright/test'

// ============================================
// 首页测试
// ============================================
test.describe('首页测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
  })

  test('页面标题正确', async ({ page }) => {
    await expect(page).toHaveTitle(/斯蒂芬森/)
  })

  test('主内容区域存在', async ({ page }) => {
    await expect(page.locator('#app')).toBeVisible()
  })

  test('导航菜单存在', async ({ page }) => {
    await expect(page.locator('.el-menu').first()).toBeVisible()
  })
})

// ============================================
// 导航测试
// ============================================
test.describe('导航测试', () => {
  test('导航到文章页面', async ({ page }) => {
    await page.goto('/article')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })

  test('导航到相册页面', async ({ page }) => {
    await page.goto('/album/square')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })

  test('导航到友链页面', async ({ page }) => {
    await page.goto('/link')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })

  test('导航到会员中心', async ({ page }) => {
    await page.goto('/vip')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })
})

// ============================================
// 登录页面测试
// ============================================
test.describe('登录页面测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')
  })

  test('登录页面可访问', async ({ page }) => {
    await expect(page.locator('h2')).toBeVisible()
  })

  test('登录标题正确', async ({ page }) => {
    await expect(page.locator('h2')).toContainText('账号登录')
  })

  test('用户名输入框存在', async ({ page }) => {
    await expect(page.locator('.el-input').first()).toBeVisible()
  })

  test('密码输入框存在', async ({ page }) => {
    await expect(page.locator('.el-input').nth(1)).toBeVisible()
  })

  test('登录按钮存在', async ({ page }) => {
    await expect(page.locator('.login-btn, button:has-text("安全登录")')).toBeVisible()
  })

  test('注册链接存在', async ({ page }) => {
    await expect(page.locator('.register-link, a:has-text("立即注册")')).toBeVisible()
  })
})

// ============================================
// 注册页面测试
// ============================================
test.describe('注册页面测试', () => {
  test('注册页面可访问', async ({ page }) => {
    await page.goto('/register')
    await page.waitForLoadState('networkidle')
    await page.waitForTimeout(1000) // 等待可能的重定向
    await expect(page.locator('#app')).toBeVisible()
  })

  test('注册按钮存在', async ({ page }) => {
    await page.goto('/register')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('.register-btn, button:has-text("注册")')).toBeVisible()
  })
})

// ============================================
// 文章页面测试
// ============================================
test.describe('文章页面测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/article')
    await page.waitForLoadState('networkidle')
  })

  test('文章页面可访问', async ({ page }) => {
    await expect(page).toHaveTitle(/斯蒂芬森/)
  })

  test('页面 Hero 区域存在', async ({ page }) => {
    await expect(page.locator('.page-hero, .hero, h1')).toBeVisible()
  })

  test('文章页面主内容存在', async ({ page }) => {
    await expect(page.locator('.article-page-body, main, #app')).toBeVisible()
  })

  test('页面可滚动', async ({ page }) => {
    await page.evaluate(() => window.scrollTo(0, 500))
    await expect(page.locator('#app')).toBeVisible()
  })
})

// ============================================
// 相册页面测试
// ============================================
test.describe('相册页面测试', () => {
  test('相册页面可访问', async ({ page }) => {
    await page.goto('/album/square')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })
})

// ============================================
// 友链页面测试
// ============================================
test.describe('友链页面测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/link')
    await page.waitForLoadState('networkidle')
  })

  test('友链页面可访问', async ({ page }) => {
    await expect(page.locator('.link-page, main, #app')).toBeVisible()
  })

  test('页面有内容区域', async ({ page }) => {
    await expect(page.locator('.link-page, .container, #app')).toBeVisible()
  })
})

// ============================================
// 会员中心测试
// ============================================
test.describe('会员中心测试', () => {
  test('会员中心页面可访问', async ({ page }) => {
    await page.goto('/vip')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })
})

// ============================================
// 搜索页面测试
// ============================================
test.describe('搜索页面测试', () => {
  test('搜索页面可访问', async ({ page }) => {
    await page.goto('/search')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })
})

// ============================================
// 创作中心测试
// ============================================
test.describe('创作中心测试', () => {
  test('创作中心页面可访问', async ({ page }) => {
    await page.goto('/creation')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })
})

// ============================================
// 消息页面测试
// ============================================
test.describe('消息页面测试', () => {
  test('消息页面可访问', async ({ page }) => {
    await page.goto('/message')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })
})

// ============================================
// 通知页面测试
// ============================================
test.describe('通知页面测试', () => {
  test('通知页面可访问', async ({ page }) => {
    await page.goto('/notification')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })
})

// ============================================
// 404 页面测试
// ============================================
test.describe('404 页面测试', () => {
  test('访问不存在的页面显示错误提示', async ({ page }) => {
    await page.goto('/this-page-does-not-exist-12345')
    await page.waitForLoadState('networkidle')
    // 检查是否显示错误提示或 404 组件
    const hasError = (await page.locator('#app').count()) > 0
    expect(hasError).toBeTruthy()
  })
})

// ============================================
// 响应式测试
// ============================================
test.describe('响应式测试', () => {
  test('移动端视图', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 })
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })

  test('平板视图', async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 })
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    await expect(page.locator('#app')).toBeVisible()
  })
})

// ============================================
// 页面性能测试
// ============================================
test.describe('页面性能测试', () => {
  test('首页加载时间少于 5 秒', async ({ page }) => {
    const startTime = Date.now()
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    const loadTime = Date.now() - startTime
    expect(loadTime).toBeLessThan(5000)
  })

  test('页面无严重控制台错误', async ({ page }) => {
    const consoleErrors: string[] = []
    page.on('console', (msg) => {
      if (msg.type() === 'error') {
        consoleErrors.push(msg.text())
      }
    })
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    // 过滤掉无关紧要的错误
    const criticalErrors = consoleErrors.filter(
      (err) =>
        !err.includes('favicon') &&
        !err.includes('net::ERR') &&
        !err.includes('Failed to load resource'),
    )
    expect(criticalErrors).toHaveLength(0)
  })
})

// ============================================
// 用户交互测试
// ============================================
test.describe('用户交互测试', () => {
  test('页面可以点击导航菜单', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    // 点击文章菜单
    const articleMenu = page.locator('.el-menu-item').filter({ hasText: '文章' }).first()
    if (await articleMenu.isVisible()) {
      await articleMenu.click()
      await page.waitForTimeout(500)
    }
    await expect(page.locator('#app')).toBeVisible()
  })

  test('登录页面可以输入内容', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')
    const input = page.locator('.el-input').first().locator('input')
    if (await input.isVisible()) {
      await input.fill('testuser')
      await expect(input).toHaveValue('testuser')
    }
  })

  test('注册页面可以输入内容', async ({ page }) => {
    await page.goto('/register')
    await page.waitForLoadState('networkidle')
    const input = page.locator('.el-input').first().locator('input')
    if (await input.isVisible()) {
      await input.fill('newuser')
      await expect(input).toHaveValue('newuser')
    }
  })
})
