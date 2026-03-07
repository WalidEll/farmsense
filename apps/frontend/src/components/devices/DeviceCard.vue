<template>
  <div
    @click="$emit('click')"
    :class="[
      'p-5 rounded-xl border-2 shadow-sm hover:shadow-lg transition-all cursor-pointer',
      device.online ? 'border-green-200 bg-white hover:border-green-300' : 'border-gray-200 bg-gray-50 hover:border-gray-300'
    ]"
  >
    <!-- Header: Status + Label -->
    <div class="flex items-start justify-between mb-4">
      <div class="flex-1">
        <h3 class="font-semibold text-gray-800">{{ device.label || device.deviceId }}</h3>
        <p class="text-xs text-gray-500 font-mono mt-0.5">{{ device.deviceId }}</p>
      </div>
      <span :class="['text-lg', device.online ? 'text-green-600' : 'text-gray-400']">
        {{ device.online ? '🟢' : '🔴' }}
      </span>
    </div>

    <!-- Last seen -->
    <p class="text-xs text-gray-400 mb-3">
      {{ device.lastSeenAt ? formatLastSeen(device.lastSeenAt) : 'Never' }}
    </p>

    <!-- Sensor values grid -->
    <div class="grid grid-cols-2 gap-2 mb-4 pb-3 border-b border-gray-100">
      <SensorValue label="💧" value="—" :status="'neutral'" />
      <SensorValue label="🌡️" value="—" :status="'neutral'" />
      <SensorValue label="💨" value="—" :status="'neutral'" />
      <SensorValue label="☀️" value="—" :status="'neutral'" />
    </div>

    <!-- Plant assignment -->
    <div v-if="device.plantName" class="text-sm text-gray-600">
      <span class="inline-block bg-green-50 border border-green-200 rounded px-2 py-1 text-xs font-medium">
        → {{ device.plantName }}
      </span>
    </div>
    <div v-else class="text-xs text-gray-400">
      Not assigned to a plant
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineEmits, defineProps } from 'vue'
import type { Device } from '@/types'

defineProps<{
  device: Device
}>()

defineEmits<{
  click: []
}>()

function formatLastSeen(timestamp: string): string {
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (minutes < 1) return 'Just now'
  if (minutes < 60) return `${minutes} min ago`
  if (hours < 24) return `${hours}h ago`
  return `${days}d ago`
}
</script>

<script lang="ts">
// Import sensor value display component
import SensorValue from './SensorValue.vue'
export default {
  components: { SensorValue }
}
</script>
