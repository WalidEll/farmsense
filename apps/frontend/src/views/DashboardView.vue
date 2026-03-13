<template>
  <div class="space-y-8">
    <!-- Header Section: Greeting & Quick Stats -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 tracking-tight">
          Plant Management Dashboard
        </h1>
      </div>
      
      <div class="flex items-center gap-3">
        <div class="relative hidden sm:block">
          <svg class="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input type="text" placeholder="Search fields or crops..." class="ps-9 pe-4 py-2 bg-white border border-gray-200 rounded-full text-sm focus:outline-none focus:ring-2 focus:ring-brand-dark/20 focus:border-brand-dark w-64 transition-all">
        </div>
        
        <RouterLink to="/alerts" class="relative bg-white border border-gray-200 text-gray-600 hover:text-brand-dark w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-50 transition-colors shadow-sm">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          <span
            v-if="alertsStore.unreadCount > 0"
            class="absolute top-0 right-0 bg-red-500 ring-2 ring-white text-white text-[10px] font-bold rounded-full w-3.5 h-3.5 flex items-center justify-center"
          ></span>
        </RouterLink>

        <!-- User Profile Avatar (Mock) -->
        <button class="w-10 h-10 rounded-full bg-orange-200 border border-gray-200 flex items-center justify-center shadow-sm hover:ring-2 ring-brand-dark/20 transition-all overflow-hidden">
          <span class="text-orange-800 font-bold text-sm">W</span>
        </button>
      </div>
    </div>

    <!-- 4 Stats Cards Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <!-- Card 1 -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm flex flex-col justify-between h-40">
        <div class="flex justify-between items-start">
          <span class="text-[11px] font-bold text-gray-500 uppercase tracking-wider">Cultivated Area</span>
          <div class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600">🚜</div>
        </div>
        <div>
          <div class="text-3xl font-extrabold text-gray-900 tracking-tight">1,240 <span class="text-sm font-medium text-gray-500 tracking-normal">Acres</span></div>
          <div class="text-xs text-status-optimalDark mt-1 font-medium flex items-center gap-1">
            <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path></svg>
            +2.5% from last month
          </div>
        </div>
      </div>

      <!-- Card 2 -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm flex flex-col justify-between h-40">
        <div class="flex justify-between items-start">
          <span class="text-[11px] font-bold text-gray-500 uppercase tracking-wider">Active Crops</span>
          <div class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600">🌿</div>
        </div>
        <div>
          <div class="text-3xl font-extrabold text-gray-900 tracking-tight">{{ plantsStore.plants.length }}</div>
          <div class="text-xs text-gray-500 mt-1 font-medium">
            Currently tracked
          </div>
        </div>
      </div>

      <!-- Card 3 -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm flex flex-col justify-between h-40">
        <div class="flex justify-between items-start">
          <span class="text-[11px] font-bold text-gray-500 uppercase tracking-wider">Irrigation Usage</span>
          <div class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600">💧</div>
        </div>
        <div>
          <div class="text-3xl font-extrabold text-gray-900 tracking-tight">450k <span class="text-sm font-medium text-gray-500 tracking-normal">Gallons</span></div>
          <div class="text-xs text-status-optimalDark mt-1 font-medium flex items-center gap-1">
            <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            Optimized consumption
          </div>
        </div>
      </div>

      <!-- Card 4 -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm flex flex-col justify-between h-40">
        <div class="flex justify-between items-start">
          <span class="text-[11px] font-bold text-gray-500 uppercase tracking-wider">Unread Alerts</span>
          <div class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600">🔔</div>
        </div>
        <div>
          <div class="text-3xl font-extrabold text-gray-900 tracking-tight">{{ alertsStore.unreadCount }} <span class="text-sm font-medium text-gray-500 tracking-normal">alerts</span></div>
          <div class="text-xs mt-1 font-medium" :class="alertsStore.unreadCount > 0 ? 'text-red-500' : 'text-status-optimalDark'">
            {{ alertsStore.unreadCount > 0 ? 'Action required' : 'All clear' }}
          </div>
        </div>
      </div>
    </div>

    <!-- Main Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      
      <!-- Left Column (2/3): Weather & Crops -->
      <div class="lg:col-span-2 space-y-8">
        
        <!-- Weather Overview (Compact) -->
        <WeatherCard class="!bg-gradient-to-br !from-blue-500 !to-blue-600 !text-white !shadow-lg !border-0" />

        <!-- Active Plants from Backend -->
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

          <!-- Loading skeleton -->
          <div v-if="plantsStore.loading" class="p-8 space-y-3">
            <div v-for="i in 3" :key="i" class="h-14 bg-gray-100 rounded-xl animate-pulse"></div>
          </div>

          <!-- Empty state -->
          <div v-else-if="plantsStore.plants.length === 0" class="p-12 text-center">
            <div class="text-5xl mb-3">🪴</div>
            <p class="text-gray-500 mb-3">{{ t('plants.empty') }}</p>
            <button @click="showForm = true" class="text-green-600 font-medium hover:underline">
              {{ t('dashboard.start_first_crop') }}
            </button>
          </div>
          
          <!-- Plant list from backend -->
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
                      {{ getDaysSincePlanted(plant) }}d
                    </span>
                  </div>
                  
                  <!-- Live sensor reading for soil moisture -->
                  <div class="flex items-center gap-2 text-xs text-gray-500">
                    <span v-if="readingsStore.latest[plant.id]?.soilMoisture != null">
                      💧 {{ readingsStore.latest[plant.id].soilMoisture }}% soil
                    </span>
                    <span v-if="readingsStore.latest[plant.id]?.temperature != null">
                      🌡 {{ readingsStore.latest[plant.id].temperature }}°C
                    </span>
                    <span v-if="!readingsStore.latest[plant.id]" class="text-gray-400 italic">No sensor data</span>
                  </div>
                  
                  <!-- Lifecycle Progress Bar (based on creation date) -->
                  <div class="relative w-full h-1.5 bg-gray-100 rounded-full overflow-hidden mt-2">
                    <div 
                      class="absolute top-0 left-0 h-full bg-green-500 rounded-full transition-all duration-1000"
                      :style="{ width: getLifecycleProgress(plant) + '%' }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

        <!-- Active Crop Cycles -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div class="p-5 border-b border-gray-50 flex items-center justify-between">
            <h2 class="font-bold text-gray-900 text-lg">Active Crop Cycles</h2>
          </div>
          <div class="divide-y divide-gray-50">
            <!-- Item 1 -->
            <div class="p-6">
              <div class="flex justify-between items-start mb-4">
                <div class="flex items-center gap-4">
                  <div class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center text-gray-600">
                    <svg class="w-5 h-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
                  </div>
                  <div>
                    <h3 class="font-bold text-gray-900 leading-tight">Winter Wheat</h3>
                    <div class="text-xs text-gray-500 mt-0.5">North Sector • Block A-C</div>
                  </div>
                </div>
                <div class="text-right">
                  <div class="font-bold text-lg text-gray-900 leading-tight">82%</div>
                  <div class="text-[9px] font-bold text-gray-400 uppercase tracking-wider">Maturity</div>
                </div>
              </div>
              
              <div class="w-full h-2.5 bg-gray-100 rounded-full overflow-hidden mb-2">
                <div class="h-full bg-brand-dark rounded-full" style="width: 82%"></div>
              </div>
              <div class="flex justify-between text-[10px] font-bold text-gray-400 uppercase tracking-wider">
                <span>Planting (Sept 12)</span>
                <span>Harvest (June 20)</span>
              </div>
            </div>

            <!-- Item 2 -->
            <div class="p-6">
              <div class="flex justify-between items-start mb-4">
                <div class="flex items-center gap-4">
                  <div class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center text-gray-600">🌿</div>
                  <div>
                    <h3 class="font-bold text-gray-900 leading-tight">Sweet Corn</h3>
                    <div class="text-xs text-gray-500 mt-0.5">East Basin • Block G-H</div>
                  </div>
                </div>
                <div class="text-right">
                  <div class="font-bold text-lg text-orange-500 leading-tight">45%</div>
                  <div class="text-[9px] font-bold text-gray-400 uppercase tracking-wider">Maturity</div>
                </div>
              </div>
              
              <div class="w-full h-2.5 bg-gray-100 rounded-full overflow-hidden mb-2">
                <div class="h-full bg-orange-500 rounded-full" style="width: 45%"></div>
              </div>
              <div class="flex justify-between text-[10px] font-bold text-gray-400 uppercase tracking-wider">
                <span>Planting (Mar 05)</span>
                <span>Harvest (Aug 12)</span>
              </div>
            </div>

            <!-- Item 3 -->
            <div class="p-6">
              <div class="flex justify-between items-start mb-4">
                <div class="flex items-center gap-4">
                  <div class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center text-gray-600">🍃</div>
                  <div>
                    <h3 class="font-bold text-gray-900 leading-tight">Soybeans</h3>
                    <div class="text-xs text-gray-500 mt-0.5">South Ridge • All Blocks</div>
                  </div>
                </div>
                <div class="text-right">
                  <div class="font-bold text-lg text-gray-900 leading-tight">15%</div>
                  <div class="text-[9px] font-bold text-gray-400 uppercase tracking-wider">Maturity</div>
                </div>
              </div>
              
              <div class="w-full h-2.5 bg-gray-100 rounded-full overflow-hidden mb-2">
                <div class="h-full bg-status-optimalDark rounded-full" style="width: 15%"></div>
              </div>
              <div class="flex justify-between text-[10px] font-bold text-gray-400 uppercase tracking-wider">
                <span>Planting (Apr 22)</span>
                <span>Harvest (Oct 15)</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column (1/3): Weather & Soil -->
      <div class="space-y-6 shrink-0">
        
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <!-- Top Half: Dark Green Weather -->
          <div class="bg-brand-dark p-6 text-white">
            <div class="flex justify-between items-start mb-6">
              <h3 class="font-bold text-lg">Weather & Soil</h3>
              <span class="text-xs font-medium text-green-200">Live Updates</span>
            </div>
            
            <div class="flex items-center gap-4 mb-8">
              <svg class="w-12 h-12 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z" />
              </svg>
              <div>
                <div class="text-4xl font-light">24°<span class="text-2xl text-green-200">C</span></div>
                <div class="text-sm font-medium text-green-100 mt-1">Partly Cloudy, Iowa</div>
              </div>
            </div>

            <div class="grid grid-cols-2 gap-3">
              <div class="bg-white/10 rounded-xl p-3 backdrop-blur-sm border border-white/5">
                <div class="flex items-center gap-1.5 text-green-200 text-[10px] font-bold tracking-wider mb-1">
                  <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z"></path></svg>
                  HUMIDITY
                </div>
                <div class="text-xl font-bold">62%</div>
              </div>
              <div class="bg-white/10 rounded-xl p-3 backdrop-blur-sm border border-white/5">
                <div class="flex items-center gap-1.5 text-green-200 text-[10px] font-bold tracking-wider mb-1">
                  <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>
                  WIND SPEED
                </div>
                <div class="text-xl font-bold">12 km/h</div>
              </div>
            </div>
          </div>

          <!-- Bottom Half: Soil Moisture -->
          <div class="p-6">
            <h4 class="text-[11px] font-bold text-gray-400 tracking-wider mb-5">SOIL MOISTURE LEVELS</h4>
            
            <div class="space-y-4">
              <!-- Surface -->
              <div>
                <div class="flex justify-between items-end mb-1.5">
                  <span class="text-sm font-semibold text-gray-800">Surface (0-10cm)</span>
                  <span class="text-sm font-bold text-gray-900">18%</span>
                </div>
                <div class="w-full h-1.5 bg-gray-100 rounded-full overflow-hidden">
                  <div class="h-full bg-status-criticalDark rounded-full" style="width: 18%"></div>
                </div>
              </div>
              
              <!-- Root Zone -->
              <div>
                <div class="flex justify-between items-end mb-1.5">
                  <span class="text-sm font-semibold text-gray-800">Root Zone (10-40cm)</span>
                  <span class="text-sm font-bold text-gray-900">42%</span>
                </div>
                <div class="w-full h-1.5 bg-gray-100 rounded-full overflow-hidden">
                  <div class="h-full bg-brand-dark rounded-full" style="width: 42%"></div>
                </div>
              </div>
              
              <!-- Deep Soil -->
              <div>
                <div class="flex justify-between items-end mb-1.5">
                  <span class="text-sm font-semibold text-gray-800">Deep Soil (40cm+)</span>
                  <span class="text-sm font-bold text-gray-900">65%</span>
                </div>
                <div class="w-full h-1.5 bg-gray-100 rounded-full overflow-hidden">
                  <div class="h-full bg-brand-dark rounded-full" style="width: 65%"></div>
                </div>
              </div>
            </div>

            <!-- Warning Alert -->
            <div class="mt-6 bg-orange-50 border border-orange-100 rounded-xl p-4 flex gap-3 text-orange-800 text-xs leading-relaxed">
              <svg class="w-4 h-4 text-orange-500 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              <div>Surface moisture is critically low. Irrigation cycle suggested for North Sector.</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <PlantForm v-if="showForm" @close="showForm = false" @saved="showForm = false" />
    <DeviceSetupModal v-if="showDeviceModal" @close="showDeviceModal = false" />
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { usePlantStore } from '@/stores/plants.store'
import { useAlertsStore } from '@/stores/alerts.store'
import { useReadingsStore } from '@/stores/readings.store'
import { useI18n } from '@/i18n'
import PlantForm from '@/components/plants/PlantForm.vue'
import DeviceSetupModal from '@/components/devices/DeviceSetupModal.vue'
import type { Plant } from '@/types'

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
  // Fetch latest sensor readings for each plant
  plantsStore.plants.forEach(p => readingsStore.fetchLatest(p.id))
  
  syncInterval = setInterval(async () => {
    await plantsStore.fetchAll(true)
    await alertsStore.fetchAll(true)
    plantsStore.plants.forEach(p => readingsStore.fetchLatest(p.id))
  }, 30000)
})

onUnmounted(() => {
  if (syncInterval) clearInterval(syncInterval)
})

function getPlantEmoji(plant: Plant) {
  const s = plant.species?.toLowerCase() ?? ''
  if (s.includes('cactus')) return '🌵'
  if (s.includes('tomato')) return '🍅'
  if (s.includes('carrot')) return '🥕'
  if (s.includes('lettuce')) return '🥬'
  if (s.includes('wheat')) return '🌾'
  if (s.includes('corn')) return '🌽'
  if (s.includes('rose')) return '🌹'
  if (s.includes('tulip')) return '🌷'
  return '🪴'
}

function getDaysSincePlanted(plant: Plant) {
  const start = new Date(plant.createdAt || Date.now())
  const diff = Date.now() - start.getTime()
  return Math.floor(diff / (1000 * 60 * 60 * 24))
}

function getLifecycleProgress(plant: Plant) {
  const days = getDaysSincePlanted(plant)
  return Math.min((days / 90) * 100, 100)
}
</script>
