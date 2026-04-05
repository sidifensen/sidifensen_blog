import { describe, it, expect, beforeEach, afterEach } from 'vitest'
import { GetJwt, SetJwt, RemoveJwt } from '../Auth'

const TEST_KEY = 'sidifensen_blog_jwt'

describe('Auth', () => {
  beforeEach(() => {
    // 清理 localStorage
    localStorage.removeItem(TEST_KEY)
  })

  afterEach(() => {
    // 清理 localStorage
    localStorage.removeItem(TEST_KEY)
  })

  describe('GetJwt', () => {
    it('应返回 null 当没有设置 token', () => {
      expect(GetJwt()).toBeNull()
    })

    it('应返回设置的 token', () => {
      localStorage.setItem(TEST_KEY, 'test-token-123')
      expect(GetJwt()).toBe('test-token-123')
    })

    it('应返回完整的 token 字符串', () => {
      const token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test'
      SetJwt(token)
      expect(GetJwt()).toBe(token)
    })
  })

  describe('SetJwt', () => {
    it('应正确设置 token', () => {
      SetJwt('new-token')
      expect(localStorage.getItem(TEST_KEY)).toBe('new-token')
    })

    it('应覆盖已有的 token', () => {
      SetJwt('first-token')
      SetJwt('second-token')
      expect(GetJwt()).toBe('second-token')
    })

    it('应处理空字符串（GetJwt返回null因为空字符串是falsy）', () => {
      SetJwt('')
      // GetJwt 内部判断 if (jwt) 会将空字符串当作 falsy，所以返回 null
      expect(GetJwt()).toBeNull()
    })
  })

  describe('RemoveJwt', () => {
    it('应移除 token', () => {
      SetJwt('test-token')
      RemoveJwt()
      expect(GetJwt()).toBeNull()
    })

    it('应在 token 不存在时不报错', () => {
      expect(() => RemoveJwt()).not.toThrow()
    })
  })
})
