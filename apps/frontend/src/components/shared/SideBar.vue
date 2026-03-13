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
      'fixed top-0 bottom-0 z-50 w-64 bg-white border-e border-gray-100 flex flex-col transition-transform duration-300 ease-[cubic-bezier(0.25,1,0.5,1)] shadow-xl lg:shadow-none',
      open ? 'translate-x-0' : '-translate-x-full lg:translate-x-0',
    ]"
  >
    <!-- Logo -->
    <div class="h-16 flex items-center gap-3 px-6 border-b border-gray-50 shrink-0">
      <div class="bg-green-100 p-2 rounded-xl text-xl">🌱</div>
      <div class="flex flex-col">
        <span class="font-bold text-gray-900 text-lg leading-tight">FarmSense</span>
        <span class="text-[10px] font-medium text-green-600 uppercase tracking-wider">Mazraati v2.0</span>
      </div>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 overflow-y-auto py-6 px-4 space-y-8 scrollbar-thin">
      <!-- Section: Farm Management -->
      <div class="space-y-1">
        <div class="px-3 mb-2 text-xs font-bold text-gray-400 uppercase tracking-wider">
          {{ t('nav.farm_management') }}
        </div>
        <RouterLink
          to="/"
          class="nav-item"
          :class="{ 'active': isActive('/') }"
          @click="$emit('close')"
        >
          <span class="icon">🏠</span>
          <span class="label">{{ t('nav.dashboard') }}</span>
        </RouterLink>
        <RouterLink
          to="/map"
          class="nav-item group"
          :class="{ 'active': isActive('/map') }"
          @click="$emit('close')"
        >
          <span class="icon">🗺️</span>
          <span class="label">{{ t('nav.map_view') }}</span>
          <span class="badge-new">{{ t('common.new') }}</span>
        </RouterLink>
        <RouterLink
          to="/plans"
          class="nav-item"
          :class="{ 'active': isActive('/plans') }"
          @click="$emit('close')"
        >
          <span class="icon">🌾</span>
          <span class="label">{{ t('nav.crop_plan') }}</span>
        </RouterLink>
        <RouterLink
          to="/crops"
          class="nav-item"
          :class="{ 'active': isActive('/crops') }"
          @click="$emit('close')"
        >
          <span class="icon">📚</span>
          <span class="label">{{ t('nav_crops') }}</span>
        </RouterLink>
      </div>

      <!-- Section: Comptabilité -->
      <div class="space-y-1">
        <div class="px-3 mb-2 text-xs font-bold text-gray-400 uppercase tracking-wider">
          {{ t('nav.accounting') }}
        </div>
        <RouterLink
          to="/accounting"
          class="nav-item"
          :class="{ 'active': isActive('/accounting') }"
          @click="$emit('close')"
        >
          <span class="icon">💰</span>
          <span class="label">{{ t('nav.accounting_dashboard') }}</span>
        </RouterLink>
        <RouterLink
          to="/accounting/transactions"
          class="nav-item"
          :class="{ 'active': isActive('/accounting/transactions') }"
          @click="$emit('close')"
        >
          <span class="icon">📑</span>
          <span class="label">{{ t('nav.transactions') }}</span>
        </RouterLink>
        <RouterLink
          to="/accounting/receipts"
          class="nav-item"
          :class="{ 'active': isActive('/accounting/receipts') }"
          @click="$emit('close')"
        >
          <span class="icon">📸</span>
          <span class="label">{{ t('nav.receipts') }}</span>
        </RouterLink>
        <RouterLink
          to="/accounting/labor"
          class="nav-item"
          :class="{ 'active': isActive('/accounting/labor') }"
          @click="$emit('close')"
        >
          <span class="icon">👷</span>
          <span class="label">{{ t('nav.labor') }}</span>
        </RouterLink>
        <RouterLink
          to="/accounting/tags"
          class="nav-item"
          :class="{ 'active': isActive('/accounting/tags') }"
          @click="$emit('close')"
        >
          <span class="icon">🏷️</span>
          <span class="label">{{ t('nav.tags') }}</span>
        </RouterLink>
        <RouterLink
          to="/accounting/team"
          class="nav-item"
          :class="{ 'active': isActive('/accounting/team') }"
          @click="$emit('close')"
        >
          <span class="icon">🤝</span>
          <span class="label">{{ t('nav.team') }}</span>
        </RouterLink>
      </div>

      <!-- Section: Aviculture -->
      <div class="space-y-1">
        <div class="px-3 mb-2 text-xs font-bold text-gray-400 uppercase tracking-wider">
          {{ t('nav.poultry') }}
        </div>
        <RouterLink
          to="/poultry"
          class="nav-item"
          :class="{ 'active': isActive('/poultry') }"
          @click="$emit('close')"
        >
          <span class="icon">🐔</span>
          <span class="label">{{ t('nav.poultry_dashboard') }}</span>
        </RouterLink>
        <RouterLink
          to="/poultry/flocks"
          class="nav-item"
          :class="{ 'active': isActive('/poultry/flocks') }"
          @click="$emit('close')"
        >
          <span class="icon">🐣</span>
          <span class="label">{{ t('nav.flocks') }}</span>
        </RouterLink>
        <RouterLink
          to="/poultry/suppliers"
          class="nav-item"
          :class="{ 'active': isActive('/poultry/suppliers') }"
          @click="$emit('close')"
        >
          <span class="icon">📦</span>
          <span class="label">{{ t('nav.suppliers') }}</span>
        </RouterLink>
        <RouterLink
          to="/poultry/customers"
          class="nav-item"
          :class="{ 'active': isActive('/poultry/customers') }"
          @click="$emit('close')"
        >
          <span class="icon">👥</span>
          <span class="label">{{ t('nav.customers') }}</span>
        </RouterLink>
      </div>

      <!-- Section: Operations -->
      <div class="space-y-1">
        <div class="px-3 mb-2 text-xs font-bold text-gray-400 uppercase tracking-wider">
          {{ t('nav.operations') }}
        </div>
        <RouterLink
          to="/tasks"
          class="nav-item"
          :class="{ 'active': isActive('/tasks') }"
          @click="$emit('close')"
        >
          <span class="icon">📋</span>
          <span class="label">{{ t('nav.tasks') }}</span>
          <span class="badge" v-if="taskCount > 0">{{ taskCount }}</span>
        </RouterLink>
        <RouterLink
          to="/nursery"
          class="nav-item"
          :class="{ 'active': isActive('/nursery') }"
          @click="$emit('close')"
        >
          <span class="icon">🌱</span>
          <span class="label">{{ t('nav.nursery') }}</span>
        </RouterLink>
      </div>

      <!-- Section: System -->
      <div class="space-y-1">
        <div class="px-3 mb-2 text-xs font-bold text-gray-400 uppercase tracking-wider">
          {{ t('nav.system') }}
        </div>
        <RouterLink
          to="/sensors"
          class="nav-item"
          :class="{ 'active': isActive('/sensors') }"
          @click="$emit('close')"
        >
          <span class="icon">📡</span>
          <span class="label">{{ t('nav.sensors') }}</span>
        </RouterLink>
        <RouterLink
          to="/alerts"
          class="nav-item"
          :class="{ 'active': isActive('/alerts') }"
          @click="$emit('close')"
        >
          <span class="icon">🔔</span>
          <span class="label">{{ t('nav.alerts') }}</span>
          <span class="badge-danger" v-if="alertsStore.unreadCount > 0">
            {{ alertsStore.unreadCount }}
          </span>
        </RouterLink>
        <RouterLink
          to="/settings"
          class="nav-item"
          :class="{ 'active': isActive('/settings') }"
          @click="$emit('close')"
        >
          <span class="icon">⚙️</span>
          <span class="label">{{ t('nav.settings') }}</span>
        </RouterLink>
      </div>
    </nav>

    <!-- Bottom section -->
    <div class="border-t border-gray-50 p-4 shrink-0 bg-gray-50/50">
      <div class="flex items-center gap-3 mb-3 px-2">
        <div class="w-8 h-8 rounded-full bg-green-100 text-green-700 flex items-center justify-center font-bold text-sm">
          {{ auth.user?.name?.charAt(0) || 'U' }}
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-gray-900 truncate">{{ auth.user?.name }}</p>
          <p class="text-xs text-gray-500 truncate">{{ auth.user?.email }}</p>
        </div>
      </div>
      
      <div class="flex items-center justify-between gap-2">
        <LanguageSwitcher />
        <button
          @click="logout"
          class="text-xs text-gray-500 hover:text-red-600 hover:bg-red-50 border border-gray-200 rounded-lg px-3 py-1.5 transition-colors flex items-center gap-1.5"
        >
          <span>🚪</span> {{ t('auth.logout') }}
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
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

