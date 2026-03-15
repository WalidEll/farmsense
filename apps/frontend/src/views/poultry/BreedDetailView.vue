<template>
  <div class="space-y-6 pb-20">
    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-10 h-10 border-4 border-green-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-center py-20 text-red-500">
      {{ error }}
    </div>

    <!-- Content -->
    <template v-else-if="breed">
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
            <h1 class="text-2xl font-bold text-gray-900">
              {{ currentLocaleBreedName }}
            </h1>
            <p class="text-sm text-gray-500">
              {{ breed.origin }} · {{ t(`breed_purpose_${breed.purpose.toLowerCase()}`) }}
            </p>
          </div>
        </div>

        <div class="flex gap-2 flex-wrap">
          <span
            :class="categoryBadgeClass"
            class="px-3 py-1 rounded-full text-xs font-semibold"
          >
            {{ t(`breed_category_${breed.category.toLowerCase()}`) }}
          </span>

          <button
            v-if="breed.isSystem === false"
            @click="showEditModal = true"
            class="px-4 py-2 rounded-xl border border-gray-200 bg-white text-gray-600 font-medium hover:bg-gray-50 transition-all shadow-sm text-sm"
          >
            ✏️ {{ t('edit') }}
          </button>

          <button
            v-if="breed.isSystem === false"
            @click="handleDelete"
            class="px-4 py-2 rounded-xl border border-red-200 bg-white text-red-600 font-medium hover:bg-red-50 transition-all shadow-sm text-sm"
          >
            🗑️ {{ t('delete') }}
          </button>
        </div>
      </div>

      <!-- Breed info card -->
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
        <div class="flex flex-col sm:flex-row gap-6">
          <div v-if="breed.imageUrl" class="w-full sm:w-40 h-40 rounded-xl overflow-hidden flex-shrink-0">
            <img :src="breed.imageUrl" :alt="breed.nameEn ?? breed.name" class="w-full h-full object-cover" />
          </div>
          <div v-else class="w-16 h-16 rounded-xl bg-green-50 flex items-center justify-center flex-shrink-0 text-3xl">
            🐓
          </div>

          <div class="flex-1 grid grid-cols-2 sm:grid-cols-3 gap-4">
            <div v-if="breed.climateSuitability">
              <p class="text-xs text-gray-400 uppercase tracking-wide">{{ t('climate') }}</p>
              <p class="text-sm font-medium text-gray-900 mt-1">{{ breed.climateSuitability }}</p>
            </div>
            <div v-if="breed.avgWeightMaleKg">
              <p class="text-xs text-gray-400 uppercase tracking-wide">{{ t('avg_weight_male') }}</p>
              <p class="text-sm font-medium text-gray-900 mt-1">{{ breed.avgWeightMaleKg }} kg</p>
            </div>
            <div v-if="breed.avgWeightFemaleKg">
              <p class="text-xs text-gray-400 uppercase tracking-wide">{{ t('avg_weight_female') }}</p>
              <p class="text-sm font-medium text-gray-900 mt-1">{{ breed.avgWeightFemaleKg }} kg</p>
            </div>
          </div>
        </div>

        <div v-if="currentLocaleDescription" class="mt-4 text-sm text-gray-600 leading-relaxed">
          {{ currentLocaleDescription }}
        </div>
      </div>

      <!-- Feeding Program -->
      <section v-if="breed.feedingPrograms?.length" class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-50">
          <h2 class="font-bold text-gray-900">🌾 {{ t('feeding_program') }}</h2>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">{{ t('growth_stage') }}</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">{{ t('age_range') }}</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">{{ t('feed_type') }}</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">{{ t('daily_qty_g') }}</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">{{ t('frequency_day') }}</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr v-for="row in breed.feedingPrograms" :key="row.id" class="hover:bg-gray-50">
                <td class="px-4 py-3">
                  <span class="px-2 py-0.5 rounded-full text-xs font-medium" :class="stageBadge(row.growthStage)">
                    {{ t(`growth_stage_${row.growthStage.toLowerCase()}`) }}
                  </span>
                </td>
                <td class="px-4 py-3 text-gray-600">{{ row.ageStartDays }}–{{ row.ageEndDays }} {{ t('days') }}</td>
                <td class="px-4 py-3 text-gray-900">{{ row.feedType }}</td>
                <td class="px-4 py-3 text-gray-900">{{ row.dailyQuantityGrams }}g</td>
                <td class="px-4 py-3 text-gray-600">{{ row.feedingFrequency }}×</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- Vaccination Schedule -->
      <section v-if="breed.vaccinationSchedule?.length" class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-50">
          <h2 class="font-bold text-gray-900">💉 {{ t('vaccination_schedule') }}</h2>
        </div>
        <div class="divide-y divide-gray-50">
          <div v-for="vax in breed.vaccinationSchedule" :key="vax.id" class="px-6 py-4 flex items-start gap-4">
            <div class="w-14 h-14 rounded-xl bg-blue-50 flex flex-col items-center justify-center flex-shrink-0">
              <span class="text-xs text-blue-500 font-semibold">{{ t('day') }}</span>
              <span class="text-lg font-bold text-blue-700">{{ vax.recommendedAgeDays }}</span>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 flex-wrap">
                <p class="font-medium text-gray-900">{{ vax.vaccineName }}</p>
                <span v-if="vax.isMandatory" class="px-2 py-0.5 rounded-full bg-red-50 text-red-600 text-xs font-semibold">
                  {{ t('mandatory') }}
                </span>
              </div>
              <p class="text-sm text-gray-500 mt-0.5">
                {{ t(`admin_route_${vax.administrationRoute.toLowerCase()}`) }}
                <span v-if="vax.dosage"> · {{ vax.dosage }}</span>
              </p>
              <p v-if="vax.notes" class="text-xs text-gray-400 mt-1">{{ vax.notes }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Production Benchmarks -->
      <section v-if="breed.productionBenchmarks?.length" class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-50">
          <h2 class="font-bold text-gray-900">📊 {{ t('production_benchmarks') }}</h2>
        </div>
        <div class="divide-y divide-gray-50">
          <div v-for="bm in breed.productionBenchmarks" :key="bm.id" class="px-6 py-4 flex items-center justify-between">
            <div>
              <p class="font-medium text-gray-900">{{ t(`metric_${bm.metricType.toLowerCase()}`) }}</p>
              <p v-if="bm.ageStartDays != null" class="text-xs text-gray-400 mt-0.5">
                {{ bm.ageStartDays }}–{{ bm.ageEndDays }} {{ t('days') }}
              </p>
            </div>
            <div class="text-right">
              <p class="text-xl font-bold text-green-600">{{ bm.expectedValue }}</p>
              <p class="text-xs text-gray-400">{{ bm.unit }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Housing Guidelines -->
      <section v-if="breed.housingGuidelines?.length" class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-50">
          <h2 class="font-bold text-gray-900">🏠 {{ t('housing_guidelines') }}</h2>
        </div>
        <div class="divide-y divide-gray-50">
          <div v-for="hg in breed.housingGuidelines" :key="hg.id" class="px-6 py-4 flex items-center justify-between">
            <div>
              <p class="font-medium text-gray-900">{{ t(`housing_param_${hg.parameterName.toLowerCase()}`) }}</p>
              <p v-if="hg.growthStage && hg.growthStage !== 'ALL'" class="text-xs text-gray-400 mt-0.5">
                {{ t(`growth_stage_${hg.growthStage.toLowerCase()}`) }}
              </p>
            </div>
            <div class="text-right">
              <p class="font-semibold text-gray-900">{{ hg.recommendedValue }}<span v-if="hg.unit" class="text-sm font-normal text-gray-400"> {{ hg.unit }}</span></p>
              <p v-if="hg.notes" class="text-xs text-gray-400 mt-0.5">{{ hg.notes }}</p>
            </div>
          </div>
        </div>
      </section>
    </template>

    <!-- Delete confirmation modal -->
    <div v-if="showDeleteConfirm" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl shadow-xl max-w-md w-full p-6 space-y-4">
        <h3 class="text-lg font-bold text-gray-900">⚠️ {{ t('breed_delete_confirm') }}</h3>
        <p v-if="activeFlockCount" class="text-sm text-red-600">
          {{ t('breed_in_use_warning', { count: activeFlockCount }) }}
        </p>
        <div class="flex gap-3 justify-end">
          <button @click="showDeleteConfirm = false" class="px-4 py-2 rounded-xl border border-gray-200 text-gray-600 hover:bg-gray-50 transition-colors text-sm font-medium">
            {{ t('cancel') }}
          </button>
          <button @click="confirmDelete" :disabled="deleting" class="px-4 py-2 rounded-xl bg-red-600 text-white hover:bg-red-700 transition-colors text-sm font-medium disabled:opacity-50">
            {{ deleting ? '...' : t('delete') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from '@/i18n'
import { useBreedsStore } from '@/stores/breeds.store'
import { useAuthStore } from '@/stores/auth.store'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()
const breedsStore = useBreedsStore()
const authStore = useAuthStore()

const loading = ref(false)
const error = ref<string | null>(null)
const showEditModal = ref(false)
const showDeleteConfirm = ref(false)
const activeFlockCount = ref<number | null>(null)
const deleting = ref(false)

const breed = computed(() => breedsStore.currentBreed)
const lang = computed(() => authStore.lang)

const currentLocaleBreedName = computed(() => {
  if (!breed.value) return ''
  if (lang.value === 'AR' && breed.value.nameAr) return breed.value.nameAr
  if (lang.value === 'EN' && breed.value.nameEn) return breed.value.nameEn
  return breed.value.name
})

const currentLocaleDescription = computed(() => {
  if (!breed.value) return null
  if (lang.value === 'AR' && breed.value.descriptionAr) return breed.value.descriptionAr
  if (lang.value === 'EN' && breed.value.descriptionEn) return breed.value.descriptionEn
  return breed.value.description ?? null
})

const categoryBadgeClass = computed(() => {
  switch (breed.value?.category) {
    case 'COMMERCIAL': return 'bg-blue-50 text-blue-700'
    case 'HERITAGE':   return 'bg-amber-50 text-amber-700'
    case 'CUSTOM':     return 'bg-purple-50 text-purple-700'
    default:           return 'bg-gray-100 text-gray-600'
  }
})

function stageBadge(stage: string): string {
  switch (stage) {
    case 'STARTER':  return 'bg-yellow-50 text-yellow-700'
    case 'GROWER':   return 'bg-green-50 text-green-700'
    case 'FINISHER': return 'bg-blue-50 text-blue-700'
    default:         return 'bg-gray-100 text-gray-600'
  }
}

async function loadBreed() {
  const id = route.params.id as string
  loading.value = true
  error.value = null
  try {
    await breedsStore.fetchBreedById(id)
  } catch {
    error.value = t('error_loading')
  } finally {
    loading.value = false
  }
}

function handleDelete() {
  showDeleteConfirm.value = true
}

async function confirmDelete() {
  if (!breed.value) return
  deleting.value = true
  try {
    await breedsStore.deleteBreed(breed.value.id, true)
    router.push({ name: 'breeds' })
  } catch (e: any) {
    if (e?.response?.status === 409) {
      activeFlockCount.value = e.response.data?.activeFlockCount ?? null
    }
  } finally {
    deleting.value = false
  }
}

onMounted(loadBreed)
</script>
