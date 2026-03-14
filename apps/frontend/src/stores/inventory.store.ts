import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/services/api'
import type {
  InventoryItem, InventoryCategory, StockMovement,
  CreateInventoryItemRequest, UpdateInventoryItemRequest,
  CreateStockMovementRequest, InventoryDashboardResponse,
  InventoryFilter, StockStatus
} from '@/types'

export const useInventoryStore = defineStore('inventory', () => {
  const items = ref<InventoryItem[]>([])
  const categories = ref<InventoryCategory[]>([])
  const movements = ref<StockMovement[]>([])
  const dashboard = ref<InventoryDashboardResponse | null>(null)
  const loading = ref(false)

  // ── Computed ──
  const lowStockItems = computed(() =>
    items.value.filter(i => i.status === 'LOW' || i.status === 'CRITICAL')
  )

  const criticalItems = computed(() =>
    items.value.filter(i => i.status === 'CRITICAL' || i.status === 'OUT_OF_STOCK')
  )

  const totalValue = computed(() =>
    items.value.reduce((sum, i) => sum + (i.currentStock * (i.unitCost ?? 0)), 0)
  )

  // ── Items CRUD ──
  async function fetchItems(filters?: InventoryFilter) {
    loading.value = true
    try {
      const params = new URLSearchParams()
      if (filters) {
        if (filters.categoryType) params.append('categoryType', filters.categoryType)
        if (filters.status) params.append('status', filters.status)
        if (filters.search) params.append('search', filters.search)
        if (filters.supplierId) params.append('supplierId', filters.supplierId)
      }
      const query = params.toString() ? `?${params.toString()}` : ''
      items.value = await api.get(`/inventory/items${query}`)
    } finally {
      loading.value = false
    }
  }

  async function fetchItemById(id: string): Promise<InventoryItem | null> {
    try {
      return await api.get(`/inventory/items/${id}`)
    } catch {
      return null
    }
  }

  async function createItem(req: CreateInventoryItemRequest): Promise<InventoryItem> {
    const data = await api.post<InventoryItem>('/inventory/items', req)
    items.value.unshift(data)
    return data
  }

  async function updateItem(id: string, req: UpdateInventoryItemRequest): Promise<InventoryItem> {
    const data = await api.put<InventoryItem>(`/inventory/items/${id}`, req)
    const idx = items.value.findIndex(i => i.id === id)
    if (idx !== -1) items.value[idx] = data
    return data
  }

  async function deleteItem(id: string) {
    await api.delete(`/inventory/items/${id}`)
    items.value = items.value.filter(i => i.id !== id)
  }

  // ── Categories ──
  async function fetchCategories() {
    categories.value = await api.get('/inventory/categories')
  }

  // ── Stock Movements ──
  async function fetchMovements(itemId?: string) {
    const url = itemId
      ? `/inventory/items/${itemId}/movements`
      : '/inventory/movements'
    movements.value = await api.get(url)
  }

  async function createMovement(req: CreateStockMovementRequest): Promise<StockMovement> {
    const data = await api.post<StockMovement>('/inventory/movements', req)
    movements.value.unshift(data)
    // Refresh the item to get updated stock
    const item = await fetchItemById(req.inventoryItemId)
    if (item) {
      const idx = items.value.findIndex(i => i.id === req.inventoryItemId)
      if (idx !== -1) items.value[idx] = item
    }
    return data
  }

  // ── Dashboard ──
  async function fetchDashboard() {
    dashboard.value = await api.get('/inventory/dashboard')
  }

  return {
    items, categories, movements, dashboard, loading,
    lowStockItems, criticalItems, totalValue,
    fetchItems, fetchItemById, createItem, updateItem, deleteItem,
    fetchCategories, fetchMovements, createMovement, fetchDashboard,
  }
})
