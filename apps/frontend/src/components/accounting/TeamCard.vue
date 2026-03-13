<template>
  <div class="bg-white rounded-2xl shadow-sm border border-gray-100/80 overflow-hidden hover:shadow-md transition-shadow duration-300">
    <div class="h-1.5 bg-gradient-to-r from-emerald-400 via-emerald-500 to-teal-500"></div>
    <div class="p-6">
      <div class="flex justify-between items-start mb-5">
        <div class="min-w-0">
          <h3 class="text-xl font-extrabold text-gray-900 tracking-tight truncate">{{ team.name }}</h3>
          <p v-if="team.description" class="text-sm text-gray-400 mt-1 line-clamp-2 leading-relaxed">{{ team.description }}</p>
        </div>
        <div class="p-2.5 bg-emerald-50 rounded-xl flex-shrink-0 ml-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M18 18.72a9.094 9.094 0 003.741-.479 3 3 0 00-4.682-2.72m.94 3.198l.001.031c0 .225-.012.447-.037.666A11.944 11.944 0 0112 21c-2.17 0-4.207-.576-5.963-1.584A6.062 6.062 0 016 18.719m12 0a5.971 5.971 0 00-.941-3.197m0 0A5.995 5.995 0 0012 12.75a5.995 5.995 0 00-5.058 2.772m0 0a3 3 0 00-4.681 2.72 8.986 8.986 0 003.74.477m.94-3.197a5.971 5.971 0 00-.94 3.197M15 6.75a3 3 0 11-6 0 3 3 0 016 0zm6 3a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0zm-13.5 0a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z" />
          </svg>
        </div>
      </div>

      <div class="flex items-center gap-2 text-sm text-gray-400 mb-6">
        <div class="h-6 w-6 rounded-lg bg-violet-100 flex items-center justify-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5 text-violet-600" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
          </svg>
        </div>
        <span class="font-medium">{{ t('team_role_owner') }}:</span>
        <span class="font-semibold text-gray-700">{{ team.ownerName }}</span>
      </div>

      <div class="flex gap-3">
        <button
          @click="$emit('invite')"
          class="flex-1 inline-flex items-center justify-center gap-2 px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 active:scale-[0.98] transition-all font-semibold text-sm"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
            <path d="M8 9a3 3 0 100-6 3 3 0 000 6zM8 11a6 6 0 016 6H2a6 6 0 016-6zM16 7a1 1 0 10-2 0v1h-1a1 1 0 100 2h1v1a1 1 0 102 0v-1h1a1 1 0 100-2h-1V7z" />
          </svg>
          {{ t('team_invite') }}
        </button>
        <button
          @click="$emit('manage')"
          class="px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 transition-colors font-semibold text-sm"
        >
          {{ t('common_edit') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/i18n'
import type { Team } from '@/types'

defineProps<{
  team: Team
}>()

defineEmits<{
  (e: 'invite'): void
  (e: 'manage'): void
}>()

const { t } = useI18n()
</script>
