<template>
  <div class="text-center">
    <div :class="['text-xl', statusColor]">{{ label }}</div>
    <div :class="['text-sm font-semibold', statusColor]">{{ value }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  label: string
  value: string
  status: 'good' | 'warning' | 'danger' | 'neutral'
}>()

const statusColor = computed(() => {
  switch (props.status) {
    case 'good':
      return 'text-green-600'
    case 'warning':
      return 'text-orange-500'
    case 'danger':
      return 'text-red-600'
    default:
      return 'text-gray-500'
  }
})
</script>
