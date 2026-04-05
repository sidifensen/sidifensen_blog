import { describe, it, expect } from 'vitest'
import { formatCompactNumber } from '../formatNumber'

describe('formatNumber', () => {
  describe('formatCompactNumber', () => {
    it('应返回小于1000的数字的字符串表示', () => {
      expect(formatCompactNumber(0)).toBe('0')
      expect(formatCompactNumber(1)).toBe('1')
      expect(formatCompactNumber(999)).toBe('999')
    })

    it('应正确处理边界值', () => {
      expect(formatCompactNumber()).toBe('0')
      expect(formatCompactNumber(null)).toBe('0')
      expect(formatCompactNumber(undefined)).toBe('0')
      expect(formatCompactNumber(NaN)).toBe('0')
    })

    it('应正确格式化千位数字', () => {
      expect(formatCompactNumber(1000)).toBe('1K')
      expect(formatCompactNumber(1500)).toBe('2K')
      expect(formatCompactNumber(21000)).toBe('21K')
      expect(formatCompactNumber(999999)).toBe('1000K')
    })

    it('应正确格式化百万位数字', () => {
      expect(formatCompactNumber(1000000)).toBe('1M')
      expect(formatCompactNumber(1500000)).toBe('2M')
      expect(formatCompactNumber(2100000)).toBe('2M')
    })

    it('应处理字符串数字输入', () => {
      expect(formatCompactNumber('500')).toBe('500')
      expect(formatCompactNumber('1500')).toBe('2K')
      expect(formatCompactNumber('2000000')).toBe('2M')
    })
  })
})
