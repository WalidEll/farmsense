<template>
  <div class="p-4 lg:p-6 space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-900">{{ t('crop_admin') }}</h1>
      <RouterLink to="/crops" class="text-sm text-gray-500 hover:text-gray-700 transition-colors">
        {{ t('common_back') }}
      </RouterLink>
    </div>

    <div class="lg:grid lg:grid-cols-3 gap-6">
      <!-- Left panel: crop list -->
      <div class="space-y-3 mb-6 lg:mb-0">
        <button
          @click="startNew"
          class="w-full px-4 py-2.5 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors flex items-center justify-center gap-2"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          {{ t('crop_create') }}
        </button>

        <!-- Loading -->
        <div v-if="store.loading && !store.crops.length" class="flex justify-center py-8">
          <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-green-600"></div>
        </div>

        <!-- Crop list -->
        <div class="space-y-1 max-h-[60vh] overflow-y-auto">
          <button
            v-for="crop in store.crops"
            :key="crop.id"
            @click="selectCrop(crop)"
            :class="[
              'w-full text-start px-3 py-2.5 rounded-lg text-sm transition-colors',
              selectedCrop?.id === crop.id
                ? 'bg-green-50 text-green-700 font-medium'
                : 'text-gray-700 hover:bg-gray-50',
            ]"
          >
            <div class="flex items-center justify-between">
              <span>{{ crop.name }}</span>
              <span class="text-xs text-gray-400">{{ crop.category }}</span>
            </div>
          </button>
        </div>
      </div>

      <!-- Right panel: forms -->
      <div class="lg:col-span-2 space-y-6">
        <!-- New crop mode -->
        <div v-if="mode === 'new'" class="bg-white border border-gray-200 rounded-xl p-5">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">{{ t('crop_create') }}</h2>
          <CropForm @saved="onCropSaved" @cancelled="mode = 'idle'" />
        </div>

        <!-- Edit crop mode -->
        <template v-if="mode === 'edit' && selectedCrop">
          <div class="bg-white border border-gray-200 rounded-xl p-5">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-semibold text-gray-900">{{ t('crop_edit') }}: {{ selectedCrop.name }}</h2>
              <button
                @click="onDelete"
                class="text-sm text-red-600 hover:text-red-700 transition-colors"
              >
                {{ t('crop_delete') }}
              </button>
            </div>
            <CropForm :crop="selectedCrop" @saved="onCropSaved" @cancelled="mode = 'idle'; selectedCrop = null" />
          </div>

          <!-- Requirements section -->
          <div class="bg-white border border-gray-200 rounded-xl p-5">
            <h3 class="text-base font-semibold text-gray-900 mb-4">{{ t('crop_requirements') }}</h3>
            <form @submit.prevent="onSaveRequirements" class="space-y-4">
              <div class="grid grid-cols-2 sm:grid-cols-3 gap-4">
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_soil') }} Min (%)</label>
                  <input v-model.number="reqForm.soilMoistureMin" type="number" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_soil') }} Max (%)</label>
                  <input v-model.number="reqForm.soilMoistureMax" type="number" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_temp') }} Min (C)</label>
                  <input v-model.number="reqForm.tempMin" type="number" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_temp') }} Max (C)</label>
                  <input v-model.number="reqForm.tempMax" type="number" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_light') }} Min (lux)</label>
                  <input v-model.number="reqForm.lightMin" type="number" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_light') }} Max (lux)</label>
                  <input v-model.number="reqForm.lightMax" type="number" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_humidity') }} Min (%)</label>
                  <input v-model.number="reqForm.humidityMin" type="number" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_humidity') }} Max (%)</label>
                  <input v-model.number="reqForm.humidityMax" type="number" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_ph') }} Min</label>
                  <input v-model.number="reqForm.phMin" type="number" step="0.1" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_ph') }} Max</label>
                  <input v-model.number="reqForm.phMax" type="number" step="0.1" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_soilType') }}</label>
                  <input v-model="reqForm.soilType" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_waterFreq') }}</label>
                  <input v-model="reqForm.waterFrequency" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
              </div>
              <button type="submit" class="px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors">
                {{ t('common_save') }}
              </button>
            </form>
          </div>

          <!-- Growth Stages section -->
          <div class="bg-white border border-gray-200 rounded-xl p-5">
            <h3 class="text-base font-semibold text-gray-900 mb-4">{{ t('crop_stages') }}</h3>

            <!-- Existing stages -->
            <div v-if="currentStages.length" class="space-y-2 mb-4">
              <div
                v-for="stage in currentStages"
                :key="stage.id"
                class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
              >
                <div class="flex items-center gap-3">
                  <span class="w-6 h-6 rounded-full bg-green-600 text-white flex items-center justify-center text-xs font-bold">
                    {{ stage.stageOrder }}
                  </span>
                  <div>
                    <p class="text-sm font-medium text-gray-900">{{ stage.name }}</p>
                    <p v-if="stage.durationDays" class="text-xs text-gray-500">{{ stage.durationDays }} {{ t('crops_daysToHarvest') }}</p>
                  </div>
                </div>
                <div class="flex gap-2">
                  <button @click="editStage(stage)" class="text-xs text-green-600 hover:text-green-700">{{ t('crop_edit') }}</button>
                  <button @click="onDeleteStage(stage.id)" class="text-xs text-red-600 hover:text-red-700">{{ t('crop_delete') }}</button>
                </div>
              </div>
            </div>

            <!-- Add/Edit stage form -->
            <form @submit.prevent="onSaveStage" class="space-y-3 border-t border-gray-100 pt-4">
              <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">#</label>
                  <input v-model.number="stageForm.stageOrder" type="number" min="1" required class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_name') }}</label>
                  <input v-model="stageForm.name" required class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_nameAr') }}</label>
                  <input v-model="stageForm.nameAr" dir="rtl" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_stage_duration', { n: '' }) }}</label>
                  <input v-model.number="stageForm.durationDays" type="number" min="0" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_description') }}</label>
                <textarea v-model="stageForm.description" rows="2" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"></textarea>
              </div>
              <div class="flex gap-2">
                <button type="submit" class="px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors">
                  {{ editingStageId ? t('common_save') : t('crop_create') }}
                </button>
                <button v-if="editingStageId" type="button" @click="cancelStageEdit" class="px-4 py-2 bg-gray-100 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-200 transition-colors">
                  {{ t('common_cancel') }}
                </button>
              </div>
            </form>
          </div>

          <!-- Nutrients section -->
          <div class="bg-white border border-gray-200 rounded-xl p-5">
            <h3 class="text-base font-semibold text-gray-900 mb-4">{{ t('crop_nutrients') }}</h3>
            <form @submit.prevent="onSaveNutrients" class="space-y-4">
              <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_nitrogen') }}</label>
                  <select v-model="nutForm.nitrogenNeed" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500">
                    <option value="">-</option>
                    <option value="LOW">LOW</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HIGH">HIGH</option>
                  </select>
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_phosphorus') }}</label>
                  <select v-model="nutForm.phosphorusNeed" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500">
                    <option value="">-</option>
                    <option value="LOW">LOW</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HIGH">HIGH</option>
                  </select>
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_potassium') }}</label>
                  <select v-model="nutForm.potassiumNeed" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500">
                    <option value="">-</option>
                    <option value="LOW">LOW</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HIGH">HIGH</option>
                  </select>
                </div>
              </div>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_fertilizer') }}</label>
                  <input v-model="nutForm.fertilizerType" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_appFrequency') }}</label>
                  <input v-model="nutForm.applicationFrequency" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
              </div>
              <button type="submit" class="px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors">
                {{ t('common_save') }}
              </button>
            </form>
          </div>

          <!-- Issues section -->
          <div class="bg-white border border-gray-200 rounded-xl p-5">
            <h3 class="text-base font-semibold text-gray-900 mb-4">{{ t('crop_issues') }}</h3>

            <!-- Existing issues -->
            <div v-if="currentIssues.length" class="space-y-2 mb-4">
              <div
                v-for="issue in currentIssues"
                :key="issue.id"
                class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
              >
                <div>
                  <p class="text-sm font-medium text-gray-900">{{ issue.name }}</p>
                  <p class="text-xs text-gray-500">{{ issue.issueType }}</p>
                </div>
                <div class="flex gap-2">
                  <button @click="editIssue(issue)" class="text-xs text-green-600 hover:text-green-700">{{ t('crop_edit') }}</button>
                  <button @click="onDeleteIssue(issue.id)" class="text-xs text-red-600 hover:text-red-700">{{ t('crop_delete') }}</button>
                </div>
              </div>
            </div>

            <!-- Add/Edit issue form -->
            <form @submit.prevent="onSaveIssue" class="space-y-3 border-t border-gray-100 pt-4">
              <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_name') }}</label>
                  <input v-model="issueForm.name" required class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_nameAr') }}</label>
                  <input v-model="issueForm.nameAr" dir="rtl" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500" />
                </div>
                <div>
                  <label class="block text-xs font-medium text-gray-500 mb-1">Type</label>
                  <select v-model="issueForm.issueType" required class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500">
                    <option value="">{{ t('common_select') }}</option>
                    <option value="PEST">{{ t('crop_issue_pest') }}</option>
                    <option value="DISEASE">{{ t('crop_issue_disease') }}</option>
                    <option value="DEFICIENCY">{{ t('crop_issue_deficiency') }}</option>
                  </select>
                </div>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_symptoms') }}</label>
                <textarea v-model="issueForm.symptoms" rows="2" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"></textarea>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_treatment') }}</label>
                <textarea v-model="issueForm.treatment" rows="2" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"></textarea>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-500 mb-1">{{ t('crop_prevention') }}</label>
                <textarea v-model="issueForm.prevention" rows="2" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"></textarea>
              </div>
              <div class="flex gap-2">
                <button type="submit" class="px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors">
                  {{ editingIssueId ? t('common_save') : t('crop_create') }}
                </button>
                <button v-if="editingIssueId" type="button" @click="cancelIssueEdit" class="px-4 py-2 bg-gray-100 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-200 transition-colors">
                  {{ t('common_cancel') }}
                </button>
              </div>
            </form>
          </div>
        </template>

        <!-- Idle state -->
        <div v-if="mode === 'idle'" class="text-center py-16 text-gray-400">
          <svg class="w-12 h-12 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
          <p class="text-sm">{{ t('crops_empty') }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { useI18n } from '@/i18n'
import { useCropsStore } from '@/stores/crops.store'
import CropForm from '@/components/crops/CropForm.vue'
import type { Crop, CropGrowthStage, CropIssue } from '@/types'

const { t } = useI18n()
const store = useCropsStore()

const selectedCrop = ref<Crop | null>(null)
const mode = ref<'idle' | 'new' | 'edit'>('idle')

// Requirements form
const reqForm = reactive({
  soilMoistureMin: undefined as number | undefined,
  soilMoistureMax: undefined as number | undefined,
  tempMin: undefined as number | undefined,
  tempMax: undefined as number | undefined,
  lightMin: undefined as number | undefined,
  lightMax: undefined as number | undefined,
  humidityMin: undefined as number | undefined,
  humidityMax: undefined as number | undefined,
  phMin: undefined as number | undefined,
  phMax: undefined as number | undefined,
  soilType: '',
  waterFrequency: '',
})

// Stage form
const editingStageId = ref<string | null>(null)
const stageForm = reactive({
  stageOrder: 1,
  name: '',
  nameAr: '',
  nameEn: '',
  durationDays: undefined as number | undefined,
  description: '',
  descriptionAr: '',
  descriptionEn: '',
})

// Nutrient form
const nutForm = reactive({
  nitrogenNeed: '',
  phosphorusNeed: '',
  potassiumNeed: '',
  fertilizerType: '',
  fertilizerTypeAr: '',
  fertilizerTypeEn: '',
  applicationFrequency: '',
})

// Issue form
const editingIssueId = ref<string | null>(null)
const issueForm = reactive({
  issueType: '',
  name: '',
  nameAr: '',
  nameEn: '',
  symptoms: '',
  symptomsAr: '',
  symptomsEn: '',
  treatment: '',
  treatmentAr: '',
  treatmentEn: '',
  prevention: '',
})

const currentStages = computed(() => {
  if (!store.currentCrop) return []
  return [...store.currentCrop.stages].sort((a, b) => a.stageOrder - b.stageOrder)
})

const currentIssues = computed(() => store.currentCrop?.issues || [])

function startNew() {
  selectedCrop.value = null
  store.currentCrop = null
  mode.value = 'new'
}

async function selectCrop(crop: Crop) {
  selectedCrop.value = crop
  mode.value = 'edit'
  await store.fetchById(crop.id)
  populateSubForms()
}

function populateSubForms() {
  const c = store.currentCrop
  if (!c) return

  // Populate requirements
  if (c.requirements) {
    reqForm.soilMoistureMin = c.requirements.soilMoistureMin
    reqForm.soilMoistureMax = c.requirements.soilMoistureMax
    reqForm.tempMin = c.requirements.tempMin
    reqForm.tempMax = c.requirements.tempMax
    reqForm.lightMin = c.requirements.lightMin
    reqForm.lightMax = c.requirements.lightMax
    reqForm.humidityMin = c.requirements.humidityMin
    reqForm.humidityMax = c.requirements.humidityMax
    reqForm.phMin = c.requirements.phMin
    reqForm.phMax = c.requirements.phMax
    reqForm.soilType = c.requirements.soilType || ''
    reqForm.waterFrequency = c.requirements.waterFrequency || ''
  } else {
    resetReqForm()
  }

  // Populate nutrients
  if (c.nutrients) {
    nutForm.nitrogenNeed = c.nutrients.nitrogenNeed || ''
    nutForm.phosphorusNeed = c.nutrients.phosphorusNeed || ''
    nutForm.potassiumNeed = c.nutrients.potassiumNeed || ''
    nutForm.fertilizerType = c.nutrients.fertilizerType || ''
    nutForm.fertilizerTypeAr = c.nutrients.fertilizerTypeAr || ''
    nutForm.fertilizerTypeEn = c.nutrients.fertilizerTypeEn || ''
    nutForm.applicationFrequency = c.nutrients.applicationFrequency || ''
  } else {
    resetNutForm()
  }

  // Reset stage/issue editing
  cancelStageEdit()
  cancelIssueEdit()
}

function resetReqForm() {
  reqForm.soilMoistureMin = undefined
  reqForm.soilMoistureMax = undefined
  reqForm.tempMin = undefined
  reqForm.tempMax = undefined
  reqForm.lightMin = undefined
  reqForm.lightMax = undefined
  reqForm.humidityMin = undefined
  reqForm.humidityMax = undefined
  reqForm.phMin = undefined
  reqForm.phMax = undefined
  reqForm.soilType = ''
  reqForm.waterFrequency = ''
}

function resetNutForm() {
  nutForm.nitrogenNeed = ''
  nutForm.phosphorusNeed = ''
  nutForm.potassiumNeed = ''
  nutForm.fertilizerType = ''
  nutForm.fertilizerTypeAr = ''
  nutForm.fertilizerTypeEn = ''
  nutForm.applicationFrequency = ''
}

function onCropSaved(crop: Crop) {
  selectedCrop.value = crop
  mode.value = 'edit'
  store.fetchById(crop.id).then(populateSubForms)
}

async function onDelete() {
  if (!selectedCrop.value) return
  await store.remove(selectedCrop.value.id)
  selectedCrop.value = null
  mode.value = 'idle'
}

async function onSaveRequirements() {
  if (!selectedCrop.value) return
  const payload: Record<string, unknown> = {}
  if (reqForm.soilMoistureMin != null) payload.soilMoistureMin = reqForm.soilMoistureMin
  if (reqForm.soilMoistureMax != null) payload.soilMoistureMax = reqForm.soilMoistureMax
  if (reqForm.tempMin != null) payload.tempMin = reqForm.tempMin
  if (reqForm.tempMax != null) payload.tempMax = reqForm.tempMax
  if (reqForm.lightMin != null) payload.lightMin = reqForm.lightMin
  if (reqForm.lightMax != null) payload.lightMax = reqForm.lightMax
  if (reqForm.humidityMin != null) payload.humidityMin = reqForm.humidityMin
  if (reqForm.humidityMax != null) payload.humidityMax = reqForm.humidityMax
  if (reqForm.phMin != null) payload.phMin = reqForm.phMin
  if (reqForm.phMax != null) payload.phMax = reqForm.phMax
  if (reqForm.soilType) payload.soilType = reqForm.soilType
  if (reqForm.waterFrequency) payload.waterFrequency = reqForm.waterFrequency
  await store.saveRequirements(selectedCrop.value.id, payload)
}

// Stage management
function editStage(stage: CropGrowthStage) {
  editingStageId.value = stage.id
  stageForm.stageOrder = stage.stageOrder
  stageForm.name = stage.name
  stageForm.nameAr = stage.nameAr || ''
  stageForm.nameEn = stage.nameEn || ''
  stageForm.durationDays = stage.durationDays
  stageForm.description = stage.description || ''
  stageForm.descriptionAr = stage.descriptionAr || ''
  stageForm.descriptionEn = stage.descriptionEn || ''
}

function cancelStageEdit() {
  editingStageId.value = null
  stageForm.stageOrder = (currentStages.value.length || 0) + 1
  stageForm.name = ''
  stageForm.nameAr = ''
  stageForm.nameEn = ''
  stageForm.durationDays = undefined
  stageForm.description = ''
  stageForm.descriptionAr = ''
  stageForm.descriptionEn = ''
}

async function onSaveStage() {
  if (!selectedCrop.value || !stageForm.name) return
  const payload: Record<string, unknown> = {
    stageOrder: stageForm.stageOrder,
    name: stageForm.name,
    nameAr: stageForm.nameAr || undefined,
    nameEn: stageForm.nameEn || undefined,
    durationDays: stageForm.durationDays || undefined,
    description: stageForm.description || undefined,
    descriptionAr: stageForm.descriptionAr || undefined,
    descriptionEn: stageForm.descriptionEn || undefined,
  }
  if (editingStageId.value) {
    await store.updateStage(selectedCrop.value.id, editingStageId.value, payload)
  } else {
    await store.addStage(selectedCrop.value.id, payload)
  }
  cancelStageEdit()
}

async function onDeleteStage(stageId: string) {
  if (!selectedCrop.value) return
  await store.deleteStage(selectedCrop.value.id, stageId)
}

// Nutrient management
async function onSaveNutrients() {
  if (!selectedCrop.value) return
  const payload: Record<string, unknown> = {}
  if (nutForm.nitrogenNeed) payload.nitrogenNeed = nutForm.nitrogenNeed
  if (nutForm.phosphorusNeed) payload.phosphorusNeed = nutForm.phosphorusNeed
  if (nutForm.potassiumNeed) payload.potassiumNeed = nutForm.potassiumNeed
  if (nutForm.fertilizerType) payload.fertilizerType = nutForm.fertilizerType
  if (nutForm.fertilizerTypeAr) payload.fertilizerTypeAr = nutForm.fertilizerTypeAr
  if (nutForm.fertilizerTypeEn) payload.fertilizerTypeEn = nutForm.fertilizerTypeEn
  if (nutForm.applicationFrequency) payload.applicationFrequency = nutForm.applicationFrequency
  await store.saveNutrients(selectedCrop.value.id, payload)
}

// Issue management
function editIssue(issue: CropIssue) {
  editingIssueId.value = issue.id
  issueForm.issueType = issue.issueType
  issueForm.name = issue.name
  issueForm.nameAr = issue.nameAr || ''
  issueForm.nameEn = issue.nameEn || ''
  issueForm.symptoms = issue.symptoms || ''
  issueForm.symptomsAr = issue.symptomsAr || ''
  issueForm.symptomsEn = issue.symptomsEn || ''
  issueForm.treatment = issue.treatment || ''
  issueForm.treatmentAr = issue.treatmentAr || ''
  issueForm.treatmentEn = issue.treatmentEn || ''
  issueForm.prevention = issue.prevention || ''
}

function cancelIssueEdit() {
  editingIssueId.value = null
  issueForm.issueType = ''
  issueForm.name = ''
  issueForm.nameAr = ''
  issueForm.nameEn = ''
  issueForm.symptoms = ''
  issueForm.symptomsAr = ''
  issueForm.symptomsEn = ''
  issueForm.treatment = ''
  issueForm.treatmentAr = ''
  issueForm.treatmentEn = ''
  issueForm.prevention = ''
}

async function onSaveIssue() {
  if (!selectedCrop.value || !issueForm.name || !issueForm.issueType) return
  const payload: Record<string, unknown> = {
    issueType: issueForm.issueType,
    name: issueForm.name,
    nameAr: issueForm.nameAr || undefined,
    nameEn: issueForm.nameEn || undefined,
    symptoms: issueForm.symptoms || undefined,
    symptomsAr: issueForm.symptomsAr || undefined,
    symptomsEn: issueForm.symptomsEn || undefined,
    treatment: issueForm.treatment || undefined,
    treatmentAr: issueForm.treatmentAr || undefined,
    treatmentEn: issueForm.treatmentEn || undefined,
    prevention: issueForm.prevention || undefined,
  }
  if (editingIssueId.value) {
    await store.updateIssue(selectedCrop.value.id, editingIssueId.value, payload)
  } else {
    await store.addIssue(selectedCrop.value.id, payload)
  }
  cancelIssueEdit()
}

async function onDeleteIssue(issueId: string) {
  if (!selectedCrop.value) return
  await store.deleteIssue(selectedCrop.value.id, issueId)
}

onMounted(() => {
  store.fetchAll()
})
</script>
