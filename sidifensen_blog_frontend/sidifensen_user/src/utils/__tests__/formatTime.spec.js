import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import {
  formatTime,
  formatDate,
  formatDateCN,
  getFriendlyTime,
  formatConversationTime,
  formatVipExpireDate,
} from '../formatTime'

// Mock Date
const fixedDate = new Date('2026-04-05T12:00:00')

describe('formatTime', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    vi.setSystemTime(fixedDate)
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  describe('formatTime', () => {
    it('应返回相对时间格式', () => {
      // 刚刚（小于1分钟）
      expect(formatTime('2026-04-05T11:59:30')).toBe('刚刚')
      expect(formatTime('2026-04-05T11:59:01')).toBe('刚刚')

      // 分钟前（小于1小时）
      expect(formatTime('2026-04-05T11:30:00')).toBe('30分钟前')
      expect(formatTime('2026-04-05T11:01:00')).toBe('59分钟前')

      // 小时前（小于1天）
      expect(formatTime('2026-04-05T10:00:00')).toBe('2小时前')
      expect(formatTime('2026-04-05T06:00:00')).toBe('6小时前')

      // 天前（小于1周）
      expect(formatTime('2026-04-04T12:00:00')).toBe('1天前')
      expect(formatTime('2026-04-01T12:00:00')).toBe('4天前')

      // 周前（小于1月）
      expect(formatTime('2026-03-28T12:00:00')).toBe('1周前')
      expect(formatTime('2026-03-15T12:00:00')).toBe('3周前')

      // 月前（小于1年）
      expect(formatTime('2026-03-05T12:00:00')).toBe('1个月前')
      expect(formatTime('2025-12-05T12:00:00')).toBe('4个月前')
    })

    it('应处理空输入', () => {
      expect(formatTime('')).toBe('')
      expect(formatTime(null)).toBe('')
      expect(formatTime(undefined)).toBe('')
    })

    it('应处理超过一年的日期', () => {
      expect(formatTime('2024-01-01T12:00:00')).toBe('2024-01-01')
    })
  })

  describe('formatDate', () => {
    it('应正确格式化日期为默认格式', () => {
      expect(formatDate('2026-04-05T14:30:45')).toBe('2026-04-05 14:30:45')
    })

    it('应正确格式化日期为指定格式', () => {
      expect(formatDate('2026-04-05T14:30:45', 'YYYY-MM-DD')).toBe('2026-04-05')
      expect(formatDate('2026-04-05T14:30:45', 'YYYY/MM/DD')).toBe('2026/04/05')
      expect(formatDate('2026-04-05T14:30:45', 'HH:mm')).toBe('14:30')
    })

    it('应处理空输入', () => {
      expect(formatDate('')).toBe('')
      expect(formatDate(null)).toBe('')
    })
  })

  describe('formatDateCN', () => {
    it('应返回中文格式日期', () => {
      const result = formatDateCN('2026-04-05')
      expect(result).toContain('2026')
      expect(result).toContain('04')
      expect(result).toContain('05')
    })

    it('应处理空输入', () => {
      expect(formatDateCN('')).toBe('')
      expect(formatDateCN(null)).toBe('')
    })
  })

  describe('getFriendlyTime', () => {
    it('今天应只显示时间', () => {
      expect(getFriendlyTime('2026-04-05T10:30:00')).toBe('10:30')
    })

    it('昨天应显示"昨天 HH:mm"', () => {
      expect(getFriendlyTime('2026-04-04T10:30:00')).toBe('昨天 10:30')
    })

    it('今年应显示MM-DD HH:mm', () => {
      expect(getFriendlyTime('2026-01-15T10:30:00')).toBe('01-15 10:30')
    })

    it('应处理空输入', () => {
      expect(getFriendlyTime('')).toBe('')
      expect(getFriendlyTime(null)).toBe('')
    })
  })

  describe('formatConversationTime', () => {
    it('应返回相对时间格式', () => {
      expect(formatConversationTime('2026-04-05T11:59:30')).toBe('刚刚')
      expect(formatConversationTime('2026-04-05T11:30:00')).toBe('30分钟前')
      expect(formatConversationTime('2026-04-05T10:00:00')).toBe('2小时前')
    })

    it('应处理空输入', () => {
      expect(formatConversationTime('')).toBe('')
      expect(formatConversationTime(null)).toBe('')
    })
  })

  describe('formatVipExpireDate', () => {
    it('应正确格式化VIP到期日期', () => {
      expect(formatVipExpireDate('2026-12-31T23:59:59')).toBe('2026-12-31 23:59')
    })

    it('无效日期应返回"--"', () => {
      expect(formatVipExpireDate('')).toBe('--')
      expect(formatVipExpireDate(null)).toBe('--')
      expect(formatVipExpireDate(undefined)).toBe('--')
    })

    it('无效日期字符串应原样返回', () => {
      expect(formatVipExpireDate('invalid')).toBe('invalid')
    })
  })
})
