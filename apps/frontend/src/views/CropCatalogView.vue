<template>
  <div class="p-4 lg:p-6 space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-900">{{ t('crops_title') }}</h1>
      <RouterLink
        to="/crops/admin"
        class="p-2 rounded-lg bg-green-50 text-green-700 hover:bg-green-100 transition-colors"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
        </svg>
      </RouterLink>
    </div>

    <!-- Search -->
    <div class="relative">
      <div class="absolute inset-y-0 start-0 flex items-center ps-3 pointer-events-none">
        <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
      </div>
      <input
        v-model="search"
        :placeholder="t('crops_search')"
        class="w-full border border-gray-300 rounded-lg ps-10 pe-4 py-2.5 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
        @input="debouncedFetch"
      />
    </div>

    <!-- Category pills -->
    <div class="flex gap-2 overflow-x-auto pb-2">
      <button
        v-for="cat in categories"
        :key="cat.value ?? 'all'"
        @click="selectCategory(cat.value)"
        :class="[
          activeCategory === cat.value
            ? 'bg-green-600 text-white'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200',
          'px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition-colors',
        ]"
      >
        {{ t(cat.label) }}
      </button>
    </div>

    <!-- Loading -->
    <div v-if="store.loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
    </div>

    <!-- Grid -->
    <div v-else-if="store.crops.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      <CropCard v-for="crop in store.crops" :key="crop.id" :crop="crop" />
    </div>

    <!-- Empty -->
    <div v-else class="text-center py-12 text-gray-500">
      <svg class="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
      </svg>
      <p>{{ t('crops_empty') }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from '@/i18n'
import { useCropsStore } from '@/stores/crops.store'
import CropCard from '@/components/crops/CropCard.vue'
import type { CropCategory } from '@/types'

const { t } = useI18n()
const store = useCropsStore()

const search = ref('')
const activeCategory = ref<CropCategory | null>(null)

const categories: { value: CropCategory | null; label: string }[] = [
  { value: null, label: 'crops_all' },
  { value: 'VEGETABLE', label: 'crops_vegetable' },
  { value: 'FRUIT', label: 'crops_fruit' },
  { value: 'HERB', label: 'crops_herb' },
  { value: 'GRAIN', label: 'crops_grain' },
  { value: 'LEGUME', label: 'crops_legume' },
  { value: 'OTHER', label: 'crops_other' },
]

let debounceTimer: ReturnType<typeof setTimeout> | null = null

function debouncedFetch() {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    store.fetchAll(activeCategory.value, search.value || undefined)
  }, 300)
}

function selectCategory(cat: CropCategory | null) {
  activeCategory.value = cat
  store.fetchAll(cat, search.value || undefined)
}

onMounted(() => {
  store.fetchAll()
})
</script>
