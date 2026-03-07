import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/services/api'
import type { Device, DeviceConfigRequest } from '@/types'

export const useDevicesStore = defineStore('devices', () => {
  const devices = ref<Device[]>([])
  const loading = ref(false)
  const error = ref('')

  async function fetchAll(silent = false) {
    if (!silent) loading.value = true
    error.value = ''
    try {
      devices.value = await api.get<Device[]>('/devices')
    } catch (e: any) {
      error.value = e?.response?.data?.error ?? 'Failed to fetch devices'
      devices.value = []
    } finally {
      if (!silent) loading.value = false
    }
  }

  async function assign(deviceId: string, plantId: string | null): Promise<Device> {
    const updated = await api.put<Device>(`/devices/${deviceId}/assign`, { plantId })
    const idx = devices.value.findIndex((d: any) => d.id === deviceId)
    if (idx !== -1) devices.value[idx] = updated
    return updated
  }

  async function label(deviceId: string, newLabel: string): Promise<Device> {
    const updated = await api.put<Device>(`/devices/${deviceId}/label`, { label: newLabel })
    const idx = devices.value.findIndex((d: any) => d.id === deviceId)
    if (idx !== -1) devices.value[idx] = updated
    return updated
  }

  async function updateConfig(deviceId: string, config: DeviceConfigRequest): Promise<Device> {
    const updated = await api.put<Device>(`/devices/${deviceId}/config`, config)
    const idx = devices.value.findIndex((d: any) => d.id === deviceId)
    if (idx !== -1) devices.value[idx] = updated
    return updated
  }

  async function deleteDevice(deviceId: string): Promise<void> {
    await api.delete(`/devices/${deviceId}`)
    devices.value = devices.value.filter(d => d.id !== deviceId)
  }

  const onlineCount = computed(() => devices.value.filter(d => d.online).length)
  const offlineCount = computed(() => devices.value.filter(d => !d.online).length)

  return { devices, loading, error, fetchAll, assign, label, updateConfig, deleteDevice, onlineCount, offlineCount }
})
