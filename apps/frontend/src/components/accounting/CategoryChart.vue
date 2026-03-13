<template>
  <div class="h-64 relative">
    <Doughnut v-if="data.length > 0" :data="chartData" :options="chartOptions" />
    <div v-else class="flex items-center justify-center h-full text-gray-400 text-sm italic">
      {{ t('tags_empty') }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Doughnut } from 'vue-chartjs'
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { useI18n } from '@/i18n'
import type { CategorySummary } from '@/types'

ChartJS.register(ArcElement, Tooltip, Legend)

const props = defineProps<{
  data: CategorySummary[]
  title: string
}>()

const { t } = useI18n()

const chartData = computed(() => ({
  labels: props.data.map(d => d.category),
  datasets: [
    {
      data: props.data.map(d => d.total),
      backgroundColor: [
        '#3b82f6', '#ef4444', '#10b981', '#f59e0b', '#8b5cf6', 
        '#06b6d4', '#ec4899', '#f97316', '#64748b', '#a855f7'
      ],
      borderWidth: 1,
    }
  ]
}))

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'right' as const,
      labels: {
        boxWidth: 12,
        font: { size: 11 }
      }
    }
  }
}
</script>
