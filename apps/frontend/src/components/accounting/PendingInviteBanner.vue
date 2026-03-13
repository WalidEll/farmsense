<template>
  <div
    v-if="invites.length > 0"
    class="relative overflow-hidden bg-gradient-to-r from-blue-600 to-indigo-600 p-5 rounded-2xl text-white shadow-lg shadow-blue-200/50"
  >
    <!-- Decorative circle -->
    <div class="absolute -top-8 -right-8 w-32 h-32 rounded-full bg-white/10"></div>

    <div class="relative flex items-center justify-between gap-4">
      <div class="flex items-center gap-4 min-w-0">
        <div class="p-2.5 bg-white/15 rounded-xl flex-shrink-0 backdrop-blur-sm">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
          </svg>
        </div>
        <div class="min-w-0">
          <p class="font-bold text-sm">{{ t('team_pending_invite') }}</p>
          <p class="text-xs text-blue-100 truncate mt-0.5">
            {{ t('team_invite') }} — {{ t('team_invite_desc') }}
          </p>
        </div>
      </div>
      <button
        @click="$emit('accept', invites[0].id)"
        class="px-5 py-2 bg-white text-blue-700 rounded-xl font-bold text-sm hover:bg-blue-50 active:scale-[0.98] transition-all flex-shrink-0 shadow-sm"
      >
        {{ t('team_accept') }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/i18n'
import type { TeamMember } from '@/types'

defineProps<{
  invites: TeamMember[]
}>()

defineEmits<{
  (e: 'accept', memberId: string): void
}>()

const { t } = useI18n()
</script>
