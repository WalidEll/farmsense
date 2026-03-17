<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div class="flex flex-col gap-1">
        <h1 class="text-2xl font-bold text-gray-900">{{ t('flocks_title') }}</h1>
        <p class="text-sm text-gray-500">{{ poultryStore.flocks.length }} {{ t('nav.flocks').toLowerCase() }}</p>
      </div>
      <button
        @click="showCreateModal = true"
        class="bg-orange-600 text-white font-bold px-6 py-2.5 rounded-xl hover:bg-orange-700 transition-all shadow-lg shadow-orange-200 flex items-center justify-center gap-2"
      >
        <span>➕</span> {{ t('flocks_create') }}
      </button>
    </div>

    <!-- Filters -->
    <div class="bg-white p-4 rounded-2xl border border-gray-100 shadow-sm flex flex-wrap gap-4">
      <!-- Status Filter -->
      <div class="flex gap-2">
        <button
          v-for="s in statusOptions"
          :key="s.val"
          @click="statusFilter = s.val as (FlockStatus | 'ALL')"
          :class="[
            'px-4 py-1.5 rounded-full text-xs font-bold transition-all border',
            statusFilter === s.val
              ? 'bg-green-600 text-white border-green-600 shadow-sm'
              : 'bg-white text-gray-500 border-gray-200 hover:border-green-300'
          ]"
        >
          {{ s.label }}
        </button>
      </div>

      <div class="w-px h-6 bg-gray-100 self-center hidden sm:block"></div>

      <!-- Purpose Filter -->
      <div class="flex gap-2">
        <button
          v-for="p in purposeOptions"
          :key="p.val"
          @click="purposeFilter = p.val as (FlockPurpose | 'ALL')"
          :class="[
            'px-4 py-1.5 rounded-full text-xs font-bold transition-all border',
            purposeFilter === p.val
              ? 'bg-orange-600 text-white border-orange-600 shadow-sm'
              : 'bg-white text-gray-500 border-gray-200 hover:border-orange-300'
          ]"
        >
          {{ p.label }}
        </button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="poultryStore.loading && !poultryStore.flocks.length" class="flex justify-center py-12">
      <div class="animate-spin text-4xl">🔄</div>
    </div>

    <!-- Empty -->
    <div
      v-else-if="!poultryStore.flocks.length"
      class="bg-white rounded-2xl border border-gray-100 shadow-sm p-12 text-center space-y-4"
    >
      <div class="text-6xl">🐣</div>
      <div class="space-y-2">
        <h3 class="text-xl font-bold text-gray-900">{{ t('flocks_empty') }}</h3>
      </div>
      <button
        @click="showCreateModal = true"
        class="text-orange-600 font-bold hover:underline"
      >
        {{ t('flocks_create') }}
      </button>
    </div>

    <!-- Grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <FlockCard
        v-for="flock in poultryStore.flocks"
        :key="flock.id"
        :flock="flock"
        @click="goToDetail(flock.id)"
      />
    </div>

    <!-- Modals -->
    <FlockForm
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @submit="handleCreate"
      :loading="poultryStore.loading"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePoultryStore } from '@/stores/poultry.store'
import { useI18n } from '@/i18n'
import FlockCard from '@/components/poultry/FlockCard.vue'
import FlockForm from '@/components/poultry/FlockForm.vue'
import type { CreateFlockRequest, FlockStatus, FlockPurpose } from '@/types'

const { t } = useI18n()
const router = useRouter()
const poultryStore = usePoultryStore()

const showCreateModal = ref(false)
const statusFilter = ref<FlockStatus | 'ALL'>('ACTIVE')
const purposeFilter = ref<FlockPurpose | 'ALL'>('ALL')

const statusOptions = [
  { val: 'ALL', label: t('alerts_all') },
  { val: 'ACTIVE', label: t('flock_status_active') },
  { val: 'SOLD', label: t('flock_status_sold') },
  { val: 'PHASED_OUT', label: t('flock_status_phased_out') },
  { val: 'FINISHED', label: t('flock_status_finished') }
]

const purposeOptions = [
  { val: 'ALL', label: t('alerts_all') },
  { val: 'LAYERS', label: t('flock_purpose_layers') },
  { val: 'BROILERS', label: t('flock_purpose_broilers') },
  { val: 'DUAL_PURPOSE', label: t('flock_purpose_dual_purpose') },
  { val: 'BREEDERS', label: t('flock_purpose_breeders') }
]

async function loadFlocks() {
  const params: any = {}
  if (statusFilter.value !== 'ALL') params.status = statusFilter.value
  if (purposeFilter.value !== 'ALL') params.purpose = purposeFilter.value
  await poultryStore.fetchFlocks(params)
}

watch([statusFilter, purposeFilter], loadFlocks)

async function handleCreate(data: any) {
  try {
    await poultryStore.createFlock(data as CreateFlockRequest)
    showCreateModal.value = false
  } catch (e) {
    console.error(e)
  }
}

function goToDetail(id: string) {
  router.push(`/poultry/flocks/${id}`)
}

onMounted(loadFlocks)
</script>
