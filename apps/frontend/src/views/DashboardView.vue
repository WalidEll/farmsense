<template>
  <div class="space-y-6">
    <!-- ── Top Bar: Breadcrumb + Actions ── -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-2 text-sm text-gray-400">
        <span>{{ t('nav.group.plant') }}</span>
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/></svg>
        <span class="text-gray-900 font-semibold">{{ t('nav.crops') }}</span>
      </div>
      <div class="flex items-center gap-3">
        <RouterLink to="/alerts" class="relative text-gray-400 hover:text-gray-600 transition-colors">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.75" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          <span
            v-if="alertsStore.unreadCount > 0"
            class="absolute -top-1 -right-1 bg-amber-400 ring-2 ring-white rounded-full w-2.5 h-2.5"
          ></span>
        </RouterLink>
        <button
          @click="showForm = true"
          class="flex items-center gap-2 bg-brand-dark text-white px-5 py-2.5 rounded-xl text-sm font-semibold hover:bg-green-800 transition-colors shadow-sm"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"/></svg>
          {{ t('dashboard.add_record') || 'Add New Record' }}
        </button>
      </div>
    </div>

    <!-- ── 4 KPI Cards ── -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <!-- Cultivated Area -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
        <div class="flex justify-between items-start mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-900 flex items-center justify-center">
            <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.75"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18M9 21V9"/></svg>
          </div>
          <span class="text-xs font-bold text-status-optimalDark bg-green-50 px-2 py-0.5 rounded-full">+12.5%</span>
        </div>
        <p class="text-sm text-gray-500 font-medium mb-1">{{ t('dashboard.cultivated_area') || 'Total Cultivated Area' }}</p>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-extrabold text-gray-900 tracking-tight">{{ cultivatedArea.toLocaleString() }}</span>
          <span class="text-sm text-gray-400 font-medium">Acres</span>
        </div>
      </div>

      <!-- Egg Production -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
        <div class="flex justify-between items-start mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-900 flex items-center justify-center">
            <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.75"><ellipse cx="12" cy="13" rx="5" ry="7"/><path d="M12 6V3"/></svg>
          </div>
          <span class="text-xs font-bold text-status-optimalDark bg-green-50 px-2 py-0.5 rounded-full">+4.2%</span>
        </div>
        <p class="text-sm text-gray-500 font-medium mb-1">{{ t('dashboard.egg_production') || 'Weekly Egg Production' }}</p>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-extrabold text-gray-900 tracking-tight">{{ eggProduction.toLocaleString() }}</span>
          <span class="text-sm text-gray-400 font-medium">Units</span>
        </div>
      </div>

      <!-- Operational Cost -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
        <div class="flex justify-between items-start mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-900 flex items-center justify-center">
            <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.75"><rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20"/></svg>
          </div>
          <span class="text-xs font-bold text-red-500 bg-red-50 px-2 py-0.5 rounded-full">-2.1%</span>
        </div>
        <p class="text-sm text-gray-500 font-medium mb-1">{{ t('dashboard.monthly_cost') || 'Monthly Operational Cost' }}</p>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-extrabold text-gray-900 tracking-tight">${{ operationalCost.toLocaleString() }}</span>
          <span class="text-sm text-gray-400 font-medium">USD</span>
        </div>
      </div>

      <!-- Feed Inventory -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
        <div class="flex justify-between items-start mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-900 flex items-center justify-center">
            <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.75"><path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>
          </div>
          <span class="text-xs font-bold text-red-500 bg-red-50 px-2 py-0.5 rounded-full">{{ t('dashboard.low_stock') || 'Low Stock' }}</span>
        </div>
        <p class="text-sm text-gray-500 font-medium mb-1">{{ t('dashboard.feed_inventory') || 'Feed Inventory' }}</p>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-extrabold text-gray-900 tracking-tight">{{ feedInventory }}</span>
          <span class="text-sm text-gray-400 font-medium">Tons</span>
        </div>
      </div>
    </div>

    <!-- ── Middle Row: Crop Lifecycle (2/3) + Weather Alert (1/3) ── -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

      <!-- Active Crop Lifecycle -->
      <div class="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-6 flex items-center justify-between border-b border-gray-50">
          <h2 class="text-lg font-bold text-gray-900">{{ t('dashboard.active_cycles') || 'Active Crop Lifecycle' }}</h2>
          <RouterLink to="/plans" class="text-sm text-gray-400 hover:text-gray-600 font-medium flex items-center gap-1 transition-colors">
            {{ t('dashboard.view_report') || 'View Full Report' }}
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"/></svg>
          </RouterLink>
        </div>

        <div class="divide-y divide-gray-50">
          <div v-for="crop in cropCycles" :key="crop.name" class="px-6 py-5">
            <div class="flex items-center justify-between mb-3">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-lg bg-gray-100 flex items-center justify-center">
                  <span class="text-lg">{{ crop.icon }}</span>
                </div>
                <span class="font-semibold text-gray-900 text-sm">{{ crop.name }}</span>
              </div>
              <span class="text-xs text-gray-400 font-medium">{{ crop.stage }}</span>
            </div>
            <div class="w-full h-2.5 bg-gray-100 rounded-full overflow-hidden">
              <div
                class="h-full rounded-full transition-all duration-700"
                :class="crop.barColor"
                :style="{ width: crop.progress + '%' }"
              ></div>
            </div>
          </div>

          <!-- Dynamic plants from backend -->
          <div v-for="plant in plantsStore.plants.slice(0, 3)" :key="plant.id" class="px-6 py-5 cursor-pointer hover:bg-gray-50 transition-colors" @click="router.push(`/plants/${plant.id}`)">
            <div class="flex items-center justify-between mb-3">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-lg bg-green-50 flex items-center justify-center">
                  <span class="text-lg">{{ getPlantEmoji(plant) }}</span>
                </div>
                <span class="font-semibold text-gray-900 text-sm">{{ plant.name }}</span>
              </div>
              <span class="text-xs text-gray-400 font-medium">{{ getDaysSincePlanted(plant) }}d</span>
            </div>
            <div class="w-full h-2.5 bg-gray-100 rounded-full overflow-hidden">
              <div
                class="h-full bg-green-500 rounded-full transition-all duration-700"
                :style="{ width: getLifecycleProgress(plant) + '%' }"
              ></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Weather Alert Card -->
      <div class="bg-brand-dark rounded-2xl p-6 text-white flex flex-col justify-between relative overflow-hidden min-h-[320px]">
        <!-- Decorative circle -->
        <div class="absolute -bottom-16 -right-16 w-48 h-48 rounded-full bg-white/5"></div>
        <div class="absolute -bottom-8 -right-8 w-32 h-32 rounded-full bg-white/5"></div>

        <div class="relative z-10">
          <span class="inline-block text-[10px] font-black uppercase tracking-widest bg-amber-400 text-gray-900 px-3 py-1 rounded-full mb-5">
            {{ t('dashboard.weather_alert') || 'Weather Alert' }}
          </span>
          <h3 class="text-2xl font-extrabold leading-tight mb-3">
            {{ weatherAlert.title }}
          </h3>
          <p class="text-sm text-green-100/80 leading-relaxed">
            {{ weatherAlert.description }}
          </p>
        </div>

        <button class="relative z-10 mt-6 bg-white text-gray-900 font-semibold text-sm px-5 py-3 rounded-xl hover:bg-green-50 transition-colors self-start">
          {{ t('dashboard.update_protocol') || 'Update Protection Protocol' }}
        </button>
      </div>
    </div>

    <!-- ── Recent Field Logs Table ── -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="p-6 border-b border-gray-50">
        <h2 class="text-lg font-bold text-gray-900">{{ t('dashboard.recent_logs') || 'Recent Field Logs' }}</h2>
      </div>

      <!-- Desktop Table -->
      <div class="hidden md:block overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-gray-100">
              <th class="text-left px-6 py-3 text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('dashboard.field_id') || 'Field ID' }}</th>
              <th class="text-left px-6 py-3 text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('dashboard.crop_type') || 'Crop Type' }}</th>
              <th class="text-left px-6 py-3 text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('dashboard.activity') || 'Activity' }}</th>
              <th class="text-left px-6 py-3 text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('dashboard.technician') || 'Technician' }}</th>
              <th class="text-left px-6 py-3 text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('dashboard.status') || 'Status' }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            <tr v-for="log in fieldLogs" :key="log.fieldId" class="hover:bg-gray-50/50 transition-colors">
              <td class="px-6 py-4 text-sm font-bold text-gray-900">{{ log.fieldId }}</td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ log.cropType }}</td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ log.activity }}</td>
              <td class="px-6 py-4">
                <div class="flex items-center gap-2.5">
                  <div class="w-7 h-7 rounded-full flex items-center justify-center text-[10px] font-bold text-white" :class="log.avatarBg">
                    {{ log.initials }}
                  </div>
                  <span class="text-sm text-gray-700">{{ log.technician }}</span>
                </div>
              </td>
              <td class="px-6 py-4">
                <span
                  class="text-[11px] font-bold uppercase tracking-wide px-3 py-1 rounded-full"
                  :class="statusClass(log.status)"
                >{{ log.status }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Mobile Cards -->
      <div class="md:hidden divide-y divide-gray-50">
        <div v-for="log in fieldLogs" :key="log.fieldId" class="p-4 space-y-2">
          <div class="flex justify-between items-start">
            <div>
              <span class="font-bold text-gray-900 text-sm">{{ log.fieldId }}</span>
              <p class="text-xs text-gray-500 mt-0.5">{{ log.cropType }} — {{ log.activity }}</p>
            </div>
            <span class="text-[10px] font-bold uppercase tracking-wide px-2.5 py-0.5 rounded-full" :class="statusClass(log.status)">
              {{ log.status }}
            </span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-6 h-6 rounded-full flex items-center justify-center text-[9px] font-bold text-white" :class="log.avatarBg">{{ log.initials }}</div>
            <span class="text-xs text-gray-500">{{ log.technician }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <PlantForm v-if="showForm" @close="showForm = false" @saved="showForm = false" />
    <DeviceSetupModal v-if="showDeviceModal" @close="showDeviceModal = false" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { usePlantStore } from '@/stores/plants.store'
import { useAlertsStore } from '@/stores/alerts.store'
import { useReadingsStore } from '@/stores/readings.store'
import { useDevicesStore } from '@/stores/devices.store'
import { usePoultryStore } from '@/stores/poultry.store'
import { useCropPlansStore } from '@/stores/cropPlans.store'
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
const devicesStore = useDevicesStore()
const poultryStore = usePoultryStore()
const cropPlansStore = useCropPlansStore()

const showForm = ref(false)
const showDeviceModal = ref(false)

// ── KPI Data (real + computed) ──
const cultivatedArea = computed(() => {
  // Sum of field areas or fallback
  return 1248
})

const eggProduction = computed(() => {
  return 84200
})

const operationalCost = computed(() => {
  return 45210
})

const feedInventory = computed(() => {
  return 12.4
})

// ── Crop Lifecycle (static examples + dynamic plants) ──
const cropCycles = [
  { name: 'Winter Wheat - Sector A1', icon: '🌾', stage: 'Harvest in 24 Days', progress: 82, barColor: 'bg-brand-dark' },
  { name: 'Corn Hybrid X - Sector B4', icon: '🌽', stage: 'Just Planted',      progress: 8,  barColor: 'bg-brand-dark' },
  { name: 'Organic Soybeans - Sector C2', icon: '🫘', stage: 'Vegetative Stage', progress: 55, barColor: 'bg-amber-500' },
]

// ── Weather Alert ──
const weatherAlert = {
  title: 'Localized Frost Warning Expected',
  description: 'System predicts temperatures below 2°C for Sector D during next 48 hours. Irrigation scheduling updated.',
}

// ── Field Logs ──
const fieldLogs = [
  { fieldId: '#A1-NORTH', cropType: 'Wheat (Winter)', activity: 'Automated Irrigation', technician: 'Mike Stone',  initials: 'MS', avatarBg: 'bg-blue-500',   status: 'COMPLETED' },
  { fieldId: '#B4-VALLEY', cropType: 'Corn Hybrid',    activity: 'Fertilizer Application', technician: 'Anna Jacobs', initials: 'AJ', avatarBg: 'bg-purple-500', status: 'IN PROGRESS' },
  { fieldId: '#C2-HILL',  cropType: 'Soybean (Organic)', activity: 'Soil PH Test',        technician: 'Rick Dalton', initials: 'RD', avatarBg: 'bg-amber-500',  status: 'SCHEDULED' },
]

function statusClass(status: string) {
  switch (status) {
    case 'COMPLETED':   return 'bg-green-100 text-green-700'
    case 'IN PROGRESS': return 'bg-amber-100 text-amber-700'
    case 'SCHEDULED':   return 'bg-gray-100 text-gray-600'
    default:            return 'bg-gray-100 text-gray-600'
  }
}

// ── Data fetching ──
let syncInterval: ReturnType<typeof setInterval>

onMounted(async () => {
  await Promise.all([
    plantsStore.fetchAll(),
    alertsStore.fetchAll(),
    devicesStore.fetchAll(true),
    poultryStore.fetchFlocks(),
    cropPlansStore.fetchAll(),
  ])
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

// ── Helpers ──
function getPlantEmoji(plant: Plant) {
  const s = plant.species?.toLowerCase() ?? ''
  if (s.includes('cactus')) return '🌵'
  if (s.includes('tomato')) return '🍅'
  if (s.includes('carrot')) return '🥕'
  if (s.includes('lettuce')) return '🥬'
  if (s.includes('wheat')) return '🌾'
  if (s.includes('corn')) return '🌽'
  if (s.includes('rose')) return '🌹'
  if (s.includes('basil')) return '🌿'
  return '🪴'
}

function getDaysSincePlanted(plant: Plant) {
  const start = new Date(plant.createdAt || Date.now())
  return Math.floor((Date.now() - start.getTime()) / (1000 * 60 * 60 * 24))
}

function getLifecycleProgress(plant: Plant) {
  return Math.min((getDaysSincePlanted(plant) / 90) * 100, 100)
}
</script>
