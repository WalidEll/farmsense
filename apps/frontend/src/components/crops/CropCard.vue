<template>
  <RouterLink
    :to="`/crops/${crop.id}`"
    class="block rounded-xl shadow hover:shadow-md transition-shadow bg-white overflow-hidden"
  >
    <!-- Category color strip -->
    <div :class="['h-2', categoryColor]"></div>

    <div class="p-4 space-y-3">
      <!-- Crop name -->
      <div>
        <h3 class="font-semibold text-gray-900 text-base">
          {{ localized(crop.name, crop.nameAr, crop.nameDarija) }}
        </h3>
        <p v-if="crop.scientificName" class="text-sm italic text-gray-400 mt-0.5">
          {{ crop.scientificName }}
        </p>
      </div>

      <!-- Bottom row: difficulty + season + days -->
      <div class="flex flex-wrap items-center gap-2 text-xs">
        <span
          v-if="crop.difficulty"
          :class="[
            'px-2 py-0.5 rounded-full font-medium',
            difficultyClass,
          ]"
        >
          {{ t(`crop_difficulty_${crop.difficulty.toLowerCase()}`) }}
        </span>
        <span v-if="crop.growingSeason" class="text-gray-500 flex items-center gap-1">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          {{ crop.growingSeason }}
        </span>
        <span v-if="crop.daysToHarvest" class="text-gray-500 flex items-center gap-1">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          {{ crop.daysToHarvest }} {{ t('crops_daysToHarvest') }}
        </span>
      </div>
    </div>
  </RouterLink>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import { useI18n } from '@/i18n'
import type { Crop } from '@/types'

const props = defineProps<{ crop: Crop }>()

const { t } = useI18n()
const auth = useAuthStore()

function localized(base?: string, ar?: string, darija?: string) {
  if (auth.user?.lang === 'AR' && ar) return ar
  if (auth.user?.lang === 'DARIJA' && darija) return darija
  return base || ''
}

const categoryColorMap: Record<string, string> = {
  VEGETABLE: 'bg-green-500',
  FRUIT: 'bg-orange-500',
  HERB: 'bg-purple-500',
  GRAIN: 'bg-amber-500',
  LEGUME: 'bg-teal-500',
  OTHER: 'bg-gray-500',
}

const categoryColor = computed(() => categoryColorMap[props.crop.category] || 'bg-gray-500')

const difficultyClass = computed(() => {
  switch (props.crop.difficulty) {
    case 'EASY': return 'bg-green-100 text-green-700'
    case 'MEDIUM': return 'bg-yellow-100 text-yellow-700'
    case 'HARD': return 'bg-red-100 text-red-700'
    default: return 'bg-gray-100 text-gray-700'
  }
})
</script>
