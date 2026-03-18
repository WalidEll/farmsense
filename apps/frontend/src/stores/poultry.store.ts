import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/services/api'
import type {
  Flock, Supplier, Customer,
  HousingLocation, MortalityEvent, MortalityEventCreateResponse,
  CreateFlockRequest, UpdateFlockRequest,
  CreateSupplierRequest, UpdateSupplierRequest,
  CreateCustomerRequest, UpdateCustomerRequest,
  CreateHousingLocationRequest, UpdateHousingLocationRequest,
  CreateMortalityEventRequest,
  FlockStatus, FlockPurpose, HousingLocationType
} from '@/types'

export const usePoultryStore = defineStore('poultry', () => {
  const flocks = ref<Flock[]>([])
  const currentFlock = ref<Flock | null>(null)
  const suppliers = ref<Supplier[]>([])
  const customers = ref<Customer[]>([])
  const housingLocations = ref<HousingLocation[]>([])
  const mortalityEvents = ref<Record<string, MortalityEvent[]>>({})
  const loading = ref(false)

  // ── Flocks ──
  async function fetchFlocks(params?: { status?: FlockStatus; purpose?: FlockPurpose }) {
    loading.value = true
    try {
      const query = params ? '?' + new URLSearchParams(params as any).toString() : ''
      flocks.value = await api.get(`/flocks${query}`)
    } finally {
      loading.value = false
    }
  }

  async function fetchFlock(id: string) {
    loading.value = true
    try {
      currentFlock.value = await api.get(`/flocks/${id}`)
    } finally {
      loading.value = false
    }
  }

  async function createFlock(req: CreateFlockRequest): Promise<Flock> {
    const flock: Flock = await api.post('/flocks', req)
    flocks.value.unshift(flock)
    return flock
  }

  async function updateFlock(id: string, req: UpdateFlockRequest): Promise<Flock> {
    const flock: Flock = await api.put(`/flocks/${id}`, req)
    const idx = flocks.value.findIndex(f => f.id === id)
    if (idx >= 0) flocks.value[idx] = flock
    if (currentFlock.value?.id === id) currentFlock.value = flock
    return flock
  }

  async function deleteFlock(id: string) {
    await api.delete(`/flocks/${id}`)
    // Soft delete sets status to PHASED_OUT
    const idx = flocks.value.findIndex(f => f.id === id)
    if (idx >= 0) flocks.value[idx].status = 'PHASED_OUT'
    if (currentFlock.value?.id === id) currentFlock.value.status = 'PHASED_OUT'
  }

  // ── Housing Locations ──
  async function fetchHousingLocations(type?: HousingLocationType) {
    loading.value = true
    try {
      const query = type ? `?type=${type}` : ''
      housingLocations.value = await api.get(`/housing-locations${query}`)
    } finally {
      loading.value = false
    }
  }

  async function createHousingLocation(req: CreateHousingLocationRequest): Promise<HousingLocation> {
    loading.value = true
    try {
      const location: HousingLocation = await api.post('/housing-locations', req)
      housingLocations.value.unshift(location)
      return location
    } finally {
      loading.value = false
    }
  }

  async function updateHousingLocation(id: string, req: UpdateHousingLocationRequest): Promise<HousingLocation> {
    loading.value = true
    try {
      const location: HousingLocation = await api.put(`/housing-locations/${id}`, req)
      const idx = housingLocations.value.findIndex(l => l.id === id)
      if (idx >= 0) housingLocations.value[idx] = location
      return location
    } finally {
      loading.value = false
    }
  }

  async function deleteHousingLocation(id: string) {
    await api.delete(`/housing-locations/${id}`)
    housingLocations.value = housingLocations.value.filter(l => l.id !== id)
  }

  // ── Mortality Events ──
  async function fetchMortalityEvents(flockId: string) {
    const events: MortalityEvent[] = await api.get(`/flocks/${flockId}/mortality-events`)
    mortalityEvents.value[flockId] = events
  }

  async function createMortalityEvent(
    flockId: string,
    req: CreateMortalityEventRequest
  ): Promise<MortalityEventCreateResponse> {
    const response: MortalityEventCreateResponse = await api.post(`/flocks/${flockId}/mortality-events`, req)
    // Update local mortality events list
    if (!mortalityEvents.value[flockId]) mortalityEvents.value[flockId] = []
    mortalityEvents.value[flockId].unshift(response)
    // Update flock headcount in local state
    const flockIdx = flocks.value.findIndex(f => f.id === flockId)
    if (flockIdx >= 0) flocks.value[flockIdx].currentBirdCount = response.updatedFlockHeadcount
    if (currentFlock.value?.id === flockId) currentFlock.value.currentBirdCount = response.updatedFlockHeadcount
    return response
  }

  // ── Suppliers ──
  async function fetchSuppliers(search?: string) {
    loading.value = true
    try {
      const query = search ? `?search=${encodeURIComponent(search)}` : ''
      suppliers.value = await api.get(`/suppliers${query}`)
    } finally {
      loading.value = false
    }
  }

  async function fetchSupplier(id: string): Promise<Supplier> {
    return await api.get(`/suppliers/${id}`)
  }

  async function createSupplier(req: CreateSupplierRequest): Promise<Supplier> {
    const supplier: Supplier = await api.post('/suppliers', req)
    suppliers.value.unshift(supplier)
    return supplier
  }

  async function updateSupplier(id: string, req: UpdateSupplierRequest): Promise<Supplier> {
    const supplier: Supplier = await api.put(`/suppliers/${id}`, req)
    const idx = suppliers.value.findIndex(s => s.id === id)
    if (idx >= 0) suppliers.value[idx] = supplier
    return supplier
  }

  async function deleteSupplier(id: string) {
    await api.delete(`/suppliers/${id}`)
    suppliers.value = suppliers.value.filter(s => s.id !== id)
  }

  // ── Customers ──
  async function fetchCustomers(search?: string) {
    loading.value = true
    try {
      const query = search ? `?search=${encodeURIComponent(search)}` : ''
      customers.value = await api.get(`/customers${query}`)
    } finally {
      loading.value = false
    }
  }

  async function fetchCustomer(id: string): Promise<Customer> {
    return await api.get(`/customers/${id}`)
  }

  async function createCustomer(req: CreateCustomerRequest): Promise<Customer> {
    const customer: Customer = await api.post('/customers', req)
    customers.value.unshift(customer)
    return customer
  }

  async function updateCustomer(id: string, req: UpdateCustomerRequest): Promise<Customer> {
    const customer: Customer = await api.put(`/customers/${id}`, req)
    const idx = customers.value.findIndex(c => c.id === id)
    if (idx >= 0) customers.value[idx] = customer
    return customer
  }

  async function deleteCustomer(id: string) {
    await api.delete(`/customers/${id}`)
    customers.value = customers.value.filter(c => c.id !== id)
  }

  return {
    flocks, currentFlock, suppliers, customers, housingLocations, mortalityEvents, loading,
    fetchFlocks, fetchFlock, createFlock, updateFlock, deleteFlock,
    fetchHousingLocations, createHousingLocation, updateHousingLocation, deleteHousingLocation,
    fetchMortalityEvents, createMortalityEvent,
    fetchSuppliers, fetchSupplier, createSupplier, updateSupplier, deleteSupplier,
    fetchCustomers, fetchCustomer, createCustomer, updateCustomer, deleteCustomer,
  }
})
