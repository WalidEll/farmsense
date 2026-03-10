<template>
  <div class="space-y-8">
    <!-- Header Section: Greeting & Quick Stats -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">
          {{ t('dashboard.greeting.' + getGreetingKey()) }}, {{ auth.user?.name?.split(' ')[0] || 'Farmer' }}! 👨‍🌾
        </h1>
        <p class="text-gray-500 text-sm mt-1">
          {{ new Date().toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}
        </p>
      </div>
      
      <div class="flex items-center gap-3">
        <button
          @click="showForm = true"
          class="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-5 py-2.5 rounded-xl font-medium transition-all shadow-sm hover:shadow-md hover:-translate-y-0.5"
        >
          <span class="text-lg">+</span> {{ t('plants.add') }}
        </button>
      </div>
    </div>

    <!-- Main Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      
      <!-- Left Column (2/3): Weather & Crops -->
      <div class="lg:col-span-2 space-y-8">
        
        <!-- Weather Overview (Compact) -->
        <WeatherCard class="!bg-gradient-to-br !from-blue-500 !to-blue-600 !text-white !shadow-lg !border-0" />

        <!-- Active Crops Lifecycle -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div class="p-6 border-b border-gray-50 flex items-center justify-between">
            <h2 class="font-bold text-gray-800 text-lg flex items-center gap-2">
              🌱 {{ t('nav.crops') }}
              <span class="bg-green-100 text-green-700 text-xs px-2 py-0.5 rounded-full">{{ plantsStore.plants.length }}</span>
            </h2>
            <RouterLink to="/crops" class="text-sm text-green-600 hover:text-green-700 font-medium">
              {{ t('dashboard.view_all') }} &rarr;
            </RouterLink>
          </div>
          
          <div v-if="plantsStore.loading" class="p-8 text-center text-gray-400">
            {{ t('dashboard.loading_crops') }}
          </div>
          
          <div v-else-if="plantsStore.plants.length === 0" class="p-12 text-center">
            <div class="text-5xl mb-3">🚜</div>
            <p class="text-gray-500">{{ t('plants.empty') }}</p>
            <button @click="showForm = true" class="text-green-600 font-medium mt-2 hover:underline">
              {{ t('dashboard.start_first_crop') }}
            </button>
          </div>

          <div v-else class="divide-y divide-gray-50">
            <div
              v-for="plant in plantsStore.plants.slice(0, 5)"
              :key="plant.id"
              class="p-4 hover:bg-gray-50 transition-colors cursor-pointer group"
              @click="router.push(`/plants/${plant.id}`)"
            >
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-xl bg-green-50 flex items-center justify-center text-2xl shadow-inner">
                  {{ getPlantEmoji(plant) }}
                </div>
                
                <div class="flex-1 min-w-0">
                  <div class="flex justify-between items-start mb-1">
                    <h3 class="font-semibold text-gray-900 group-hover:text-green-700 transition-colors">{{ plant.name }}</h3>
                    <span class="text-xs font-medium px-2 py-0.5 rounded bg-gray-100 text-gray-600">
                      {{ t('dashboard.days_old', { n: getDaysSincePlanted(plant) }) }}
                    </span>
                  </div>
                  
                  <!-- Lifecycle Progress Bar -->
                  <div class="relative w-full h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div 
                      class="absolute top-0 left-0 h-full bg-green-500 rounded-full transition-all duration-1000"
                      :style="{ width: getLifecycleProgress(plant) + '%' }"
                    ></div>
                  </div>
                  <div class="flex justify-between text-[10px] text-gray-400 mt-1 uppercase tracking-wide">
                    <span>{{ t('dashboard.lifecycle_sown') }}</span>
                    <span>{{ t('dashboard.lifecycle_harvest') }}</span>
                  </div>
                </div>

                <!-- Quick Status Icons -->
                <div class="flex gap-2 shrink-0">
                  <StatusBadge :value="readingsStore.latest[plant.id]?.soilMoisture" type="moisture" />
                  <StatusBadge :value="readingsStore.latest[plant.id]?.temperature" type="temp" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column (1/3): Attention & Quick Actions -->
      <div class="space-y-8">
        
        <!-- Quick Actions Grid -->
        <div class="grid grid-cols-2 gap-3">
          <button class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 hover:shadow-md hover:border-green-200 transition-all text-left group">
            <div class="w-10 h-10 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">📝</div>
            <div class="font-semibold text-gray-800 text-sm">{{ t('dashboard.log_task') }}</div>
          </button>
          <button class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 hover:shadow-md hover:border-green-200 transition-all text-left group">
            <div class="w-10 h-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">💧</div>
            <div class="font-semibold text-gray-800 text-sm">{{ t('dashboard.water_all') }}</div>
          </button>
          <button @click="showDeviceModal = true" class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 hover:shadow-md hover:border-green-200 transition-all text-left group">
            <div class="w-10 h-10 rounded-full bg-purple-100 text-purple-600 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">📡</div>
            <div class="font-semibold text-gray-800 text-sm">{{ t('dashboard.add_sensor') }}</div>
          </button>
          <button class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 hover:shadow-md hover:border-green-200 transition-all text-left group">
            <div class="w-10 h-10 rounded-full bg-yellow-100 text-yellow-600 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">📦</div>
            <div class="font-semibold text-gray-800 text-sm">{{ t('dashboard.harvest') }}</div>
          </button>
        </div>

        <!-- Priority Alerts -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div class="p-4 border-b border-gray-50 flex items-center justify-between">
            <h3 class="font-bold text-gray-800 text-sm flex items-center gap-2">
              🔔 {{ t('nav.alerts') }}
            </h3>
            <span v-if="alertsStore.unreadCount > 0" class="text-xs bg-red-100 text-red-600 font-bold px-2 py-0.5 rounded-full">
              {{ alertsStore.unreadCount }} {{ t('dashboard.new_alerts') }}
            </span>
          </div>
          
          <div class="max-h-[300px] overflow-y-auto">
            <AlertFeed :limit="5" compact />
          </div>
          
          <RouterLink to="/alerts" class="block p-3 text-center text-xs font-medium text-gray-500 hover:text-gray-800 hover:bg-gray-50 border-t border-gray-50">
            {{ t('dashboard.view_all_alerts') }}
          </RouterLink>
        </div>


      </div>
    </div>

    <!-- Modals -->
    <PlantForm v-if="showForm" @close="showForm = false" @saved="showForm = false" />
    <DeviceSetupModal v-if="showDeviceModal" @close="showDeviceModal = false" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { usePlantStore } from '@/stores/plants.store'
import { useAlertsStore } from '@/stores/alerts.store'
import { useReadingsStore } from '@/stores/readings.store'
import { useI18n } from '@/i18n'
import PlantForm from '@/components/plants/PlantForm.vue'
import AlertFeed from '@/components/alerts/AlertFeed.vue'
import DeviceSetupModal from '@/components/devices/DeviceSetupModal.vue'
import WeatherCard from '@/components/weather/WeatherCard.vue'
import StatusBadge from '@/components/dashboard/StatusBadge.vue'

const { t } = useI18n()
const router = useRouter()
const auth = useAuthStore()
const plantsStore = usePlantStore()
const alertsStore = useAlertsStore()
const readingsStore = useReadingsStore()

const showForm = ref(false)
const showDeviceModal = ref(false)

let syncInterval: ReturnType<typeof setInterval>

onMounted(async () => {
  await Promise.all([
    plantsStore.fetchAll(),
    alertsStore.fetchAll()
  ])
  plantsStore.plants.forEach(p => readingsStore.fetchLatest(p.id))
  
  syncInterval = setInterval(() => {
    plantsStore.fetchAll(true)
    alertsStore.fetchAll(true)
  }, 15000)
})

onUnmounted(() => {
  if (syncInterval) clearInterval(syncInterval)
})

function getGreetingKey() {
  const hour = new Date().getHours()
  if (hour < 12) return 'morning'
  if (hour < 18) return 'afternoon'
  return 'evening'
}

function getPlantEmoji(plant: any) {
  const s = plant.species?.toLowerCase() ?? ''
  if (s.includes('cactus')) return '🌵'
  if (s.includes('tomato')) return '🍅'
  if (s.includes('carrot')) return '🥕'
  if (s.includes('lettuce')) return '🥬'
  return '🪴'
}

function getDaysSincePlanted(plant: any) {
  // Mock logic: assume plantedAt exists, or fallback to created_at
  const start = new Date(plant.plantedAt || plant.createdAt || Date.now())
  const diff = Date.now() - start.getTime()
  return Math.floor(diff / (1000 * 60 * 60 * 24))
}

function getLifecycleProgress(plant: any) {
  // Mock logic: 90 days maturity average
  const days = getDaysSincePlanted(plant)
  return Math.min((days / 90) * 100, 100)
}
</script>
