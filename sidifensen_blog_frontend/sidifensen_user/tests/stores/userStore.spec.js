import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '@/stores/userStore'

describe('userStore', () => {
  beforeEach(() => {
    // 每个测试前创建新的 pinia 实例
    setActivePinia(createPinia())
  })

  it('初始状态 user 为空对象', () => {
    const store = useUserStore()
    expect(store.user).toEqual({})
  })

  it('设置用户信息', () => {
    const store = useUserStore()
    const userInfo = {
      id: 1,
      username: 'testuser',
      avatar: 'https://example.com/avatar.png'
    }
    store.user = userInfo
    expect(store.user).toEqual(userInfo)
    expect(store.user.username).toBe('testuser')
  })

  it('清除用户信息', () => {
    const store = useUserStore()
    // 先设置用户
    store.user = { id: 1, username: 'test' }
    expect(store.user.username).toBe('test')

    // 清除用户
    store.clearUser()
    expect(store.user).toBeNull()
  })

  it('用户信息可以更新单个字段', () => {
    const store = useUserStore()
    store.user = { id: 1, username: 'original' }
    store.user.username = 'updated'
    expect(store.user.username).toBe('updated')
    expect(store.user.id).toBe(1)
  })
})
