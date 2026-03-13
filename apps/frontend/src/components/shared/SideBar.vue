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
    <!-- Enterprise Badge -->
    <div class="px-6 pt-4 pb-2">
      <div class="flex items-center gap-2 text-[9px] font-black text-gray-400 uppercase tracking-[0.2em]">
        <div class="w-1.5 h-1.5 rounded-full bg-brand-dark animate-pulse"></div>
        ENTERPRISE EDITION
      </div>
    </div>

    <!-- Group Selector / Logo Area -->
    <div class="h-16 flex items-center gap-3 px-6 shrink-0">
      <div class="w-9 h-9 rounded-xl bg-brand-dark flex items-center justify-center text-lg shadow-sm text-white">🌿</div>
      <div class="flex flex-col">
        <span class="font-black text-gray-900 text-base leading-tight tracking-tight">FarmSense</span>
        <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">Dashboard</span>
      </div>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 overflow-y-auto pt-4 pb-12 px-3 space-y-1.5 scrollbar-thin">
      <div v-for="group in navigationConfig" :key="group.nameKey" class="mb-2">
        <button
          @click="toggleGroup(group.nameKey)"
          class="w-full flex items-center justify-between px-3 py-2.5 rounded-xl transition-all duration-200 group/header"
          :class="expandedGroup === group.nameKey ? 'text-brand-dark bg-gray-50/50' : 'text-gray-500 hover:bg-gray-50 hover:text-gray-900'"
        >
          <div class="flex items-center gap-3">
            <div 
              class="w-8 h-8 flex items-center justify-center rounded-lg transition-colors group-hover/header:bg-white group-hover/header:shadow-sm"
              :class="expandedGroup === group.nameKey ? 'bg-white shadow-sm text-brand-dark' : 'text-gray-400'"
              v-html="group.icon"
            ></div>
            <span class="text-sm font-bold tracking-tight">{{ t(group.nameKey) }}</span>
          </div>
          <span class="transform transition-transform duration-300 text-[10px]" :class="{ 'rotate-180': expandedGroup === group.nameKey }">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M19 9l-7 7-7-7" /></svg>
          </span>
        </button>
        
        <div 
          v-show="expandedGroup === group.nameKey" 
          class="mt-1 ps-4 border-s border-gray-100 ms-7 space-y-1 overflow-hidden"
        >
          <RouterLink
            v-for="item in group.items"
            :key="item.to"
            :to="item.to"
            class="nav-item"
            active-class=""
            :exact-active-class="item.exact ? 'active' : ''"
            :class="{ 'active': !item.exact && isActive(item.to) }"
            @click="handleNavClick"
          >
            <div class="icon-wrapper" v-html="item.icon"></div>
            <span class="label">{{ t(item.labelKey) }}</span>
            
            <div v-if="isActive(item.to, item.exact)" class="active-pill"></div>

            <template v-if="item.badge">
              <span v-if="item.badge === 'new'" class="badge-new">{{ t('common.new') }}</span>
              <span v-else-if="item.badge.type === 'danger' && getBadgeValue(item.badge.key) > 0" class="badge-danger">
                {{ getBadgeValue(item.badge.key) }}
              </span>
              <span v-else-if="item.badge.type === 'count' && getBadgeValue(item.badge.key) > 0" class="badge">
                {{ getBadgeValue(item.badge.key) }}
              </span>
            </template>
          </RouterLink>
        </div>
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
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { useAlertsStore } from '@/stores/alerts.store'
import { useI18n } from '@/i18n'
import LanguageSwitcher from './LanguageSwitcher.vue'
import { navigationConfig, type NavItem } from '@/config/navigation'

const props = defineProps<{ open: boolean }>()
const emit = defineEmits<{ close: [] }>()

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const alertsStore = useAlertsStore()

// Mock task count for now
const taskCount = ref(3)

const expandedGroup = ref<string | null>(null)

watch(() => route.path, () => {
  for (const group of navigationConfig) {
    if (group.items.some((item: NavItem) => isActive(item.to, item.exact))) {
      expandedGroup.value = group.nameKey
      break
    }
  }
}, { immediate: true })

function toggleGroup(nameKey: string) {
  expandedGroup.value = expandedGroup.value === nameKey ? null : nameKey
}

function handleNavClick() {
  emit('close')
}

function isActive(to: string, exact?: boolean) {
  if (exact) return route.path === to
  // For non-exact, match if path starts with 'to' (but don't match '/' against everything)
  if (to === '/') return route.path === '/'
  return route.path === to || route.path.startsWith(to + '/')
}

function getBadgeValue(key: string) {
  if (key === 'alerts') return alertsStore.unreadCount
  if (key === 'tasks') return taskCount.value
  return 0
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.nav-item {
  @apply flex items-center gap-3 px-4 py-2.5 rounded-xl text-[14px] font-medium transition-all duration-200 text-gray-400 hover:text-gray-900 hover:bg-gray-50/50 relative;
}

.nav-item.active {
  @apply text-gray-900 bg-gray-100/80 font-bold;
  box-shadow: none;
}

.nav-item .icon-wrapper {
  @apply w-5 h-5 flex items-center justify-center shrink-0 text-gray-400 transition-colors duration-200;
}

.nav-item.active .icon-wrapper {
  @apply text-brand-dark;
}

.nav-item:hover .icon-wrapper {
  @apply text-gray-600;
}

.active-pill {
  @apply absolute right-0 top-1.5 bottom-1.5 w-1 bg-brand-dark rounded-s-full shadow-[0_0_10px_rgba(27,66,53,0.3)];
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
