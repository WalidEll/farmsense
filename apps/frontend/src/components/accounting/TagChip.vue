<template>
  <span
    class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium border"
    :style="{
      backgroundColor: tag.color + '1A',
      borderColor: tag.color + '4D',
      color: tag.color
    }"
  >
    {{ tag.name }}
    <button
      v-if="removable"
      @click.stop="$emit('remove')"
      class="hover:text-gray-900 transition-colors"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" viewBox="0 0 20 20" fill="currentColor">
        <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
      </svg>
    </button>
  </span>
</template>

<script setup lang="ts">
import type { Tag } from '@/types'

defineProps<{
  tag: { id: string; name: string; color: string }
  removable?: boolean
}>()

defineEmits<{
  (e: 'remove'): void
}>()
</script>
