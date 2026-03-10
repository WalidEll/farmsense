<template>
  <div class="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow">
    <!-- Type color strip -->
    <div :class="['h-1.5', typeColor]"></div>

    <div class="p-4 space-y-3">
      <!-- Name + type badge -->
      <div class="flex items-start justify-between gap-2">
        <div class="min-w-0">
          <h4 class="font-semibold text-gray-900 text-sm truncate">
            {{ localized(location.name, location.nameAr, location.nameEn) }}
          </h4>
          <p v-if="localizedDesc" class="text-xs text-gray-500 mt-0.5 line-clamp-2">
            {{ localizedDesc }}
          </p>
        </div>
        <span :class="['px-2 py-0.5 rounded-full text-xs font-medium whitespace-nowrap shrink-0', typeBadge]">
          {{ t(`location_type_${location.locationType.toLowerCase()}`) }}
        </span>
      </div>

      <!-- Area -->
      <div v-if="location.areaM2" class="flex items-center gap-1.5 text-xs text-gray-500">
        <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6z" />
        </svg>
        {{ t('location_area') }}: {{ location.areaM2 }} m2
      </div>

      <!-- Actions -->
      <div class="flex items-center gap-2 pt-1 border-t border-gray-50">
        <button
          @click="$emit('edit', location)"
          class="text-xs text-gray-500 hover:text-green-600 transition-colors"
        >
          {{ t('common_edit') }}
        </button>
        <button
          @click="$emit('delete', location)"
          class="text-xs text-gray-500 hover:text-red-600 transition-colors"
        >
          {{ t('delete') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import { useI18n } from '@/i18n'
import type { FarmLocation } from '@/types'

const props = defineProps<{ location: FarmLocation }>()
defineEmits<{ edit: [location: FarmLocation]; delete: [location: FarmLocation] }>()

const { t } = useI18n()
const auth = useAuthStore()

function localized(base?: string, ar?: string, en?: string) {
  if (auth.user?.lang === 'AR' && ar) return ar
  if (auth.user?.lang === 'EN' && en) return en
  return base || ''
}

const localizedDesc = computed(() =>
  localized(props.location.description, props.location.descriptionAr, props.location.descriptionEn)
)

const typeColorMap: Record<string, string> = {
  BED: 'bg-green-500',
  POT: 'bg-orange-500',
  ROW: 'bg-blue-500',
  GREENHOUSE: 'bg-teal-500',
  FIELD: 'bg-amber-500',
  INDOOR: 'bg-purple-500',
  OTHER: 'bg-gray-500',
}

const typeColor = computed(() => typeColorMap[props.location.locationType] || 'bg-gray-500')

const typeBadgeMap: Record<string, string> = {
  BED: 'bg-green-100 text-green-700',
  POT: 'bg-orange-100 text-orange-700',
  ROW: 'bg-blue-100 text-blue-700',
  GREENHOUSE: 'bg-teal-100 text-teal-700',
  FIELD: 'bg-amber-100 text-amber-700',
  INDOOR: 'bg-purple-100 text-purple-700',
  OTHER: 'bg-gray-100 text-gray-700',
}

const typeBadge = computed(() => typeBadgeMap[props.location.locationType] || 'bg-gray-100 text-gray-700')
</script>
