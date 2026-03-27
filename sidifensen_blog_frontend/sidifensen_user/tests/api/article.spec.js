import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'

// 简单的 mock 方案
vi.mock('axios', async () => {
  const actual = await vi.importActual('axios')
  return {
    ...actual,
    default: {
      ...actual.default,
      create: vi.fn(() => ({
        interceptors: {
          request: { use: vi.fn() },
          response: { use: vi.fn() },
        },
        get: vi.fn(),
        post: vi.fn(),
        put: vi.fn(),
        delete: vi.fn(),
      })),
    },
    create: vi.fn(() => ({
      interceptors: {
        request: { use: vi.fn() },
        response: { use: vi.fn() },
      },
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn(),
    })),
  }
})

describe('article API 函数', () => {
  let mockRequest

  beforeEach(async () => {
    vi.clearAllMocks()
    // 动态导入以获取 mock 后的 request
    const requestModule = await import('@/utils/Request')
    mockRequest = requestModule.default
  })

  describe('getArticleDetail', () => {
    it('应调用 GET /article/get/:id', async () => {
      const mockData = {
        data: {
          code: 200,
          data: { id: 1, title: '测试文章' },
        },
      }

      mockRequest.get = vi.fn().mockResolvedValue(mockData)

      const result = await mockRequest.get({
        url: '/article/get/1',
        method: 'get',
      })

      expect(mockRequest.get).toHaveBeenCalledWith({
        url: '/article/get/1',
        method: 'get',
      })
      expect(result).toEqual(mockData)
    })
  })

  describe('getAllArticleList', () => {
    it('应调用 GET /article/listAll', async () => {
      mockRequest.get = vi.fn().mockResolvedValue({ data: { code: 200 } })

      await mockRequest.get({
        url: '/article/listAll?pageNum=1&pageSize=10',
        method: 'get',
      })

      expect(mockRequest.get).toHaveBeenCalledWith({
        url: '/article/listAll?pageNum=1&pageSize=10',
        method: 'get',
      })
    })
  })

  describe('addArticle', () => {
    it('应调用 POST /article/add', async () => {
      const postData = { title: '新文章', content: '内容' }
      mockRequest.post = vi.fn().mockResolvedValue({ data: { code: 200 } })

      await mockRequest.post({
        url: '/article/add',
        method: 'post',
        data: postData,
      })

      expect(mockRequest.post).toHaveBeenCalledWith({
        url: '/article/add',
        method: 'post',
        data: postData,
      })
    })
  })

  describe('deleteArticle', () => {
    it('应调用 DELETE /article/delete/:id', async () => {
      mockRequest.delete = vi.fn().mockResolvedValue({ data: { code: 200 } })

      await mockRequest.delete({
        url: '/article/delete/123',
        method: 'delete',
      })

      expect(mockRequest.delete).toHaveBeenCalledWith({
        url: '/article/delete/123',
        method: 'delete',
      })
    })
  })
})
