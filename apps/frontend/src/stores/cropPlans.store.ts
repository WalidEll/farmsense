import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/services/api'
import type {
  CropPlan, FarmLocation, PlantingItem, PlantingTask,
  CreateCropPlanRequest, UpdateCropPlanRequest,
  CreateLocationRequest, CreatePlantingRequest, UpdatePlantingRequest,
  PlantingYieldRequest,
} from '@/types'

export const useCropPlansStore = defineStore('cropPlans', () => {
  const plans = ref<CropPlan[]>([])
  const currentPlan = ref<CropPlan | null>(null)
  const locations = ref<FarmLocation[]>([])
  const plantings = ref<PlantingItem[]>([])
  const tasks = ref<PlantingTask[]>([])
  const todayTasks = ref<PlantingTask[]>([])
  const loading = ref(false)

  // ── Plans ──
  async function fetchPlans() {
    loading.value = true
    try {
      plans.value = await api.get('/crop-plans')
    } finally {
      loading.value = false
    }
  }

  async function fetchPlan(id: string) {
    loading.value = true
    try {
      currentPlan.value = await api.get(`/crop-plans/${id}`)
    } finally {
      loading.value = false
    }
  }

  async function createPlan(req: CreateCropPlanRequest): Promise<CropPlan> {
    const plan: CropPlan = await api.post('/crop-plans', req)
    plans.value.unshift(plan)
    return plan
  }

  async function updatePlan(id: string, req: UpdateCropPlanRequest): Promise<CropPlan> {
    const plan: CropPlan = await api.put(`/crop-plans/${id}`, req)
    const idx = plans.value.findIndex(p => p.id === id)
    if (idx >= 0) plans.value[idx] = plan
    if (currentPlan.value?.id === id) currentPlan.value = plan
    return plan
  }

  async function deletePlan(id: string) {
    await api.delete(`/crop-plans/${id}`)
    plans.value = plans.value.filter(p => p.id !== id)
    if (currentPlan.value?.id === id) currentPlan.value = null
  }

  // ── Locations ──
  async function fetchLocations() {
    locations.value = await api.get('/locations')
  }

  async function createLocation(req: CreateLocationRequest): Promise<FarmLocation> {
    const loc: FarmLocation = await api.post('/locations', req)
    locations.value.push(loc)
    return loc
  }

  async function updateLocation(id: string, req: Partial<CreateLocationRequest>): Promise<FarmLocation> {
    const loc: FarmLocation = await api.put(`/locations/${id}`, req)
    const idx = locations.value.findIndex(l => l.id === id)
    if (idx >= 0) locations.value[idx] = loc
    return loc
  }

  async function deleteLocation(id: string) {
    await api.delete(`/locations/${id}`)
    locations.value = locations.value.filter(l => l.id !== id)
  }

  // ── Plantings ──
  async function fetchPlantings(planId: string) {
    plantings.value = await api.get(`/crop-plans/${planId}/plantings`)
  }

  async function createPlanting(planId: string, req: CreatePlantingRequest): Promise<PlantingItem> {
    const planting: PlantingItem = await api.post(`/crop-plans/${planId}/plantings`, req)
    plantings.value.push(planting)
    return planting
  }

  async function updatePlanting(planId: string, id: string, req: UpdatePlantingRequest): Promise<PlantingItem> {
    const planting: PlantingItem = await api.put(`/crop-plans/${planId}/plantings/${id}`, req)
    const idx = plantings.value.findIndex(p => p.id === id)
    if (idx >= 0) plantings.value[idx] = planting
    return planting
  }

  async function deletePlanting(planId: string, id: string) {
    await api.delete(`/crop-plans/${planId}/plantings/${id}`)
    plantings.value = plantings.value.filter(p => p.id !== id)
  }

  async function setYield(planId: string, id: string, req: PlantingYieldRequest): Promise<PlantingItem> {
    const planting: PlantingItem = await api.put(`/crop-plans/${planId}/plantings/${id}/yield`, req)
    const idx = plantings.value.findIndex(p => p.id === id)
    if (idx >= 0) plantings.value[idx] = planting
    return planting
  }

  // ── Tasks ──
  async function fetchTasks(planId: string) {
    tasks.value = await api.get(`/crop-plans/${planId}/tasks`)
  }

  async function fetchTodayTasks() {
    todayTasks.value = await api.get('/tasks/today')
  }

  async function updateTask(id: string, req: { status: string; skipReason?: string }): Promise<PlantingTask> {
    const task: PlantingTask = await api.put(`/tasks/${id}`, req)
    for (const list of [tasks.value, todayTasks.value]) {
      const idx = list.findIndex(t => t.id === id)
      if (idx >= 0) list[idx] = task
    }
    return task
  }

  return {
    plans, currentPlan, locations, plantings, tasks, todayTasks, loading,
    fetchPlans, fetchPlan, createPlan, updatePlan, deletePlan,
    fetchLocations, createLocation, updateLocation, deleteLocation,
    fetchPlantings, createPlanting, updatePlanting, deletePlanting, setYield,
    fetchTasks, fetchTodayTasks, updateTask,
  }
})
