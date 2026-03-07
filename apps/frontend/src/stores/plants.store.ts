import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/services/api'
import { offlineQueue } from '@/services/offline-queue'
import type { Plant, CreatePlantRequest, UpdatePlantRequest } from '@/types'

export const usePlantStore = defineStore('plants', () => {
  const plants = ref<Plant[]>([])
  const loading = ref(false)

  async function fetchAll(silent = false) {
    if (!silent) loading.value = true
    try {
      plants.value = await api.get('/plants')
    } finally {
      if (!silent) loading.value = false
    }
  }

  async function create(req: CreatePlantRequest): Promise<Plant> {
    const plant: Plant = await api.post('/plants', req)
    plants.value.unshift(plant)
    return plant
  }

  async function update(id: string, req: UpdatePlantRequest): Promise<Plant> {
    const updated: Plant = await api.put(`/plants/${id}`, req)
    const idx = plants.value.findIndex(p => p.id === id)
    if (idx !== -1) plants.value[idx] = updated
    return updated
  }

  async function remove(id: string): Promise<void> {
    await api.delete(`/plants/${id}`)
    plants.value = plants.value.filter(p => p.id !== id)
  }

  return { plants, loading, fetchAll, create, update, remove }
})
