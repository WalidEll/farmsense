import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/services/api'
import type { SensorReading } from '@/types'

export const useReadingsStore = defineStore('readings', () => {
  const latest = ref<Record<string, SensorReading>>({})   // keyed by plantId
  const history = ref<Record<string, SensorReading[]>>({}) // keyed by plantId
  const loading = ref(false)

  async function fetchLatest(plantId: string) {
    try {
      const data = await api.get<SensorReading>(`/readings/${plantId}/latest`)
      latest.value[plantId] = data
    } catch {
      // no readings yet — ignore 404
    }
  }

  async function fetchHistory(plantId: string, hours = 24, silent = false) {
    if (!silent) loading.value = true
    try {
      const data = await api.get<SensorReading[]>(`/readings/${plantId}/history?hours=${hours}`)
      history.value[plantId] = data
    } finally {
      if (!silent) loading.value = false
    }
  }

  async function addManual(req: {
    plantId: string
    temperature?: number
    humidity?: number
    soilMoisture?: number
    lightLux?: number
  }) {
    const data = await api.post<SensorReading>('/readings/manual', req)
    latest.value[req.plantId] = data
    return data
  }

  return { latest, history, loading, fetchLatest, fetchHistory, addManual }
})
