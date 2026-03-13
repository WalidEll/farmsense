import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/services/api'
import type {
  Transaction, TransactionResponse, CreateTransactionRequest, UpdateTransactionRequest,
  TransactionFilter, DashboardResponse, ApprovalRequest, ApprovalLogEntry
} from '@/types'

export const useAccountingStore = defineStore('accounting', () => {
  const transactions = ref<Transaction[]>([])
  const dashboard = ref<DashboardResponse | null>(null)
  const categories = ref<string[]>([])
  const loading = ref(false)

  async function fetchTransactions(filters?: TransactionFilter) {
    loading.value = true
    try {
      const params = new URLSearchParams()
      if (filters) {
        if (filters.type) params.append('type', filters.type)
        if (filters.category) params.append('category', filters.category)
        if (filters.from) params.append('from', filters.from)
        if (filters.to) params.append('to', filters.to)
        if (filters.approvalStatus) params.append('approvalStatus', filters.approvalStatus)
        if (filters.tagIds) filters.tagIds.forEach(id => params.append('tagIds', id))
      }
      const query = params.toString() ? `?${params.toString()}` : ''
      transactions.value = await api.get(`/transactions${query}`)
    } finally {
      loading.value = false
    }
  }

  async function fetchDashboard(from?: string, to?: string) {
    const params = new URLSearchParams()
    if (from) params.append('from', from)
    if (to) params.append('to', to)
    const query = params.toString() ? `?${params.toString()}` : ''
    dashboard.value = await api.get(`/transactions/dashboard${query}`)
  }

  async function fetchCategories() {
    categories.value = await api.get('/transactions/categories')
  }

  async function createTransaction(req: CreateTransactionRequest): Promise<Transaction> {
    const data = await api.post('/transactions', req)
    transactions.value.unshift(data)
    return data
  }

  async function updateTransaction(id: string, req: UpdateTransactionRequest): Promise<Transaction> {
    const data = await api.put(`/transactions/${id}`, req)
    const idx = transactions.value.findIndex(t => t.id === id)
    if (idx !== -1) transactions.value[idx] = data
    return data
  }

  async function deleteTransaction(id: string) {
    await api.delete(`/transactions/${id}`)
    transactions.value = transactions.value.filter(t => t.id !== id)
  }

  async function approveTransaction(id: string, req: ApprovalRequest) {
    const data = await api.post(`/transactions/${id}/approve`, req)
    const idx = transactions.value.findIndex(t => t.id === id)
    if (idx !== -1) transactions.value[idx] = data
    return data
  }

  async function fetchTransactionById(id: string): Promise<Transaction | null> {
    try {
      return await api.get(`/transactions/${id}`)
    } catch {
      return null
    }
  }

  async function fetchApprovalLogs(id: string): Promise<ApprovalLogEntry[]> {
    return await api.get(`/transactions/${id}/approval-history`)
  }

  // Alias for dashboard view template
  const dashboardData = dashboard

  return {
    transactions, dashboard, dashboardData, categories, loading,
    fetchTransactions, fetchDashboard, fetchCategories,
    fetchTransactionById,
    createTransaction, updateTransaction, deleteTransaction,
    approveTransaction, fetchApprovalLogs
  }
})
