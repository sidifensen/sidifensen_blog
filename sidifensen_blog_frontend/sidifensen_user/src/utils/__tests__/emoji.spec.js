import { describe, it, expect } from 'vitest'
import { emojiList, default as emojiModule } from '../emoji'

describe('emoji', () => {
  describe('emojiList', () => {
    it('应包含 emojiList 数组', () => {
      expect(Array.isArray(emojiList)).toBe(true)
    })

    it('应包含预期的 emoji 数量', () => {
      // emojiList 应该有数百个 emoji
      expect(emojiList.length).toBeGreaterThan(400)
    })

    it('应包含常见的 emoji 类别', () => {
      // 笑脸表情
      expect(emojiList).toContain('😀')
      expect(emojiList).toContain('😂')
      expect(emojiList).toContain('😊')

      // 心形表情
      expect(emojiList).toContain('❤️')
      expect(emojiList).toContain('💔')

      // 手势表情
      expect(emojiList).toContain('👍')
      expect(emojiList).toContain('👎')
      expect(emojiList).toContain('👏')

      // 符号标记
      expect(emojiList).toContain('✅')
      expect(emojiList).toContain('❌')
      expect(emojiList).toContain('⚠️')

      // 数字
      expect(emojiList).toContain('1️⃣')
      expect(emojiList).toContain('0️⃣')

      // 箭头
      expect(emojiList).toContain('⬆️')
      expect(emojiList).toContain('⬇️')
      expect(emojiList).toContain('⬅️')
      expect(emojiList).toContain('➡️')
    })

    it('不应包含重复的 emoji', () => {
      const uniqueEmojis = new Set(emojiList)
      expect(uniqueEmojis.size).toBe(emojiList.length)
    })
  })

  describe('default export', () => {
    it('应导出 emojiList', () => {
      expect(emojiModule.emojiList).toBe(emojiList)
    })
  })
})
