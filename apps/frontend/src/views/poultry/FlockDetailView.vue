<template>
  <div v-if="flock" class="space-y-6 pb-20">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div class="flex items-center gap-4">
        <button
          @click="router.back()"
          class="w-10 h-10 rounded-xl bg-white border border-gray-100 flex items-center justify-center hover:bg-gray-50 transition-colors shadow-sm"
        >
          <span>⬅️</span>
        </button>
        <div>
          <h1 class="text-2xl font-bold text-gray-900">{{ flock.name }}</h1>
          <p class="text-sm text-gray-500">{{ flock.breed }} — {{ t(`flock_purpose_${flock.purpose.toLowerCase()}`) }}</p>
        </div>
      </div>
      <div class="flex gap-2">
        <button
          @click="showEditModal = true"
          class="px-4 py-2 rounded-xl border border-gray-200 bg-white text-gray-600 font-bold hover:bg-gray-50 transition-all shadow-sm flex items-center gap-2"
        >
          ✏️ {{ t('common.edit') }}
        </button>
        <button
          @click="handleDelete"
          class="px-4 py-2 rounded-xl border border-red-100 bg-red-50 text-red-600 font-bold hover:bg-red-100 transition-all shadow-sm flex items-center gap-2"
        >
          🗑️ {{ t('delete') }}
        </button>
      </div>
    </div>

    <!-- Status Bar -->
    <div class="bg-white p-1 rounded-2xl border border-gray-100 shadow-sm flex overflow-x-auto no-scrollbar">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        @click="activeTab = tab.id"
        :class="[
          'flex-1 px-6 py-3 rounded-xl font-bold text-sm transition-all whitespace-nowrap flex items-center justify-center gap-2',
          activeTab === tab.id
            ? 'bg-orange-600 text-white shadow-lg shadow-orange-200'
            : 'text-gray-500 hover:bg-gray-50'
        ]"
      >
        <span>{{ tab.icon }}</span> {{ t(tab.label) }}
      </button>
    </div>

    <!-- Tab Content: Overview -->
    <div v-if="activeTab === 'overview'" class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- KPI Cards -->
      <div class="lg:col-span-2 grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm space-y-1">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">{{ t('flock_bird_count') }}</p>
          <p class="text-3xl font-black text-gray-900">{{ flock.birdCount }}</p>
        </div>
        <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm space-y-1 border-l-4 border-l-orange-500">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">{{ t('flock_current_birds') }}</p>
          <p class="text-3xl font-black text-orange-600">{{ flock.currentBirdCount }}</p>
        </div>
        <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm space-y-1">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">{{ t('flock_start_date') }}</p>
          <p class="text-lg font-bold text-gray-700">{{ flock.startDate ? new Date(flock.startDate).toLocaleDateString() : '---' }}</p>
        </div>
        <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm space-y-1">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">{{ t('flock_status_active') }}</p>
          <span :class="['px-3 py-1 rounded-full text-xs font-black uppercase tracking-widest', statusClass]">
            {{ t(`flock_status_${flock.status.toLowerCase()}`) }}
          </span>
        </div>
      </div>

      <!-- Details Sidebar -->
      <div class="space-y-6">
        <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm space-y-4">
          <h3 class="font-bold text-gray-900 flex items-center gap-2 border-b border-gray-50 pb-3">
            📋 {{ t('crop_overview') }}
          </h3>
          <div class="space-y-3 text-sm">
            <div class="flex justify-between">
              <span class="text-gray-500">{{ t('flock_source') }}</span>
              <span class="font-medium text-gray-900">{{ flock.source || '---' }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-500">{{ t('flock_supplier') }}</span>
              <span class="font-medium text-gray-900">{{ flock.supplierName || '---' }}</span>
            </div>
          </div>
          <div v-if="flock.notes" class="pt-2">
            <p class="text-xs font-bold text-gray-400 uppercase mb-1">{{ t('plan_description') }}</p>
            <p class="text-gray-600 italic leading-relaxed">{{ flock.notes }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Coming Soon Placeholder for other tabs -->
    <div v-else class="bg-white rounded-3xl p-12 text-center border border-gray-100 shadow-sm space-y-4">
      <div class="text-6xl">{{ tabs.find(t => t.id === activeTab)?.icon }}</div>
      <h3 class="text-xl font-bold text-gray-900">{{ t('common.coming_soon_title') }}</h3>
      <p class="text-gray-500 max-w-md mx-auto">{{ t('common.coming_soon_subtitle', { name: activeTab }) }}</p>
    </div>

    <!-- Edit Modal -->
    <FlockForm
      v-if="showEditModal"
      :initial-data="flock"
      @close="showEditModal = false"
      @submit="handleUpdate"
      :loading="poultryStore.loading"
    />
  </div>

  <div v-else-if="poultryStore.loading" class="flex justify-center py-20">
    <div class="animate-spin text-4xl">🔄</div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePoultryStore } from '@/stores/poultry.store'
import { useI18n } from '@/i18n'
import FlockForm from '@/components/poultry/FlockForm.vue'
import type { UpdateFlockRequest } from '@/types'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const poultryStore = usePoultryStore()

const activeTab = ref('overview')
const showEditModal = ref(false)

const flock = computed(() => poultryStore.currentFlock)

const tabs = [
  { id: 'overview', label: 'crop_overview', icon: '📋' },
  { id: 'health', label: 'nav_nursery', icon: '💉' }, // Reusing keys where possible
  { id: 'production', label: 'nav_operations', icon: '🥚' },
  { id: 'finances', label: 'nav_farm_management', icon: '💰' }
]

const statusClass = computed(() => {
  if (!flock.value) return ''
  switch (flock.value.status) {
    case 'ACTIVE': return 'bg-green-100 text-green-700'
    case 'SOLD': return 'bg-blue-100 text-blue-700'
    case 'FINISHED': return 'bg-gray-100 text-gray-700'
    default: return 'bg-gray-100 text-gray-700'
  }
})

async function handleUpdate(data: any) {
  if (!flock.value) return
  try {
    await poultryStore.updateFlock(flock.value.id, data as UpdateFlockRequest)
    showEditModal.value = false
  } catch (e) {
    console.error(e)
  }
}

async function handleDelete() {
  if (!flock.value) return
  if (confirm(t('plant_delete_confirm'))) {
    await poultryStore.deleteFlock(flock.value.id)
    router.push('/poultry/flocks')
  }
}

onMounted(() => {
  const id = route.params.id as string
  poultryStore.fetchFlock(id)
})
</script>