// Mock task count for now
const taskCount = ref(3)

function isActive(to: string) {
  if (to === '/') return route.path === '/'
  return route.path.startsWith(to) && to !== '/'
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.nav-item {
  @apply flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 text-gray-600 hover:bg-gray-50 hover:text-gray-900 relative overflow-hidden;
}

.nav-item.active {
  @apply bg-green-50/80 text-green-800 shadow-sm shadow-green-100/50;
}

.nav-item .icon {
  @apply text-lg w-6 text-center transition-transform duration-200;
}

.nav-item:hover .icon {
  @apply scale-110;
}

.badge {
  @apply ms-auto bg-gray-100 text-gray-600 text-[10px] font-bold px-2 py-0.5 rounded-md;
}

.badge-danger {
  @apply ms-auto bg-red-100 text-red-600 text-[10px] font-bold px-2 py-0.5 rounded-md animate-pulse;
}

.badge-new {
  @apply ms-auto bg-blue-100 text-blue-600 text-[9px] font-bold px-1.5 py-0.5 rounded uppercase tracking-wide;
}

.scrollbar-thin::-webkit-scrollbar {
  width: 4px;
}
.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
}
.scrollbar-thin::-webkit-scrollbar-thumb {
  background-color: #e5e7eb;
  border-radius: 20px;
}
</style>
