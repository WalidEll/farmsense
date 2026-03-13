import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import StatusBadge from './StatusBadge.vue'

describe('StatusBadge', () => {
  it('renders green when value is optimal', () => {
    const wrapper = mount(StatusBadge, {
      props: {
        value: 50,
        type: 'moisture',
        min: 30,
        max: 80
      }
    })
    expect(wrapper.classes()).toContain('text-green-600')
  })

  it('renders red when value is below min', () => {
    const wrapper = mount(StatusBadge, {
      props: {
        value: 20,
        type: 'moisture',
        min: 30,
        max: 80
      }
    })
    expect(wrapper.classes()).toContain('text-red-600')
  })

  it('renders blue when moisture is above max (wet)', () => {
    const wrapper = mount(StatusBadge, {
      props: {
        value: 90,
        type: 'moisture',
        min: 30,
        max: 80
      }
    })
    expect(wrapper.classes()).toContain('text-blue-600')
  })

  it('renders amber warn zone when moisture is near min', () => {
    // Range 80-30 = 50. Warning zone 10% = 5. 
    // Near min: 30 + 3 = 33
    const wrapper = mount(StatusBadge, {
      props: {
        value: 33,
        type: 'moisture',
        min: 30,
        max: 80
      }
    })
    expect(wrapper.classes()).toContain('text-amber-600')
  })

  it('renders orange warn zone when temp is near max', () => {
    // Range 30-10 = 20. Warning zone 10% = 2.
    // Near max: 30 - 1 = 29
    const wrapper = mount(StatusBadge, {
      props: {
        value: 29,
        type: 'temp',
        min: 10,
        max: 30
      }
    })
    expect(wrapper.classes()).toContain('text-orange-600')
  })

  it('renders gray when value is null', () => {
    const wrapper = mount(StatusBadge, {
      props: {
        type: 'moisture'
      }
    })
    expect(wrapper.classes()).toContain('text-gray-300')
  })
})
