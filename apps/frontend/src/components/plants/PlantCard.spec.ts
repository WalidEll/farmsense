import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import PlantCard from './PlantCard.vue'
import { createPinia, setActivePinia } from 'pinia'

// Mock i18n
vi.mock('@/i18n', () => ({
  useI18n: () => ({
    t: (key: string) => key.replace(/\./g, '_')
  })
}))

// Mock readings store
vi.mock('@/stores/readings.store', () => ({
  useReadingsStore: () => ({
    latest: {
      'plant-1': {
        soilMoisture: 50,
        temperature: 25.5,
        humidity: 40,
        lightLux: 1000
      }
    }
  })
}))

describe('PlantCard', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  const mockPlant = {
    id: 'plant-1',
    name: 'My Basil',
    species: 'Basil',
    location: 'Kitchen',
    soilMin: 40,
    soilMax: 80,
    tempMin: 15,
    tempMax: 30,
    lightMin: 500
  }

  it('renders plant name and species', () => {
    const wrapper = mount(PlantCard, {
      props: { plant: mockPlant }
    })
    expect(wrapper.text()).toContain('My Basil')
    expect(wrapper.text()).toContain('Basil')
  })

  it('renders sensor values when available', () => {
    const wrapper = mount(PlantCard, {
      props: { plant: mockPlant }
    })
    expect(wrapper.text()).toContain('50%')
    expect(wrapper.text()).toContain('25.5°C')
  })

  it('emits click event on click', async () => {
    const wrapper = mount(PlantCard, {
      props: { plant: mockPlant }
    })
    await wrapper.trigger('click')
    expect(wrapper.emitted()).toHaveProperty('click')
  })

  it('emits delete event when delete button is clicked', async () => {
    const wrapper = mount(PlantCard, {
      props: { plant: mockPlant }
    })
    const deleteBtn = wrapper.find('button')
    await deleteBtn.trigger('click')
    expect(wrapper.emitted()).toHaveProperty('delete')
  })
})
