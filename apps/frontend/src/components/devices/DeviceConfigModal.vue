<script setup lang="ts">
import { ref, watch } from 'vue'
import { useDevicesStore } from '@/stores/devices.store'
import type { Device, DeviceConfigRequest } from '@/types'
import { useI18n } from '@/i18n'

const props = defineProps<{
  show: boolean
  device: Device | null
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'updated', device: Device): void
}>()

const { t } = useI18n()
const devicesStore = useDevicesStore()

const loading = ref(false)
const config = ref<DeviceConfigRequest>({
  readIntervalMs: 900000,
  soilDryValue: 1024,
  soilWetValue: 300
})
const readIntervalMinutes = ref(15)

watch(
  () => props.show,
  (newVal) => {
    if (newVal && props.device) {
      config.value.readIntervalMs = props.device.readIntervalMs || 900000
      config.value.soilDryValue = props.device.soilDryValue ?? 1024
      config.value.soilWetValue = props.device.soilWetValue ?? 300
      readIntervalMinutes.value = Math.floor(config.value.readIntervalMs / 60000)
    }
  }
)

async function handleSubmit() {
  if (!props.device) return

  loading.value = true
  try {
    const payload: DeviceConfigRequest = {
      readIntervalMs: readIntervalMinutes.value * 60000,
      soilDryValue: config.value.soilDryValue,
      soilWetValue: config.value.soilWetValue
    }
    const updated = await devicesStore.updateConfig(props.device.id, payload)
    emit('updated', updated)
    closeModal()
  } catch (e) {
    console.error('Failed to update config', e)
  } finally {
    loading.value = false
  }
}

function closeModal() {
  emit('close')
}
</script>

<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center p-4">
    <!-- Backdrop overlay -->
    <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="closeModal"></div>

    <!-- Modal dialog -->
    <div class="relative bg-white dark:bg-slate-800 rounded-2xl shadow-xl w-full max-w-md overflow-hidden transform transition-all">
      <div class="px-6 py-4 border-b border-slate-200 dark:border-slate-700 flex items-center justify-between">
        <h3 class="text-lg font-semibold text-slate-900 dark:text-white">
          {{ t('deviceConfig.title') }}
        </h3>
        <button @click="closeModal" class="text-slate-400 hover:text-slate-500 transition-colors">
          <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="p-6">
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
              {{ t('deviceConfig.readInterval') }}
            </label>
            <input
              type="number"
              v-model.number="readIntervalMinutes"
              min="1"
              required
              class="w-full px-4 py-2 rounded-xl border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500 outline-none transition-all"
            />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
              {{ t('deviceConfig.soilDry') }}
            </label>
            <input
              type="number"
              v-model.number="config.soilDryValue"
              min="0"
              max="1024"
              required
              class="w-full px-4 py-2 rounded-xl border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500 outline-none transition-all"
            />
            <p class="text-xs text-slate-500 mt-1">{{ t('deviceConfig.soilDryHelp') }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
              {{ t('deviceConfig.soilWet') }}
            </label>
            <input
              type="number"
              v-model.number="config.soilWetValue"
              min="0"
              max="1024"
              required
              class="w-full px-4 py-2 rounded-xl border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500 outline-none transition-all"
            />
            <p class="text-xs text-slate-500 mt-1">{{ t('deviceConfig.soilWetHelp') }}</p>
          </div>
        </div>

        <div class="mt-8 flex gap-3">
          <button
            type="button"
            @click="closeModal"
            class="flex-1 px-4 py-2 text-slate-700 dark:text-slate-200 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 rounded-xl font-medium transition-colors"
          >
            {{ t('cancel') }}
          </button>
          <button
            type="submit"
            :disabled="loading"
            class="flex-1 px-4 py-2 bg-emerald-500 hover:bg-emerald-600 text-white rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            <svg v-if="loading" class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ t('save') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
