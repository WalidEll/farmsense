<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div class="flex flex-col gap-1">
        <h1 class="text-2xl font-bold text-gray-900">{{ t('housing_locations_title') }}</h1>
        <p class="text-sm text-gray-500">{{ poultryStore.housingLocations.length }} {{ t('housing_location').toLowerCase() }}</p>
      </div>
      <button
        @click="showCreateModal = true"
        class="bg-orange-600 text-white font-bold px-6 py-2.5 rounded-xl hover:bg-orange-700 transition-all shadow-lg shadow-orange-200 flex items-center justify-center gap-2"
      >
        <span>➕</span> {{ t('housing_location_create') }}
      </button>
    </div>

    <!-- Loading -->
    <div v-if="poultryStore.loading && !poultryStore.housingLocations.length" class="flex justify-center py-12">
      <div class="animate-spin text-4xl">🔄</div>
    </div>

    <!-- Empty -->
    <div
      v-else-if="!poultryStore.housingLocations.length"
      class="bg-white rounded-2xl border border-gray-100 shadow-sm p-12 text-center space-y-4"
    >
      <div class="text-6xl">🏠</div>
      <div class="space-y-2">
        <h3 class="text-xl font-bold text-gray-900">{{ t('housing_locations_empty') }}</h3>
      </div>
      <button @click="showCreateModal = true" class="text-orange-600 font-bold hover:underline">
        {{ t('housing_location_create') }}
      </button>
    </div>

    <!-- Grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="loc in poultryStore.housingLocations"
        :key="loc.id"
        class="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 space-y-4 hover:border-orange-200 hover:shadow-md transition-all"
      >
        <!-- Card Header -->
        <div class="flex justify-between items-start">
          <div class="flex items-center gap-3">
            <div class="w-11 h-11 bg-orange-50 text-orange-600 rounded-xl flex items-center justify-center text-xl">
              {{ typeEmoji(loc.locationType) }}
            </div>
            <div>
              <h3 class="font-bold text-gray-900">{{ loc.name }}</h3>
              <p class="text-xs text-gray-400 uppercase tracking-wide">
                {{ t(`housing_location_type_${loc.locationType.toLowerCase()}`) }}
              </p>
            </div>
          </div>
          <div class="flex gap-1">
            <button
              @click="startEdit(loc)"
              class="w-8 h-8 rounded-lg bg-gray-50 hover:bg-orange-50 text-gray-400 hover:text-orange-600 transition-colors flex items-center justify-center text-sm"
            >
              ✏️
            </button>
            <button
              @click="handleDelete(loc)"
              class="w-8 h-8 rounded-lg bg-gray-50 hover:bg-red-50 text-gray-400 hover:text-red-600 transition-colors flex items-center justify-center text-sm"
            >
              🗑️
            </button>
          </div>
        </div>

        <!-- Stats -->
        <div class="bg-gray-50 rounded-xl p-3 flex items-center justify-between">
          <span class="text-xs font-bold text-gray-400 uppercase tracking-tight">{{ t('housing_location_flocks') }}</span>
          <span class="font-bold text-gray-700">{{ loc.currentFlockCount }}</span>
        </div>

        <!-- Notes -->
        <p v-if="loc.notes" class="text-xs text-gray-500 italic leading-relaxed">{{ loc.notes }}</p>
      </div>
    </div>

    <!-- Create Modal -->
    <HousingLocationForm
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @submit="handleCreate"
      :loading="poultryStore.loading"
    />

    <!-- Edit Modal -->
    <HousingLocationForm
      v-if="editTarget"
      :initial-data="editTarget"
      @close="editTarget = null"
      @submit="handleUpdate"
      :loading="poultryStore.loading"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { usePoultryStore } from '@/stores/poultry.store'
import { useI18n } from '@/i18n'
import HousingLocationForm from '@/components/poultry/HousingLocationForm.vue'
import type { HousingLocation, HousingLocationType, CreateHousingLocationRequest } from '@/types'

const poultryStore = usePoultryStore()
const { t } = useI18n()

const showCreateModal = ref(false)
const editTarget = ref<HousingLocation | null>(null)

function typeEmoji(type: HousingLocationType) {
  switch (type) {
    case 'COOP': return '🏚️'
    case 'PEN': return '🟫'
    case 'FREE_RANGE': return '🌿'
    default: return '🏠'
  }
}

function startEdit(loc: HousingLocation) {
  editTarget.value = loc
}

async function handleCreate(data: CreateHousingLocationRequest) {
  await poultryStore.createHousingLocation(data)
  showCreateModal.value = false
}

async function handleUpdate(data: CreateHousingLocationRequest) {
  if (!editTarget.value) return
  await poultryStore.updateHousingLocation(editTarget.value.id, data)
  editTarget.value = null
}

async function handleDelete(loc: HousingLocation) {
  if (confirm(`${loc.name}?`)) {
    try {
      await poultryStore.deleteHousingLocation(loc.id)
    } catch (e: any) {
      alert(e?.message ?? t('common.error'))
    }
  }
}

onMounted(() => {
  poultryStore.fetchHousingLocations()
})
</script>
