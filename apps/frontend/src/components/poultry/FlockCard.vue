<template>
  <div
    class="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 cursor-pointer hover:shadow-md hover:border-orange-200 transition-all group flex flex-col gap-4"
    @click="$emit('click')"
  >
    <!-- Header -->
    <div class="flex justify-between items-start">
      <div class="flex items-center gap-3">
        <div class="w-12 h-12 bg-orange-50 text-orange-600 rounded-xl flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
          {{ flock.purpose === 'LAYERS' ? '🥚' : '🍗' }}
        </div>
        <div>
          <h3 class="font-bold text-gray-900 group-hover:text-orange-700 transition-colors">
            {{ flock.name }}
          </h3>
          <p class="text-xs text-gray-500">{{ flock.breed || t('common.optional') }}</p>
        </div>
      </div>
      <div class="flex flex-col items-end gap-1">
        <span :class="['text-[10px] font-bold px-2 py-0.5 rounded-md uppercase tracking-wider', statusClass]">
          {{ t(`flock_status_${flock.status.toLowerCase()}`) }}
        </span>
        <span class="text-[9px] font-medium text-gray-400 uppercase tracking-widest">
          {{ t(`flock_purpose_${flock.purpose.toLowerCase()}`) }}
        </span>
      </div>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-2 gap-3 bg-gray-50/50 p-3 rounded-xl border border-gray-100">
      <div class="flex flex-col">
        <span class="text-[10px] font-bold text-gray-400 uppercase tracking-tight">{{ t('flock_bird_count') }}</span>
        <span class="font-bold text-gray-700">{{ flock.birdCount }}</span>
      </div>
      <div class="flex flex-col">
        <span class="text-[10px] font-bold text-gray-400 uppercase tracking-tight">{{ t('flock_current_birds') }}</span>
        <span class="font-bold text-orange-600">{{ flock.currentBirdCount }}</span>
      </div>
    </div>

    <!-- Info -->
    <div class="flex items-center justify-between mt-auto pt-2 border-t border-gray-50">
      <div class="flex items-center gap-1.5 text-xs text-gray-500">
        <span>📅</span>
        <span>{{ flock.startDate ? new Date(flock.startDate).toLocaleDateString() : '---' }}</span>
      </div>
      <div v-if="flock.supplierName" class="flex items-center gap-1.5 text-xs text-gray-500">
        <span>📦</span>
        <span class="truncate max-w-[80px]">{{ flock.supplierName }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from '@/i18n'
import type { Flock } from '@/types'

const props = defineProps<{ flock: Flock }>()
defineEmits<{ click: [] }>()

const { t } = useI18n()

const statusClass = computed(() => {
  switch (props.flock.status) {
    case 'ACTIVE': return 'bg-green-100 text-green-700'
    case 'SOLD': return 'bg-blue-100 text-blue-700'
    case 'FINISHED': return 'bg-gray-100 text-gray-700'
    default: return 'bg-gray-100 text-gray-700'
  }
})
</script>
