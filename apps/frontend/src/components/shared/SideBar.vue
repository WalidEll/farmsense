<template>
  <!-- Mobile backdrop -->
  <div
    v-if="open"
    class="fixed inset-0 bg-black/40 backdrop-blur-[2px] z-40 lg:hidden"
    @click="$emit('close')"
  ></div>

  <!-- Sidebar -->
  <aside
    :class="[
      'fixed top-0 bottom-0 z-50 w-[17rem] bg-white border-e border-gray-100 flex flex-col transition-transform duration-300 ease-[cubic-bezier(0.25,1,0.5,1)] shadow-xl lg:shadow-none',
      open ? 'translate-x-0' : '-translate-x-full lg:translate-x-0',
    ]"
  >
    <!-- Logo / Home Link -->
    <RouterLink
      to="/"
      class="h-16 flex items-center gap-3 px-6 shrink-0 hover:bg-gray-50/50 transition-colors group"
      @click="handleNavClick"
    >
      <div class="w-9 h-9 rounded-xl bg-brand-dark flex items-center justify-center text-lg shadow-sm text-white group-hover:scale-105 transition-transform">
        🌿
      </div>
      <div class="flex flex-col">
        <span class="font-black text-gray-900 text-base leading-tight tracking-tight">FarmSense</span>
        <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">{{ t('nav.dashboard') }}</span>
      </div>
    </RouterLink>

    <!-- Dashboard Quick Link -->
    <div class="px-3 mb-1">
      <RouterLink
        to="/"
        class="flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm font-semibold transition-all duration-200"
        :class="route.path === '/' ? 'bg-green-50 text-brand-dark' : 'text-gray-500 hover:text-gray-900 hover:bg-gray-50'"
        @click="handleNavClick"
      >
        <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round">
          <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>
        </svg>
        <span>{{ t('nav.dashboard') }}</span>
        <div v-if="route.path === '/'" class="ml-auto w-1.5 h-1.5 rounded-full bg-brand-dark"></div>
      </RouterLink>
    </div>

    <!-- Divider -->
    <div class="mx-5 border-t border-gray-100 mb-2"></div>

    <!-- Navigation Groups -->
    <nav class="flex-1 overflow-y-auto pb-12 px-3 space-y-0.5 scrollbar-thin">
      <div v-for="group in navigationConfig" :key="group.nameKey" class="mb-1">
        <!-- Group Header -->
        <button
          @click="toggleGroup(group.nameKey)"
          class="w-full flex items-center justify-between px-3 py-2 rounded-xl transition-all duration-200 group/header"
          :class="isGroupExpanded(group.nameKey) ? 'text-brand-dark' : 'text-gray-500 hover:bg-gray-50 hover:text-gray-700'"
        >
          <div class="flex items-center gap-2.5">
            <div
              class="w-7 h-7 flex items-center justify-center rounded-lg transition-all duration-200"
              :class="isGroupExpanded(group.nameKey) ? 'bg-green-100 text-brand-dark' : 'text-gray-400 group-hover/header:text-gray-500'"
              v-html="group.icon"
            ></div>
            <span class="text-[13px] font-bold tracking-tight">{{ t(group.nameKey) }}</span>
          </div>
          <svg
            class="w-3.5 h-3.5 transition-transform duration-300"
            :class="{ 'rotate-180': isGroupExpanded(group.nameKey) }"
            fill="none" viewBox="0 0 24 24" stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M19 9l-7 7-7-7" />
          </svg>
        </button>

        <!-- Group Items (collapsible) -->
        <transition
          enter-active-class="transition-all duration-300 ease-out"
          enter-from-class="max-h-0 opacity-0"
          enter-to-class="max-h-96 opacity-100"
          leave-active-class="transition-all duration-200 ease-in"
          leave-from-class="max-h-96 opacity-100"
          leave-to-class="max-h-0 opacity-0"
        >
          <div
            v-show="isGroupExpanded(group.nameKey)"
            class="mt-0.5 ps-3.5 border-s-2 border-gray-100 ms-6 space-y-0.5 overflow-hidden"
          >
            <RouterLink
              v-for="item in group.items"
              :key="item.to"
              :to="item.to"
              class="nav-item"
              :class="{ 'active': isActive(item.to, item.exact) }"
              @click="handleNavClick"
            >
              <div class="icon-wrapper" v-html="item.icon"></div>
              <span class="label">{{ t(item.labelKey) }}</span>

              <!-- Active indicator -->
              <div v-if="isActive(item.to, item.exact)" class="active-dot"></div>

              <!-- Badges -->
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
        </transition>
      </div>
    </nav>

    <!-- Bottom section -->
    <div class="border-t border-gray-100 p-4 shrink-0 bg-gray-50/30">
      <div class="flex items-center gap-3 mb-3 px-2">
        <div class="w-8 h-8 rounded-full bg-green-100 text-brand-dark flex items-center justify-center font-bold text-sm">
          {{ auth.user?.name?.charAt(0)?.toUpperCase() || 'U' }}
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-semibold text-gray-900 truncate">{{ auth.user?.name }}</p>
          <p class="text-[11px] text-gray-400 truncate">{{ auth.user?.email }}</p>
        </div>
      </div>

      <div class="flex items-center justify-between gap-2">
        <LanguageSwitcher />
        <button
          @click="logout"
          class="text-xs text-gray-500 hover:text-red-600 hover:bg-red-50 border border-gray-200 rounded-lg px-3 py-1.5 transition-colors flex items-center gap-1.5"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
          </svg>
          {{ t('auth.logout') }}
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

