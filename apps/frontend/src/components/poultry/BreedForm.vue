<template>
  <div class="fixed inset-0 bg-black/50 z-[60] flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-2xl overflow-hidden flex flex-col max-h-[90vh]">
      <!-- Header -->
      <div class="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50/50">
        <h3 class="text-xl font-bold text-gray-900">
          {{ initialData ? t('breed_form_edit') : t('breed_form_title') }}
        </h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 transition-colors">
          <span class="text-2xl">×</span>
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-4 overflow-y-auto flex-1">
        <!-- Required Fields -->
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">
              {{ t('breed_form_name') }} <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.name"
              type="text"
              required
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
              :placeholder="t('breed_form_name')"
            />
          </div>
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">
              {{ t('breed_form_purpose') }} <span class="text-red-500">*</span>
            </label>
            <select
              v-model="form.purpose"
              required
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none bg-white"
            >
              <option value="LAYERS">{{ t('breed_purposes_layers') }}</option>
              <option value="BROILERS">{{ t('breed_purposes_broilers') }}</option>
              <option value="DUAL_PURPOSE">{{ t('breed_purposes_dual') }}</option>
            </select>
          </div>
        </div>

        <!-- Category (locked to CUSTOM) -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('breed_filter_category') }}
          </label>
          <input
            type="text"
            value="CUSTOM"
            disabled
            class="w-full border border-gray-200 rounded-xl px-4 py-2 bg-gray-50 text-gray-500"
          />
        </div>

        <!-- Optional Fields -->
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">{{ t('breed_form_name_ar') }}</label>
            <input
              v-model="form.nameAr"
              type="text"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
            />
          </div>
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">{{ t('breed_form_name_en') }}</label>
            <input
              v-model="form.nameEn"
              type="text"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">{{ t('breed_form_origin') }}</label>
            <input
              v-model="form.origin"
              type="text"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
            />
          </div>
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">{{ t('breed_form_climate') }}</label>
            <input
              v-model="form.climateSuitability"
              type="text"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">{{ t('breed_form_weight_male') }}</label>
            <input
              v-model.number="form.avgWeightMaleKg"
              type="number"
              step="0.1"
              min="0"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
            />
          </div>
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">{{ t('breed_form_weight_female') }}</label>
            <input
              v-model.number="form.avgWeightFemaleKg"
              type="number"
              step="0.1"
              min="0"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">{{ t('breed_form_description') }}</label>
          <textarea
            v-model="form.description"
            rows="2"
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
          ></textarea>
        </div>

        <!-- Feeding Programs Section -->
        <div class="border border-gray-200 rounded-xl p-4">
          <div class="flex justify-between items-center mb-3">
            <h4 class="font-bold text-gray-800">{{ t('breed_feeding_program') }}</h4>
            <button
              type="button"
              @click="addFeedingProgram"
              class="text-sm text-green-600 hover:text-green-700"
            >+ {{ t('breed_form_add_feeding') }}</button>
          </div>
          <div v-for="(fp, idx) in form.feedingPrograms" :key="idx" class="grid grid-cols-6 gap-2 mb-2 items-end">
            <div class="col-span-1">
              <label class="text-xs text-gray-500">{{ t('growth_stage') }}</label>
              <select v-model="fp.growthStage" class="w-full border rounded-lg px-2 py-1 text-sm">
                <option value="STARTER">{{ t('breed_growth_starter') }}</option>
                <option value="GROWER">{{ t('breed_growth_grower') }}</option>
                <option value="FINISHER">{{ t('breed_growth_finisher') }}</option>
              </select>
            </div>
            <div class="col-span-1">
              <label class="text-xs text-gray-500">{{ t('age_range') }}</label>
              <input v-model.number="fp.ageStartDays" type="number" class="w-full border rounded-lg px-2 py-1 text-sm" placeholder="Start" />
            </div>
            <div class="col-span-1">
              <label class="text-xs text-gray-500">&nbsp;</label>
              <input v-model.number="fp.ageEndDays" type="number" class="w-full border rounded-lg px-2 py-1 text-sm" placeholder="End" />
            </div>
            <div class="col-span-2">
              <label class="text-xs text-gray-500">{{ t('feed_type') }}</label>
              <input v-model="fp.feedType" type="text" class="w-full border rounded-lg px-2 py-1 text-sm" />
            </div>
            <div class="col-span-1">
              <button type="button" @click="removeFeedingProgram(idx)" class="text-red-500 text-sm">×</button>
            </div>
          </div>
        </div>

        <!-- Vaccination Schedule Section -->
        <div class="border border-gray-200 rounded-xl p-4">
          <div class="flex justify-between items-center mb-3">
            <h4 class="font-bold text-gray-800">{{ t('breed_vaccination_schedule') }}</h4>
            <button
              type="button"
              @click="addVaccination"
              class="text-sm text-green-600 hover:text-green-700"
            >+ {{ t('breed_form_add_vaccination') }}</button>
          </div>
          <div v-for="(vax, idx) in form.vaccinationSchedule" :key="idx" class="grid grid-cols-5 gap-2 mb-2 items-end">
            <div class="col-span-2">
              <label class="text-xs text-gray-500">{{ t('vaccine_name') }}</label>
              <input v-model="vax.vaccineName" type="text" class="w-full border rounded-lg px-2 py-1 text-sm" />
            </div>
            <div class="col-span-1">
              <label class="text-xs text-gray-500">{{ t('age_days') }}</label>
              <input v-model.number="vax.recommendedAgeDays" type="number" class="w-full border rounded-lg px-2 py-1 text-sm" />
            </div>
            <div class="col-span-1">
              <label class="text-xs text-gray-500">{{ t('route') }}</label>
              <select v-model="vax.administrationRoute" class="w-full border rounded-lg px-2 py-1 text-sm">
                <option value="INJECTION">{{ t('admin_route_injection') }}</option>
                <option value="ORAL">{{ t('admin_route_oral') }}</option>
                <option value="DRINKING_WATER">{{ t('admin_route_drinking_water') }}</option>
                <option value="EYE_DROP">{{ t('admin_route_eye_drop') }}</option>
                <option value="SPRAY">{{ t('admin_route_spray') }}</option>
              </select>
            </div>
            <div class="col-span-1">
              <button type="button" @click="removeVaccination(idx)" class="text-red-500 text-sm">×</button>
            </div>
          </div>
        </div>

        <!-- Housing Guidelines Section -->
        <div class="border border-gray-200 rounded-xl p-4">
          <div class="flex justify-between items-center mb-3">
            <h4 class="font-bold text-gray-800">{{ t('breed_housing_guidelines') }}</h4>
            <button
              type="button"
              @click="addHousingGuideline"
              class="text-sm text-green-600 hover:text-green-700"
            >+ {{ t('breed_form_add_housing') }}</button>
          </div>
          <div v-for="(hg, idx) in form.housingGuidelines" :key="idx" class="grid grid-cols-4 gap-2 mb-2 items-end">
            <div class="col-span-1">
              <label class="text-xs text-gray-500">{{ t('parameter') }}</label>
              <input v-model="hg.parameterName" type="text" class="w-full border rounded-lg px-2 py-1 text-sm" />
            </div>
            <div class="col-span-1">
              <label class="text-xs text-gray-500">{{ t('value') }}</label>
              <input v-model="hg.recommendedValue" type="text" class="w-full border rounded-lg px-2 py-1 text-sm" />
            </div>
            <div class="col-span-1">
              <label class="text-xs text-gray-500">{{ t('unit') }}</label>
              <input v-model="hg.unit" type="text" class="w-full border rounded-lg px-2 py-1 text-sm" />
            </div>
            <div class="col-span-1">
              <button type="button" @click="removeHousingGuideline(idx)" class="text-red-500 text-sm">×</button>
            </div>
          </div>
        </div>

        <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>
      </form>

      <!-- Actions -->
      <div class="p-6 border-t border-gray-100 flex justify-end gap-3 bg-gray-50/50">
        <button
          type="button"
          @click="$emit('close')"
          class="px-6 py-2.5 rounded-xl border border-gray-200 text-gray-600 font-medium hover:bg-gray-100 transition-colors"
        >
          {{ t('common.cancel') }}
        </button>
        <button
          @click="handleSubmit"
          :disabled="loading"
          class="px-6 py-2.5 rounded-xl bg-green-700 text-white font-bold hover:bg-green-800 disabled:opacity-50 transition-all shadow-lg shadow-green-200"
        >
          {{ loading ? '...' : t('common.save') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from '@/i18n'
import { useBreedsStore } from '@/stores/breeds.store'
import type { BreedDetail, CreateBreedRequest, UpdateBreedRequest, FeedingProgramTemplate, VaccinationTemplate, HousingGuideline } from '@/types'

const props = defineProps<{
  initialData?: BreedDetail
  loading?: boolean
}>()

const emit = defineEmits<{
  close: []
  submit: [data: CreateBreedRequest | UpdateBreedRequest]
}>()

const { t } = useI18n()
const breedsStore = useBreedsStore()
const error = ref('')

const form = reactive<any>({
  name: props.initialData?.name || '',
  nameAr: props.initialData?.nameAr || '',
  nameEn: props.initialData?.nameEn || '',
  category: 'CUSTOM',
  purpose: props.initialData?.purpose || 'LAYERS',
  origin: props.initialData?.origin || '',
  description: props.initialData?.description || '',
  climateSuitability: props.initialData?.climateSuitability || '',
  avgWeightMaleKg: props.initialData?.avgWeightMaleKg || null,
  avgWeightFemaleKg: props.initialData?.avgWeightFemaleKg || null,
  feedingPrograms: props.initialData?.feedingPrograms || [],
  vaccinationSchedule: props.initialData?.vaccinationSchedule || [],
  productionBenchmarks: props.initialData?.productionBenchmarks || [],
  housingGuidelines: props.initialData?.housingGuidelines || []
})

function addFeedingProgram() {
  form.feedingPrograms.push({
    growthStage: 'STARTER',
    ageStartDays: 0,
    ageEndDays: 21,
    feedType: '',
    dailyQuantityGrams: 0,
    feedingFrequency: 2,
    sortOrder: form.feedingPrograms.length
  })
}

function removeFeedingProgram(idx: number) {
  form.feedingPrograms.splice(idx, 1)
}

function addVaccination() {
  form.vaccinationSchedule.push({
    vaccineName: '',
    recommendedAgeDays: 1,
    administrationRoute: 'INJECTION',
    isMandatory: false,
    sortOrder: form.vaccinationSchedule.length
  })
}

function removeVaccination(idx: number) {
  form.vaccinationSchedule.splice(idx, 1)
}

function addHousingGuideline() {
  form.housingGuidelines.push({
    parameterName: '',
    recommendedValue: '',
    sortOrder: form.housingGuidelines.length
  })
}

function removeHousingGuideline(idx: number) {
  form.housingGuidelines.splice(idx, 1)
}

async function handleSubmit() {
  if (!form.name || !form.purpose) return
  error.value = ''
  
  const submitData: any = {
    ...form,
    category: 'CUSTOM'
  }
  
  emit('submit', submitData)
}
</script>
