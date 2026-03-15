import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/services/api'
import { offlineQueue } from '@/services/offline-queue'
import type {
  BreedSummary,
  BreedDetail,
  CreateBreedRequest,
  UpdateBreedRequest,
  BreedCategory,
  BreedPurpose
} from '@/types'

export const useBreedsStore = defineStore('breeds', () => {
  const breeds = ref<BreedSummary[]>([])
  const currentBreed = ref<BreedDetail | null>(null)
  const comparedBreeds = ref<BreedDetail[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const totalElements = ref(0)
  const totalPages = ref(0)
  const currentPage = ref(0)

  // Filter state
  const searchQuery = ref('')
  const categoryFilter = ref<BreedCategory | null>(null)
  const purposeFilter = ref<BreedPurpose | null>(null)

  const canCompare = computed(() => comparedBreeds.value.length > 0)

  async function fetchBreeds(page = 0, size = 12) {
    loading.value = true
    error.value = null
    try {
      const params = new URLSearchParams()
      params.set('page', String(page))
      params.set('size', String(size))
      if (searchQuery.value) params.set('search', searchQuery.value)
      if (categoryFilter.value) params.set('category', categoryFilter.value)
      if (purposeFilter.value) params.set('purpose', purposeFilter.value)

      const response = await api.get(`/breeds?${params.toString()}`)
      breeds.value = response.content
      totalElements.value = response.totalElements
      totalPages.value = response.totalPages
      currentPage.value = response.number
    } catch (e: any) {
      error.value = e.message || 'Failed to fetch breeds'
    } finally {
      loading.value = false
    }
  }

  async function fetchBreedById(id: string): Promise<BreedDetail> {
    loading.value = true
    error.value = null
    try {
      const breed = await api.get(`/breeds/${id}`)
      currentBreed.value = breed
      return breed
    } catch (e: any) {
      error.value = e.message || 'Failed to fetch breed'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function fetchBreedTemplates(id: string): Promise<BreedDetail> {
    return await api.get(`/breeds/${id}/templates`)
  }

  async function createBreed(req: CreateBreedRequest): Promise<BreedDetail> {
    loading.value = true
    error.value = null
    try {
      const breed = await api.post('/breeds', req)
      breeds.value.unshift(breed)
      return breed
    } catch (e: any) {
      if (!navigator.onLine) {
        await offlineQueue.enqueue('POST', '/breeds', req)
        const tempBreed = { ...req, id: `temp-${Date.now()}`, isSystem: false } as unknown as BreedDetail
        breeds.value.unshift(tempBreed)
        return tempBreed
      }
      error.value = e.message || 'Failed to create breed'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function updateBreed(id: string, req: UpdateBreedRequest): Promise<BreedDetail> {
    loading.value = true
    error.value = null
    try {
      const breed = await api.put(`/breeds/${id}`, req)
      const idx = breeds.value.findIndex(b => b.id === id)
      if (idx >= 0) breeds.value[idx] = breed
      if (currentBreed.value?.id === id) currentBreed.value = breed
      return breed
    } catch (e: any) {
      error.value = e.message || 'Failed to update breed'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function deleteBreed(id: string, confirm = false) {
    loading.value = true
    error.value = null
    try {
      const query = confirm ? '?confirm=true' : ''
      await api.delete(`/breeds/${id}${query}`)
      breeds.value = breeds.value.filter(b => b.id !== id)
      if (currentBreed.value?.id === id) currentBreed.value = null
    } catch (e: any) {
      error.value = e.message || 'Failed to delete breed'
      throw e
    } finally {
      loading.value = false
    }
  }

  function addToCompare(breed: BreedDetail) {
    if (comparedBreeds.value.length >= 3) {
      throw new Error('Maximum 3 breeds can be compared')
    }
    if (!comparedBreeds.value.find(b => b.id === breed.id)) {
      comparedBreeds.value.push(breed)
    }
  }

  function removeFromCompare(id: string) {
    comparedBreeds.value = comparedBreeds.value.filter(b => b.id !== id)
  }

  function clearCompare() {
    comparedBreeds.value = []
  }

  function setFilters(search?: string, category?: BreedCategory | null, purpose?: BreedPurpose | null) {
    if (search !== undefined) searchQuery.value = search
    if (category !== undefined) categoryFilter.value = category
    if (purpose !== undefined) purposeFilter.value = purpose
  }

  function clearFilters() {
    searchQuery.value = ''
    categoryFilter.value = null
    purposeFilter.value = null
  }

  return {
    breeds,
    currentBreed,
    comparedBreeds,
    loading,
    error,
    totalElements,
    totalPages,
    currentPage,
    searchQuery,
    categoryFilter,
    purposeFilter,
    canCompare,
    fetchBreeds,
    fetchBreedById,
    fetchBreedTemplates,
    createBreed,
    updateBreed,
    deleteBreed,
    addToCompare,
    removeFromCompare,
    clearCompare,
    setFilters,
    clearFilters,
  }
})
