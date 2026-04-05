import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { formatTimeAgo, formatTime } from '../timeUtils'

// Mock Date
const fixedDate = new Date('2026-04-05T12:00:00')

describe('timeUtils', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    vi.setSystemTime(fixedDate)
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  describe('formatTimeAgo', () => {
    it('应返回"刚刚"当时间差小于1分钟', () => {
      expect(formatTimeAgo('2026-04-05T11:59:30')).toBe('刚刚')
      expect(formatTimeAgo('2026-04-05T11:59:01')).toBe('刚刚')
    })

    it('应返回分钟前当时间差小于1小时', () => {
      expect(formatTimeAgo('2026-04-05T11:30:00')).toBe('30分钟前')
      expect(formatTimeAgo('2026-04-05T11:01:00')).toBe('59分钟前')
    })

    it('应返回小时前当时间差小于1天', () => {
      expect(formatTimeAgo('2026-04-05T10:00:00')).toBe('2小时前')
      expect(formatTimeAgo('2026-04-05T06:00:00')).toBe('6小时前')
    })

    it('应返回天前当时间差小于7天', () => {
      expect(formatTimeAgo('2026-04-04T12:00:00')).toBe('1天前')
      expect(formatTimeAgo('2026-04-01T12:00:00')).toBe('4天前')
    })

    it('应返回日期格式当时间超过7天（今年）', () => {
      expect(formatTimeAgo('2026-03-28T12:00:00')).toBe('03-28')
      expect(formatTimeAgo('2026-01-15T12:00:00')).toBe('01-15')
    })

    it('应返回完整日期格式当时间超过7天（往年）', () => {
      expect(formatTimeAgo('2025-01-15T12:00:00')).toBe('2025-01-15')
    })

    it('应处理空输入', () => {
      expect(formatTimeAgo('')).toBe('')
      expect(formatTimeAgo(null)).toBe('')
      expect(formatTimeAgo(undefined)).toBe('')
    })
  })

  describe('formatTime', () => {
    it('应正确格式化时间戳为默认格式', () => {
      expect(formatTime('2026-04-05T14:30:45')).toBe('2026-04-05 14:30:45')
    })

    it('应正确格式化时间戳为指定格式', () => {
      expect(formatTime('2026-04-05T14:30:45', 'YYYY-MM-DD')).toBe('2026-04-05')
      expect(formatTime('2026-04-05T14:30:45', 'YYYY/MM/DD')).toBe('2026/04/05')
      expect(formatTime('2026-04-05T14:30:45', 'HH:mm')).toBe('14:30')
      expect(formatTime('2026-04-05T14:30:45', 'HH:mm:ss')).toBe('14:30:45')
    })

    it('应处理空输入', () => {
      expect(formatTime('')).toBe('')
      expect(formatTime(null)).toBe('')
      expect(formatTime(undefined)).toBe('')
    })

    it('应正确处理边界情况', () => {
      expect(formatTime('2026-01-01T00:00:00', 'YYYY-MM-DD')).toBe('2026-01-01')
      expect(formatTime('2026-12-31T23:59:59', 'YYYY-MM-DD')).toBe('2026-12-31')
    })
  })
})
