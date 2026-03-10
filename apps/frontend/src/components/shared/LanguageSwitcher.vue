<template>
  <div class="relative group">
    <select
      :value="current"
      @change="handleChange"
      class="appearance-none bg-gray-50 border border-gray-200 text-gray-700 text-xs rounded-lg focus:ring-green-500 focus:border-green-500 block w-full px-2.5 py-1.5 pr-8 cursor-pointer hover:bg-white transition-colors font-medium"
    >
      <option v-for="l in langs" :key="l.value" :value="l.value">
        {{ l.label }}
      </option>
    </select>
    <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none text-gray-400">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
      </svg>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import type { Lang } from '@/types'

const auth = useAuthStore()
const current = computed(() => auth.lang)

const langs = [
  { value: 'FR' as Lang, label: 'Français' },
  { value: 'EN' as Lang, label: 'English' },
  { value: 'AR' as Lang, label: 'العربية' },
]

function handleChange(event: Event) {
  const target = event.target as HTMLSelectElement
  const l = target.value as Lang
  if (auth.user) {
    auth.user.lang = l
    // Note: In a real app, you might also want to persist this to the backend
    localStorage.setItem('user', JSON.stringify(auth.user))
  }
}
</script>