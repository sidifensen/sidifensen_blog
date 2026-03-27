import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SvgIcon from '@/components/SvgIcon.vue'

describe('SvgIcon.vue', () => {
  it('renders with default props', () => {
    const wrapper = mount(SvgIcon, {
      props: { name: 'github' }
    })

    // 验证 svg 元素存在
    const svg = wrapper.find('svg')
    expect(svg.exists()).toBe(true)

    // 验证 use 元素存在
    const use = wrapper.find('use')
    expect(use.exists()).toBe(true)

    // jsdom 对 xlink:href 的处理方式：href 而不是 xlink:href
    const href = use.attributes('href') || use.element.getAttribute('xlink:href')
    expect(href).toBe('#github')
  })

  it('renders with custom prefix', () => {
    const wrapper = mount(SvgIcon, {
      props: { name: 'github', prefix: '$' }
    })

    const use = wrapper.find('use')
    const href = use.attributes('href') || use.element.getAttribute('xlink:href')
    expect(href).toBe('$github')
  })

  it('renders with custom color', () => {
    const wrapper = mount(SvgIcon, {
      props: { name: 'github', color: '#ff0000' }
    })

    const use = wrapper.find('use')
    expect(use.attributes('fill')).toBe('#ff0000')
  })

  it('renders with custom dimensions', () => {
    const wrapper = mount(SvgIcon, {
      props: { name: 'github', width: '24px', height: '24px' }
    })

    const svg = wrapper.find('svg')
    expect(svg.attributes('style')).toContain('width: 24px')
    expect(svg.attributes('style')).toContain('height: 24px')
  })

  it('renders with marginRight', () => {
    const wrapper = mount(SvgIcon, {
      props: { name: 'github', marginRight: '8px' }
    })

    const svg = wrapper.find('svg')
    // jsdom 将 camelCase 转为 kebab-case
    expect(svg.attributes('style')).toContain('margin-right: 8px')
  })

  it('uses default values correctly', () => {
    const wrapper = mount(SvgIcon, {
      props: { name: 'github' }
    })

    const svg = wrapper.find('svg')
    const use = wrapper.find('use')

    // 检查默认值
    expect(svg.attributes('style')).toContain('width: 16px')
    expect(svg.attributes('style')).toContain('height: 16px')
    const href = use.attributes('href') || use.element.getAttribute('xlink:href')
    expect(href).toBe('#github')
    expect(use.attributes('fill')).toBe('')
  })
})
