<template>
  <nav class="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-40">
    <div class="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
      <!-- Logo -->
      <RouterLink to="/" class="flex items-center gap-2 font-bold text-green-800">
        <span class="text-xl">🌱</span>
        <span class="hidden sm:inline">FarmSense</span>
      </RouterLink>

      <!-- Center nav links -->
      <div class="hidden md:flex items-center gap-6 mx-auto">
        <RouterLink
          to="/"
          class="text-sm text-gray-600 hover:text-green-700 font-medium"
          active-class="text-green-700"
        >
          🏠 Dashboard
        </RouterLink>
        <RouterLink
          to="/sensors"
          class="text-sm text-gray-600 hover:text-green-700 font-medium"
          active-class="text-green-700"
        >
          📡 Sensors
        </RouterLink>
      </div>

      <!-- Right actions -->
      <div class="flex items-center gap-3">
        <LanguageSwitcher />

        <!-- Alerts bell -->
        <RouterLink to="/" class="relative text-gray-600 hover:text-green-700 p-1">
          🔔
          <span
            v-if="alertsStore.unreadCount > 0"
            class="absolute -top-0.5 -right-0.5 bg-red-500 text-white text-[10px] font-bold rounded-full w-4 h-4 flex items-center justify-center"
          >
            {{ alertsStore.unreadCount > 9 ? '9+' : alertsStore.unreadCount }}
          </span>
        </RouterLink>

        <!-- User + logout -->
        <div class="flex items-center gap-2">
          <span class="text-sm text-gray-600 hidden sm:inline">{{ auth.user?.name }}</span>
          <button
            @click="logout"
            class="text-xs text-gray-500 hover:text-red-600 border border-gray-200 rounded px-2 py-1"
          >
            {{ t('auth.logout') }}
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { useAlertsStore } from '@/stores/alerts.store'
import { useI18n } from '@/i18n'
import LanguageSwitcher from './LanguageSwitcher.vue'

const { t } = useI18n()
const auth = useAuthStore()
const alertsStore = useAlertsStore()
const router = useRouter()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>
