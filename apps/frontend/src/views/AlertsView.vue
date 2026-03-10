<template>
  <div>
    <div class="max-w-5xl mx-auto px-4 py-8">
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center gap-3">
          <h2 class="text-xl font-bold text-gray-800">{{ t('alerts.title') }}</h2>
          <span
            v-if="alertsStore.unreadCount > 0"
            class="inline-flex items-center justify-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-red-100 text-red-700"
          >
            {{ alertsStore.unreadCount }}
          </span>
        </div>
        <div class="flex items-center gap-2">
          <button
            @click="alertsStore.fetchAll()"
            class="flex items-center gap-2 border border-gray-300 text-gray-700 hover:bg-gray-50 px-4 py-2 rounded-lg text-sm font-medium"
            :disabled="alertsStore.loading"
          >
            &#8635; {{ t('common.refresh') }}
          </button>
          <button
            @click="showPreferences = true"
            class="flex items-center gap-2 border border-gray-300 text-gray-700 hover:bg-gray-50 px-4 py-2 rounded-lg text-sm font-medium"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            {{ t('alerts.preferences') }}
          </button>
        </div>
      </div>

      <!-- Filter buttons -->
      <div class="flex flex-wrap gap-2 mb-6">
        <button
          @click="filter = 'all'"
          :class="[
            'px-4 py-1.5 rounded-full text-sm font-medium transition-colors',
            filter === 'all'
              ? 'bg-green-700 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200',
          ]"
        >
          {{ t('alerts.all') }}
        </button>
        <button
          @click="filter = 'unread'"
          :class="[
            'px-4 py-1.5 rounded-full text-sm font-medium transition-colors',
            filter === 'unread'
              ? 'bg-green-700 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200',
          ]"
        >
          {{ t('alerts.unread') }}
          <span v-if="alertsStore.unreadCount > 0" class="ms-1">({{ alertsStore.unreadCount }})</span>
        </button>
        <button
          @click="filter = 'HIGH'"
          :class="[
            'px-4 py-1.5 rounded-full text-sm font-medium transition-colors',
            filter === 'HIGH'
              ? 'bg-red-600 text-white'
              : 'bg-red-50 text-red-700 hover:bg-red-100',
          ]"
        >
          HIGH
        </button>
        <button
          @click="filter = 'MEDIUM'"
          :class="[
            'px-4 py-1.5 rounded-full text-sm font-medium transition-colors',
            filter === 'MEDIUM'
              ? 'bg-amber-500 text-white'
              : 'bg-amber-50 text-amber-700 hover:bg-amber-100',
          ]"
        >
          MEDIUM
        </button>
        <button
          @click="filter = 'LOW'"
          :class="[
            'px-4 py-1.5 rounded-full text-sm font-medium transition-colors',
            filter === 'LOW'
              ? 'bg-blue-600 text-white'
              : 'bg-blue-50 text-blue-700 hover:bg-blue-100',
          ]"
        >
          LOW
        </button>
      </div>

      <!-- Loading -->
      <div v-if="alertsStore.loading" class="text-center py-16 text-gray-400">
        <div class="text-4xl mb-2">&#128276;</div>
        <p>{{ t('common.loading') }}</p>
      </div>

      <!-- Empty state -->
      <div v-else-if="filtered.length === 0" class="text-center py-16">
        <div class="text-6xl mb-4">&#127881;</div>
        <p class="text-gray-500 text-lg">{{ t('alerts.empty') }}</p>
      </div>

      <!-- Alert list -->
      <div v-else class="space-y-3">
        <div
          v-for="alert in filtered"
          :key="alert.id"
          :class="[
            'flex items-start gap-4 p-4 rounded-xl border transition-colors',
            alert.ackAt
              ? 'bg-gray-50 border-gray-100 opacity-60'
              : severityBg(alert.severity),
          ]"
        >
          <!-- Icon -->
          <span class="text-2xl flex-shrink-0 mt-0.5">{{ alertIcon(alert.type) }}</span>

          <!-- Content -->
          <div class="flex-1 min-w-0">
            <p class="font-medium text-gray-800">{{ alertMsg(alert) }}</p>
            <div class="flex flex-wrap items-center gap-2 mt-1.5">
              <span v-if="alert.plantName" class="text-xs text-gray-500">
                &#127793; {{ alert.plantName }}
              </span>
              <span class="text-xs text-gray-400">{{ timeAgo(alert.triggeredAt) }}</span>
              <span v-if="alert.waSent" class="text-xs text-green-600">&#9989; WhatsApp</span>
            </div>
          </div>

          <!-- Severity + Acknowledge -->
          <div class="flex-shrink-0 flex flex-col items-end gap-2">
            <span
              :class="[
                'text-xs font-bold px-2.5 py-0.5 rounded-full',
                severityChip(alert.severity),
              ]"
            >
              {{ alert.severity }}
            </span>
            <button
              v-if="!alert.ackAt"
              @click="alertsStore.acknowledge(alert.id)"
              class="text-xs text-gray-400 hover:text-green-700 transition-colors flex items-center gap-1"
            >
              &#10003; {{ t('alerts.acknowledge') }}
            </button>
          </div>
        </div>
      </div>

      <!-- Stats footer -->
      <div v-if="alertsStore.alerts.length > 0" class="mt-8 pt-6 border-t border-gray-200">
        <div class="grid grid-cols-3 gap-4 text-sm text-center">
          <div>
            <span class="font-semibold text-gray-800">{{ alertsStore.alerts.length }}</span>
            <span class="text-gray-500 ms-1">{{ t('alerts.all') }}</span>
          </div>
          <div>
            <span class="font-semibold text-red-600">{{ alertsStore.unreadCount }}</span>
            <span class="text-gray-500 ms-1">{{ t('alerts.unread') }}</span>
          </div>
          <div>
            <span class="font-semibold text-green-600">{{ alertsStore.alerts.length - alertsStore.unreadCount }}</span>
            <span class="text-gray-500 ms-1">{{ t('alerts.acknowledge') }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Preferences modal -->
    <AlertPreferences
      v-if="showPreferences"
      @close="showPreferences = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAlertsStore } from '@/stores/alerts.store'
import { useAuthStore } from '@/stores/auth.store'
import { useI18n } from '@/i18n'
import AlertPreferences from '@/components/alerts/AlertPreferences.vue'
import type { Alert } from '@/types'

const { t } = useI18n()
const alertsStore = useAlertsStore()
const auth = useAuthStore()

const filter = ref<'all' | 'unread' | 'HIGH' | 'MEDIUM' | 'LOW'>('all')
const showPreferences = ref(false)

const filtered = computed(() => {
  let list = alertsStore.alerts
  if (filter.value === 'unread') {
    list = list.filter(a => !a.ackAt)
  } else if (filter.value === 'HIGH' || filter.value === 'MEDIUM' || filter.value === 'LOW') {
    list = list.filter(a => a.severity === filter.value)
  }
  return list
})

onMounted(async () => {
  await alertsStore.fetchAll()
})

function alertMsg(alert: Alert) {
  const lang = auth.lang
  if (lang === 'AR' && alert.msgAr) return alert.msgAr
  if (lang === 'EN' && alert.msgEn) return alert.msgEn
  return alert.msgFr ?? alert.type
}

function alertIcon(type: Alert['type']) {
  const map: Record<string, string> = {
    SOIL_DRY: '\uD83D\uDCA7',
    SOIL_WET: '\uD83C\uDF0A',
    TEMP_HIGH: '\uD83C\uDF21\uFE0F',
    TEMP_LOW: '\uD83E\uDD76',
    LIGHT_LOW: '\u2600\uFE0F',
    DEVICE_OFFLINE: '\uD83D\uDCF5',
  }
  return map[type] ?? '\u26A0\uFE0F'
}

function severityBg(s: Alert['severity']) {
  return s === 'HIGH'
    ? 'bg-red-50 border-red-200'
    : s === 'MEDIUM'
      ? 'bg-amber-50 border-amber-200'
      : 'bg-blue-50 border-blue-200'
}

function severityChip(s: Alert['severity']) {
  return s === 'HIGH'
    ? 'bg-red-100 text-red-700'
    : s === 'MEDIUM'
      ? 'bg-amber-100 text-amber-700'
      : 'bg-blue-100 text-blue-700'
}

function timeAgo(iso: string) {
  const diff = Math.floor((Date.now() - new Date(iso).getTime()) / 1000)
  if (diff < 60) return `${diff}s`
  if (diff < 3600) return `${Math.floor(diff / 60)}m`
  if (diff < 86400) return `${Math.floor(diff / 3600)}h`
  return `${Math.floor(diff / 86400)}d`
}
</script>
