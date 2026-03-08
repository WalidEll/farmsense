<template>
  <!-- Mobile backdrop -->
  <div
    v-if="open"
    class="fixed inset-0 bg-black/40 z-40 lg:hidden"
    @click="$emit('close')"
  ></div>

  <!-- Sidebar -->
  <aside
    :class="[
      'fixed top-0 bottom-0 z-50 w-60 bg-white border-e border-gray-200 flex flex-col transition-transform duration-200 ease-in-out',
      open ? 'translate-x-0' : '-translate-x-full lg:translate-x-0',
    ]"
  >
    <!-- Logo -->
    <div class="h-14 flex items-center gap-2 px-5 border-b border-gray-100 shrink-0">
      <span class="text-xl">🌱</span>
      <span class="font-bold text-green-800 text-lg">FarmSense</span>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 py-4 px-3 space-y-1 overflow-y-auto">
      <RouterLink
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        :class="[
          'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors',
          isActive(item.to)
            ? 'bg-green-50 text-green-700'
            : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900',
        ]"
        @click="$emit('close')"
      >
        <span class="text-lg w-6 text-center">{{ item.icon }}</span>
        <span>{{ t(item.label) }}</span>
        <!-- Alerts badge -->
        <span
          v-if="item.badge && item.badge > 0"
          class="ms-auto bg-red-500 text-white text-[10px] font-bold rounded-full w-5 h-5 flex items-center justify-center"
        >
          {{ item.badge > 9 ? '9+' : item.badge }}
        </span>
      </RouterLink>
    </nav>

    <!-- Bottom section -->
    <div class="border-t border-gray-100 p-4 space-y-3 shrink-0">
      <!-- Language switcher -->
      <LanguageSwitcher />

      <!-- User + logout -->
      <div class="flex items-center justify-between">
        <span class="text-sm text-gray-600 truncate max-w-[140px]">{{ auth.user?.name }}</span>
        <button
          @click="logout"
          class="text-xs text-gray-500 hover:text-red-600 border border-gray-200 rounded px-2 py-1 transition-colors"
        >
          {{ t('auth_logout') }}
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { useAlertsStore } from '@/stores/alerts.store'
import { useI18n } from '@/i18n'
import LanguageSwitcher from './LanguageSwitcher.vue'

defineProps<{ open: boolean }>()
defineEmits<{ close: [] }>()

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const alertsStore = useAlertsStore()

const navItems = computed(() => [
  { icon: '🏠', label: 'nav_dashboard', to: '/' },
  { icon: '📡', label: 'nav_devices', to: '/sensors' },
  { icon: '🔔', label: 'nav_alerts', to: '/alerts', badge: alertsStore.unreadCount },
  { icon: '⚙️', label: 'nav_settings', to: '/settings' },
])

function isActive(to: string) {
  if (to === '/') return route.path === '/'
  return route.path.startsWith(to)
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>
