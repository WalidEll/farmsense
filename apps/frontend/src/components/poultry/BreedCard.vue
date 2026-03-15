<template>
  <div
    class="bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-all cursor-pointer overflow-hidden group"
    @click="$emit('click', breed.id)"
  >
    <!-- Image -->
    <div class="relative h-40 bg-gray-100 overflow-hidden">
      <img
        v-if="breed.imageUrl"
        :src="breed.imageUrl"
        :alt="displayName"
        class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
      />
      <div v-else class="w-full h-full flex items-center justify-center text-6xl">
        🐔
      </div>
      
      <!-- Category Badge -->
      <span
        :class="[
          'absolute top-3 left-3 px-2.5 py-1 rounded-full text-xs font-bold',
          categoryClass
        ]"
      >
        {{ categoryLabel }}
      </span>

      <!-- System Badge -->
      <span
        v-if="breed.isSystem"
        class="absolute top-3 right-3 px-2.5 py-1 rounded-full text-xs font-bold bg-gray-100 text-gray-600"
      >
        {{ t('breed_custom_badge') }}
      </span>
    </div>

    <!-- Content -->
    <div class="p-4">
      <h3 class="font-bold text-gray-900 text-lg mb-1">{{ displayName }}</h3>
      
      <div class="flex items-center gap-2 text-sm text-gray-500 mb-3">
        <span class="px-2 py-0.5 rounded-full bg-gray-100 text-gray-600 text-xs font-medium">
          {{ purposeLabel }}
        </span>
        <span v-if="breed.origin">• {{ breed.origin }}</span>
      </div>

      <!-- Weight Range -->
      <div v-if="breed.avgWeightMaleKg || breed.avgWeightFemaleKg" class="text-sm text-gray-600">
        <span class="font-medium">{{ t('breed_for') }}:</span>
        <span v-if="breed.avgWeightMaleKg && breed.avgWeightFemaleKg">
          {{ breed.avgWeightFemaleKg }} - {{ breed.avgWeightMaleKg }} kg
        </span>
        <span v-else-if="breed.avgWeightMaleKg">
          {{ breed.avgWeightMaleKg }} kg
        </span>
        <span v-else-if="breed.avgWeightFemaleKg">
          {{ breed.avgWeightFemaleKg }} kg
        </span>
      </div>

      <!-- Compare Checkbox -->
      <div class="mt-4 pt-4 border-t border-gray-100 flex items-center gap-2" @click.stop>
        <input
          type="checkbox"
          :checked="isSelected"
          @change="$emit('toggle-compare', breed)"
          class="w-4 h-4 text-green-600 rounded border-gray-300 focus:ring-green-500"
        />
        <span class="text-xs text-gray-500">{{ t('breed_compare') }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from '@/i18n'
import { useAuthStore } from '@/stores/auth.store'
import type { BreedSummary } from '@/types'

const { t } = useI18n()
const authStore = useAuthStore()

const props = defineProps<{
  breed: BreedSummary
  isSelected?: boolean
}>()

defineEmits<{
  click: [id: string]
  'toggle-compare': [breed: BreedSummary]
}>()

const displayName = computed(() => {
  const lang = authStore.lang
  if (lang === 'AR' && props.breed.nameAr) return props.breed.nameAr
  if (lang === 'EN' && props.breed.nameEn) return props.breed.nameEn
  return props.breed.name
})

const categoryClass = computed(() => {
  switch (props.breed.category) {
    case 'COMMERCIAL':
      return 'bg-blue-100 text-blue-700'
    case 'HERITAGE':
      return 'bg-amber-100 text-amber-700'
    case 'CUSTOM':
      return 'bg-green-100 text-green-700'
    default:
      return 'bg-gray-100 text-gray-700'
  }
})

const categoryLabel = computed(() => {
  switch (props.breed.category) {
    case 'COMMERCIAL':
      return t('breed_categories_commercial')
    case 'HERITAGE':
      return t('breed_categories_heritage')
    case 'CUSTOM':
      return t('breed_categories_custom')
    default:
      return props.breed.category
  }
})

const purposeLabel = computed(() => {
  switch (props.breed.purpose) {
    case 'LAYERS':
      return t('breed_purposes_layers')
    case 'BROILERS':
      return t('breed_purposes_broilers')
    case 'DUAL_PURPOSE':
      return t('breed_purposes_dual')
    default:
      return props.breed.purpose
  }
})
</script>
