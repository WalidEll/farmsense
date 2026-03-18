<template>
  <div class="space-y-6 pb-20 min-h-screen bg-gray-50/50">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 px-1">
      <div class="flex flex-col gap-1">
        <h1 class="text-2xl font-black text-gray-900 tracking-tight">{{ t('housing_locations_title') }}</h1>
        <div class="flex items-center gap-2 text-sm text-gray-500">
          <span class="w-1.5 h-1.5 rounded-full bg-green-500"></span>
          {{ poultryStore.housingLocations.length }} {{ t('housing_location').toLowerCase() }}
        </div>
      </div>
      <div class="flex gap-3">
        <button
          @click="showCreateModal = true"
          class="bg-green-700 text-white font-bold px-6 py-2.5 rounded-xl hover:bg-green-800 transition-all shadow-lg shadow-green-200 flex items-center justify-center gap-2"
        >
          <span class="text-lg">➕</span> {{ t('housing_location_create') }}
        </button>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex flex-col justify-between group hover:border-green-200 transition-all">
        <div class="flex items-center justify-between mb-2">
          <p class="text-gray-400 text-xs font-bold uppercase tracking-wider">{{ t('housing_location_capacity_total') }}</p>
          <span class="text-green-600 bg-green-50 p-1.5 rounded-lg text-lg">📊</span>
        </div>
        <p class="text-3xl font-black text-gray-900">{{ totalCapacity }} <span class="text-sm font-bold text-gray-300 uppercase">Birds</span></p>
      </div>
      
      <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex flex-col justify-between group hover:border-green-200 transition-all">
        <div class="flex items-center justify-between mb-2">
          <p class="text-gray-400 text-xs font-bold uppercase tracking-wider">{{ t('housing_location_capacity_occupied') }}</p>
          <span class="text-blue-600 bg-blue-50 p-1.5 rounded-lg text-lg">🏠</span>
        </div>
        <p class="text-3xl font-black text-gray-900">{{ occupiedCount }} <span class="text-sm font-bold text-gray-300 uppercase">Units</span></p>
        <div class="mt-2 text-[10px] font-black text-green-600 bg-green-50 px-2 py-0.5 rounded-full w-fit">
          {{ Math.round((occupiedCount / (poultryStore.housingLocations.length || 1)) * 100) }}% OCCUPANCY
        </div>
      </div>

      <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex flex-col justify-between group hover:border-gray-200 transition-all">
        <div class="flex items-center justify-between mb-2">
          <p class="text-gray-400 text-xs font-bold uppercase tracking-wider">{{ t('housing_location_status_empty') }}</p>
          <span class="text-gray-400 bg-gray-50 p-1.5 rounded-lg text-lg">✨</span>
        </div>
        <p class="text-3xl font-black text-gray-900">{{ emptyCount }} <span class="text-sm font-bold text-gray-300 uppercase">Units</span></p>
      </div>

      <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex flex-col justify-between group hover:border-orange-200 transition-all">
        <div class="flex items-center justify-between mb-2">
          <p class="text-gray-400 text-xs font-bold uppercase tracking-wider">{{ t('housing_location_status_maintenance') }}</p>
          <span class="text-orange-500 bg-orange-50 p-1.5 rounded-lg text-lg">🛠️</span>
        </div>
        <p class="text-3xl font-black text-gray-900">{{ maintenanceCount }} <span class="text-sm font-bold text-gray-300 uppercase">Units</span></p>
        <div v-if="maintenanceCount > 0" class="mt-2 text-[10px] font-black text-orange-600 bg-orange-50 px-2 py-0.5 rounded-full w-fit animate-pulse">
          REQUIRES ATTENTION
        </div>
      </div>
    </div>

    <!-- Toolbar -->
    <div class="flex flex-col sm:flex-row items-center justify-between gap-4 bg-white p-3 rounded-2xl border border-gray-100 shadow-sm">
      <div class="flex bg-gray-100 p-1 rounded-xl w-full sm:w-fit">
        <button 
          @click="viewMode = 'grid'"
          :class="[
            'flex-1 sm:flex-none px-6 py-2 rounded-lg text-sm font-black transition-all',
            viewMode === 'grid' ? 'bg-white shadow-sm text-green-700' : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          GRID VIEW
        </button>
        <button 
          @click="viewMode = 'list'"
          :class="[
            'flex-1 sm:flex-none px-6 py-2 rounded-lg text-sm font-black transition-all',
            viewMode === 'list' ? 'bg-white shadow-sm text-green-700' : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          LIST
        </button>
      </div>

      <div class="flex items-center gap-6 overflow-x-auto w-full sm:w-fit px-2 py-1">
        <div class="flex items-center gap-2 text-[10px] font-black text-gray-400 uppercase tracking-tighter whitespace-nowrap">
          <div class="w-3 h-3 bg-green-500 rounded-sm"></div>
          {{ t('housing_location_status_active') }}
        </div>
        <div class="flex items-center gap-2 text-[10px] font-black text-gray-400 uppercase tracking-tighter whitespace-nowrap">
          <div class="w-3 h-3 bg-gray-300 rounded-sm"></div>
          {{ t('housing_location_status_empty') }}
        </div>
        <div class="flex items-center gap-2 text-[10px] font-black text-gray-400 uppercase tracking-tighter whitespace-nowrap">
          <div class="w-3 h-3 bg-orange-500 rounded-sm"></div>
          {{ t('housing_location_status_maintenance') }}
        </div>
        <div class="h-4 w-px bg-gray-200"></div>
        <button class="text-gray-400 hover:text-gray-900 flex items-center gap-1 text-[10px] font-black uppercase tracking-tighter whitespace-nowrap transition-colors">
          <span>🔍</span> FILTER
        </button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="poultryStore.loading && !poultryStore.housingLocations.length" class="flex justify-center py-12">
      <div class="animate-spin text-4xl">🔄</div>
    </div>

    <!-- Empty -->
    <div
      v-else-if="!poultryStore.housingLocations.length"
      class="bg-white rounded-3xl border border-gray-100 shadow-sm p-16 text-center space-y-6"
    >
      <div class="text-7xl">🏘️</div>
      <div class="space-y-2">
        <h3 class="text-2xl font-black text-gray-900">{{ t('housing_locations_empty') }}</h3>
        <p class="text-gray-500 max-w-xs mx-auto">Start mapping your farm by adding your first coop or pen unit.</p>
      </div>
      <button @click="showCreateModal = true" class="bg-green-700 text-white font-bold px-8 py-3 rounded-2xl hover:bg-green-800 transition-all shadow-lg">
        {{ t('housing_location_create') }}
      </button>
    </div>

    <!-- Visual Grid / Map -->
    <div 
      v-else 
      :class="[
        viewMode === 'grid' 
          ? 'bg-white border border-gray-100 rounded-3xl p-6 sm:p-10 shadow-sm relative overflow-hidden' 
          : 'space-y-3'
      ]"
    >
      <!-- Grid Background Decoration -->
      <div 
        v-if="viewMode === 'grid'"
        class="absolute inset-0 opacity-[0.03] pointer-events-none" 
        style="background-image: linear-gradient(#000 1px, transparent 1px), linear-gradient(90deg, #000 1px, transparent 1px); background-size: 40px 40px;"
      ></div>

      <div 
        :class="[
          viewMode === 'grid' 
            ? 'relative grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 sm:gap-6' 
            : 'flex flex-col gap-3'
        ]"
      >
        <div
          v-for="loc in poultryStore.housingLocations"
          :key="loc.id"
          @click="startEdit(loc)"
          :class="[
            'relative rounded-2xl border-2 transition-all cursor-pointer group',
            statusClasses(loc.status),
            viewMode === 'grid' ? 'aspect-square flex flex-col p-4 sm:p-5' : 'flex items-center justify-between p-5'
          ]"
        >
          <!-- Status Dot -->
          <div 
            v-if="viewMode === 'grid'"
            :class="[
              'absolute top-3 right-3 w-2.5 h-2.5 rounded-full border-2 border-white shadow-sm transition-transform group-hover:scale-125',
              statusDotColor(loc.status)
            ]"
          ></div>

          <div :class="[viewMode === 'grid' ? 'flex-1' : 'flex items-center gap-5 flex-1']">
            <div>
              <p :class="[
                'text-[10px] font-black uppercase tracking-widest mb-1',
                statusTextMuted(loc.status)
              ]">
                {{ loc.name }}
              </p>
              <h3 :class="[
                'text-sm font-black leading-tight truncate',
                statusTextPrimary(loc.status)
              ]">
                {{ loc.status === 'ACTIVE' ? (loc.currentFlockName || 'Active Batch') : (loc.status === 'MAINTENANCE' ? 'Maintenance' : t('housing_location_unassigned')) }}
              </h3>
            </div>
          </div>

          <!-- Usage Info -->
          <div :class="[viewMode === 'grid' ? 'mt-auto flex items-end justify-between' : 'flex items-center gap-10']">
            <div :class="[viewMode === 'grid' ? '' : 'w-48']">
              <p :class="['text-[10px] font-black uppercase', statusTextMuted(loc.status)]">
                {{ loc.status === 'ACTIVE' ? Math.round((loc.currentFlockCount / (loc.capacity || 1)) * 100) + '% CAP.' : typeEmoji(loc.locationType) }}
              </p>
            </div>
            
            <div class="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
              <span class="text-xs">✏️</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer Info -->
    <div class="flex flex-col md:flex-row items-center justify-between gap-4 px-2 py-4 text-gray-400">
      <p class="text-[10px] font-black uppercase tracking-widest">Layout map v3.1 • Updated now</p>
      <div class="flex gap-6">
        <button class="text-[10px] font-black uppercase tracking-widest hover:text-gray-900 transition-colors underline underline-offset-4 decoration-gray-200 hover:decoration-green-500">
          {{ t('housing_location_export') }}
        </button>
        <button class="text-[10px] font-black uppercase tracking-widest hover:text-gray-900 transition-colors underline underline-offset-4 decoration-gray-200 hover:decoration-green-500">
          {{ t('housing_location_print') }}
        </button>
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
import { ref, computed, onMounted } from 'vue'
import { usePoultryStore } from '@/stores/poultry.store'
import { useI18n } from '@/i18n'
import HousingLocationForm from '@/components/poultry/HousingLocationForm.vue'
import type { HousingLocation, HousingLocationType, HousingLocationStatus, CreateHousingLocationRequest } from '@/types'

