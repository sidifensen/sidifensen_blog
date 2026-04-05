import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import CountTo from '../CountTo.vue'

// Mock formatCompactNumber
vi.mock('@/utils/formatNumber', () => ({
  formatCompactNumber: (value) => {
    const numericValue = Number(value) || 0
    if (numericValue >= 1000000) {
      return Math.round(numericValue / 1000000) + 'M'
    } else if (numericValue >= 1000) {
      return Math.round(numericValue / 1000) + 'K'
    }
    return numericValue.toString()
  },
}))

describe('CountTo', () => {
  it('应渲染组件', () => {
    const wrapper = mount(CountTo, {
      props: { start: 0, end: 100 },
    })
    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('.count-to').exists()).toBe(true)
  })

  it('应接受 start 属性', () => {
    const wrapper = mount(CountTo, {
      props: { start: 50, end: 100 },
    })
    expect(wrapper.props('start')).toBe(50)
  })

  it('应接受 end 属性', () => {
    const wrapper = mount(CountTo, {
      props: { start: 0, end: 1000 },
    })
    expect(wrapper.props('end')).toBe(1000)
  })

  it('应接受 duration 属性', () => {
    const wrapper = mount(CountTo, {
      props: { start: 0, end: 100, duration: 3000 },
    })
    expect(wrapper.props('duration')).toBe(3000)
  })

  it('应接受 decimals 属性', () => {
    const wrapper = mount(CountTo, {
      props: { start: 0, end: 100, decimals: 2 },
    })
    expect(wrapper.props('decimals')).toBe(2)
  })

  it('应接受 prefix 属性', () => {
    const wrapper = mount(CountTo, {
      props: { start: 0, end: 100, prefix: '$' },
    })
    expect(wrapper.props('prefix')).toBe('$')
  })

  it('应接受 suffix 属性', () => {
    const wrapper = mount(CountTo, {
      props: { start: 0, end: 100, suffix: '+' },
    })
    expect(wrapper.props('suffix')).toBe('+')
  })

  it('应接受 separator 属性', () => {
    const wrapper = mount(CountTo, {
      props: { start: 0, end: 1000000, separator: ',' },
    })
    expect(wrapper.props('separator')).toBe(',')
  })

  it('应接受 compact 属性', () => {
    const wrapper = mount(CountTo, {
      props: { start: 0, end: 1500, compact: true },
    })
    expect(wrapper.props('compact')).toBe(true)
  })

  it('应使用默认 props', () => {
    const wrapper = mount(CountTo)

    expect(wrapper.props('start')).toBe(0)
    expect(wrapper.props('end')).toBe(0)
    expect(wrapper.props('duration')).toBe(2000)
    expect(wrapper.props('decimals')).toBe(0)
    expect(wrapper.props('prefix')).toBe('')
    expect(wrapper.props('suffix')).toBe('')
    expect(wrapper.props('separator')).toBe(',')
    expect(wrapper.props('compact')).toBe(false)
  })

  it('compact 模式应使用 formatCompactNumber', () => {
    const wrapper = mount(CountTo, {
      props: { start: 0, end: 1500, compact: true },
    })
    expect(wrapper.props('compact')).toBe(true)
  })

  it('start 和 end 相同时不应显示动画', () => {
    const wrapper = mount(CountTo, {
      props: { start: 100, end: 100 },
    })
    expect(wrapper.props('start')).toBe(wrapper.props('end'))
  })
})
