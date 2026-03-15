<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div class="flex flex-col gap-1">
        <h1 class="text-2xl font-bold text-gray-900">{{ t('breed_catalogue') }}</h1>
        <p class="text-sm text-gray-500">
          {{ breedsStore.totalElements }} {{ t('breed_catalogue').toLowerCase() }}
        </p>
      </div>
      <button
        @click="openCreateModal"
        class="bg-green-600 text-white font-bold px-6 py-2.5 rounded-xl hover:bg-green-700 transition-all shadow-lg shadow-green-200 flex items-center justify-center gap-2"
      >
        <span>➕</span> {{ t('breed_form_title') }}
      </button>
    </div>

    <!-- Search & Filters -->
    <div class="bg-white p-4 rounded-2xl border border-gray-100 shadow-sm space-y-4">
      <!-- Search -->
      <div class="relative">
        <input
          v-model="searchInput"
          @input="debouncedSearch"
          type="text"
          :placeholder="t('breed_search_placeholder')"
          maxlength="100"
          class="w-full pl-10 pr-4 py-2.5 rounded-xl border border-gray-200 focus:border-green-500 focus:ring-2 focus:ring-green-200 transition-all"
        />
        <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
      </div>

      <!-- Filters Row -->
      <div class="flex flex-wrap gap-3">
        <!-- Category Filter -->
        <select
          v-model="categoryFilter"
          @change="applyFilters"
          class="px-4 py-2 rounded-xl border border-gray-200 text-sm font-medium focus:border-green-500 focus:ring-2 focus:ring-green-200"
        >
          <option :value="null">{{ t('breed_filter_category') }}: {{ t('breed_filter_category') }}</option>
          <option value="COMMERCIAL">{{ t('breed_categories_commercial') }}</option>
          <option value="HERITAGE">{{ t('breed_categories_heritage') }}</option>
          <option value="CUSTOM">{{ t('breed_categories_custom') }}</option>
        </select>

        <!-- Purpose Filter -->
        <select
          v-model="purposeFilter"
          @change="applyFilters"
          class="px-4 py-2 rounded-xl border border-gray-200 text-sm font-medium focus:border-green-500 focus:ring-2 focus:ring-green-200"
        >
          <option :value="null">{{ t('breed_filter_purpose') }}: All</option>
          <option value="LAYERS">{{ t('breed_purposes_layers') }}</option>
          <option value="BROILERS">{{ t('breed_purposes_broilers') }}</option>
          <option value="DUAL_PURPOSE">{{ t('breed_purposes_dual') }}</option>
        </select>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="breedsStore.loading && !breedsStore.breeds.length" class="flex justify-center py-12">
      <div class="animate-spin text-4xl">🔄</div>
    </div>

    <!-- Empty State -->
    <div
      v-else-if="!breedsStore.breeds.length"
      class="bg-white rounded-2xl border border-gray-100 p-12 text-center"
    >
      <div class="text-6xl mb-4">🐔</div>
      <h3 class="text-xl font-bold text-gray-900 mb-2">{{ t('breed_empty_state') }}</h3>
      <p class="text-gray-500 mb-6">{{ t('breed_empty_search') }}</p>
      <button
        @click="openCreateModal"
        class="bg-green-600 text-white font-bold px-6 py-2.5 rounded-xl hover:bg-green-700 transition-all"
      >
        {{ t('breed_empty_create') }}
      </button>
    </div>

    <!-- Breeds Grid -->
    <div v-else>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <BreedCard
          v-for="breed in breedsStore.breeds"
          :key="breed.id"
          :breed="breed"
          :is-selected="isInCompare(breed.id)"
          @click="router.push(`/poultry/breeds/${breed.id}`)"
          @toggle-compare="toggleCompare"
        />
      </div>

      <!-- Pagination -->
      <div v-if="breedsStore.totalPages > 1" class="mt-8 flex justify-center gap-2">
        <button
          @click="changePage(breedsStore.currentPage - 1)"
          :disabled="breedsStore.currentPage === 0"
          class="px-4 py-2 rounded-xl border border-gray-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
        >
          ←
        </button>
        <span class="px-4 py-2 text-sm font-medium text-gray-600">
          {{ breedsStore.currentPage + 1 }} / {{ breedsStore.totalPages }}
        </span>
        <button
          @click="changePage(breedsStore.currentPage + 1)"
          :disabled="breedsStore.currentPage >= breedsStore.totalPages - 1"
          class="px-4 py-2 rounded-xl border border-gray-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
        >
          →
        </button>
      </div>
    </div>

    <!-- Compare Bar -->
    <div
      v-if="breedsStore.comparedBreeds.length > 0"
      class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 shadow-lg p-4 z-40"
    >
      <div class="max-w-7xl mx-auto flex items-center justify-between">
        <div class="flex items-center gap-4">
          <span class="font-bold text-gray-900">
            {{ t('breed_compare') }} ({{ breedsStore.comparedBreeds.length }}/3)
          </span>
          <div class="flex gap-2">
            <div
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="flex items-center gap-2 bg-gray-100 px-3 py-1.5 rounded-full"
            >
              <span class="text-sm font-medium">{{ breed.name }}</span>
              <button
                @click="removeFromCompare(breed.id)"
                class="text-gray-400 hover:text-red-500"
              >
                ✕
              </button>
            </div>
          </div>
        </div>
        <div class="flex gap-3">
          <button
            @click="clearCompare"
            class="px-4 py-2 text-gray-600 font-medium hover:text-gray-900"
          >
            Clear
          </button>
          <button
            @click="goToCompare"
            class="bg-green-600 text-white font-bold px-6 py-2 rounded-xl hover:bg-green-700 transition-all"
          >
            {{ t('breed_compare') }} →
          </button>
        </div>
      </div>
    </div>

    <!-- Create Breed Modal -->
    <BreedForm
      v-if="showCreateModal"
      :loading="saving"
      @close="showCreateModal = false"
      @submit="handleCreateSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '@/i18n'
