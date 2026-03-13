<template>
  <div class="bg-white p-4 rounded-2xl shadow-sm border border-gray-100/80 hover:shadow-md transition-shadow duration-300">
    <div class="flex flex-wrap gap-3 items-end">
      <!-- Type -->
      <div class="min-w-[150px]">
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('transaction_type') }}</label>
        <select
          v-model="filters.type"
          class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5 bg-gray-50/50"
        >
          <option value="">{{ t('common_all') }}</option>
          <option value="EXPENSE">{{ t('transaction_expense') }}</option>
          <option value="INCOME">{{ t('transaction_income') }}</option>
        </select>
      </div>

      <!-- Category -->
      <div class="flex-1 min-w-[200px]">
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('transaction_category') }}</label>
        <div class="relative">
          <svg xmlns="http://www.w3.org/2000/svg" class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" />
          </svg>
          <input
            type="text"
            v-model="filters.category"
            class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5 pl-10 bg-gray-50/50"
            :placeholder="t('crops_search')"
          >
        </div>
      </div>

      <!-- Date Range -->
      <div class="flex-1 min-w-[280px]">
        <DateRangeFilter v-model:from="filters.from" v-model:to="filters.to" />
      </div>

      <!-- Buttons -->
      <div class="flex items-center gap-2">
        <button
          @click="$emit('filter', filters)"
          class="px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 active:scale-[0.98] transition-all text-sm font-semibold"
        >
          {{ t('common_refresh') }}
        </button>
        <button
          @click="reset"
          class="px-5 py-2.5 border border-gray-200 text-gray-500 rounded-xl hover:bg-gray-50 transition-colors text-sm font-semibold"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useI18n } from '@/i18n'
import DateRangeFilter from './DateRangeFilter.vue'

const { t } = useI18n()

const filters = reactive({
  type: '',
  category: '',
  from: '',
  to: '',
  approvalStatus: '',
  tagIds: []
})

const emit = defineEmits<{
  (e: 'filter', filters: any): void
}>()

const reset = () => {
  filters.type = ''
  filters.category = ''
  filters.from = ''
  filters.to = ''
  filters.approvalStatus = ''
  filters.tagIds = []
  emit('filter', filters)
}
</script>
