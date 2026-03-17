<template>
  <div class="space-y-4">
    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-8">
      <div class="animate-spin text-3xl">🔄</div>
    </div>

    <!-- Empty -->
    <div v-else-if="!events.length" class="text-center py-10 space-y-2">
      <div class="text-5xl">📋</div>
      <p class="text-gray-500 font-medium">{{ t('mortality_event_empty') }}</p>
    </div>

    <!-- Table -->
    <div v-else class="overflow-x-auto rounded-2xl border border-gray-100">
      <table class="w-full text-sm">
        <thead>
          <tr class="bg-gray-50 text-left">
            <th class="px-4 py-3 font-bold text-gray-500 text-xs uppercase tracking-wider">{{ t('mortality_event_date') }}</th>
            <th class="px-4 py-3 font-bold text-gray-500 text-xs uppercase tracking-wider">{{ t('mortality_event_type') }}</th>
            <th class="px-4 py-3 font-bold text-gray-500 text-xs uppercase tracking-wider">{{ t('mortality_event_count') }}</th>
            <th class="px-4 py-3 font-bold text-gray-500 text-xs uppercase tracking-wider">{{ t('mortality_event_cause') }}</th>
            <th class="px-4 py-3 font-bold text-gray-500 text-xs uppercase tracking-wider hidden sm:table-cell">{{ t('plan_description') }}</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-for="ev in events" :key="ev.id" class="bg-white hover:bg-gray-50/50 transition-colors">
            <td class="px-4 py-3 text-gray-700 whitespace-nowrap">
              {{ new Date(ev.mortalityDate).toLocaleDateString() }}
            </td>
            <td class="px-4 py-3">
              <span :class="['text-xs font-bold px-2 py-0.5 rounded-md uppercase tracking-wide', typeClass(ev.type)]">
                {{ t(`mortality_type_${ev.type.toLowerCase()}`) }}
              </span>
            </td>
            <td class="px-4 py-3 font-bold text-red-600">{{ ev.count }}</td>
            <td class="px-4 py-3 text-gray-600">
              {{ ev.cause ? t(`mortality_cause_${ev.cause.toLowerCase()}`) : '—' }}
            </td>
            <td class="px-4 py-3 text-gray-500 hidden sm:table-cell max-w-[200px] truncate">
              {{ ev.notes || '—' }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/i18n'
import type { MortalityEvent, MortalityType } from '@/types'

defineProps<{
  events: MortalityEvent[]
  loading?: boolean
}>()

const { t } = useI18n()

function typeClass(type: MortalityType) {
  return type === 'CULL'
    ? 'bg-amber-100 text-amber-700'
    : 'bg-red-100 text-red-700'
}
</script>
