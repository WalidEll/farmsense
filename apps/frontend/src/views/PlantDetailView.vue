<template>
  <div>
    <div class="max-w-4xl mx-auto px-4 py-8">
      <!-- Back -->
      <button @click="router.push('/')" class="flex items-center gap-1 text-gray-500 hover:text-green-700 mb-6 text-sm">
        ← {{ t('common.back') }}
      </button>

      <div v-if="!plant" class="text-center py-16 text-gray-400">{{ t('common.loading') }}</div>

      <template v-else>
        <!-- Plant header -->
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-800">{{ plant.name }}</h2>
            <div class="flex items-center gap-3 mt-1">
              <p v-if="plant.species" class="text-gray-500 text-sm">{{ plant.species }}</p>
              
              <!-- Device status chip -->
              <div v-if="assignedDevice" class="flex items-center gap-1">
                <button @click="showAssign = true"
                     class="flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-medium border hover:bg-gray-100 transition-colors"
                     :class="assignedDevice.online ? 'bg-green-50 text-green-700 border-green-200' : 'bg-gray-50 text-gray-600 border-gray-200'"
                     :title="t('plantDetail.reassign')">
                  <span class="w-1.5 h-1.5 rounded-full" :class="assignedDevice.online ? 'bg-green-500' : 'bg-gray-400'"></span>
                  {{ assignedDevice.label || assignedDevice.deviceId }}
                </button>
                <button @click="showConfig = true" class="p-1 text-gray-400 hover:text-green-600 transition-colors rounded-full hover:bg-gray-100" :title="t('plantDetail.deviceSettings')">
                  <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  </svg>
                </button>
              </div>
              <button v-else @click="showAssign = true" 
                      class="text-xs bg-gray-100 hover:bg-gray-200 text-gray-700 px-3 py-1 rounded-full font-medium transition-colors">
                + {{ t('sensor.assign') }}
              </button>
            </div>
          </div>
          <button @click="showEdit = true" class="text-sm text-green-700 hover:underline">
            {{ t('common.edit') }}
          </button>
        </div>

        <!-- Latest sensor cards -->
        <div class="flex items-center justify-between mb-3" v-if="latestReading">
          <div class="flex items-center gap-2">
            <span v-if="latestReading.source === 'MANUAL'" class="text-xs bg-purple-100 text-purple-700 px-2 py-0.5 rounded-full font-medium">
              ✏️ {{ t('manual_indicator') }}
            </span>
          </div>
          <button @click="showManual = true" class="text-xs bg-purple-50 hover:bg-purple-100 text-purple-700 px-3 py-1.5 rounded-full font-medium transition-colors flex items-center gap-1">
            ✏️ {{ t('manual_add') }}
          </button>
        </div>
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-8" v-if="latestReading">
          <SensorCard icon="💧" :label="t('sensors.soil')" :value="latestReading.soilMoisture" unit="%" :min="plant.soilMin" :max="plant.soilMax" />
          <SensorCard icon="🌡️" :label="t('sensors.temp')" :value="latestReading.temperature" unit="°C" :min="plant.tempMin" :max="plant.tempMax" />
          <SensorCard icon="💦" :label="t('sensors.humidity')" :value="latestReading.humidity" unit="%" />
          <SensorCard icon="☀️" :label="t('sensors.light')" :value="latestReading.lightLux" unit=" lux" :min="plant.lightMin" />
        </div>
        <div v-else class="bg-white rounded-xl p-6 text-center text-gray-400 mb-8 border border-dashed border-gray-300">
          <p>{{ t('sensors.noData') }}</p>
          <button @click="showManual = true" class="mt-3 text-sm bg-purple-50 hover:bg-purple-100 text-purple-700 px-4 py-2 rounded-lg font-medium transition-colors">
            ✏️ {{ t('manual_add') }}
          </button>
        </div>

        <!-- Care Schedule -->
        <CareScheduleCard :plant-id="plant.id" class="mb-6" />

        <!-- History chart -->
        <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="font-semibold text-gray-700">{{ t('sensors.history') }}</h3>
            <div class="flex gap-1">
              <button
                v-for="h in [24, 168, 720]"
                :key="h"
                @click="changeHours(h)"
                :class="[
                  'px-3 py-1 rounded text-xs font-medium',
                  hours === h ? 'bg-green-700 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200',
                ]"
              >
                {{ h === 24 ? '24h' : h === 168 ? '7d' : '30d' }}
              </button>
            </div>
          </div>
          <SensorChart :readings="historyReadings" :hours="hours" />
        </div>

        <!-- Alerts for this plant -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <h3 class="font-semibold text-gray-700 mb-4">{{ t('nav.alerts') }}</h3>
          <AlertFeed :plant-id="plant.id" :limit="10" />
        </div>
      </template>

      <!-- Edit modal -->
      <PlantForm v-if="showEdit" :plant="plant" @close="showEdit = false" @saved="showEdit = false" />

      <!-- Manual reading modal -->
      <ManualReadingForm v-if="showManual" :plant-id="plantId" @close="showManual = false" @saved="onManualSaved" />
      
      <!-- Device modals -->
      <DeviceAssignModal 
        v-if="showAssign" 
        :plant-id="plant.id" 
        @close="showAssign = false" 
        @assigned="showAssign = false" 
        @openSetup="showAssign = false; showSetup = true" 
      />
      <DeviceSetupModal v-if="showSetup" @close="showSetup = false" />
      <DeviceConfigModal
        v-if="showConfig"
        :show="showConfig"
        :device="assignedDevice"
        @close="showConfig = false"
        @updated="showConfig = false"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePlantStore } from '@/stores/plants.store'
