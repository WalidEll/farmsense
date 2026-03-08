<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Sidebar -->
    <SideBar :open="sidebarOpen" @close="sidebarOpen = false" />

    <!-- Main area (shifted right on desktop for sidebar) -->
    <div class="lg:ms-60 flex flex-col min-h-screen">
      <!-- Mobile top bar (only visible below lg) -->
      <header class="sticky top-0 z-30 bg-white border-b border-gray-200 h-14 flex items-center justify-between px-4 lg:hidden">
        <button
          @click="sidebarOpen = true"
          class="p-2 -ms-2 text-gray-600 hover:text-gray-900"
          aria-label="Toggle menu"
        >
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>

        <RouterLink to="/" class="flex items-center gap-2 font-bold text-green-800">
          <span class="text-xl">🌱</span>
          <span>FarmSense</span>
        </RouterLink>

        <!-- Alerts bell (mobile) -->
        <RouterLink to="/alerts" class="relative text-gray-600 hover:text-green-700 p-2 -me-2">
          🔔
          <span
            v-if="alertsStore.unreadCount > 0"
            class="absolute top-1 end-1 bg-red-500 text-white text-[10px] font-bold rounded-full w-4 h-4 flex items-center justify-center"
          >
            {{ alertsStore.unreadCount > 9 ? '9+' : alertsStore.unreadCount }}
          </span>
        </RouterLink>
      </header>

      <!-- Offline banner -->
      <OfflineBanner />

      <!-- Page content -->
      <main class="flex-1">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAlertsStore } from '@/stores/alerts.store'
import SideBar from './SideBar.vue'
import OfflineBanner from './OfflineBanner.vue'

const alertsStore = useAlertsStore()
const sidebarOpen = ref(false)
</script>
