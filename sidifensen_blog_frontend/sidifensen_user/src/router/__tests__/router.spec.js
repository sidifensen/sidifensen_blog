import { describe, it, expect } from 'vitest'

describe('Router Configuration Tests', () => {
  let router

  beforeEach(async () => {
    // 动态导入路由
    router = (await import('@/router')).default
  })

  it('应导出 router 实例', () => {
    expect(router).toBeDefined()
    expect(typeof router.push).toBe('function')
    expect(typeof router.replace).toBe('function')
    expect(typeof router.go).toBe('function')
  })

  it('应包含所有必要的路由', () => {
    const routeNames = router.getRoutes().map((r) => r.name)
    expect(routeNames).toContain('index')
    expect(routeNames).toContain('Home')
    expect(routeNames).toContain('Article')
    expect(routeNames).toContain('Search')
    expect(routeNames).toContain('Album')
    expect(routeNames).toContain('UserHomepage')
    expect(routeNames).toContain('ArticleDetail')
    expect(routeNames).toContain('Setting')
    expect(routeNames).toContain('Message')
    expect(routeNames).toContain('Notification')
    expect(routeNames).toContain('VipCenter')
    expect(routeNames).toContain('Creation')
    expect(routeNames).toContain('Editor')
    expect(routeNames).toContain('NotFound')
  })

  it('应有正确的首页路由', () => {
    const homeRoute = router.getRoutes().find((r) => r.name === 'Home')
    expect(homeRoute).toBeDefined()
    expect(homeRoute.path).toBe('/')
  })

  it('应有 404 路由', () => {
    const notFoundRoute = router.getRoutes().find((r) => r.name === 'NotFound')
    expect(notFoundRoute).toBeDefined()
    expect(notFoundRoute.path).toBe('/:pathMatch(.*)*')
  })

  it('应有登录和注册路由', () => {
    const routeNames = router.getRoutes().map((r) => r.name)
    expect(routeNames).toContain('login')
    expect(routeNames).toContain('register')
  })

  it('应有创作中心路由', () => {
    const creationRoute = router.getRoutes().find((r) => r.name === 'Creation')
    expect(creationRoute).toBeDefined()
    expect(creationRoute.path).toBe('/creation')
  })
})
