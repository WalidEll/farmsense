<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useI18n } from '@/i18n'
import type { AlertPreference } from '@/types'

const emit = defineEmits<{
  (e: 'close'): void
}>()

const { t } = useI18n()
const loading = ref(false)
const saving = ref(false)
const saved = ref(false)

const pref = ref<AlertPreference>({
  soilDryEnabled: true,
  soilWetEnabled: true,
  tempHighEnabled: true,
  tempLowEnabled: true,
  lightLowEnabled: true,
  deviceOfflineEnabled: true,
  quietHoursStart: undefined,
  quietHoursEnd: undefined,
  channelWhatsapp: true,
  channelPush: true,
})

onMounted(async () => {
  loading.value = true
  try {
    const data = await api.get<AlertPreference>('/alerts/preferences')
    pref.value = data
  } catch (e) {
    console.error('Failed to load alert preferences', e)
  } finally {
    loading.value = false
  }
})

async function handleSave() {
  saving.value = true
  saved.value = false
  try {
    const data = await api.put<AlertPreference>('/alerts/preferences', pref.value)
    pref.value = data
    saved.value = true
    setTimeout(() => { saved.value = false }, 2000)
  } catch (e) {
    console.error('Failed to save alert preferences', e)
  } finally {
    saving.value = false
  }
}

function closeModal() {
  emit('close')
}

const alertTypes = [
  { key: 'soilDryEnabled' as const, icon: '\uD83D\uDCA7', label: 'alerts.pref.soilDry' },
  { key: 'soilWetEnabled' as const, icon: '\uD83C\uDF0A', label: 'alerts.pref.soilWet' },
  { key: 'tempHighEnabled' as const, icon: '\uD83C\uDF21\uFE0F', label: 'alerts.pref.tempHigh' },
  { key: 'tempLowEnabled' as const, icon: '\uD83E\uDD76', label: 'alerts.pref.tempLow' },
  { key: 'lightLowEnabled' as const, icon: '\u2600\uFE0F', label: 'alerts.pref.lightLow' },
  { key: 'deviceOfflineEnabled' as const, icon: '\uD83D\uDCF5', label: 'alerts.pref.deviceOffline' },
]

const hourOptions = Array.from({ length: 24 }, (_, i) => ({
  value: i,
  label: `${i.toString().padStart(2, '0')}:00`,
}))
</script>

