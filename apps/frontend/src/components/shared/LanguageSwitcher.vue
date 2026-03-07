<template>
  <div class="flex gap-1">
    <button
      v-for="l in langs"
      :key="l.value"
      @click="setLang(l.value)"
      :class="[
        'px-2 py-1 rounded text-xs font-medium',
        current === l.value
          ? 'bg-green-700 text-white'
          : 'bg-gray-100 text-gray-600 hover:bg-green-100',
      ]"
    >
      {{ l.label }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import type { Lang } from '@/types'

const auth = useAuthStore()
const current = computed(() => auth.lang)

const langs = [
  { value: 'FR' as Lang, label: 'FR' },
  { value: 'AR' as Lang, label: 'ع' },
  { value: 'DARIJA' as Lang, label: 'DR' },
]

function setLang(l: Lang) {
  if (auth.user) auth.user.lang = l
}
</script>
