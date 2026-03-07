<template>
  <div class="bg-white rounded-xl border border-gray-100 shadow-sm p-4 text-center">
    <div class="text-2xl mb-1">{{ icon }}</div>
    <div class="text-xs text-gray-400 mb-1">{{ label }}</div>
    <div v-if="value != null" :class="['text-xl font-bold', statusColor]">
      {{ formatted }}
    </div>
    <div v-else class="text-xl font-bold text-gray-300">—</div>
    <div v-if="statusText" class="text-xs mt-1 font-medium" :class="statusColor">
      {{ statusText }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from '@/i18n'

const props = defineProps<{
  icon: string
  label: string
  value?: number | null
  unit?: string
  min?: number
  max?: number
}>()

const { t } = useI18n()

const formatted = computed(() => {
  if (props.value == null) return '—'
  const num = Number(props.value)
  return (Number.isInteger(num) ? num : num.toFixed(1)) + (props.unit ?? '')
})

const statusColor = computed(() => {
  const v = props.value
  if (v == null) return 'text-gray-400'
  if (props.min != null && v < props.min) return 'text-red-600'
  if (props.max != null && v > props.max) return 'text-red-600'
  return 'text-green-700'
})

const statusText = computed(() => {
  const v = props.value
  if (v == null || (props.min == null && props.max == null)) return ''
  if (props.min != null && v < props.min) return t('sensors.low')
  if (props.max != null && v > props.max) return t('sensors.high')
  return t('sensors.ok')
})
</script>