import { useBreedsStore } from '@/stores/breeds.store'
import BreedCard from '@/components/poultry/BreedCard.vue'
import BreedForm from '@/components/poultry/BreedForm.vue'
import type { BreedSummary, BreedCategory, BreedPurpose, CreateBreedRequest } from '@/types'

const { t } = useI18n()
const router = useRouter()
const breedsStore = useBreedsStore()

const showCreateModal = ref(false)
const saving = ref(false)
const breedsStore = useBreedsStore()

const searchInput = ref('')
const categoryFilter = ref<BreedCategory | null>(null)
const purposeFilter = ref<BreedPurpose | null>(null)

let debounceTimer: ReturnType<typeof setTimeout> | null = null

function debouncedSearch() {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    applyFilters()
  }, 300)
}

function applyFilters() {
  breedsStore.setFilters(searchInput.value, categoryFilter.value, purposeFilter.value)
  breedsStore.fetchBreeds()
}

function changePage(page: number) {
  breedsStore.fetchBreeds(page)
}

function isInCompare(id: string): boolean {
  return breedsStore.comparedBreeds.some(b => b.id === id)
}

async function toggleCompare(breed: BreedSummary) {
  if (isInCompare(breed.id)) {
    breedsStore.removeFromCompare(breed.id)
  } else {
    try {
      const detail = await breedsStore.fetchBreedById(breed.id)
      breedsStore.addToCompare(detail)
    } catch (e) {
      console.error('Failed to add to compare:', e)
    }
  }
}

function removeFromCompare(id: string) {
  breedsStore.removeFromCompare(id)
}

function clearCompare() {
  breedsStore.clearCompare()
}

function goToCompare() {
  router.push('/poultry/breeds/compare')
}

function openCreateModal() {
  showCreateModal.value = true
}

async function handleCreateSubmit(data: CreateBreedRequest) {
  saving.value = true
  try {
    await breedsStore.createBreed(data)
    showCreateModal.value = false
    breedsStore.fetchBreeds()
  } catch (e) {
    console.error('Failed to create breed:', e)
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  breedsStore.fetchBreeds()
})
</script>
