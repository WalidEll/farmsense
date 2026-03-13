<template>
  <div class="bg-white rounded-2xl shadow-sm border border-gray-100/80 overflow-hidden hover:shadow-md transition-shadow duration-300">
    <table class="min-w-full">
      <thead>
        <tr class="border-b border-gray-100">
          <th class="px-6 py-3.5 text-left text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('labor_work_date') }}</th>
          <th class="px-6 py-3.5 text-left text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('labor_worker_name') }}</th>
          <th class="px-6 py-3.5 text-left text-[11px] font-bold text-gray-400 uppercase tracking-wider hidden sm:table-cell">{{ t('labor_activity') }}</th>
          <th class="px-6 py-3.5 text-left text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('labor_hours_worked') }}</th>
          <th class="px-6 py-3.5 text-right text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('labor_total_cost') }}</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-50">
        <tr
          v-for="log in logs"
          :key="log.id"
          class="group hover:bg-gray-50/50 transition-colors"
        >
          <td class="px-6 py-4 whitespace-nowrap">
            <span class="text-sm font-medium text-gray-700">
              {{ new Date(log.workDate).toLocaleDateString('fr-MA', { day: 'numeric', month: 'short' }) }}
            </span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex items-center gap-3">
              <div class="h-8 w-8 rounded-lg bg-amber-50 flex items-center justify-center text-amber-700 font-bold text-[10px] uppercase flex-shrink-0">
                {{ log.workerName.substring(0, 2) }}
              </div>
              <div class="min-w-0">
                <p class="text-sm font-semibold text-gray-900 truncate">{{ log.workerName }}</p>
                <p v-if="log.workerRole" class="text-[11px] text-gray-400 truncate">{{ log.workerRole }}</p>
              </div>
            </div>
          </td>
          <td class="px-6 py-4 whitespace-nowrap hidden sm:table-cell">
            <span class="text-sm text-gray-500">{{ log.activity || '—' }}</span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="text-sm text-gray-700">
              <span class="font-semibold">{{ log.hoursWorked }}h</span>
              <span class="text-gray-400 text-xs ml-1">@ {{ log.hourlyRate }} MAD</span>
            </div>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-right">
            <span class="text-sm font-extrabold text-gray-900 tabular-nums">
              {{ formatCurrency(log.hoursWorked * log.hourlyRate) }}
            </span>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Empty State -->
    <div v-if="logs.length === 0" class="py-16 flex flex-col items-center gap-3">
      <div class="w-14 h-14 rounded-2xl bg-gray-50 flex items-center justify-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M20.25 14.15v4.25c0 1.094-.787 2.036-1.872 2.18-2.087.277-4.216.42-6.378.42s-4.291-.143-6.378-.42c-1.085-.144-1.872-1.086-1.872-2.18v-4.25m16.5 0a2.18 2.18 0 00.75-1.661V8.706c0-1.081-.768-2.015-1.837-2.175a48.114 48.114 0 00-3.413-.387m4.5 8.006c-.194.165-.42.295-.673.38A23.978 23.978 0 0112 15.75c-2.648 0-5.195-.429-7.577-1.22a2.016 2.016 0 01-.673-.38m0 0A2.18 2.18 0 013 12.489V8.706c0-1.081.768-2.015 1.837-2.175a48.111 48.111 0 013.413-.387m7.5 0V5.25A2.25 2.25 0 0013.5 3h-3a2.25 2.25 0 00-2.25 2.25v.894m7.5 0a48.667 48.667 0 00-7.5 0M12 12.75h.008v.008H12v-.008z" />
        </svg>
      </div>
      <p class="text-sm text-gray-400 font-medium">{{ t('tags_empty') }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/i18n'
import type { LaborLog } from '@/types'

defineProps<{
  logs: LaborLog[]
}>()

const { t } = useI18n()

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD', maximumFractionDigits: 0 }).format(value)
}
</script>
