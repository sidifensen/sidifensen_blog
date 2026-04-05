import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import TagCloud from '../TagCloud.vue'

// Mock vue-router
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: vi.fn(),
  }),
}))

describe('TagCloud', () => {
  it('应渲染组件', () => {
    const wrapper = mount(TagCloud, {
      props: { tags: [] },
    })
    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('.tag-cloud').exists()).toBe(true)
  })

  it('应接受 tags 属性', () => {
    const tags = [{ name: 'JavaScript' }, { name: 'Vue' }, { name: 'React' }]
    const wrapper = mount(TagCloud, {
      props: { tags },
    })
    expect(wrapper.props('tags')).toEqual(tags)
  })

  it('应接受 max 属性', () => {
    const tags = [{ name: 'JavaScript' }, { name: 'Vue' }, { name: 'React' }, { name: 'Angular' }]
    const wrapper = mount(TagCloud, {
      props: { tags, max: 2 },
    })
    expect(wrapper.props('max')).toBe(2)
  })

  it('应接受 clickable 属性', () => {
    const wrapper = mount(TagCloud, {
      props: { tags: [], clickable: false },
    })
    expect(wrapper.props('clickable')).toBe(false)
  })

  it('应显示所有标签当 max 为 0', () => {
    const tags = [{ name: 'JavaScript' }, { name: 'Vue' }, { name: 'React' }]
    const wrapper = mount(TagCloud, {
      props: { tags, max: 0 },
    })
    expect(wrapper.findAll('.tag-cloud-item').length).toBe(3)
  })

  it('应限制显示数量当设置 max', () => {
    const tags = [{ name: 'JavaScript' }, { name: 'Vue' }, { name: 'React' }, { name: 'Angular' }]
    const wrapper = mount(TagCloud, {
      props: { tags, max: 2 },
    })
    expect(wrapper.findAll('.tag-cloud-item').length).toBe(2)
  })

  it('应显示标签名称', () => {
    const tags = [{ name: 'JavaScript' }]
    const wrapper = mount(TagCloud, {
      props: { tags },
    })
    expect(wrapper.find('.tag-cloud-item').text()).toBe('JavaScript')
  })

  it('clickable 为 true 时应有 clickable 类', () => {
    const tags = [{ name: 'JavaScript' }]
    const wrapper = mount(TagCloud, {
      props: { tags, clickable: true },
    })
    expect(wrapper.find('.tag-cloud-item.clickable').exists()).toBe(true)
  })

  it('clickable 为 false 时不应有 clickable 类', () => {
    const tags = [{ name: 'JavaScript' }]
    const wrapper = mount(TagCloud, {
      props: { tags, clickable: false },
    })
    expect(wrapper.find('.tag-cloud-item.clickable').exists()).toBe(false)
  })

  it('应发射 tag-click 事件', () => {
    const tags = [{ name: 'JavaScript' }]
    const wrapper = mount(TagCloud, {
      props: { tags },
    })

    wrapper.find('.tag-cloud-item').trigger('click')
    expect(wrapper.emitted('tag-click')).toBeTruthy()
    expect(wrapper.emitted('tag-click')[0]).toEqual(['JavaScript'])
  })

  it('空标签数组应渲染空组件', () => {
    const wrapper = mount(TagCloud, {
      props: { tags: [] },
    })
    expect(wrapper.findAll('.tag-cloud-item').length).toBe(0)
  })

  it('应使用默认 clickable 为 true', () => {
    const wrapper = mount(TagCloud, {
      props: { tags: [] },
    })
    expect(wrapper.props('clickable')).toBe(true)
  })
})
