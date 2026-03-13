<template>
  <div class="space-y-6">
    <div class="flex flex-col gap-1">
      <h1 class="text-2xl font-bold text-gray-900">{{ t('poultry_dashboard_title') }}</h1>
      <p class="text-sm text-gray-500">{{ auth.user?.name }} — FarmSense Poultry</p>
    </div>

    <!-- KPI Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex items-center gap-4">
        <div class="w-12 h-12 bg-orange-100 text-orange-600 rounded-xl flex items-center justify-center text-2xl">
          🐣
        </div>
        <div>
          <p class="text-sm font-medium text-gray-500">{{ t('poultry_active_flocks') }}</p>
          <p class="text-2xl font-bold text-gray-900">{{ activeFlocksCount }}</p>
        </div>
      </div>

      <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex items-center gap-4">
        <div class="w-12 h-12 bg-blue-100 text-blue-600 rounded-xl flex items-center justify-center text-2xl">
          🐔
        </div>
        <div>
          <p class="text-sm font-medium text-gray-500">{{ t('poultry_total_birds') }}</p>
          <p class="text-2xl font-bold text-gray-900">{{ totalBirdsCount }}</p>
        </div>
      </div>
    </div>

    <!-- Quick Links -->
    <div class="space-y-4">
      <h2 class="text-lg font-bold text-gray-900">{{ t('poultry_quick_links') }}</h2>
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <RouterLink
          to="/poultry/flocks"
          class="bg-white p-4 rounded-xl border border-gray-100 shadow-sm hover:border-green-300 hover:shadow-md transition-all flex items-center gap-3"
        >
          <span class="text-xl">🐣</span>
          <span class="font-medium text-gray-700">{{ t('nav.flocks') }}</span>
        </RouterLink>
        <RouterLink
          to="/poultry/suppliers"
          class="bg-white p-4 rounded-xl border border-gray-100 shadow-sm hover:border-green-300 hover:shadow-md transition-all flex items-center gap-3"
        >
          <span class="text-xl">📦</span>
          <span class="font-medium text-gray-700">{{ t('nav.suppliers') }}</span>
        </RouterLink>
        <RouterLink
          to="/poultry/customers"
          class="bg-white p-4 rounded-xl border border-gray-100 shadow-sm hover:border-green-300 hover:shadow-md transition-all flex items-center gap-3"
        >
          <span class="text-xl">👥</span>
          <span class="font-medium text-gray-700">{{ t('nav.customers') }}</span>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { usePoultryStore } from '@/stores/poultry.store'
import { useAuthStore } from '@/stores/auth.store'
import { useI18n } from '@/i18n'

const { t } = useI18n()
const poultryStore = usePoultryStore()
const auth = useAuthStore()

const activeFlocksCount = computed(() => 
  poultryStore.flocks.filter(f => f.status === 'ACTIVE').length
)

const totalBirdsCount = computed(() => 
  poultryStore.flocks
    .filter(f => f.status === 'ACTIVE')
    .reduce((sum, f) => sum + f.currentBirdCount, 0)
)

onMounted(() => {
  poultryStore.fetchFlocks()
})
</script>