import { useReadingsStore } from '@/stores/readings.store'
import { useDevicesStore } from '@/stores/devices.store'
import { useI18n } from '@/i18n'
import SensorCard from '@/components/sensors/SensorCard.vue'
import SensorChart from '@/components/sensors/SensorChart.vue'
import PlantForm from '@/components/plants/PlantForm.vue'
import AlertFeed from '@/components/alerts/AlertFeed.vue'
import DeviceAssignModal from '@/components/devices/DeviceAssignModal.vue'
import DeviceSetupModal from '@/components/devices/DeviceSetupModal.vue'
import DeviceConfigModal from '@/components/devices/DeviceConfigModal.vue'
import CareScheduleCard from '@/components/plants/CareScheduleCard.vue'
import ManualReadingForm from '@/components/sensors/ManualReadingForm.vue'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const plantsStore = usePlantStore()
const readingsStore = useReadingsStore()
const devicesStore = useDevicesStore()

const plantId = route.params.id as string
const showEdit = ref(false)
const showAssign = ref(false)
const showSetup = ref(false)
const showConfig = ref(false)
const showManual = ref(false)
const hours = ref(24)

const plant = computed(() => plantsStore.plants.find((p: any) => p.id === plantId))
const assignedDevice = computed(() => devicesStore.devices.find((d: any) => d.plantId === plantId))
const latestReading = computed(() => readingsStore.latest[plantId])
const historyReadings = computed(() => readingsStore.history[plantId] ?? [])

async function changeHours(h: number) {
  hours.value = h
  await readingsStore.fetchHistory(plantId, h)
}

async function onManualSaved() {
  showManual.value = false
  await fetchAllData()
}

let syncInterval: ReturnType<typeof setInterval>

async function fetchAllData(silent = false) {
  await Promise.all([
    readingsStore.fetchLatest(plantId),
    readingsStore.fetchHistory(plantId, hours.value, silent),
    devicesStore.fetchAll(silent)
  ])
}

onMounted(async () => {
  if (!plantsStore.plants.length) await plantsStore.fetchAll()
  await fetchAllData()
  syncInterval = setInterval(() => fetchAllData(true), 15000)
})

onUnmounted(() => {
  if (syncInterval) clearInterval(syncInterval)
})
</script>
