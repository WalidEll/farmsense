<template>
  <form @submit.prevent="submit" class="bg-white p-5 rounded-2xl shadow-sm border border-gray-100/80 space-y-5">
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('labor_worker_name') }} *</label>
        <input
          type="text"
          v-model="form.workerName"
          required
          class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
          :placeholder="t('labor_worker_name')"
        >
      </div>
      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('labor_worker_role') }}</label>
        <input
          type="text"
          v-model="form.workerRole"
          class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
          :placeholder="t('labor_worker_role')"
        >
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('labor_work_date') }} *</label>
        <input
          type="date"
          v-model="form.workDate"
          required
          class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
        >
      </div>
      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('labor_hourly_rate') }} *</label>
        <div class="relative">
          <input
            type="number"
            step="0.01"
            v-model="form.hourlyRate"
            required
            class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5 pr-14"
          >
          <div class="absolute inset-y-0 right-0 pr-4 flex items-center pointer-events-none">
            <span class="text-xs font-bold text-gray-300 uppercase">MAD</span>
          </div>
        </div>
      </div>
      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('labor_hours_worked') }} *</label>
        <input
          type="number"
          step="0.5"
          v-model="form.hoursWorked"
          required
          class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
        >
      </div>
    </div>

    <div>
      <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('labor_activity') }}</label>
      <input
        type="text"
        v-model="form.activity"
        class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
        :placeholder="t('labor_activity')"
      >
    </div>

    <!-- Total & Actions -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 pt-4 border-t border-gray-100">
      <div class="flex items-center gap-2">
        <div class="p-1.5 bg-amber-50 rounded-lg">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-amber-600" viewBox="0 0 20 20" fill="currentColor">
            <path d="M8.433 7.418c.155-.103.346-.196.567-.267v1.698a2.305 2.305 0 01-.567-.267C8.07 8.34 8 8.114 8 8c0-.114.07-.34.433-.582zM11 12.849v-1.698c.22.071.412.164.567.267.364.243.433.468.433.582 0 .114-.07.34-.433.582a2.305 2.305 0 01-.567.267z" />
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-13a1 1 0 10-2 0v.092a4.535 4.535 0 00-1.676.662C6.602 6.234 6 7.009 6 8c0 .99.602 1.765 1.324 2.246.48.32 1.054.545 1.676.662v1.941c-.391-.127-.68-.317-.843-.504a1 1 0 10-1.51 1.31c.562.649 1.413 1.076 2.353 1.253V15a1 1 0 102 0v-.092a4.535 4.535 0 001.676-.662C13.398 13.766 14 12.991 14 12c0-.99-.602-1.765-1.324-2.246A4.535 4.535 0 0011 9.092V7.151c.391.127.68.317.843.504a1 1 0 101.511-1.31c-.563-.649-1.413-1.076-2.354-1.253V5z" clip-rule="evenodd" />
          </svg>
        </div>
        <span class="text-sm font-medium text-gray-500">{{ t('labor_total_cost') }}:</span>
        <span class="text-base font-extrabold text-gray-900 tabular-nums">{{ totalCost }} MAD</span>
      </div>
      <div class="flex gap-2">
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
    </div>
  </form>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import { useI18n } from '@/i18n'
import type { CreateLaborLogRequest } from '@/types'

const props = defineProps<{
  initialData?: Partial<CreateLaborLogRequest>
}>()

const { t } = useI18n()

const form = reactive<CreateLaborLogRequest>({
  workerName: props.initialData?.workerName || '',
  workerRole: props.initialData?.workerRole || '',
  hourlyRate: props.initialData?.hourlyRate || 0,
  hoursWorked: props.initialData?.hoursWorked || 0,
  workDate: props.initialData?.workDate || new Date().toISOString().split('T')[0],
  activity: props.initialData?.activity || '',
  notes: props.initialData?.notes || ''
})

const totalCost = computed(() => (form.hourlyRate * form.hoursWorked).toFixed(2))

const emit = defineEmits<{
  (e: 'submit', data: CreateLaborLogRequest): void
  (e: 'cancel'): void
}>()

const submit = () => emit('submit', { ...form })
</script>
