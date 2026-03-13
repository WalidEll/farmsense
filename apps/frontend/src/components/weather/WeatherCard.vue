<template>
  <div>
    <!-- No location set → Setup prompt -->
    <div v-if="!authStore.hasLocation" class="bg-white rounded-xl border border-blue-200 p-5 mb-6">
      <div class="text-center">
        <div class="text-3xl mb-2">🌤️</div>
        <p class="text-gray-600 mb-4">{{ t('weather.setLocation') }}</p>

        <!-- Manual input form -->
        <div v-if="showManualInput" class="max-w-sm mx-auto space-y-3 mb-4">
          <div class="flex gap-2">
            <div class="flex-1">
              <label class="text-xs text-gray-500 block mb-1">{{ t('weather.latitude') }}</label>
              <input
                v-model.number="manualLat"
                type="number"
                step="0.01"
                min="-90"
                max="90"
                class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="33.57"
              />
            </div>
            <div class="flex-1">
              <label class="text-xs text-gray-500 block mb-1">{{ t('weather.longitude') }}</label>
              <input
                v-model.number="manualLng"
                type="number"
                step="0.01"
                min="-180"
                max="180"
                class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="-7.59"
              />
            </div>
          </div>
          <button
            @click="saveManualLocation"
            :disabled="savingLocation"
            class="w-full bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-50"
          >
            {{ savingLocation ? '...' : t('weather.saveLocation') }}
          </button>
        </div>

        <div class="flex gap-3 justify-center">
          <button
            @click="autoDetectLocation"
            :disabled="detectingLocation"
            class="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-50"
          >
            📍 {{ detectingLocation ? '...' : t('weather.autoDetect') }}
          </button>
          <button
            @click="showManualInput = !showManualInput"
            class="flex items-center gap-2 border border-gray-300 text-gray-700 hover:bg-gray-50 px-4 py-2 rounded-lg text-sm font-medium"
          >
            ✏️ {{ t('weather.enterManually') }}
          </button>
        </div>

        <p v-if="locationError" class="text-red-500 text-sm mt-3">{{ t('weather.locationError') }}</p>
      </div>
    </div>

    <!-- Loading state -->
    <div v-else-if="loading" class="mb-6">
      <div class="bg-gradient-to-r from-blue-500 to-blue-600 rounded-xl p-5 animate-pulse">
        <div class="h-16 bg-white/20 rounded-lg"></div>
      </div>
    </div>

    <!-- Weather loaded -->
    <div v-else-if="weather" class="mb-6">
      <div class="bg-gradient-to-r from-blue-500 to-blue-600 rounded-xl p-5 text-white shadow-lg">
        <!-- Current weather -->
        <div class="flex items-center justify-between mb-4">
          <div>
            <div class="flex items-center gap-3">
              <span class="text-4xl">{{ weather.weatherIcon }}</span>
              <div>
                <div class="text-3xl font-bold">{{ Math.round(weather.temperature) }}°C</div>
                <div class="text-blue-100 text-sm">{{ weather.weatherDescription }}</div>
              </div>
            </div>
          </div>
          <div class="text-right text-sm text-blue-100 space-y-1">
            <div>💧 {{ t('weather.humidity') }}: {{ Math.round(weather.humidity) }}%</div>
            <div>💨 {{ t('weather.wind') }}: {{ weather.windSpeed }} km/h</div>
            <div>🌧️ {{ t('weather.precipitation') }}: {{ weather.precipitation }} mm</div>
          </div>
        </div>

        <!-- 7-day forecast -->
        <div v-if="weather.daily && weather.daily.length > 0">
          <div class="text-xs text-blue-200 mb-2 font-medium">{{ t('weather.forecast') }}</div>
          <div class="flex gap-2 overflow-x-auto pb-1 -mx-1 px-1">
            <div
              v-for="day in weather.daily"
              :key="day.date"
              class="flex-shrink-0 bg-white/15 backdrop-blur rounded-lg px-3 py-2 text-center min-w-[70px]"
            >
              <div class="text-xs text-blue-100">{{ formatDayName(day.date) }}</div>
              <div class="text-lg my-1">{{ day.weatherIcon }}</div>
              <div class="text-xs font-medium">
                <span>{{ Math.round(day.tempMax) }}°</span>
                <span class="text-blue-200 ml-1">{{ Math.round(day.tempMin) }}°</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Error state (silent — just don't show) -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import { api } from '@/services/api'
import { useI18n } from '@/i18n'
import type { WeatherData } from '@/types'

const { t } = useI18n()
const authStore = useAuthStore()

const weather = ref<WeatherData | null>(null)
const loading = ref(false)
const showManualInput = ref(false)
const manualLat = ref<number | null>(null)
const manualLng = ref<number | null>(null)
const savingLocation = ref(false)
const detectingLocation = ref(false)
const locationError = ref(false)

onMounted(async () => {
  if (authStore.hasLocation) {
    await fetchWeather()
  }
})

async function fetchWeather() {
  loading.value = true
  try {
    weather.value = await api.get<WeatherData>('/weather')
  } catch (e) {
    console.error('Failed to fetch weather:', e)
    // Silent fail — weather is non-critical
  } finally {
    loading.value = false
  }
}

async function autoDetectLocation() {
  if (!navigator.geolocation) {
    locationError.value = true
    return
  }

  detectingLocation.value = true
  locationError.value = false

  try {
    const position = await new Promise<GeolocationPosition>((resolve, reject) => {
      navigator.geolocation.getCurrentPosition(resolve, reject, {
        enableHighAccuracy: false,
        timeout: 10000,
        maximumAge: 600000,
      })
    })

    await authStore.updateLocation(position.coords.latitude, position.coords.longitude)
    await fetchWeather()
  } catch (e) {
    console.error('Geolocation failed:', e)
    locationError.value = true
  } finally {
    detectingLocation.value = false
  }
}

async function saveManualLocation() {
  if (manualLat.value == null || manualLng.value == null) return
  if (manualLat.value < -90 || manualLat.value > 90) return
  if (manualLng.value < -180 || manualLng.value > 180) return

  savingLocation.value = true
  try {
    await authStore.updateLocation(manualLat.value, manualLng.value)
    showManualInput.value = false
    await fetchWeather()
  } catch (e) {
    console.error('Failed to save location:', e)
  } finally {
    savingLocation.value = false
  }
}

function formatDayName(dateStr: string): string {
  const date = new Date(dateStr + 'T00:00:00')
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  const diff = Math.floor((date.getTime() - today.getTime()) / 86400000)
  if (diff === 0) return "Auj."
  if (diff === 1) return "Dem."

  return date.toLocaleDateString('fr-FR', { weekday: 'short' })
}
</script>
