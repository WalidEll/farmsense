import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/services/api'
import type { Receipt, ReceiptUploadResponse, ReceiptConfirmRequest, Transaction } from '@/types'

export const useReceiptsStore = defineStore('receipts', () => {
  const receipts = ref<Receipt[]>([])
  const currentReceipt = ref<Receipt | null>(null)
  const loading = ref(false)

  async function fetchReceipts() {
    loading.value = true
    try {
      receipts.value = await api.get('/receipts')
    } finally {
      loading.value = false
    }
  }

  async function fetchReceipt(id: string): Promise<Receipt> {
    const data = await api.get(`/receipts/${id}`)
    const idx = receipts.value.findIndex(r => r.id === id)
    if (idx !== -1) receipts.value[idx] = data
    return data
  }

  async function uploadReceipt(file: File): Promise<ReceiptUploadResponse> {
    const formData = new FormData()
    formData.append('file', file)
    // api service handles multipart via axios
    return await api.post('/receipts/upload', formData)
  }

  async function confirmReceipt(id: string, req: ReceiptConfirmRequest): Promise<Transaction> {
    return await api.post(`/receipts/${id}/confirm`, req)
  }

  async function deleteReceipt(id: string) {
    await api.delete(`/receipts/${id}`)
    receipts.value = receipts.value.filter(r => r.id !== id)
  }

  async function pollOcrStatus(id: string, interval = 2000, maxAttempts = 30): Promise<Receipt | null> {
    for (let i = 0; i < maxAttempts; i++) {
      const receipt = await fetchReceipt(id)
      currentReceipt.value = receipt
      if (receipt.ocrStatus !== 'PENDING') return receipt
      await new Promise(r => setTimeout(r, interval))
    }
    return currentReceipt.value
  }

  return { receipts, currentReceipt, loading, fetchReceipts, fetchReceipt, uploadReceipt, confirmReceipt, deleteReceipt, pollOcrStatus }
})
