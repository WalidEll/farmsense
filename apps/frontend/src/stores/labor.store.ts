import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/services/api'
import type { LaborLog, CreateLaborLogRequest, UpdateLaborLogRequest } from '@/types'

export const useLaborStore = defineStore('labor', () => {
  const laborLogs = ref<LaborLog[]>([])
  const loading = ref(false)

  async function fetchLaborLogs(from?: string, to?: string) {
    loading.value = true
    try {
      const params = new URLSearchParams()
      if (from) params.append('from', from)
      if (to) params.append('to', to)
      const query = params.toString() ? `?${params.toString()}` : ''
      laborLogs.value = await api.get(`/labor${query}`)
    } finally {
      loading.value = false
    }
  }

  async function createLaborLog(req: CreateLaborLogRequest): Promise<LaborLog> {
    const data = await api.post('/labor', req)
    laborLogs.value.unshift(data)
    return data
  }

  async function updateLaborLog(id: string, req: UpdateLaborLogRequest): Promise<LaborLog> {
    const data = await api.put(`/labor/${id}`, req)
    const idx = laborLogs.value.findIndex(l => l.id === id)
    if (idx !== -1) laborLogs.value[idx] = data
    return data
  }

  async function deleteLaborLog(id: string) {
    await api.delete(`/labor/${id}`)
    laborLogs.value = laborLogs.value.filter(l => l.id !== id)
  }

  return { laborLogs, loading, fetchLaborLogs, createLaborLog, updateLaborLog, deleteLaborLog }
})
