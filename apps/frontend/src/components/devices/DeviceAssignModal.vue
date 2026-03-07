<template>
  <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md">
      <div class="flex items-center justify-between p-6 border-b">
        <h3 class="text-lg font-bold text-gray-800">{{ t('sensor.assign_title') }}</h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-2xl leading-none">×</button>
      </div>

      <div class="p-6 space-y-4">
        <div v-if="loading" class="text-center py-4 text-gray-400">{{ t('common.loading') }}</div>
        
        <div v-else-if="unassignedDevices.length === 0" class="text-center py-6 text-gray-500">
          <p class="mb-4">{{ t('sensor.no_unassigned') }}</p>
          <button @click="openSetup" class="text-green-700 underline text-sm">{{ t('sensor.setup_title') }}</button>
        </div>

        <form v-else @submit.prevent="submit" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('sensor.select') }}</label>
            <select v-model="selectedDeviceId" class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-green-500" required>
              <option value="" disabled>{{ t('common.select') }}</option>
              <option v-for="d in unassignedDevices" :key="d.id" :value="d.id">
                {{ d.label || d.deviceId }}
              </option>
            </select>
          </div>

          <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>

          <div class="flex gap-3 pt-4">
            <button type="button" @click="$emit('close')" class="flex-1 border border-gray-300 text-gray-700 rounded-lg py-2 hover:bg-gray-50">
              {{ t('common.cancel') }}
            </button>
            <button type="submit" :disabled="submitting || !selectedDeviceId" class="flex-1 bg-green-700 hover:bg-green-800 text-white rounded-lg py-2 font-medium disabled:opacity-50">
              {{ submitting ? '...' : t('save') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDevicesStore } from '@/stores/devices.store'
import { useI18n } from '@/i18n'

const props = defineProps<{ plantId: string }>()
const emit = defineEmits<{ close: []; assigned: []; openSetup: [] }>()
const { t } = useI18n()
const devicesStore = useDevicesStore()

const loading = ref(false)
const submitting = ref(false)
const error = ref('')
const selectedDeviceId = ref('')

const unassignedDevices = computed(() => {
  return devicesStore.devices.filter((d: any) => !d.plantId)
})

async function fetchDevices() {
  loading.value = true
  await devicesStore.fetchAll()
  loading.value = false
}

async function submit() {
  if (!selectedDeviceId.value) return
  submitting.value = true
  error.value = ''
  try {
    await devicesStore.assign(selectedDeviceId.value, props.plantId)
    emit('assigned')
  } catch (e: any) {
    error.value = e?.response?.data?.error ?? t('error.generic')
  } finally {
    submitting.value = false
  }
}

function openSetup() {
  emit('close')
  emit('openSetup')
}

onMounted(fetchDevices)
</script>
