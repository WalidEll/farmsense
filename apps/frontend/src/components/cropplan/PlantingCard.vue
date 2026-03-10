<template>
  <div class="bg-white border border-gray-200 rounded-xl p-4 space-y-3">
    <!-- Header row -->
    <div class="flex items-start justify-between gap-3">
      <div class="min-w-0">
        <h4 class="font-semibold text-gray-900 text-sm truncate">{{ planting.cropName }}</h4>
        <p v-if="planting.farmLocationName" class="text-xs text-gray-500 mt-0.5 flex items-center gap-1">
          <svg class="w-3 h-3 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          {{ planting.farmLocationName }}
        </p>
      </div>
      <span :class="['px-2 py-0.5 rounded-full text-xs font-medium whitespace-nowrap shrink-0', statusBadge]">
        {{ t(`planting_status_${planting.status.toLowerCase()}`) }}
      </span>
    </div>

    <!-- Dates row -->
    <div class="flex flex-wrap gap-3 text-xs text-gray-500">
      <span v-if="planting.plannedSowDate" class="flex items-center gap-1">
        <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        {{ t('planting_sow_date') }}: {{ formatDate(planting.plannedSowDate) }}
      </span>
      <span v-if="planting.plannedHarvestDate" class="flex items-center gap-1">
        <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        {{ t('planting_harvest_date') }}: {{ formatDate(planting.plannedHarvestDate) }}
      </span>
    </div>

    <!-- Quantity / Area -->
    <div class="flex flex-wrap gap-4 text-xs text-gray-500">
      <span v-if="planting.quantity">
        {{ t('planting_quantity') }}: {{ planting.quantity }}
      </span>
      <span v-if="planting.areaM2">
        {{ t('planting_area') }}: {{ planting.areaM2 }} m2
      </span>
    </div>

    <!-- Yield info (if harvested) -->
    <div v-if="planting.yieldAmount" class="text-xs text-green-700 bg-green-50 rounded-lg px-3 py-1.5">
      {{ planting.yieldAmount }} {{ planting.yieldUnit }}
      <span v-if="planting.qualityRating"> - {{ planting.qualityRating }}/5</span>
    </div>

    <!-- Notes -->
    <p v-if="planting.notes" class="text-xs text-gray-400 italic line-clamp-2">{{ planting.notes }}</p>

    <!-- Action buttons -->
    <div class="flex items-center gap-2 pt-1 border-t border-gray-50">
      <button
        @click="$emit('edit', planting)"
        class="text-xs text-gray-500 hover:text-green-600 transition-colors"
      >
        {{ t('common_edit') }}
      </button>
      <button
        @click="$emit('delete', planting)"
        class="text-xs text-gray-500 hover:text-red-600 transition-colors"
      >
        {{ t('delete') }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from '@/i18n'
import type { PlantingItem } from '@/types'

const props = defineProps<{ planting: PlantingItem }>()
defineEmits<{ edit: [planting: PlantingItem]; delete: [planting: PlantingItem] }>()

const { t } = useI18n()

const statusBadgeMap: Record<string, string> = {
  PLANNED: 'bg-gray-100 text-gray-600',
  SOWN: 'bg-blue-100 text-blue-700',
  TRANSPLANTED: 'bg-indigo-100 text-indigo-700',
  GROWING: 'bg-green-100 text-green-700',
  HARVESTING: 'bg-amber-100 text-amber-700',
  COMPLETED: 'bg-teal-100 text-teal-700',
  FAILED: 'bg-red-100 text-red-700',
}

const statusBadge = computed(() => statusBadgeMap[props.planting.status] || 'bg-gray-100 text-gray-600')

function formatDate(d: string) {
  return new Date(d).toLocaleDateString()
}
</script>
