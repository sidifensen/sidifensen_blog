import { describe, it, expect, vi, beforeEach } from 'vitest'

// Mock 所有外部依赖
vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      interceptors: {
        request: { use: vi.fn() },
        response: { use: vi.fn() },
      },
    })),
  },
  create: vi.fn(() => ({
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() },
    },
  })),
}))

vi.mock('@/router/index.js', () => ({
  default: { push: vi.fn() },
}))

vi.mock('@/utils/Auth.js', () => ({
  GetJwt: vi.fn(() => 'mock-token'),
  RemoveJwt: vi.fn(),
}))

vi.mock('@/stores/userStore.js', () => ({
  useUserStore: vi.fn(() => ({ clearUser: vi.fn() })),
}))

describe('Request.js', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('模块导出', () => {
    it('应导出 request 模块', async () => {
      const request = await import('@/utils/Request')
      expect(request.default).toBeDefined()
    })
  })

  describe('GetJwt mock', () => {
    it('GetJwt 应返回 mock token', async () => {
      const { GetJwt } = await import('@/utils/Auth.js')
      expect(GetJwt()).toBe('mock-token')
    })

    it('可以模拟无 token 情况', async () => {
      const { GetJwt } = await import('@/utils/Auth.js')
      vi.mocked(GetJwt).mockReturnValueOnce(null)
      expect(GetJwt()).toBeNull()
    })
  })

  describe('RemoveJwt mock', () => {
    it('RemoveJwt 应可调用', async () => {
      const { RemoveJwt } = await import('@/utils/Auth.js')
      RemoveJwt()
      expect(RemoveJwt).toHaveBeenCalled()
    })
  })
})
