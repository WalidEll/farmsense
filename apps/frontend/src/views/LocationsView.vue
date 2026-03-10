<template>
  <div class="p-4 lg:p-6 space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-900">{{ t('locations_title') }}</h1>
      <button
        @click="openCreateModal"
        class="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors text-sm font-medium"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        {{ t('locations_create') }}
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
    </div>

    <!-- Grid -->
    <div v-else-if="store.locations.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      <LocationCard
        v-for="loc in store.locations"
        :key="loc.id"
        :location="loc"
        @edit="openEditModal"
        @delete="handleDelete"
      />
    </div>

    <!-- Empty -->
    <div v-else class="text-center py-12 text-gray-500">
      <svg class="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
      </svg>
      <p>{{ t('locations_empty') }}</p>
    </div>

    <!-- Create/Edit Location Modal -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40" @click.self="closeModal">
      <div class="bg-white rounded-xl shadow-xl w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto">
        <div class="p-6 space-y-4">
          <h2 class="text-lg font-bold text-gray-900">
            {{ editing ? t('locations_edit') : t('locations_create') }}
          </h2>

          <!-- Name -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('location_name') }} *</label>
            <input
              v-model="form.name"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Name AR -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('location_name') }} (AR) <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model="form.nameAr"
              dir="rtl"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Name EN -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('location_name') }} (EN) <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model="form.nameEn"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Description -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('plan_description') }} <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <textarea
              v-model="form.description"
              rows="2"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            ></textarea>
          </div>

          <!-- Location Type -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('location_type') }} *</label>
            <select
              v-model="form.locationType"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            >
              <option value="BED">{{ t('location_type_bed') }}</option>
              <option value="POT">{{ t('location_type_pot') }}</option>
              <option value="ROW">{{ t('location_type_row') }}</option>
              <option value="GREENHOUSE">{{ t('location_type_greenhouse') }}</option>
              <option value="FIELD">{{ t('location_type_field') }}</option>
              <option value="INDOOR">{{ t('location_type_indoor') }}</option>
              <option value="OTHER">{{ t('location_type_other') }}</option>
            </select>
          </div>

          <!-- Area -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('location_area') }} (m2) <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model.number="form.areaM2"
              type="number"
              min="0"
              step="0.1"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Latitude -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('weather_latitude') }} <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model.number="form.latitude"
              type="number"
              step="0.000001"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Longitude -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('weather_longitude') }} <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model.number="form.longitude"
              type="number"
              step="0.000001"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Error -->
          <p v-if="error" class="text-sm text-red-600">{{ error }}</p>

          <!-- Buttons -->
          <div class="flex justify-end gap-3 pt-2">
            <button
              @click="closeModal"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
            >
              {{ t('cancel') }}
            </button>
            <button
              @click="submitLocation"
              :disabled="saving"
              class="px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50"
            >
              {{ saving ? t('loading') : t('save') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from '@/i18n'
import { useCropPlansStore } from '@/stores/cropPlans.store'
import LocationCard from '@/components/cropplan/LocationCard.vue'
import type { FarmLocation, LocationType } from '@/types'

const { t } = useI18n()
const store = useCropPlansStore()

const loading = ref(false)
const showModal = ref(false)
const editing = ref<FarmLocation | null>(null)
const saving = ref(false)
const error = ref('')

const form = ref({
  name: '',
  nameAr: '',
  nameEn: '',
  description: '',
  locationType: 'BED' as LocationType,
  areaM2: undefined as number | undefined,
  latitude: undefined as number | undefined,
  longitude: undefined as number | undefined,
})

function resetForm() {
  form.value = {
    name: '',
    nameAr: '',
    nameEn: '',
    description: '',
    locationType: 'BED',
    areaM2: undefined,
    latitude: undefined,
    longitude: undefined,
  }
}

function openCreateModal() {
  editing.value = null
  resetForm()
  showModal.value = true
}

function openEditModal(loc: FarmLocation) {
  editing.value = loc
  form.value = {
    name: loc.name,
    nameAr: loc.nameAr || '',
    nameEn: loc.nameEn || '',
    description: loc.description || '',
    locationType: loc.locationType,
    areaM2: loc.areaM2,
    latitude: loc.latitude,
    longitude: loc.longitude,
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editing.value = null
  error.value = ''
  resetForm()
}

async function submitLocation() {
  if (!form.value.name.trim()) {
    error.value = t('error_generic')
    return
  }
  saving.value = true
  error.value = ''
  try {
    const req = {
      name: form.value.name,
      nameAr: form.value.nameAr || undefined,
      nameEn: form.value.nameEn || undefined,
      description: form.value.description || undefined,
      locationType: form.value.locationType,
      areaM2: form.value.areaM2 || undefined,
      latitude: form.value.latitude || undefined,
      longitude: form.value.longitude || undefined,
    }
    if (editing.value) {
      await store.updateLocation(editing.value.id, req)
    } else {
      await store.createLocation(req)
    }
    closeModal()
  } catch {
    error.value = t('error_generic')
  } finally {
    saving.value = false
  }
}

async function handleDelete(loc: FarmLocation) {
  try {
    await store.deleteLocation(loc.id)
  } catch {
    // silent
  }
}

onMounted(async () => {
  loading.value = true
  try {
    await store.fetchLocations()
  } finally {
    loading.value = false
  }
})
</script>
