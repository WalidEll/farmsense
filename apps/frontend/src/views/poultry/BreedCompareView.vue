<template>
  <div class="space-y-6 pb-20">
    <!-- Header -->
    <div class="flex items-center gap-4">
      <button
        @click="router.back()"
        class="w-10 h-10 rounded-xl bg-white border border-gray-100 flex items-center justify-center hover:bg-gray-50 transition-colors shadow-sm"
      >
        <span>←</span>
      </button>
      <div>
        <h1 class="text-2xl font-bold text-gray-900">{{ t('breed_compare') }}</h1>
        <p class="text-sm text-gray-500">
          {{ breedsStore.comparedBreeds.length }} {{ t('breed_catalogue').toLowerCase() }}
        </p>
      </div>
    </div>

    <!-- No breeds selected -->
    <div v-if="!breedsStore.comparedBreeds.length" class="bg-white rounded-2xl border border-gray-100 p-12 text-center">
      <div class="text-6xl mb-4">📊</div>
      <h3 class="text-xl font-bold text-gray-900 mb-2">{{ t('breed_compare') }}</h3>
      <p class="text-gray-500 mb-6">{{ t('breed_compare_empty') || 'Select breeds to compare from the catalogue' }}</p>
      <button
        @click="router.push('/poultry/breeds')"
        class="bg-green-600 text-white font-bold px-6 py-2.5 rounded-xl hover:bg-green-700 transition-all"
      >
        {{ t('breed_catalogue') }}
      </button>
    </div>

    <!-- Compare Grid -->
    <div v-else class="overflow-x-auto">
      <table class="w-full min-w-[600px]">
        <thead>
          <tr>
            <th class="p-4 text-left bg-gray-50 rounded-tl-xl"></th>
            <th
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="p-4 bg-white border-b-2 border-green-200"
            >
              <div class="text-center">
                <div v-if="breed.imageUrl" class="w-20 h-20 mx-auto rounded-xl overflow-hidden mb-2">
                  <img :src="breed.imageUrl" :alt="breed.name" class="w-full h-full object-cover" />
                </div>
                <div v-else class="w-20 h-20 mx-auto rounded-xl bg-green-50 flex items-center justify-center mb-2 text-4xl">
                  🐔
                </div>
                <h3 class="font-bold text-gray-900">{{ getBreedName(breed) }}</h3>
                <button
                  @click="removeFromCompare(breed.id)"
                  class="text-xs text-red-500 hover:text-red-700 mt-1"
                >
                  {{ t('remove') || 'Remove' }}
                </button>
              </div>
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <!-- Category -->
          <tr>
            <td class="p-4 font-semibold text-gray-700 bg-gray-50">{{ t('breed_filter_category') }}</td>
            <td
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="p-4 text-center"
            >
              <span :class="categoryClass(breed.category)">
                {{ t(`breed_categories_${breed.category.toLowerCase()}`) }}
              </span>
            </td>
          </tr>

          <!-- Purpose -->
          <tr>
            <td class="p-4 font-semibold text-gray-700 bg-gray-50">{{ t('breed_filter_purpose') }}</td>
            <td
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="p-4 text-center"
            >
              {{ t(`breed_purposes_${breed.purpose.toLowerCase().replace('_', '')}`) || t(`breed_purposes_${breed.purpose.toLowerCase()}`) }}
            </td>
          </tr>

          <!-- Origin -->
          <tr>
            <td class="p-4 font-semibold text-gray-700 bg-gray-50">{{ t('breed_form_origin') }}</td>
            <td
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="p-4 text-center text-gray-600"
            >
              {{ breed.origin || '-' }}
            </td>
          </tr>

          <!-- Weight -->
          <tr>
            <td class="p-4 font-semibold text-gray-700 bg-gray-50">{{ t('avg_weight_male') }} / {{ t('avg_weight_female') }}</td>
            <td
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="p-4 text-center text-gray-600"
            >
              <span v-if="breed.avgWeightMaleKg || breed.avgWeightFemaleKg">
                {{ breed.avgWeightFemaleKg || '-' }} / {{ breed.avgWeightMaleKg || '-' }} kg
              </span>
              <span v-else>-</span>
            </td>
          </tr>

          <!-- Climate -->
          <tr>
            <td class="p-4 font-semibold text-gray-700 bg-gray-50">{{ t('climate') }}</td>
            <td
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="p-4 text-center text-gray-600"
            >
              {{ breed.climateSuitability || '-' }}
            </td>
          </tr>

          <!-- Feeding Programs -->
          <tr>
            <td class="p-4 font-semibold text-gray-700 bg-gray-50">{{ t('breed_feeding_program') }}</td>
            <td
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="p-4"
            >
              <div v-if="breed.feedingPrograms?.length" class="space-y-1">
                <div
                  v-for="fp in breed.feedingPrograms"
                  :key="fp.id"
                  class="flex justify-between text-sm"
                >
                  <span class="px-2 py-0.5 rounded text-xs" :class="stageClass(fp.growthStage)">
                    {{ t(`growth_stage_${fp.growthStage.toLowerCase()}`) }}
                  </span>
                  <span class="text-gray-600">{{ fp.dailyQuantityGrams }}g</span>
                </div>
              </div>
              <span v-else class="text-gray-400">-</span>
            </td>
          </tr>

          <!-- Vaccination Count -->
          <tr>
            <td class="p-4 font-semibold text-gray-700 bg-gray-50">{{ t('vaccination_schedule') }}</td>
            <td
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="p-4 text-center"
            >
              <span v-if="breed.vaccinationSchedule?.length" class="text-green-600 font-bold">
                {{ breed.vaccinationSchedule.length }}
              </span>
              <span v-else class="text-gray-400">-</span>
            </td>
          </tr>

          <!-- Production Benchmarks -->
          <tr>
            <td class="p-4 font-semibold text-gray-700 bg-gray-50">{{ t('breed_production_benchmarks') }}</td>
            <td
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="p-4"
            >
              <div v-if="breed.productionBenchmarks?.length" class="space-y-1">
                <div
                  v-for="bm in breed.productionBenchmarks.slice(0, 3)"
                  :key="bm.id"
                  class="flex justify-between text-sm"
                >
                  <span class="text-gray-600">{{ bm.metricType }}</span>
                  <span class="font-semibold text-green-600">{{ bm.expectedValue }} {{ bm.unit }}</span>
                </div>
              </div>
              <span v-else class="text-gray-400">-</span>
            </td>
          </tr>

          <!-- Housing Guidelines -->
          <tr>
            <td class="p-4 font-semibold text-gray-700 bg-gray-50 rounded-bl-xl">{{ t('breed_housing_guidelines') }}</td>
            <td
              v-for="breed in breedsStore.comparedBreeds"
              :key="breed.id"
              class="p-4"
            >
              <div v-if="breed.housingGuidelines?.length" class="space-y-1">
                <div
                  v-for="hg in breed.housingGuidelines.slice(0, 2)"
                  :key="hg.id"
                  class="text-sm"
                >
                  <span class="text-gray-600">{{ hg.parameterName }}:</span>
                  <span class="font-semibold"> {{ hg.recommendedValue }} {{ hg.unit || '' }}</span>
                </div>
              </div>
              <span v-else class="text-gray-400">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '@/i18n'
