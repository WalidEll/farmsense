<template>
  <div class="h-80 relative">
    <Bar :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js'
import { useI18n } from '@/i18n'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

const props = defineProps<{
  income: number
  expenses: number
}>()

const { t } = useI18n()

const chartData = computed(() => ({
  labels: [t('dashboard_income_vs_expense')],
  datasets: [
    {
      label: t('transaction_income'),
      data: [props.income],
      backgroundColor: 'rgba(16, 185, 129, 0.8)',
      borderRadius: 8,
    },
    {
      label: t('transaction_expense'),
      data: [props.expenses],
      backgroundColor: 'rgba(239, 68, 68, 0.8)',
      borderRadius: 8,
    }
  ]
}))

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: (value: any) => `${value} DH`
      }
    }
  },
  plugins: {
    legend: {
      position: 'bottom' as const,
    }
  }
}
</script>