const poultryStore = usePoultryStore()
const { t } = useI18n()

const showCreateModal = ref(false)
const editTarget = ref<HousingLocation | null>(null)
const viewMode = ref<'grid' | 'list'>('grid')

// --- Stats ---
const totalCapacity = computed(() => 
  poultryStore.housingLocations.reduce((acc, loc) => acc + (loc.capacity || 0), 0)
)
const occupiedCount = computed(() => 
  poultryStore.housingLocations.filter(loc => loc.status === 'ACTIVE').length
)
const emptyCount = computed(() => 
  poultryStore.housingLocations.filter(loc => loc.status === 'EMPTY').length
)
const maintenanceCount = computed(() => 
  poultryStore.housingLocations.filter(loc => loc.status === 'MAINTENANCE').length
)

function typeEmoji(type: HousingLocationType) {
  switch (type) {
    case 'COOP': return '🏘️'
    case 'PEN': return '🎪'
    case 'FREE_RANGE': return '🌿'
    default: return '🏠'
  }
}

function statusClasses(status: HousingLocationStatus) {
  switch (status) {
    case 'ACTIVE': return 'bg-green-500/5 border-green-500 hover:shadow-xl hover:shadow-green-500/10 hover:-translate-y-1'
    case 'MAINTENANCE': return 'bg-orange-500/5 border-orange-500 hover:shadow-xl hover:shadow-orange-500/10 hover:-translate-y-1'
    case 'EMPTY': return 'bg-gray-50/50 border-gray-200 border-dashed hover:border-gray-400'
    default: return 'bg-white border-gray-100'
  }
}