<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center p-4">
    <!-- Backdrop overlay -->
    <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="closeModal"></div>

    <!-- Modal dialog -->
    <div class="relative bg-white rounded-2xl shadow-xl w-full max-w-lg overflow-hidden transform transition-all max-h-[90vh] flex flex-col">
      <!-- Header -->
      <div class="px-6 py-4 border-b border-slate-200 flex items-center justify-between flex-shrink-0">
        <div>
          <h3 class="text-lg font-semibold text-slate-900">{{ t('alerts.pref.title') }}</h3>
          <p class="text-sm text-slate-500 mt-0.5">{{ t('alerts.pref.subtitle') }}</p>
        </div>
        <button @click="closeModal" class="text-slate-400 hover:text-slate-500 transition-colors">
          <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="p-8 text-center text-gray-400">
        <p>{{ t('common.loading') }}</p>
      </div>

      <!-- Form -->
      <div v-else class="overflow-y-auto p-6 space-y-6">
        <!-- Alert type toggles -->
        <div>
          <h4 class="text-sm font-semibold text-slate-700 mb-3">{{ t('alerts.filter.severity') }}</h4>
          <div class="space-y-2">
            <label
              v-for="at in alertTypes"
              :key="at.key"
              class="flex items-center justify-between p-3 rounded-xl border border-slate-200 hover:border-green-300 transition-colors cursor-pointer"
            >
              <div class="flex items-center gap-3">
                <span class="text-xl">{{ at.icon }}</span>
                <span class="text-sm font-medium text-slate-700">{{ t(at.label) }}</span>
              </div>
              <div class="relative">
                <input
                  type="checkbox"
                  v-model="pref[at.key]"
                  class="sr-only peer"
                />
                <div class="w-10 h-6 bg-slate-200 peer-checked:bg-green-600 rounded-full transition-colors"></div>
                <div class="absolute top-0.5 start-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform peer-checked:translate-x-4 rtl:peer-checked:-translate-x-4"></div>
              </div>
            </label>
          </div>
        </div>

        <!-- Quiet hours -->
        <div>
          <h4 class="text-sm font-semibold text-slate-700 mb-3">{{ t('alerts.pref.quietHours') }}</h4>
          <p class="text-xs text-slate-500 mb-3">{{ t('alerts.pref.subtitle') }}</p>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-medium text-slate-600 mb-1">
                {{ t('alerts.pref.quietStart') }}
              </label>
              <select
                v-model="pref.quietHoursStart"
                class="w-full px-3 py-2 rounded-xl border border-slate-300 bg-white text-slate-900 text-sm focus:ring-2 focus:ring-green-500 outline-none transition-all"
              >
                <option :value="undefined">--</option>
                <option v-for="h in hourOptions" :key="h.value" :value="h.value">
                  {{ h.label }}
                </option>
              </select>
            </div>
            <div>
              <label class="block text-xs font-medium text-slate-600 mb-1">
                {{ t('alerts.pref.quietEnd') }}
              </label>
              <select
                v-model="pref.quietHoursEnd"
                class="w-full px-3 py-2 rounded-xl border border-slate-300 bg-white text-slate-900 text-sm focus:ring-2 focus:ring-green-500 outline-none transition-all"
              >
                <option :value="undefined">--</option>
                <option v-for="h in hourOptions" :key="h.value" :value="h.value">
                  {{ h.label }}
                </option>
              </select>
            </div>
          </div>
        </div>

        <!-- Channels -->
        <div>
          <h4 class="text-sm font-semibold text-slate-700 mb-3">{{ t('alerts.pref.channels') }}</h4>
          <div class="space-y-2">
            <label class="flex items-center justify-between p-3 rounded-xl border border-slate-200 hover:border-green-300 transition-colors cursor-pointer">
              <div class="flex items-center gap-3">
                <span class="text-xl">&#128172;</span>
                <span class="text-sm font-medium text-slate-700">{{ t('alerts.pref.whatsapp') }}</span>
              </div>
              <div class="relative">
                <input
                  type="checkbox"
                  v-model="pref.channelWhatsapp"
                  class="sr-only peer"
                />
                <div class="w-10 h-6 bg-slate-200 peer-checked:bg-green-600 rounded-full transition-colors"></div>
                <div class="absolute top-0.5 start-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform peer-checked:translate-x-4 rtl:peer-checked:-translate-x-4"></div>
              </div>
            </label>
            <label class="flex items-center justify-between p-3 rounded-xl border border-slate-200 hover:border-green-300 transition-colors cursor-pointer">
              <div class="flex items-center gap-3">
                <span class="text-xl">&#128276;</span>
                <span class="text-sm font-medium text-slate-700">{{ t('alerts.pref.push') }}</span>
              </div>
              <div class="relative">
                <input
                  type="checkbox"
                  v-model="pref.channelPush"
                  class="sr-only peer"
                />
                <div class="w-10 h-6 bg-slate-200 peer-checked:bg-green-600 rounded-full transition-colors"></div>
                <div class="absolute top-0.5 start-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform peer-checked:translate-x-4 rtl:peer-checked:-translate-x-4"></div>
              </div>
            </label>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div v-if="!loading" class="px-6 py-4 border-t border-slate-200 flex items-center gap-3 flex-shrink-0">
        <button
          type="button"
          @click="closeModal"
          class="flex-1 px-4 py-2 text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-xl font-medium transition-colors"
        >
          {{ t('common.cancel') }}
        </button>
        <button
          @click="handleSave"
          :disabled="saving"
          class="flex-1 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
        >
          <svg v-if="saving" class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          {{ t('common.save') }}
        </button>
      </div>

      <!-- Saved toast -->
      <div
        v-if="saved"
        class="absolute bottom-20 inset-x-0 flex justify-center"
      >
        <div class="bg-green-600 text-white text-sm font-medium px-4 py-2 rounded-full shadow-lg">
          &#10003; {{ t('alerts.pref.saved') }}
        </div>
      </div>
    </div>
  </div>
</template>
