<template>
  <div>
    <div class="max-w-5xl mx-auto px-4 py-8 space-y-6">
      <!-- Header -->
      <h2 class="text-xl font-bold text-gray-800">{{ t('settings.title') }}</h2>

      <!-- Language Section -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h3 class="text-base font-semibold text-gray-800 mb-1">
          Language / &#x627;&#x644;&#x644;&#x63A;&#x629; / Langue
        </h3>
        <p class="text-sm text-gray-500 mb-4">{{ t('settings.language') }}</p>
        <div class="flex gap-3">
          <button
            v-for="l in langs"
            :key="l.value"
            @click="setLang(l.value)"
            :class="[
              'px-6 py-3 rounded-xl text-sm font-semibold transition-all',
              current === l.value
                ? 'bg-green-700 text-white shadow-md ring-2 ring-green-700/30'
                : 'bg-gray-100 text-gray-600 hover:bg-green-50 hover:text-green-700',
            ]"
          >
            {{ l.label }}
          </button>
        </div>
      </div>

      <!-- Profile Section -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h3 class="text-base font-semibold text-gray-800 mb-1">{{ t('settings.profile') }}</h3>
        <p class="text-sm text-gray-500 mb-4">{{ t('settings.profileSubtitle') }}</p>
        <div class="space-y-3">
          <div class="flex items-center gap-3 text-sm">
            <span class="w-24 text-gray-500 shrink-0">{{ t('settings.name') }}</span>
            <span class="text-gray-800 font-medium">{{ auth.user?.name ?? '---' }}</span>
          </div>
          <div class="flex items-center gap-3 text-sm">
            <span class="w-24 text-gray-500 shrink-0">{{ t('settings.email') }}</span>
            <span class="text-gray-800 font-medium">{{ auth.user?.email ?? '---' }}</span>
          </div>
          <div class="flex items-center gap-3 text-sm">
            <span class="w-24 text-gray-500 shrink-0">{{ t('settings.whatsapp') }}</span>
            <span class="text-gray-800 font-medium" dir="ltr">{{ auth.user?.phoneWa ?? '---' }}</span>
          </div>
        </div>
      </div>

      <!-- Devices Overview Section -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h3 class="text-base font-semibold text-gray-800 mb-4">{{ t('settings.devicesOverview') }}</h3>

        <!-- Loading state -->
        <div v-if="devicesStore.loading" class="text-sm text-gray-400">
          {{ t('common.loading') }}
        </div>

        <template v-else>
          <div class="flex items-center gap-6 mb-4">
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-green-500 inline-block"></span>
              <span class="text-sm text-gray-700">
                {{ t('settings.devicesConnected', { n: devicesStore.devices.length }) }}
              </span>
            </div>
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-blue-500 inline-block"></span>
              <span class="text-sm text-gray-700">
                {{ t('settings.devicesOnline', { n: devicesStore.onlineCount }) }}
              </span>
            </div>
          </div>

          <router-link
            to="/sensors"
            class="inline-flex items-center gap-2 bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          >
            {{ t('settings.goToDevices') }}
          </router-link>
        </template>
      </div>

      <!-- About Section -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h3 class="text-base font-semibold text-gray-800 mb-4">{{ t('settings.about') }}</h3>
        <div class="space-y-2 text-sm">
          <div class="flex items-center gap-3">
            <span class="text-gray-500">{{ t('settings.version') }}</span>
            <span class="text-gray-800 font-medium">FarmSense v0</span>
          </div>
          <p class="text-gray-500">{{ t('settings.description') }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import { useDevicesStore } from '@/stores/devices.store'
import { useI18n } from '@/i18n'
import type { Lang } from '@/types'

const { t } = useI18n()
const auth = useAuthStore()
const devicesStore = useDevicesStore()

const current = computed(() => auth.lang)

const langs = [
  { value: 'FR' as Lang, label: 'FR' },
  { value: 'AR' as Lang, label: '\u0639' },
  { value: 'EN' as Lang, label: 'EN' },
]

function setLang(l: Lang) {
  if (auth.user) auth.user.lang = l
}

onMounted(async () => {
  if (devicesStore.devices.length === 0) {
    await devicesStore.fetchAll(true)
  }
})
</script>
