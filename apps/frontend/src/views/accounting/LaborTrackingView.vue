<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <div>
        <h1 class="text-2xl font-extrabold text-gray-900 tracking-tight">{{ t('labor_title') }}</h1>
        <p class="text-sm text-gray-400 mt-0.5 font-medium">{{ laborLogs.length }} {{ t('labor_entries') || 'entrees' }}</p>
      </div>
      <button
        v-if="!showForm"
        @click="showForm = true"
        class="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 active:scale-[0.98] transition-all duration-200 font-semibold text-sm shadow-sm shadow-emerald-200"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
        </svg>
        {{ t('common_new') }}
      </button>
    </div>

    <!-- Monthly Summary KPIs -->
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <div class="relative overflow-hidden bg-gradient-to-br from-sky-50 to-blue-50 p-5 rounded-2xl border border-sky-100/60 group">
        <div class="absolute -top-4 -right-4 w-20 h-20 rounded-full bg-sky-500 opacity-[0.06]"></div>
        <div class="relative flex items-center gap-4">
          <div class="p-2.5 bg-sky-100 rounded-xl text-sky-600 shadow-sm">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <p class="text-xs font-semibold text-sky-800/60 uppercase tracking-widest">{{ t('labor_total_hours') }}</p>
            <p class="text-2xl font-extrabold text-sky-900 tracking-tight mt-0.5">{{ totalHours }}<span class="text-sm font-semibold ml-1 opacity-50">h</span></p>
          </div>
        </div>
      </div>
      <div class="relative overflow-hidden bg-gradient-to-br from-emerald-50 to-green-50 p-5 rounded-2xl border border-emerald-100/60 group">
        <div class="absolute -top-4 -right-4 w-20 h-20 rounded-full bg-emerald-500 opacity-[0.06]"></div>
        <div class="relative flex items-center gap-4">
          <div class="p-2.5 bg-emerald-100 rounded-xl text-emerald-600 shadow-sm">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 6v12m-3-2.818l.879.659c1.171.879 3.07.879 4.242 0 1.172-.879 1.172-2.303 0-3.182C13.536 12.219 12.768 12 12 12c-.725 0-1.45-.22-2.003-.659-1.106-.879-1.106-2.303 0-3.182s2.9-.879 4.006 0l.415.33M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <p class="text-xs font-semibold text-emerald-800/60 uppercase tracking-widest">{{ t('labor_total_cost') }}</p>
            <p class="text-2xl font-extrabold text-emerald-900 tracking-tight mt-0.5">{{ totalCost }}<span class="text-sm font-semibold ml-1 opacity-50">DH</span></p>
          </div>
        </div>
      </div>
    </div>

    <!-- Form (animated entry) -->
    <LaborLogForm
      v-if="showForm"
      @submit="onSubmit"
      @cancel="showForm = false"
    />

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-16">
      <div class="flex flex-col items-center gap-3">
        <div class="animate-spin rounded-full h-10 w-10 border-[3px] border-gray-200 border-t-emerald-500"></div>
        <p class="text-sm text-gray-400 font-medium">{{ t('loading') }}...</p>
      </div>
    </div>

    <!-- Table -->
    <div v-else class="bg-white rounded-2xl shadow-sm border border-gray-100/80 overflow-hidden">
      <LaborLogTable :logs="laborLogs" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useI18n } from '@/i18n'
import { useLaborStore } from '@/stores/labor.store'
import { storeToRefs } from 'pinia'
import LaborLogForm from '@/components/accounting/LaborLogForm.vue'
import LaborLogTable from '@/components/accounting/LaborLogTable.vue'
import type { CreateLaborLogRequest } from '@/types'

const { t } = useI18n()
const store = useLaborStore()
const { laborLogs, loading } = storeToRefs(store)

const showForm = ref(false)

const totalHours = computed(() => laborLogs.value.reduce((acc, log) => acc + log.hoursWorked, 0))
const totalCost = computed(() => laborLogs.value.reduce((acc, log) => acc + (log.hoursWorked * log.hourlyRate), 0).toFixed(2))

onMounted(() => {
  store.fetchLaborLogs()
})

const onSubmit = async (data: CreateLaborLogRequest) => {
  await store.createLaborLog(data)
  showForm.value = false
}
</script>
