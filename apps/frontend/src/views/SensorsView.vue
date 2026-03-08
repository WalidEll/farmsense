<template>
  <div>
    <div class="max-w-5xl mx-auto px-4 py-8">
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-xl font-bold text-gray-800">{{ t('nav.devices') }}</h2>
        <div class="flex items-center gap-2">
          <button
            @click="devicesStore.fetchAll()"
            class="flex items-center gap-2 border border-gray-300 text-gray-700 hover:bg-gray-50 px-4 py-2 rounded-lg text-sm font-medium"
            :disabled="devicesStore.loading"
          >
            ↻ {{ t('common.refresh') }}
          </button>
          <router-link
            to="/dashboard"
            class="flex items-center gap-2 bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded-lg text-sm font-medium"
          >
            📡 {{ t('devices.add') }}
          </router-link>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="devicesStore.loading" class="text-center py-16 text-gray-400">
        <div class="text-4xl mb-2">📡</div>
        <p>{{ t('common.loading') }}</p>
      </div>

      <!-- Error -->
      <div v-else-if="devicesStore.error" class="text-center py-16 text-red-500">
        <div class="text-4xl mb-2">⚠️</div>
        <p class="mb-4">{{ devicesStore.error }}</p>
        <button
          @click="devicesStore.fetchAll()"
          class="text-green-700 underline font-medium"
        >
          {{ t('common.retry') }}
        </button>
      </div>

      <!-- Empty state -->
      <div v-else-if="devicesStore.devices.length === 0" class="text-center py-16">
        <div class="text-6xl mb-4">📡</div>
        <p class="text-gray-500 text-lg mb-4">{{ t('devices.empty') }}</p>
        <router-link
          to="/dashboard"
          class="inline-block bg-green-700 hover:bg-green-800 text-white px-6 py-2.5 rounded-lg font-medium"
        >
          {{ t('devices.addFirst') }}
        </router-link>
      </div>

      <!-- Device grid -->
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <DeviceCard
          v-for="device in devicesStore.devices"
          :key="device.id"
          :device="device"
          @click="selectedDevice = device; showDetailModal = true"
        />
      </div>

      <!-- Stats footer -->
      <div v-if="devicesStore.devices.length > 0" class="mt-8 pt-6 border-t border-gray-200">
        <div class="grid grid-cols-2 gap-4 text-sm">
          <div>
            <span class="text-green-600 font-semibold">🟢 {{ devicesStore.onlineCount }}</span>
            <span class="text-gray-500 ml-2">{{ t('devices.online') }}</span>
          </div>
          <div>
            <span class="text-gray-400 font-semibold">🔴 {{ devicesStore.offlineCount }}</span>
            <span class="text-gray-500 ml-2">{{ t('devices.offline') }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Device detail modal -->
    <DeviceDetailModal
      v-if="showDetailModal && selectedDevice"
      :device="selectedDevice"
      @close="showDetailModal = false"
      @updated="onDeviceUpdated"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useDevicesStore } from '@/stores/devices.store'
import { useI18n } from '@/i18n'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import DeviceDetailModal from '@/components/devices/DeviceDetailModal.vue'
import type { Device } from '@/types'

const { t } = useI18n()
const devicesStore = useDevicesStore()

const showDetailModal = ref(false)
const selectedDevice = ref<Device | null>(null)

onMounted(async () => {
  await devicesStore.fetchAll()
})

function onDeviceUpdated(device: Device) {
  selectedDevice.value = device
}
</script>
