<template>
  <div class="min-h-screen bg-gray-50">
    <NavBar />
    <OfflineBanner />

    <main class="max-w-6xl mx-auto px-4 py-8">
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-xl font-bold text-gray-800">{{ t('nav.plants') }}</h2>
        <div class="relative">
          <button
            @click="showDropdown = !showDropdown"
            class="flex items-center gap-2 bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          >
            <span>+</span> {{ t('common.actions') }}
            <svg class="w-4 h-4 ml-1 transition-transform" :class="{ 'rotate-180': showDropdown }" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
          </button>
          
          <div v-if="showDropdown" class="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-100 overflow-hidden z-20">
            <button
              @click="showForm = true; showDropdown = false"
              class="w-full text-left px-4 py-3 text-sm text-gray-700 hover:bg-green-50 hover:text-green-700 flex items-center gap-2 transition-colors border-b border-gray-100"
            >
              🌿 {{ t('plants.add') }}
            </button>
            <button
              @click="showDeviceModal = true; showDropdown = false"
              class="w-full text-left px-4 py-3 text-sm text-gray-700 hover:bg-green-50 hover:text-green-700 flex items-center gap-2 transition-colors"
            >
              📡 {{ t('dashboard.connectSensor') }}
            </button>
          </div>
        </div>
      </div>
      
      <!-- Invisible overlay to close dropdown -->
      <div v-if="showDropdown" @click="showDropdown = false" class="fixed inset-0 z-10"></div>

      <!-- Weather Card -->
      <WeatherCard />

      <!-- Loading -->
      <div v-if="plantsStore.loading" class="text-center py-16 text-gray-400">
        <div class="text-4xl mb-2">🌿</div>
        <p>{{ t('common.loading') }}</p>
      </div>

      <!-- Empty state -->
      <div v-else-if="plantsStore.plants.length === 0" class="text-center py-16">
        <div class="text-6xl mb-4">🪴</div>
        <p class="text-gray-500 text-lg">{{ t('plants.empty') }}</p>
        <button
          @click="showForm = true"
          class="mt-4 bg-green-700 hover:bg-green-800 text-white px-6 py-2.5 rounded-lg font-medium"
        >
          {{ t('plants.addFirst') }}
        </button>
      </div>

      <!-- Plant grid -->
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <PlantCard
          v-for="plant in plantsStore.plants"
          :key="plant.id"
          :plant="plant"
          @click="router.push(`/plants/${plant.id}`)"
          @delete="deletePlant(plant.id)"
        />
      </div>

      <!-- Alerts sidebar (collapsed on mobile) -->
      <div class="mt-8" v-if="alertsStore.alerts.length">
        <h3 class="text-lg font-semibold text-gray-700 mb-3">{{ t('nav.alerts') }}</h3>
        <AlertFeed :limit="5" />
      </div>
    </main>

    <!-- Plant form modal -->
    <PlantForm v-if="showForm" @close="showForm = false" @saved="showForm = false" />

    <!-- Device setup modal -->
    <DeviceSetupModal v-if="showDeviceModal" @close="showDeviceModal = false" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePlantStore } from '@/stores/plants.store'
import { useAlertsStore } from '@/stores/alerts.store'
import { useReadingsStore } from '@/stores/readings.store'
import { useI18n } from '@/i18n'
import NavBar from '@/components/shared/NavBar.vue'
import OfflineBanner from '@/components/shared/OfflineBanner.vue'
import PlantCard from '@/components/plants/PlantCard.vue'
import PlantForm from '@/components/plants/PlantForm.vue'
import AlertFeed from '@/components/alerts/AlertFeed.vue'
import DeviceSetupModal from '@/components/devices/DeviceSetupModal.vue'
import WeatherCard from '@/components/weather/WeatherCard.vue'

const { t } = useI18n()
const router = useRouter()
const plantsStore = usePlantStore()
const alertsStore = useAlertsStore()
const readingsStore = useReadingsStore()
const showForm = ref(false)
const showDeviceModal = ref(false)
const showDropdown = ref(false)

let syncInterval: ReturnType<typeof setInterval>

async function fetchAllData(silent = false) {
  await plantsStore.fetchAll(silent)
  await alertsStore.fetchAll(silent)
  // Pre-fetch latest readings for all plants
  plantsStore.plants.forEach(p => readingsStore.fetchLatest(p.id))
}

onMounted(async () => {
  await fetchAllData()
  syncInterval = setInterval(() => fetchAllData(true), 15000)
})

onUnmounted(() => {
  if (syncInterval) clearInterval(syncInterval)
})

async function deletePlant(id: string) {
  if (confirm(t('plants.confirmDelete'))) {
    await plantsStore.remove(id)
  }
}
</script>
