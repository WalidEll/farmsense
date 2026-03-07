<template>
  <div class="bg-white rounded-xl shadow-sm p-6">
    <h3 class="font-semibold text-gray-700 mb-4">{{ t('care_schedule') }}</h3>

    <!-- Loading -->
    <div v-if="loading" class="space-y-3">
      <div v-for="i in 3" :key="i" class="h-14 bg-gray-100 rounded-lg animate-pulse" />
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-center py-4 text-red-400 text-sm">
      {{ t('common_error') }}
    </div>

    <!-- Schedule rows -->
    <div v-else-if="schedule" class="space-y-3">
      <!-- Watering -->
      <div class="flex items-center justify-between bg-gray-50 rounded-lg px-4 py-3">
        <div class="flex items-center gap-3">
          <span class="text-xl">💧</span>
          <div>
            <div class="font-medium text-gray-700 text-sm">{{ t('care_water') }}</div>
            <div class="text-xs text-gray-400" v-if="schedule.watering.lastDoneAt">{{ lastDoneText(schedule.watering) }}</div>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <span class="px-2 py-0.5 rounded-full text-xs font-medium" :class="statusColor(schedule.watering)">
            {{ statusText(schedule.watering) }}
          </span>
          <button @click="markDone('WATER')" class="px-2.5 py-1 rounded-lg text-xs font-medium bg-green-100 text-green-700 hover:bg-green-200 transition-colors" :title="t('care_mark_done')">
            ✓ {{ t('care_done') }}
          </button>
        </div>
      </div>

      <!-- Fertilising -->
      <div class="flex items-center justify-between bg-gray-50 rounded-lg px-4 py-3">
        <div class="flex items-center gap-3">
          <span class="text-xl">🧪</span>
          <div>
            <div class="font-medium text-gray-700 text-sm">{{ t('care_fertilise') }}</div>
            <div class="text-xs text-gray-400" v-if="schedule.fertilising.lastDoneAt">{{ lastDoneText(schedule.fertilising) }}</div>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <span class="px-2 py-0.5 rounded-full text-xs font-medium" :class="statusColor(schedule.fertilising)">
            {{ statusText(schedule.fertilising) }}
          </span>
          <button @click="markDone('FERTILISE')" class="px-2.5 py-1 rounded-lg text-xs font-medium bg-green-100 text-green-700 hover:bg-green-200 transition-colors" :title="t('care_mark_done')">
            ✓ {{ t('care_done') }}
          </button>
        </div>
      </div>

      <!-- Repotting -->
      <div class="flex items-center justify-between bg-gray-50 rounded-lg px-4 py-3">
        <div class="flex items-center gap-3">
          <span class="text-xl">🪴</span>
          <div>
            <div class="font-medium text-gray-700 text-sm">{{ t('care_repot') }}</div>
            <div class="text-xs text-gray-400" v-if="schedule.repotting.lastDoneAt">{{ lastDoneText(schedule.repotting) }}</div>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <span class="px-2 py-0.5 rounded-full text-xs font-medium" :class="statusColor(schedule.repotting)">
            {{ statusText(schedule.repotting) }}
          </span>
          <button @click="markDone('REPOT')" class="px-2.5 py-1 rounded-lg text-xs font-medium bg-green-100 text-green-700 hover:bg-green-200 transition-colors" :title="t('care_mark_done')">
            ✓ {{ t('care_done') }}
          </button>
        </div>
      </div>

      <!-- Care history toggle -->
      <div class="pt-3 border-t border-gray-100">
        <button
          @click="showHistory = !showHistory"
          class="text-sm text-gray-500 hover:text-green-700 flex items-center gap-1"
        >
          <span>{{ t('care_history') }}</span>
          <span class="text-xs">{{ showHistory ? '▲' : '▼' }}</span>
        </button>

        <div v-if="showHistory" class="mt-3 space-y-2">
          <div v-if="!schedule.recentLog.length" class="text-sm text-gray-400">
            {{ t('care_no_history') }}
          </div>
          <div
            v-for="entry in schedule.recentLog"
            :key="entry.id"
            class="flex items-center justify-between text-sm bg-gray-50 rounded-lg px-3 py-2"
          >
            <div class="flex items-center gap-2">
              <span>{{ taskIcon(entry.taskType) }}</span>
              <span class="text-gray-700">{{ taskLabel(entry.taskType) }}</span>
            </div>
            <span class="text-gray-400 text-xs">{{ formatRelative(entry.doneAt) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from '@/i18n'
import api from '@/services/api'
import type { CareSchedule, CareTask } from '@/types'

const props = defineProps<{ plantId: string }>()

const { t } = useI18n()

const schedule = ref<CareSchedule | null>(null)
const loading = ref(true)
const error = ref(false)
const showHistory = ref(false)

async function fetchSchedule() {
  try {
    schedule.value = await api.get<CareSchedule>(`/plants/${props.plantId}/care`)
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}

async function markDone(taskType: string) {
  try {
    schedule.value = await api.post<CareSchedule>(`/plants/${props.plantId}/care/${taskType}/done`)
  } catch {
    // silent fail
  }
}

function statusText(task: CareTask): string {
  if (task.overdue) return t('care_overdue')
  if (task.daysRemaining === 0) return t('care_due_today')
  if (task.daysRemaining === 1) return t('care_due_tomorrow')
  return t('care_due_in').replace('{n}', String(task.daysRemaining))
}

function statusColor(task: CareTask): string {
  if (task.overdue || task.daysRemaining <= 0) return 'bg-red-100 text-red-700'
  if (task.daysRemaining === 1) return 'bg-orange-100 text-orange-700'
  if (task.daysRemaining <= 3) return 'bg-yellow-100 text-yellow-700'
  return 'bg-green-100 text-green-700'
}

function lastDoneText(task: CareTask): string {
  if (!task.lastDoneAt) return ''
  const days = Math.floor(
    (Date.now() - new Date(task.lastDoneAt).getTime()) / (1000 * 60 * 60 * 24)
  )
  return t('care_last_done').replace('{n}', String(days))
}

function taskIcon(type: string): string {
  switch (type) {
    case 'WATER': return '💧'
    case 'FERTILISE': return '🧪'
    case 'REPOT': return '🪴'
    default: return '📋'
  }
}

function taskLabel(type: string): string {
  switch (type) {
    case 'WATER': return t('care_water')
    case 'FERTILISE': return t('care_fertilise')
    case 'REPOT': return t('care_repot')
    default: return type
  }
}

function formatRelative(iso: string): string {
  const days = Math.floor((Date.now() - new Date(iso).getTime()) / (1000 * 60 * 60 * 24))
  if (days === 0) return t('care_due_today')
  return t('care_last_done').replace('{n}', String(days))
}

onMounted(fetchSchedule)
</script>
