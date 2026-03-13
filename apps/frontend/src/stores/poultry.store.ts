import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/services/api'
import type {
  Flock, Supplier, Customer,
  CreateFlockRequest, UpdateFlockRequest,
  CreateSupplierRequest, UpdateSupplierRequest,
  CreateCustomerRequest, UpdateCustomerRequest,
  FlockStatus, FlockPurpose
} from '@/types'

export const usePoultryStore = defineStore('poultry', () => {
  const flocks = ref<Flock[]>([])
  const currentFlock = ref<Flock | null>(null)
  const suppliers = ref<Supplier[]>([])
  const customers = ref<Customer[]>([])
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
    // Soft delete sets status to FINISHED
    const idx = flocks.value.findIndex(f => f.id === id)
    if (idx >= 0) flocks.value[idx].status = 'FINISHED'
    if (currentFlock.value?.id === id) currentFlock.value.status = 'FINISHED'
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
    flocks, currentFlock, suppliers, customers, loading,
    fetchFlocks, fetchFlock, createFlock, updateFlock, deleteFlock,
    fetchSuppliers, fetchSupplier, createSupplier, updateSupplier, deleteSupplier,
    fetchCustomers, fetchCustomer, createCustomer, updateCustomer, deleteCustomer,
  }
})
