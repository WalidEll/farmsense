<template>
  <div v-if="readings.length === 0" class="text-center py-8 text-gray-300 text-sm">
    {{ t('sensors.noHistory') }}
  </div>
  <div v-else>
    <!-- Manual reading legend -->
    <div v-if="hasManualReadings" class="flex items-center gap-2 mb-2 justify-end">
      <span class="inline-flex items-center gap-1 text-xs text-purple-600 bg-purple-50 px-2 py-0.5 rounded-full">
        <span class="w-2 h-2 bg-purple-500 rounded-sm rotate-45 inline-block"></span>
        {{ t('manual_indicator') }}
      </span>
    </div>
    <div class="relative h-64">
      <Line :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js'
import { useI18n } from '@/i18n'
import type { SensorReading } from '@/types'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, Filler)

const props = defineProps<{ readings: SensorReading[]; hours?: number }>()

const { t } = useI18n()

const hasManualReadings = computed(() => props.readings.some(r => r.source === 'MANUAL'))

const labels = computed(() =>
  props.readings.map(r => {
    const d = new Date(r.recordedAt)
    return props.hours && props.hours <= 48
      ? d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      : d.toLocaleDateString([], { month: 'short', day: 'numeric' })
  })
)

// Manual readings get larger points to distinguish them visually
const manualPointRadii = computed(() =>
  props.readings.map(r => r.source === 'MANUAL' ? 6 : 2)
)

const manualPointStyles = computed(() =>
  props.readings.map(r => r.source === 'MANUAL' ? 'rectRot' as const : 'circle' as const)
)

const chartData = computed(() => ({
  labels: labels.value,
  datasets: [
    {
      label: t('sensors.soil') + ' (%)',
      data: props.readings.map(r => r.soilMoisture),
      borderColor: '#3b82f6',
      backgroundColor: 'rgba(59,130,246,0.08)',
      fill: true,
      tension: 0.4,
      pointRadius: manualPointRadii.value,
      pointStyle: manualPointStyles.value,
      pointBackgroundColor: props.readings.map(r => r.source === 'MANUAL' ? '#8b5cf6' : '#3b82f6'),
    },
    {
      label: t('sensors.temp') + ' (°C)',
      data: props.readings.map(r => r.temperature),
      borderColor: '#f97316',
      backgroundColor: 'rgba(249,115,22,0.08)',
      fill: true,
      tension: 0.4,
      pointRadius: manualPointRadii.value,
      pointStyle: manualPointStyles.value,
      pointBackgroundColor: props.readings.map(r => r.source === 'MANUAL' ? '#8b5cf6' : '#f97316'),
    },
    {
      label: t('sensors.humidity') + ' (%)',
      data: props.readings.map(r => r.humidity),
      borderColor: '#06b6d4',
      backgroundColor: 'rgba(6,182,212,0.08)',
      fill: true,
      tension: 0.4,
      pointRadius: manualPointRadii.value,
      pointStyle: manualPointStyles.value,
      pointBackgroundColor: props.readings.map(r => r.source === 'MANUAL' ? '#8b5cf6' : '#06b6d4'),
    },
  ],
}))

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'top' as const, labels: { boxWidth: 12, font: { size: 11 } } },
    tooltip: {
      mode: 'index' as const,
      intersect: false,
      callbacks: {
        afterTitle(items: any[]) {
          if (!items.length) return ''
          const idx = items[0].dataIndex
          const reading = props.readings[idx]
          return reading?.source === 'MANUAL' ? `✏️ ${t('manual_indicator')}` : ''
        },
      },
    },
  },
  scales: {
    x: { ticks: { font: { size: 10 }, maxTicksLimit: 8 } },
    y: { ticks: { font: { size: 10 } } },
  },
}
</script>
