<template>
  <div
    class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 cursor-pointer hover:shadow-md hover:border-green-200 transition-all group"
    @click="$emit('click')"
  >
    <!-- Header -->
    <div class="flex items-start justify-between mb-3">
      <div class="flex items-center gap-2">
        <span class="text-2xl">{{ plantEmoji }}</span>
        <div>
          <h3 class="font-semibold text-gray-800 group-hover:text-green-700">{{ plant.name }}</h3>
          <p v-if="plant.species" class="text-xs text-gray-400">{{ plant.species }}</p>
        </div>
      </div>
      <button
        @click.stop="$emit('delete')"
        class="text-gray-300 hover:text-red-400 text-lg leading-none opacity-0 group-hover:opacity-100"
        title="Delete"
      >
        ×
      </button>
    </div>

    <!-- Sensor values -->
    <div class="grid grid-cols-2 gap-2" v-if="reading">
      <div class="bg-blue-50 rounded-lg p-2 text-center">
        <div class="text-xs text-blue-400 mb-0.5">💧 {{ t('sensors.soil') }}</div>
        <div :class="['font-semibold text-sm', soilStatus]">
          {{ reading.soilMoisture != null ? reading.soilMoisture + '%' : '—' }}
        </div>
      </div>
      <div class="bg-orange-50 rounded-lg p-2 text-center">
        <div class="text-xs text-orange-400 mb-0.5">🌡️ {{ t('sensors.temp') }}</div>
        <div :class="['font-semibold text-sm', tempStatus]">
          {{ reading.temperature != null ? reading.temperature.toFixed(1) + '°C' : '—' }}
        </div>
      </div>
      <div class="bg-cyan-50 rounded-lg p-2 text-center">
        <div class="text-xs text-cyan-400 mb-0.5">💦 {{ t('sensors.humidity') }}</div>
        <div class="font-semibold text-sm text-cyan-700">
          {{ reading.humidity != null ? reading.humidity + '%' : '—' }}
        </div>
      </div>
      <div class="bg-yellow-50 rounded-lg p-2 text-center">
        <div class="text-xs text-yellow-500 mb-0.5">☀️ {{ t('sensors.light') }}</div>
        <div :class="['font-semibold text-sm', lightStatus]">
          {{ reading.lightLux != null ? Math.round(reading.lightLux) + ' lx' : '—' }}
        </div>
      </div>
    </div>

    <!-- No data yet -->
    <div v-else class="text-center py-4 text-gray-300 text-sm">
      {{ t('sensors.noData') }}
    </div>

    <!-- Watering status badge -->
    <div v-if="wateringStatus" class="mt-2 text-xs flex items-center gap-1" :class="wateringStatus.color">
      💧 {{ wateringStatus.text }}
    </div>

    <!-- Location badge -->
    <div v-if="plant.location" class="mt-1 text-xs text-gray-400">
      📍 {{ plant.location }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useReadingsStore } from '@/stores/readings.store'
import { useI18n } from '@/i18n'
import type { Plant } from '@/types'

const props = defineProps<{ plant: Plant }>()
defineEmits<{ click: []; delete: [] }>()

const { t } = useI18n()
const readingsStore = useReadingsStore()

const reading = computed(() => readingsStore.latest[props.plant.id])

const plantEmoji = computed(() => {
  const s = props.plant.species?.toLowerCase() ?? ''
  if (s.includes('cactus') || s.includes('succulent')) return '🌵'
  if (s.includes('rose')) return '🌹'
  if (s.includes('tulip')) return '🌷'
  if (s.includes('herb') || s.includes('mint') || s.includes('basil')) return '🌿'
  return '🪴'
})

const soilStatus = computed(() => {
  const v = reading.value?.soilMoisture
  if (v == null) return 'text-gray-400'
  if (v < props.plant.soilMin) return 'text-red-600'
  if (v > props.plant.soilMax) return 'text-blue-600'
  return 'text-green-600'
})

const tempStatus = computed(() => {
  const v = reading.value?.temperature
  if (v == null) return 'text-gray-400'
  if (v < props.plant.tempMin || v > props.plant.tempMax) return 'text-red-600'
  return 'text-orange-600'
})

const lightStatus = computed(() => {
  const v = reading.value?.lightLux
  if (v == null) return 'text-gray-400'
  if (v < props.plant.lightMin) return 'text-red-600'
  return 'text-yellow-600'
})

const wateringStatus = computed(() => {
  const interval = props.plant.wateringIntervalDays
  const lastWatered = props.plant.lastWateredAt
  if (!interval || !lastWatered) return null

  const days = Math.floor(
    (Date.now() - new Date(lastWatered).getTime()) / (1000 * 60 * 60 * 24)
  )
  const remaining = interval - days

  if (remaining < 0) return { text: t('care_overdue'), color: 'text-red-500' }
  if (remaining === 0) return { text: t('care_due_today'), color: 'text-orange-500' }
  if (remaining === 1) return { text: t('care_due_tomorrow'), color: 'text-orange-400' }
  return { text: t('care_due_in').replace('{n}', String(remaining)), color: 'text-green-500' }
})
</script>
