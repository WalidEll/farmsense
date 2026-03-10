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
  min?: number
  max?: number
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
  
  const min = props.min ?? (props.type === 'moisture' ? 30 : 5)
  const max = props.max ?? (props.type === 'moisture' ? 80 : 35)

  if (props.value < min) return 'text-red-600 bg-red-50'
  if (props.value > max) return 'text-blue-600 bg-blue-50'
  
  // Orange: within 10% of limits (as per US-011)
  const range = max - min
  const warningThreshold = range * 0.1
  if (props.value < min + warningThreshold || props.value > max - warningThreshold) {
    return props.type === 'temp' ? 'text-orange-600 bg-orange-50' : 'text-amber-600 bg-amber-50'
  }

  return 'text-green-600 bg-green-50'
})
</script>