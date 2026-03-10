import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/services/api'
import type { Crop, CropDetail, CropCategory, CreateCropRequest, UpdateCropRequest, CropRequirement, CropGrowthStage, CropNutrient, CropIssue } from '@/types'

export const useCropsStore = defineStore('crops', () => {
  const crops = ref<Crop[]>([])
  const currentCrop = ref<CropDetail | null>(null)
  const loading = ref(false)

  async function fetchAll(category?: CropCategory | null, search?: string) {
    loading.value = true
    try {
      const params = new URLSearchParams()
      if (category) params.set('category', category)
      if (search) params.set('search', search)
      const qs = params.toString()
      crops.value = await api.get(`/crops${qs ? '?' + qs : ''}`)
    } finally {
      loading.value = false
    }
  }

  async function fetchById(id: string) {
    loading.value = true
    try {
      currentCrop.value = await api.get(`/crops/${id}`)
    } finally {
      loading.value = false
    }
  }

  async function create(req: CreateCropRequest): Promise<Crop> {
    const crop: Crop = await api.post('/crops', req)
    crops.value.unshift(crop)
    return crop
  }

  async function update(id: string, req: UpdateCropRequest): Promise<Crop> {
    const updated: Crop = await api.put(`/crops/${id}`, req)
    const idx = crops.value.findIndex(c => c.id === id)
    if (idx !== -1) crops.value[idx] = updated
    return updated
  }

  async function remove(id: string) {
    await api.delete(`/crops/${id}`)
    crops.value = crops.value.filter(c => c.id !== id)
    if (currentCrop.value?.id === id) currentCrop.value = null
  }

  // Sub-resources
  async function saveRequirements(cropId: string, req: Record<string, unknown>): Promise<CropRequirement> {
    const result: CropRequirement = await api.put(`/crops/${cropId}/requirements`, req)
    if (currentCrop.value?.id === cropId) currentCrop.value.requirements = result
    return result
  }

  async function addStage(cropId: string, req: Record<string, unknown>): Promise<CropGrowthStage> {
    const stage: CropGrowthStage = await api.post(`/crops/${cropId}/stages`, req)
    if (currentCrop.value?.id === cropId) currentCrop.value.stages.push(stage)
    return stage
  }

  async function updateStage(cropId: string, stageId: string, req: Record<string, unknown>): Promise<CropGrowthStage> {
    const stage: CropGrowthStage = await api.put(`/crops/${cropId}/stages/${stageId}`, req)
    if (currentCrop.value?.id === cropId) {
      const idx = currentCrop.value.stages.findIndex(s => s.id === stageId)
      if (idx !== -1) currentCrop.value.stages[idx] = stage
    }
    return stage
  }

  async function deleteStage(cropId: string, stageId: string) {
    await api.delete(`/crops/${cropId}/stages/${stageId}`)
    if (currentCrop.value?.id === cropId) {
      currentCrop.value.stages = currentCrop.value.stages.filter(s => s.id !== stageId)
    }
  }

  async function saveNutrients(cropId: string, req: Record<string, unknown>): Promise<CropNutrient> {
    const result: CropNutrient = await api.put(`/crops/${cropId}/nutrients`, req)
    if (currentCrop.value?.id === cropId) currentCrop.value.nutrients = result
    return result
  }

  async function addIssue(cropId: string, req: Record<string, unknown>): Promise<CropIssue> {
    const issue: CropIssue = await api.post(`/crops/${cropId}/issues`, req)
    if (currentCrop.value?.id === cropId) currentCrop.value.issues.push(issue)
    return issue
  }

  async function updateIssue(cropId: string, issueId: string, req: Record<string, unknown>): Promise<CropIssue> {
    const issue: CropIssue = await api.put(`/crops/${cropId}/issues/${issueId}`, req)
    if (currentCrop.value?.id === cropId) {
      const idx = currentCrop.value.issues.findIndex(i => i.id === issueId)
      if (idx !== -1) currentCrop.value.issues[idx] = issue
    }
    return issue
  }

  async function deleteIssue(cropId: string, issueId: string) {
    await api.delete(`/crops/${cropId}/issues/${issueId}`)
    if (currentCrop.value?.id === cropId) {
      currentCrop.value.issues = currentCrop.value.issues.filter(i => i.id !== issueId)
    }
  }

  return {
    crops, currentCrop, loading,
    fetchAll, fetchById, create, update, remove,
    saveRequirements, addStage, updateStage, deleteStage,
    saveNutrients, addIssue, updateIssue, deleteIssue,
  }
})
