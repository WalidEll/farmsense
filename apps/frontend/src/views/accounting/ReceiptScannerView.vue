<template>
  <div class="space-y-8">
    <!-- Header -->
    <div>
      <h1 class="text-2xl font-extrabold text-gray-900 tracking-tight">{{ t('nav_receipts') }}</h1>
      <p class="text-sm text-gray-400 mt-1 font-medium">{{ t('receipt_scanner_subtitle') || 'Scannez vos recus et laissez l\'IA extraire les donnees' }}</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      <!-- Upload Section -->
      <div class="space-y-6">
        <ReceiptDropZone @upload="onUpload" />

        <!-- Current Receipt Status -->
        <div v-if="currentReceipt" class="bg-white p-5 rounded-2xl shadow-sm border border-gray-100/80">
          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3 min-w-0">
              <div class="p-2 bg-gray-50 rounded-xl flex-shrink-0">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
                </svg>
              </div>
              <div class="min-w-0">
                <p class="font-semibold text-gray-800 text-sm truncate">{{ currentReceipt.originalFilename }}</p>
                <p class="text-[11px] text-gray-400">{{ new Date(currentReceipt.createdAt).toLocaleString('fr-MA') }}</p>
              </div>
            </div>
            <span :class="[
              currentReceipt.ocrStatus === 'PROCESSED' ? 'text-emerald-700 bg-emerald-50 border-emerald-200' :
              currentReceipt.ocrStatus === 'FAILED' ? 'text-rose-700 bg-rose-50 border-rose-200' : 'text-amber-700 bg-amber-50 border-amber-200',
              'px-2.5 py-1 rounded-lg text-[11px] font-bold uppercase border flex-shrink-0'
            ]">
              {{ currentReceipt.ocrStatus === 'PROCESSED' ? t('receipt_processed') || 'Traite' :
                 currentReceipt.ocrStatus === 'FAILED' ? t('receipt_failed') : t('receipt_processing') }}
            </span>
          </div>

          <!-- Processing spinner -->
          <div v-if="loading" class="flex items-center gap-3 text-sm text-gray-400 mt-4 pt-4 border-t border-gray-50">
            <div class="animate-spin h-4 w-4 border-2 border-emerald-500 border-t-transparent rounded-full"></div>
            {{ t('receipt_processing') }}...
          </div>
        </div>

        <!-- Receipt History -->
        <div v-if="receipts.length > 0" class="space-y-3">
          <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider px-1">{{ t('receipt_history') || 'Historique' }}</h3>
          <div
            v-for="receipt in receipts"
            :key="receipt.id"
            @click="currentReceipt = receipt"
            :class="[
              currentReceipt?.id === receipt.id ? 'border-emerald-200 bg-emerald-50/30 ring-1 ring-emerald-100' : 'border-gray-100 hover:bg-gray-50/50',
              'bg-white p-4 rounded-xl shadow-sm border flex justify-between items-center cursor-pointer transition-all duration-200'
            ]"
          >
            <div class="flex items-center gap-3 min-w-0">
              <div :class="[
                receipt.ocrStatus === 'PROCESSED' ? 'text-emerald-500' : receipt.ocrStatus === 'FAILED' ? 'text-rose-500' : 'text-amber-500',
                'flex-shrink-0'
              ]">
                <svg v-if="receipt.ocrStatus === 'PROCESSED'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <svg v-else-if="receipt.ocrStatus === 'FAILED'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
                </svg>
                <div v-else class="animate-spin h-5 w-5 border-2 border-amber-500 border-t-transparent rounded-full"></div>
              </div>
              <div class="min-w-0">
                <p class="font-medium text-gray-800 text-sm truncate">{{ receipt.originalFilename }}</p>
                <p class="text-[11px] text-gray-400">{{ new Date(receipt.createdAt).toLocaleString('fr-MA') }}</p>
              </div>
            </div>
            <div v-if="receipt.ocrAmount" class="text-right flex-shrink-0">
              <p class="text-sm font-bold text-gray-800">{{ receipt.ocrAmount }} DH</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Results Section -->
      <div v-if="currentReceipt?.ocrStatus === 'PROCESSED'" class="space-y-6">
        <OcrResultsForm
          :initial-data="{
            amount: currentReceipt.ocrAmount,
            category: currentReceipt.ocrCategory,
            transactionDate: currentReceipt.ocrDate,
            description: `OCR: ${currentReceipt.originalFilename}`
          }"
          @confirm="onConfirm"
          @cancel="currentReceipt = null"
        />
      </div>
      <div v-else-if="currentReceipt?.ocrStatus === 'PENDING'" class="flex flex-col items-center justify-center py-20 bg-gradient-to-br from-gray-50 to-white rounded-2xl border-2 border-dashed border-gray-200">
        <div class="relative mb-6">
          <div class="animate-spin h-14 w-14 border-4 border-emerald-200 border-t-emerald-500 rounded-full"></div>
          <div class="absolute inset-0 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-emerald-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M2.036 12.322a1.012 1.012 0 010-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178z" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
          </div>
        </div>
        <p class="text-gray-600 font-semibold">{{ t('receipt_processing') }}</p>
        <p class="text-sm text-gray-400 mt-1">{{ t('receipt_ai_analyzing') || 'L\'IA analyse votre recu...' }}</p>
      </div>
      <div v-else-if="!currentReceipt" class="hidden lg:flex flex-col items-center justify-center py-20 bg-gray-50/50 rounded-2xl border-2 border-dashed border-gray-100">
        <div class="p-4 bg-gray-100 rounded-2xl text-gray-300 mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h3.75M9 15h3.75M9 18h3.75m3 .75H18a2.25 2.25 0 002.25-2.25V6.108c0-1.135-.845-2.098-1.976-2.192a48.424 48.424 0 00-1.123-.08m-5.801 0c-.065.21-.1.433-.1.664 0 .414.336.75.75.75h4.5a.75.75 0 00.75-.75 2.25 2.25 0 00-.1-.664m-5.8 0A2.251 2.251 0 0113.5 2.25H15c1.012 0 1.867.668 2.15 1.586m-5.8 0c-.376.023-.75.05-1.124.08C9.095 4.01 8.25 4.973 8.25 6.108V8.25m0 0H4.875c-.621 0-1.125.504-1.125 1.125v11.25c0 .621.504 1.125 1.125 1.125h9.75c.621 0 1.125-.504 1.125-1.125V9.375c0-.621-.504-1.125-1.125-1.125H8.25z" />
          </svg>
        </div>
        <p class="text-gray-400 font-medium text-sm">{{ t('receipt_select_hint') || 'Uploadez ou selectionnez un recu' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useI18n } from '@/i18n'
import { useReceiptsStore } from '@/stores/receipts.store'
import { storeToRefs } from 'pinia'
import ReceiptDropZone from '@/components/accounting/ReceiptDropZone.vue'
import OcrResultsForm from '@/components/accounting/OcrResultsForm.vue'
import type { ReceiptConfirmRequest } from '@/types'

const { t } = useI18n()
const store = useReceiptsStore()
const { receipts, currentReceipt, loading } = storeToRefs(store)

onMounted(() => {
  store.fetchReceipts()
})

const onUpload = async (file: File) => {
  const response = await store.uploadReceipt(file)
  if (response?.id) {
    store.pollOcrStatus(response.id)
  }
  store.fetchReceipts()
}

const onConfirm = async (data: ReceiptConfirmRequest) => {
  if (currentReceipt.value) {
    await store.confirmReceipt(currentReceipt.value.id, data)
    currentReceipt.value = null
    store.fetchReceipts()
  }
}
</script>