import { useBreedsStore } from '@/stores/breeds.store'
import { useAuthStore } from '@/stores/auth.store'
import type { BreedSummary } from '@/types'

const router = useRouter()
const { t } = useI18n()
const breedsStore = useBreedsStore()
const authStore = useAuthStore()

function getBreedName(breed: BreedSummary): string {
  const lang = authStore.lang
  if (lang === 'AR' && breed.nameAr) return breed.nameAr
  if (lang === 'EN' && breed.nameEn) return breed.nameEn
  return breed.name
}

function categoryClass(category: string): string {
  switch (category) {
    case 'COMMERCIAL': return 'px-2 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-700'
    case 'HERITAGE': return 'px-2 py-1 rounded-full text-xs font-semibold bg-amber-100 text-amber-700'
    case 'CUSTOM': return 'px-2 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-700'
    default: return 'px-2 py-1 rounded-full text-xs font-semibold bg-gray-100 text-gray-600'
  }
}

function stageClass(stage: string): string {
  switch (stage) {
    case 'STARTER': return 'bg-yellow-100 text-yellow-700'
    case 'GROWER': return 'bg-green-100 text-green-700'
    case 'FINISHER': return 'bg-blue-100 text-blue-700'
    default: return 'bg-gray-100 text-gray-600'
  }
}

function removeFromCompare(id: string) {
  breedsStore.removeFromCompare(id)
}

onMounted(() => {
  if (!breedsStore.comparedBreeds.length) {
    router.push('/poultry/breeds')
  }
})
</script>
