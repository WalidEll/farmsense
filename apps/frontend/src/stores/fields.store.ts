import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Field, CreateFieldRequest, UpdateFieldRequest } from '@/types'
import { api } from '@/services/api'
import { offlineQueue } from '@/services/offline-queue'

export const useFieldStore = defineStore('fields', () => {
  const fields = ref<Field[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const isEditingLayout = ref(false)

  // No local queue needed, using imported offlineQueue

  // Computed
  const fieldsById = computed(() => {
    return fields.value.reduce((acc, field) => {
      acc[field.id] = field
      return acc
    }, {} as Record<string, Field>)
  })

  const stats = computed(() => {
    return {
      total: fields.value.length,
      optimal: fields.value.filter(f => f.status === 'OPTIMAL').length,
      warning: fields.value.filter(f => f.status === 'WARNING').length,
      critical: fields.value.filter(f => f.status === 'CRITICAL').length
    }
  })

  // Actions
  async function fetchFields(force = false) {
    if (loading.value || (fields.value.length > 0 && !force)) return
    
    loading.value = true
    error.value = null
    
    try {
      // In a real app, this would be an API call
      // For now, we'll initialize with some mock data if empty
      // const response = await api.get<Field[]>('/fields')
      // fields.value = response.data
      
      if (fields.value.length === 0) {
        fields.value = [
          { id: 'F-01', name: 'West Block', shape: 'RECTANGLE', x: 50, y: 50, width: 220, height: 180, status: 'OPTIMAL' },
          { id: 'F-02', name: 'North Border', shape: 'RECTANGLE', x: 280, y: 50, width: 220, height: 180, status: 'OPTIMAL' },
          { id: 'F-03', name: 'Hillside', shape: 'RECTANGLE', x: 510, y: 50, width: 220, height: 180, status: 'WARNING' },
          { id: 'F-04', name: 'Stream Side', shape: 'RECTANGLE', x: 740, y: 50, width: 220, height: 180, status: 'OPTIMAL' },
          { id: 'F-05', name: 'South Garden', shape: 'RECTANGLE', x: 50, y: 240, width: 220, height: 180, status: 'CRITICAL' },
          { id: 'F-06', name: 'Central Hub', shape: 'RECTANGLE', x: 280, y: 240, width: 450, height: 180, status: 'OPTIMAL' },
          { id: 'F-07', name: 'East Gate', shape: 'RECTANGLE', x: 740, y: 240, width: 220, height: 180, status: 'OPTIMAL' }
        ]
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch fields'
    } finally {
      loading.value = false
    }
  }

  async function saveField(request: CreateFieldRequest) {
    loading.value = true
    try {
      // Logic for saving to backend would go here
      const newField: Field = {
        ...request,
        status: 'OPTIMAL'
      }
      fields.value.push(newField)
      return newField
    } catch (err: any) {
      error.value = err.message || 'Failed to save field'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateField(id: string, updates: UpdateFieldRequest) {
    const index = fields.value.findIndex(f => f.id === id)
    if (index === -1) return

    try {
      fields.value[index] = { ...fields.value[index], ...updates }
      // Sync to backend...
    } catch (err: any) {
      error.value = err.message || 'Failed to update field'
    }
  }

  async function deleteField(id: string) {
    fields.value = fields.value.filter(f => f.id !== id)
    // Sync to backend...
  }

  function toggleEditing() {
    isEditingLayout.value = !isEditingLayout.value
  }

  return {
    fields,
    loading,
    error,
    isEditingLayout,
    fieldsById,
    stats,
    fetchFields,
    saveField,
    updateField,
    deleteField,
    toggleEditing
  }
})