function statusDotColor(status: HousingLocationStatus) {
  switch (status) {
    case 'ACTIVE': return 'bg-green-500 animate-pulse'
    case 'MAINTENANCE': return 'bg-orange-500'
    case 'EMPTY': return 'bg-gray-300'
    default: return 'bg-gray-300'
  }
}

function statusTextPrimary(status: HousingLocationStatus) {
  switch (status) {
    case 'ACTIVE': return 'text-green-900'
    case 'MAINTENANCE': return 'text-orange-900'
    case 'EMPTY': return 'text-gray-400 italic font-medium'
    default: return 'text-gray-900'
  }
}

function statusTextMuted(status: HousingLocationStatus) {
  switch (status) {
    case 'ACTIVE': return 'text-green-600'
    case 'MAINTENANCE': return 'text-orange-600'
    case 'EMPTY': return 'text-gray-400'
    default: return 'text-gray-500'
  }
}

function startEdit(loc: HousingLocation) {
  editTarget.value = loc
}

async function handleCreate(data: CreateHousingLocationRequest) {
  try {
    await poultryStore.createHousingLocation(data)
    showCreateModal.value = false
  } catch (e: any) {
    alert(e?.message || t('common_error'))
  }
}

async function handleUpdate(data: CreateHousingLocationRequest) {
  if (!editTarget.value) return
  try {
    await poultryStore.updateHousingLocation(editTarget.value.id, data)
    editTarget.value = null
  } catch (e: any) {
    alert(e?.message || t('common_error'))
  }
}

async function handleDelete(loc: HousingLocation) {
  if (confirm(`${loc.name}?`)) {
    try {
      await poultryStore.deleteHousingLocation(loc.id)
    } catch (e: any) {
      alert(e?.message ?? t('common_error'))
    }
  }
}

onMounted(() => {
  poultryStore.fetchHousingLocations()
})
</script>
