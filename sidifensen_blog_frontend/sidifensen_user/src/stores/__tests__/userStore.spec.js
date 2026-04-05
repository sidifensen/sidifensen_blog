import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../userStore'

// Mock Auth module
vi.mock('@/utils/Auth', () => ({
  RemoveJwt: vi.fn(),
}))

describe('userStore', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    setActivePinia(createPinia())
    localStorage.clear()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  describe('initial state', () => {
    it('应初始化为空用户对象', () => {
      const userStore = useUserStore()
      expect(userStore.user).toEqual({})
    })

    it('isLoggedIn 应为 false', () => {
      const userStore = useUserStore()
      expect(userStore.isLoggedIn).toBe(false)
    })
  })

  describe('isLoggedIn computed', () => {
    it('当 user.id 存在时应返回 true', () => {
      const userStore = useUserStore()
      userStore.user = { id: 1, nickname: 'test' }
      expect(userStore.isLoggedIn).toBe(true)
    })

    it('当 user.id 不存在时应返回 false', () => {
      const userStore = useUserStore()
      userStore.user = { nickname: 'test' }
      expect(userStore.isLoggedIn).toBe(false)
    })

    it('当 user 为空时应返回 false', () => {
      const userStore = useUserStore()
      userStore.user = {}
      expect(userStore.isLoggedIn).toBe(false)
    })

    it('当 user 为 null 时应返回 false', () => {
      const userStore = useUserStore()
      userStore.user = null
      expect(userStore.isLoggedIn).toBe(false)
    })
  })

  describe('clearUser', () => {
    it('应清除用户数据', () => {
      const userStore = useUserStore()
      userStore.user = { id: 1, nickname: 'test' }
      userStore.clearUser()
      expect(userStore.user).toBeNull()
    })
  })
})