// Multiple groups can be open simultaneously
const expandedGroups = ref<Set<string>>(new Set())

// Auto-expand groups that contain the active route + any defaultOpen groups
watch(() => route.path, () => {
  for (const group of navigationConfig) {
    if (group.defaultOpen || group.items.some((item: NavItem) => isActive(item.to, item.exact))) {
      expandedGroups.value.add(group.nameKey)
    }
  }
}, { immediate: true })

function isGroupExpanded(nameKey: string) {
  return expandedGroups.value.has(nameKey)
}

function toggleGroup(nameKey: string) {
  if (expandedGroups.value.has(nameKey)) {
    expandedGroups.value.delete(nameKey)
  } else {
    expandedGroups.value.add(nameKey)
  }
}

function handleNavClick() {
  emit('close')
}

function isActive(to: string, exact?: boolean) {
  if (exact) return route.path === to
  if (to === '/') return route.path === '/'
  return route.path === to || route.path.startsWith(to + '/')
}

function getBadgeValue(key: string) {
  if (key === 'alerts') return alertsStore.unreadCount
  return 0
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.nav-item {
  @apply flex items-center gap-2.5 px-3 py-2 rounded-xl text-[13px] font-medium transition-all duration-200 text-gray-400 relative;
}
.nav-item:hover {
  @apply text-gray-800;
  background-color: rgba(249, 250, 251, 0.8);
}

.nav-item.active {
  @apply font-bold;
  color: #1B4235;
  background-color: rgba(27, 66, 53, 0.06);
}

.nav-item .icon-wrapper {
  @apply flex items-center justify-center shrink-0 text-gray-400 transition-colors duration-200;
  width: 18px;
  height: 18px;
}

.nav-item.active .icon-wrapper {
  color: #1B4235;
}

.nav-item:hover .icon-wrapper {
  @apply text-gray-600;
}

.active-dot {
  @apply ml-auto rounded-full flex-shrink-0;
  width: 6px;
  height: 6px;
  background-color: #1B4235;
}

.badge {
  @apply ms-auto bg-gray-100 text-gray-600 font-bold px-2 py-0.5 rounded-md;
  font-size: 10px;
}

.badge-danger {
  @apply ms-auto bg-red-100 text-red-600 font-bold px-2 py-0.5 rounded-md animate-pulse;
  font-size: 10px;
}

.badge-new {
  @apply ms-auto bg-blue-100 text-blue-600 font-bold px-1.5 py-0.5 rounded uppercase tracking-wide;
  font-size: 9px;
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
