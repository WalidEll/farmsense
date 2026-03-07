import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'
import type { Alert } from '@/types'

export const useAlertsStore = defineStore('alerts', () => {
  const alerts = ref<Alert[]>([])
  const loading = ref(false)

  const unreadCount = computed(
    () => alerts.value.filter((a) => !a.ackAt).length
  )

  async function fetchAll(silent = false) {
    if (!silent) loading.value = true
    try {
      alerts.value = await api.get<Alert[]>('/alerts')
    } finally {
      if (!silent) loading.value = false
    }
  }

  async function acknowledge(id: string) {
    const updated = await api.patch<Alert>(`/alerts/${id}/ack`, {})
    const idx = alerts.value.findIndex((a) => a.id === id)
    if (idx !== -1) alerts.value[idx] = updated
  }

  return { alerts, loading, unreadCount, fetchAll, acknowledge }
})
