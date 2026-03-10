<template>
  <div :class="[
    'flex items-center gap-1 text-[10px] font-bold px-2 py-1 rounded-md transition-colors',
    colorClass
  ]">
    <span>{{ icon }}</span>
    <span>{{ label }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  value?: number | null
  type: 'moisture' | 'temp'
}>()

const icon = computed(() => {
  if (props.value == null) return '—'
  return props.type === 'moisture' ? '💧' : '🌡️'
})

const label = computed(() => {
  if (props.value == null) return '—'
  return props.type === 'temp' ? props.value.toFixed(1) + '°' : props.value + '%'
})

const colorClass = computed(() => {
  if (props.value == null) return 'text-gray-300 bg-gray-50'
  
  if (props.type === 'moisture') {
    if (props.value < 30) return 'text-red-600 bg-red-50'
    if (props.value > 80) return 'text-blue-600 bg-blue-50'
    return 'text-green-600 bg-green-50'
  } else {
    // Temp
    if (props.value > 35) return 'text-red-600 bg-red-50'
    if (props.value < 5) return 'text-blue-600 bg-blue-50'
    return 'text-orange-600 bg-orange-50'
  }
})
</script>