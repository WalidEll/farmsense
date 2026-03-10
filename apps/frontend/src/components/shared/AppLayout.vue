<template>
  <div class="min-h-screen bg-[#F3F4F6] font-sans text-gray-900">
    <!-- Sidebar -->
    <SideBar :open="sidebarOpen" @close="sidebarOpen = false" />

    <!-- Main area (shifted right on desktop for sidebar) -->
    <div class="lg:ms-64 flex flex-col min-h-screen transition-all duration-300">
      <!-- Mobile top bar (only visible below lg) -->
      <header class="sticky top-0 z-30 bg-white/80 backdrop-blur-md border-b border-gray-200 h-16 flex items-center justify-between px-4 lg:hidden">
        <div class="flex items-center gap-3">
          <button
            @click="sidebarOpen = true"
            class="p-2 -ms-2 text-gray-600 hover:text-green-700 rounded-lg hover:bg-green-50 transition-colors"
            aria-label="Toggle menu"
          >
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          <span class="font-bold text-gray-800 text-lg">FarmSense</span>
        </div>

        <!-- Alerts bell (mobile) -->
        <RouterLink to="/alerts" class="relative text-gray-600 hover:text-green-700 p-2 -me-2 rounded-full hover:bg-gray-100">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          <span
            v-if="alertsStore.unreadCount > 0"
            class="absolute top-1.5 end-1.5 bg-red-500 ring-2 ring-white text-white text-[10px] font-bold rounded-full w-4 h-4 flex items-center justify-center"
          >
            {{ alertsStore.unreadCount > 9 ? '9+' : alertsStore.unreadCount }}
          </span>
        </RouterLink>
      </header>

      <!-- Offline banner -->
      <OfflineBanner />

      <!-- Page content -->
      <main class="flex-1 p-4 lg:p-8 max-w-7xl mx-auto w-full">
        <RouterView v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </RouterView>
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

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
