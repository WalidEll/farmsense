<template>
  <div class="bg-white p-5 rounded-2xl shadow-sm border border-gray-100/80">
    <div class="flex items-center gap-3 mb-5">
      <div class="p-2 bg-emerald-50 rounded-xl">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h3.75M9 15h3.75M9 18h3.75m3 .75H18a2.25 2.25 0 002.25-2.25V6.108c0-1.135-.845-2.098-1.976-2.192a48.424 48.424 0 00-1.123-.08m-5.801 0c-.065.21-.1.433-.1.664 0 .414.336.75.75.75h4.5a.75.75 0 00.75-.75 2.25 2.25 0 00-.1-.664m-5.8 0A2.251 2.251 0 0113.5 2.25H15c1.012 0 1.867.668 2.15 1.586m-5.8 0c-.376.023-.75.05-1.124.08C9.095 4.01 8.25 4.973 8.25 6.108V8.25m0 0H4.875c-.621 0-1.125.504-1.125 1.125v11.25c0 .621.504 1.125 1.125 1.125h9.75c.621 0 1.125-.504 1.125-1.125V9.375c0-.621-.504-1.125-1.125-1.125H8.25zM6.75 12h.008v.008H6.75V12zm0 3h.008v.008H6.75V15zm0 3h.008v.008H6.75V18z" />
        </svg>
      </div>
      <h3 class="text-base font-bold text-gray-800">{{ t('receipt_confirm') }}</h3>
    </div>

    <form @submit.prevent="$emit('confirm', form)" class="space-y-4">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('receipt_date') }}</label>
          <input
            type="date"
            v-model="form.transactionDate"
            required
            class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
          >
        </div>
        <div>
          <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('receipt_amount') }}</label>
          <div class="relative">
            <input
              type="number"
              step="0.01"
              v-model="form.amount"
              required
              class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5 pr-14"
            >
            <div class="absolute inset-y-0 right-0 pr-4 flex items-center pointer-events-none">
              <span class="text-xs font-bold text-gray-300 uppercase">MAD</span>
            </div>
          </div>
        </div>
      </div>

      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('receipt_vendor') }} / {{ t('transaction_category') }}</label>
        <input
          type="text"
          v-model="form.category"
          required
          class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
        >
      </div>

      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('transaction_description') }}</label>
        <textarea
          v-model="form.description"
          rows="3"
          class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
        ></textarea>
      </div>

      <div class="flex justify-end gap-2 pt-4 border-t border-gray-100">
        <button
          type="button"
          @click="$emit('cancel')"
          class="px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 transition-colors font-semibold text-sm"
        >
          {{ t('cancel') }}
        </button>
        <button
          type="submit"
          class="px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 active:scale-[0.98] transition-all font-semibold text-sm"
        >
          {{ t('common_save') }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'
import { useI18n } from '@/i18n'
import type { ReceiptConfirmRequest } from '@/types'

const props = defineProps<{
  initialData: Partial<ReceiptConfirmRequest>
}>()

const { t } = useI18n()

const form = reactive<ReceiptConfirmRequest>({
  type: 'EXPENSE',
  category: props.initialData.category || '',
  amount: props.initialData.amount || 0,
  transactionDate: props.initialData.transactionDate || new Date().toISOString().split('T')[0],
  description: props.initialData.description || '',
  paymentMethod: 'CASH',
  tagIds: []
})

watch(() => props.initialData, (newData) => {
  if (newData.category) form.category = newData.category
  if (newData.amount) form.amount = newData.amount
  if (newData.transactionDate) form.transactionDate = newData.transactionDate
  if (newData.description) form.description = newData.description
}, { deep: true })

defineEmits<{
  (e: 'confirm', data: ReceiptConfirmRequest): void
  (e: 'cancel'): void
}>()
</script>
