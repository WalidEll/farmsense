<template>
  <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-2xl max-h-[90vh] overflow-hidden flex flex-col">
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b">
        <div>
          <h3 class="text-lg font-bold text-gray-800">{{ device.label || device.deviceId }}</h3>
          <p class="text-xs text-gray-500 font-mono mt-1">{{ device.deviceId }}</p>
        </div>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-2xl leading-none">×</button>
      </div>

      <!-- Tabs -->
      <div class="flex gap-1 px-6 pt-4 border-b bg-gray-50">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'px-4 py-2 text-sm font-medium rounded-t border-b-2 transition-colors',
            activeTab === tab.id
              ? 'border-green-600 text-green-700 bg-white'
              : 'border-transparent text-gray-600 hover:text-gray-800'
          ]"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- Content -->
      <div class="overflow-y-auto flex-1 p-6">
        <!-- Info Tab -->
        <div v-if="activeTab === 'info'" class="space-y-4">
          <InfoRow label="Device ID">
            <code class="bg-gray-100 px-2 py-1 rounded text-sm">{{ device.deviceId }}</code>
          </InfoRow>
          <InfoRow label="Status">
            <span :class="device.online ? 'text-green-600 font-semibold' : 'text-gray-400 font-semibold'">
              {{ device.online ? '🟢 Online' : '🔴 Offline' }}
            </span>
          </InfoRow>
          <InfoRow label="Last Seen">
            {{ device.lastSeenAt ? new Date(device.lastSeenAt).toLocaleString() : 'Never' }}
          </InfoRow>
          <InfoRow label="Claimed">
            {{ device.claimedAt ? new Date(device.claimedAt).toLocaleString() : 'Not claimed' }}
          </InfoRow>
          <InfoRow label="Read Interval">
            {{ device.readIntervalMs ? (device.readIntervalMs / 1000 / 60).toFixed(0) : '—' }} min
          </InfoRow>
          <InfoRow label="Assigned Plant">
            <span v-if="device.plantName" class="font-medium text-green-700">
              → {{ device.plantName }}
            </span>
            <span v-else class="text-gray-500">Not assigned</span>
          </InfoRow>
        </div>

        <!-- Chart Tab -->
        <div v-else-if="activeTab === 'chart'" class="flex items-center justify-center h-64 text-gray-400">
          <p>📊 Chart visualization coming soon</p>
        </div>

        <!-- Settings Tab -->
        <div v-else-if="activeTab === 'settings'" class="space-y-5">
          <!-- Rename -->
          <SettingSection title="Device Name">
            <div class="flex gap-2">
              <input
                v-model="editLabel"
                type="text"
                class="flex-1 border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                placeholder="Device name"
              />
              <button
                @click="saveLabel"
                :disabled="renaming"
                class="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded-lg font-medium disabled:opacity-50"
              >
                {{ renaming ? '...' : 'Save' }}
              </button>
            </div>
          </SettingSection>

          <!-- Assign to plant -->
          <SettingSection title="Assign to Plant">
            <div class="flex gap-2">
              <select
                v-model="selectedPlantId"
                class="flex-1 border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
              >
                <option value="">-- Select plant --</option>
                <option v-for="plant in plants" :key="plant.id" :value="plant.id">
                  {{ plant.name }}
                </option>
              </select>
              <button
                @click="saveAssignment"
                :disabled="assigning"
                class="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded-lg font-medium disabled:opacity-50"
              >
                {{ assigning ? '...' : 'Assign' }}
              </button>
            </div>
          </SettingSection>

          <!-- Unassign -->
          <SettingSection v-if="device.plantId" title="Current Assignment">
            <button
              @click="unassignPlant"
              :disabled="assigning"
              class="w-full border border-orange-300 text-orange-700 hover:bg-orange-50 px-4 py-2 rounded-lg font-medium disabled:opacity-50"
            >
              {{ assigning ? '...' : '✕ Unassign from Plant' }}
            </button>
          </SettingSection>

          <!-- Delete -->
          <SettingSection title="Danger Zone" class-name="pt-4 border-t">
            <button
              @click="confirmDelete"
              class="w-full border border-red-300 text-red-700 hover:bg-red-50 px-4 py-2 rounded-lg font-medium"
            >
              🗑️ Delete Device
            </button>
          </SettingSection>
        </div>

        <!-- Calibration Tab -->
        <div v-else-if="activeTab === 'calibration'" class="space-y-5">
          <p class="text-sm text-gray-600 mb-4">
            💡 Lower ADC values = drier soil. Measure dry soil and wet soil to find correct values.
          </p>

          <SettingSection title="Soil Dry Value (ADC)">
            <input
              v-model.number="soilDryValue"
              type="number"
              min="0"
              max="4095"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
            />
            <p class="text-xs text-gray-500 mt-1">Current: {{ device.soilDryValue }}</p>
          </SettingSection>

          <SettingSection title="Soil Wet Value (ADC)">
            <input
              v-model.number="soilWetValue"
              type="number"
              min="0"
              max="4095"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
            />
            <p class="text-xs text-gray-500 mt-1">Current: {{ device.soilWetValue }}</p>
          </SettingSection>

          <button
            @click="saveCalibration"
            :disabled="calibrating"
            class="w-full bg-green-700 hover:bg-green-800 text-white px-4 py-3 rounded-lg font-medium disabled:opacity-50"
          >
            {{ calibrating ? '...' : '💾 Save Calibration' }}
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Delete confirmation modal -->
  <div v-if="showDeleteConfirm" class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl p-6 max-w-sm">
      <h3 class="text-lg font-bold text-gray-800 mb-2">Delete Device?</h3>
      <p class="text-gray-600 mb-6">
        This will permanently delete <strong>{{ device.label || device.deviceId }}</strong>.
        This action cannot be undone.
      </p>
      <div class="flex gap-3">
        <button
          @click="showDeleteConfirm = false"
          class="flex-1 border border-gray-300 text-gray-700 rounded-lg py-2 hover:bg-gray-50"
        >
          Cancel
        </button>
        <button
          @click="performDelete"
          :disabled="deleting"
          class="flex-1 bg-red-600 hover:bg-red-700 text-white rounded-lg py-2 font-medium disabled:opacity-50"
        >
          {{ deleting ? '...' : 'Delete' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDevicesStore } from '@/stores/devices.store'
import { usePlantStore } from '@/stores/plants.store'
import type { Device, DeviceConfigRequest } from '@/types'

const props = defineProps<{
  device: Device
}>()

const emit = defineEmits<{
  close: []
  updated: [device: Device]
}>()

const devicesStore = useDevicesStore()
const plantStore = usePlantStore()

const activeTab = ref('info')
const tabs = [
  { id: 'info', label: 'ℹ️ Info' },
  { id: 'chart', label: '📊 Chart' },
  { id: 'settings', label: '⚙️ Settings' },
  { id: 'calibration', label: '🔧 Calibration' }
]

// Form state
const editLabel = ref(props.device.label || '')
const selectedPlantId = ref(props.device.plantId || '')
const soilDryValue = ref(props.device.soilDryValue)
const soilWetValue = ref(props.device.soilWetValue)

// Loading states
const renaming = ref(false)
const assigning = ref(false)
const calibrating = ref(false)
const deleting = ref(false)
const showDeleteConfirm = ref(false)

const plants = computed(() => plantStore.plants)

onMounted(() => {
  plantStore.fetchAll(true)
})

async function saveLabel() {
  renaming.value = true
  try {
    const updated = await devicesStore.label(props.device.id, editLabel.value)
    emit('updated', updated)
  } catch (e) {
    console.error('Failed to rename device:', e)
  } finally {
    renaming.value = false
  }
}

async function saveAssignment() {
  assigning.value = true
  try {
    const updated = await devicesStore.assign(props.device.id, selectedPlantId.value || null)
    selectedPlantId.value = updated.plantId || ''
    emit('updated', updated)
  } catch (e) {
    console.error('Failed to assign device:', e)
  } finally {
    assigning.value = false
  }
}

async function unassignPlant() {
  assigning.value = true
  try {
    const updated = await devicesStore.assign(props.device.id, null)
    selectedPlantId.value = ''
    emit('updated', updated)
  } catch (e) {
    console.error('Failed to unassign device:', e)
  } finally {
    assigning.value = false
  }
}

async function saveCalibration() {
  calibrating.value = true
  try {
    const config: DeviceConfigRequest = {
      readIntervalMs: props.device.readIntervalMs,
      soilDryValue: soilDryValue.value,
      soilWetValue: soilWetValue.value
    }
    const updated = await devicesStore.updateConfig(props.device.id, config)
    emit('updated', updated)
  } catch (e) {
    console.error('Failed to save calibration:', e)
  } finally {
    calibrating.value = false
  }
}

function confirmDelete() {
  showDeleteConfirm.value = true
}

async function performDelete() {
  deleting.value = true
  try {
    await devicesStore.deleteDevice(props.device.id)
    emit('close')
  } catch (e) {
    console.error('Failed to delete device:', e)
  } finally {
    deleting.value = false
  }
}
</script>

<script lang="ts">
export default {
  components: {
    InfoRow: {
      template: `
        <div class="flex justify-between items-center">
          <span class="text-gray-600">{{ label }}</span>
          <slot />
        </div>
      `,
      props: ['label']
    },
    SettingSection: {
      template: `
        <div :class="['py-3', className]">
          <h4 class="text-sm font-semibold text-gray-700 mb-2">{{ title }}</h4>
          <slot />
        </div>
      `,
      props: ['title', 'className']
    }
  }
}
</script>
