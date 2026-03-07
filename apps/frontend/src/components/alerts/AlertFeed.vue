<template>
  <div class="space-y-2">
    <div v-if="filtered.length === 0" class="text-center py-6 text-gray-300 text-sm">
      {{ t('alerts.empty') }}
    </div>
    <div
      v-for="alert in filtered"
      :key="alert.id"
      :class="[
        'flex items-start gap-3 p-3 rounded-xl border text-sm',
        alert.ackAt ? 'bg-gray-50 border-gray-100 opacity-60' : severityBg(alert.severity),
      ]"
    >
      <span class="text-xl flex-shrink-0">{{ alertIcon(alert.type) }}</span>
      <div class="flex-1 min-w-0">
        <p class="font-medium text-gray-800">{{ alertMsg(alert) }}</p>
        <p class="text-xs text-gray-400 mt-0.5">
          {{ alert.plantName }} · {{ timeAgo(alert.triggeredAt) }}
        </p>
      </div>
      <div class="flex-shrink-0">
        <span :class="['text-xs font-bold px-2 py-0.5 rounded-full', severityChip(alert.severity)]">
          {{ alert.severity }}
        </span>
        <button
          v-if="!alert.ackAt"
          @click="acknowledge(alert.id)"
          class="block mt-1 text-xs text-gray-400 hover:text-green-700"
        >
          ✓ ack
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAlertsStore } from '@/stores/alerts.store'
import { useAuthStore } from '@/stores/auth.store'
import { useI18n } from '@/i18n'
import type { Alert } from '@/types'

const props = defineProps<{ plantId?: string; limit?: number }>()

const { t } = useI18n()
const alertsStore = useAlertsStore()
const auth = useAuthStore()

const filtered = computed(() => {
  let list = alertsStore.alerts
  if (props.plantId) list = list.filter(a => a.plantId === props.plantId)
  return props.limit ? list.slice(0, props.limit) : list
})

function acknowledge(id: string) {
  alertsStore.acknowledge(id)
}

function alertMsg(alert: Alert) {
  const lang = auth.lang
  if (lang === 'AR' && alert.msgAr) return alert.msgAr
  if (lang === 'DARIJA' && alert.msgDarija) return alert.msgDarija
  return alert.msgFr ?? alert.type
}

function alertIcon(type: Alert['type']) {
  const map: Record<string, string> = {
    SOIL_DRY: '💧', SOIL_WET: '🌊', TEMP_HIGH: '🌡️',
    TEMP_LOW: '🥶', LIGHT_LOW: '☀️', DEVICE_OFFLINE: '📵',
  }
  return map[type] ?? '⚠️'
}

function severityBg(s: Alert['severity']) {
  return s === 'HIGH' ? 'bg-red-50 border-red-200' : s === 'MEDIUM' ? 'bg-amber-50 border-amber-200' : 'bg-blue-50 border-blue-200'
}

function severityChip(s: Alert['severity']) {
  return s === 'HIGH' ? 'bg-red-100 text-red-700' : s === 'MEDIUM' ? 'bg-amber-100 text-amber-700' : 'bg-blue-100 text-blue-700'
}

function timeAgo(iso: string) {
  const diff = Math.floor((Date.now() - new Date(iso).getTime()) / 1000)
  if (diff < 60) return `${diff}s`
  if (diff < 3600) return `${Math.floor(diff / 60)}m`
  if (diff < 86400) return `${Math.floor(diff / 3600)}h`
  return `${Math.floor(diff / 86400)}d`
}
</script>
