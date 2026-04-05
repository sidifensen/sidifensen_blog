import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SkeletonLoader from '../SkeletonLoader.vue'

describe('SkeletonLoader', () => {
  it('应渲染组件', () => {
    const wrapper = mount(SkeletonLoader)
    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('.skeleton-loader').exists()).toBe(true)
  })

  it('应使用默认 type 为 card', () => {
    const wrapper = mount(SkeletonLoader)
    expect(wrapper.props('type')).toBe('card')
  })

  it('应接受 article type', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { type: 'article' },
    })
    expect(wrapper.props('type')).toBe('article')
  })

  it('应接受 card type', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { type: 'card' },
    })
    expect(wrapper.props('type')).toBe('card')
  })

  it('应接受 list type', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { type: 'list' },
    })
    expect(wrapper.props('type')).toBe('list')
  })

  it('应接受 profile type', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { type: 'profile' },
    })
    expect(wrapper.props('type')).toBe('profile')
  })

  it('应使用默认 count 为 1', () => {
    const wrapper = mount(SkeletonLoader)
    expect(wrapper.props('count')).toBe(1)
  })

  it('应接受自定义数量', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { count: 3 },
    })
    expect(wrapper.props('count')).toBe(3)
  })

  it('count 为 0 时应渲染 1 个骨架屏', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { count: 0 },
    })
    // 组件内部确保至少显示1个
    const items = wrapper.findAll('.skeleton-item')
    expect(items.length).toBeGreaterThanOrEqual(1)
  })

  it('负数 count 应至少显示 1 个', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { count: -5 },
    })
    const items = wrapper.findAll('.skeleton-item')
    expect(items.length).toBeGreaterThanOrEqual(1)
  })

  it('应渲染多个骨架屏当 count > 1', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { count: 3 },
    })
    const items = wrapper.findAll('.skeleton-item')
    expect(items.length).toBe(3)
  })

  it('article 类型应有正确的 CSS 类', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { type: 'article', count: 1 },
    })
    expect(wrapper.find('.skeleton-article').exists()).toBe(true)
  })

  it('card 类型应有正确的 CSS 类', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { type: 'card', count: 1 },
    })
    expect(wrapper.find('.skeleton-card').exists()).toBe(true)
  })

  it('list 类型应有正确的 CSS 类', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { type: 'list', count: 1 },
    })
    expect(wrapper.find('.skeleton-list').exists()).toBe(true)
  })

  it('profile 类型应有正确的 CSS 类', () => {
    const wrapper = mount(SkeletonLoader, {
      props: { type: 'profile', count: 1 },
    })
    expect(wrapper.find('.skeleton-profile').exists()).toBe(true)
  })
})
