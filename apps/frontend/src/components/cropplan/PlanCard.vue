<template>
  <RouterLink
    :to="`/plans/${plan.id}`"
    class="block rounded-xl shadow hover:shadow-md transition-shadow bg-white overflow-hidden"
  >
    <!-- Status color strip -->
    <div :class="['h-2', statusColor]"></div>

    <div class="p-4 space-y-3">
      <!-- Plan name + year -->
      <div>
        <h3 class="font-semibold text-gray-900 text-base">
          {{ localized(plan.name, plan.nameAr, plan.nameEn) }}
        </h3>
        <p v-if="localizedDesc" class="text-sm text-gray-500 mt-0.5 line-clamp-2">
          {{ localizedDesc }}
        </p>
      </div>

      <!-- Badges row -->
      <div class="flex flex-wrap items-center gap-2 text-xs">
        <span :class="['px-2 py-0.5 rounded-full font-medium', seasonClass]">
          {{ t(`plan_season_${plan.season.toLowerCase()}`) }}
        </span>
        <span :class="['px-2 py-0.5 rounded-full font-medium', statusBadge]">
          {{ t(`plan_status_${plan.status.toLowerCase()}`) }}
        </span>
        <span class="text-gray-500">{{ plan.year }}</span>
      </div>

      <!-- Footer info -->
      <div class="flex items-center justify-between text-xs text-gray-400 pt-1 border-t border-gray-50">
        <span class="flex items-center gap-1">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
          {{ plan.plantingCount }} {{ t('plantings_title') }}
        </span>
      </div>
    </div>
  </RouterLink>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import { useI18n } from '@/i18n'
import type { CropPlan } from '@/types'

const props = defineProps<{ plan: CropPlan }>()

const { t } = useI18n()
const auth = useAuthStore()

function localized(base?: string, ar?: string, en?: string) {
  if (auth.user?.lang === 'AR' && ar) return ar
  if (auth.user?.lang === 'EN' && en) return en
  return base || ''
}

const localizedDesc = computed(() =>
  localized(props.plan.description, props.plan.descriptionAr, props.plan.descriptionEn)
)

const statusColorMap: Record<string, string> = {
  DRAFT: 'bg-gray-400',
  ACTIVE: 'bg-green-500',
  COMPLETED: 'bg-blue-500',
  ARCHIVED: 'bg-amber-500',
}

const statusColor = computed(() => statusColorMap[props.plan.status] || 'bg-gray-400')

const statusBadgeMap: Record<string, string> = {
  DRAFT: 'bg-gray-100 text-gray-600',
  ACTIVE: 'bg-green-100 text-green-700',
  COMPLETED: 'bg-blue-100 text-blue-700',
  ARCHIVED: 'bg-amber-100 text-amber-700',
}

const statusBadge = computed(() => statusBadgeMap[props.plan.status] || 'bg-gray-100 text-gray-600')

const seasonClassMap: Record<string, string> = {
  SPRING: 'bg-green-100 text-green-700',
  SUMMER: 'bg-orange-100 text-orange-700',
  AUTUMN: 'bg-amber-100 text-amber-700',
  WINTER: 'bg-blue-100 text-blue-700',
  YEAR_ROUND: 'bg-purple-100 text-purple-700',
}

const seasonClass = computed(() => seasonClassMap[props.plan.season] || 'bg-gray-100 text-gray-700')
</script>
