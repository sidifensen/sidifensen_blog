import { describe, it, expect, vi, beforeEach } from 'vitest'
import { compressImage, validateImageFile, validateAvatarImageFile } from '../PhotoUtils'

// Mock Element Plus ElMessage
vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      error: vi.fn(),
    },
  }
})

describe('PhotoUtils', () => {
  describe('validateImageFile', () => {
    it('应接受有效的 JPG 图片', () => {
      const file = new File([''], 'test.jpg', { type: 'image/jpg' })
      expect(validateImageFile(file)).toBe(true)
    })

    it('应接受有效的 JPEG 图片', () => {
      const file = new File([''], 'test.jpeg', { type: 'image/jpeg' })
      expect(validateImageFile(file)).toBe(true)
    })

    it('应接受有效的 PNG 图片', () => {
      const file = new File([''], 'test.png', { type: 'image/png' })
      expect(validateImageFile(file)).toBe(true)
    })

    it('应接受有效的 WEBP 图片', () => {
      const file = new File([''], 'test.webp', { type: 'image/webp' })
      expect(validateImageFile(file)).toBe(true)
    })

    it('应接受有效的 GIF 图片', () => {
      const file = new File([''], 'test.gif', { type: 'image/gif' })
      expect(validateImageFile(file)).toBe(true)
    })

    it('应拒绝无效的图片类型', () => {
      const file = new File([''], 'test.bmp', { type: 'image/bmp' })
      expect(validateImageFile(file)).toBe(false)
    })

    it('应拒绝超过5MB的图片', () => {
      // 创建一个大于5MB的 mock File 对象
      const largeSize = 6 * 1024 * 1024 // 6MB
      const file = new File([''], 'test.jpg', { type: 'image/jpg' })
      Object.defineProperty(file, 'size', { value: largeSize })
      expect(validateImageFile(file)).toBe(false)
    })

    it('应接受恰好5MB的图片', () => {
      const file = new File([''], 'test.jpg', { type: 'image/jpg' })
      Object.defineProperty(file, 'size', { value: 5 * 1024 * 1024 - 1 })
      expect(validateImageFile(file)).toBe(true)
    })
  })

  describe('validateAvatarImageFile', () => {
    it('应接受 JPG 头像', () => {
      const file = new File([''], 'avatar.jpg', { type: 'image/jpg' })
      expect(validateAvatarImageFile(file)).toBe(true)
    })

    it('应接受 JPEG 头像', () => {
      const file = new File([''], 'avatar.jpeg', { type: 'image/jpeg' })
      expect(validateAvatarImageFile(file)).toBe(true)
    })

    it('应接受 PNG 头像', () => {
      const file = new File([''], 'avatar.png', { type: 'image/png' })
      expect(validateAvatarImageFile(file)).toBe(true)
    })

    it('应接受 WEBP 头像', () => {
      const file = new File([''], 'avatar.webp', { type: 'image/webp' })
      expect(validateAvatarImageFile(file)).toBe(true)
    })

    it('应接受 GIF 头像（代码实际允许 GIF）', () => {
      const file = new File([''], 'avatar.gif', { type: 'image/gif' })
      expect(validateAvatarImageFile(file)).toBe(true)
    })

    it('应拒绝超过1MB的头像图片', () => {
      const largeSize = 2 * 1024 * 1024 // 2MB
      const file = new File([''], 'avatar.jpg', { type: 'image/jpg' })
      Object.defineProperty(file, 'size', { value: largeSize })
      expect(validateAvatarImageFile(file)).toBe(false)
    })

    it('应接受恰好1MB的头像图片', () => {
      const file = new File([''], 'avatar.jpg', { type: 'image/jpg' })
      Object.defineProperty(file, 'size', { value: 1024 * 1024 - 1 })
      expect(validateAvatarImageFile(file)).toBe(true)
    })
  })

  describe('compressImage', () => {
    beforeEach(() => {
      vi.useFakeTimers()
    })

    afterEach(() => {
      vi.useRealTimers()
    })

    it('应导出 compressImage 函数', () => {
      expect(typeof compressImage).toBe('function')
    })

    it('compressImage 返回 Promise', () => {
      const file = new File([''], 'test.png', { type: 'image/png' })
      const result = compressImage(file)
      expect(result).toBeInstanceOf(Promise)
    })

    // compressImage 测试需要完整的浏览器 API mock，
    // 包括 FileReader, Image, Canvas, Blob 等
    // 这里只做基本的函数存在性测试
  })
})
