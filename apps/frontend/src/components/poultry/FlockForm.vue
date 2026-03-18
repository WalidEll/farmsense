<template>
  <div class="fixed inset-0 bg-black/50 z-[60] flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-2xl overflow-hidden flex flex-col max-h-[90vh]">
      <!-- Header -->
      <div class="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50/50">
        <h3 class="text-xl font-bold text-gray-900">
          {{ initialData ? t('common.edit') : t('flocks_create') }}
        </h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 transition-colors">
          <span class="text-2xl">×</span>
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-4 overflow-y-auto flex-1">
        <!-- Names -->
        <div class="space-y-3">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-bold text-gray-700 mb-1">
                {{ t('flock_name') }} <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.name"
                type="text"
                required
                class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
                :placeholder="t('flock_name')"
              />
            </div>
            <div>
              <label class="block text-sm font-bold text-gray-700 mb-1">
                {{ t('flock_batch_code') }} <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.batchCode"
                type="text"
                :required="!initialData"
                :readonly="!!initialData"
                class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none font-mono"
                :class="{ 'bg-gray-50 text-gray-500 cursor-not-allowed': !!initialData }"
                placeholder="FL-2026-001"
              />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-3 text-xs">
            <div>
              <label class="block font-bold text-gray-500 mb-1">{{ t('crop_nameAr') }}</label>
              <input v-model="form.nameAr" type="text" class="w-full border border-gray-200 rounded-lg px-3 py-1.5 outline-none" />
            </div>
            <div>
              <label class="block font-bold text-gray-500 mb-1">Name (EN)</label>
              <input v-model="form.nameEn" type="text" class="w-full border border-gray-200 rounded-lg px-3 py-1.5 outline-none" />
            </div>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">
              {{ t('flock_bird_count') }} <span class="text-red-500">*</span>
            </label>
            <input
              v-model.number="form.birdCount"
              type="number"
              required
              min="1"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
            />
          </div>
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">
              {{ t('flock_purpose') }} <span class="text-red-500">*</span>
            </label>
            <select
              v-model="form.purpose"
              required
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none bg-white"
            >
              <option value="LAYERS">{{ t('flock_purpose_layers') }}</option>
              <option value="BROILERS">{{ t('flock_purpose_broilers') }}</option>
              <option value="DUAL_PURPOSE">{{ t('flock_purpose_dual_purpose') }}</option>
              <option value="BREEDERS">{{ t('flock_purpose_breeders') }}</option>
            </select>
          </div>
        </div>

        <!-- Breed Selector -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('flock_breed') }}
          </label>
          <div class="relative">
            <input
              v-model="breedSearch"
              type="text"
              @focus="showBreedPicker = true"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
              :placeholder="t('breed_search_placeholder')"
            />
            <div
              v-if="selectedBreedName"
              class="absolute right-3 top-1/2 -translate-y-1/2 flex items-center gap-2"
            >
              <span class="text-sm text-gray-600">{{ selectedBreedName }}</span>
              <button
                type="button"
                @click="clearBreed"
                class="text-gray-400 hover:text-red-500"
              >×</button>
            </div>
            <!-- Breed Picker Dropdown -->
            <div
              v-if="showBreedPicker && breedList.length"
              class="absolute z-10 mt-1 w-full bg-white border border-gray-200 rounded-xl shadow-lg max-h-60 overflow-y-auto"
            >
              <button
                v-for="breed in breedList"
                :key="breed.id"
                type="button"
                @click="selectBreed(breed)"
                class="w-full px-4 py-2 text-left hover:bg-gray-50 flex items-center justify-between"
              >
                <span class="font-medium">{{ getBreedName(breed) }}</span>
                <span class="text-xs text-gray-400">{{ breed.purpose }}</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Housing Location -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('housing_location') }}
          </label>
          <div class="flex gap-2">
            <select
              v-model="form.housingLocationId"
              class="flex-1 border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none bg-white"
            >
              <option :value="undefined">{{ t('common.select') }}</option>
              <option v-for="loc in poultryStore.housingLocations" :key="loc.id" :value="loc.id">
                {{ loc.name }} ({{ t(`housing_location_type_${loc.locationType.toLowerCase()}`) }})
              </option>
            </select>
            <button
              type="button"
              @click="showCreateHousingModal = true"
              class="w-11 h-11 flex items-center justify-center bg-gray-100 hover:bg-green-100 text-gray-500 hover:text-green-600 rounded-xl transition-colors"
            >
              ➕
            </button>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">
              {{ t('flock_start_date') }}
            </label>
            <input
              v-model="form.startDate"
              type="date"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
            />
          </div>
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">
              {{ t('flock_supplier') }}
            </label>
            <select
              v-model="form.supplierId"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none bg-white"
            >
              <option :value="undefined">{{ t('common.select') }}</option>
              <option v-for="s in poultryStore.suppliers" :key="s.id" :value="s.id">
                {{ s.name }}
              </option>
            </select>
          </div>
        </div>

        <!-- Auto-generated Programs -->
        <div v-if="selectedBreedTemplates" class="space-y-4 pt-2">
          <!-- Feeding Program -->
          <div v-if="selectedBreedTemplates.feedingPrograms?.length" class="bg-green-50 rounded-xl p-4">
            <h4 class="font-bold text-green-800 mb-2">🌾 {{ t('feeding_program') }}</h4>
            <div class="space-y-2">
              <div
                v-for="fp in selectedBreedTemplates.feedingPrograms"
                :key="fp.id"
                class="flex items-center gap-2 text-sm"
              >
                <span class="px-2 py-0.5 rounded bg-green-100 text-green-700 text-xs">
                  {{ t(`growth_stage_${fp.growthStage.toLowerCase()}`) }}
                </span>
                <span class="text-gray-600">{{ fp.feedType }} - {{ fp.dailyQuantityGrams }}g</span>
              </div>
            </div>
          </div>

          <!-- Vaccination Schedule -->
          <div v-if="selectedBreedTemplates.vaccinationSchedule?.length" class="bg-blue-50 rounded-xl p-4">
            <h4 class="font-bold text-blue-800 mb-2">💉 {{ t('vaccination_schedule') }}</h4>
            <div class="space-y-1 text-sm text-gray-600">
              <div v-for="vax in selectedBreedTemplates.vaccinationSchedule.slice(0, 3)" :key="vax.id">
                {{ vax.vaccineName }} ({{ t('day') }} {{ vax.recommendedAgeDays }})
              </div>
              <div v-if="selectedBreedTemplates.vaccinationSchedule.length > 3" class="text-xs text-gray-400">
                +{{ selectedBreedTemplates.vaccinationSchedule.length - 3 }} more
              </div>
            </div>
          </div>

          <!-- Housing Guidelines -->
          <div v-if="selectedBreedTemplates.housingGuidelines?.length" class="bg-amber-50 rounded-xl p-4">
            <h4 class="font-bold text-amber-800 mb-2">🏠 {{ t('housing_guidelines') }}</h4>
            <div class="space-y-1 text-sm text-gray-600">
              <div v-for="hg in selectedBreedTemplates.housingGuidelines.slice(0, 2)" :key="hg.id">
                {{ hg.parameterName }}: {{ hg.recommendedValue }} {{ hg.unit || '' }}
              </div>
            </div>
          </div>
        </div>

        <div v-if="initialData">
          <label class="block text-sm font-bold text-gray-700 mb-1">
            Status
          </label>
          <select
            v-model="form.status"
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none bg-white"
          >
            <option value="ACTIVE">{{ t('flock_status_active') }}</option>
            <option value="SOLD">{{ t('flock_status_sold') }}</option>
            <option value="PHASED_OUT">{{ t('flock_status_phased_out') }}</option>
            <option value="FINISHED">{{ t('flock_status_finished') }}</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('plan_description') }}
          </label>
          <textarea
            v-model="form.notes"
            rows="2"
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
          ></textarea>
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

    <!-- Breed Change Confirmation -->
    <div v-if="showBreedChangeConfirm" class="fixed inset-0 bg-black/50 z-[70] flex items-center justify-center p-4">
      <div class="bg-white rounded-2xl shadow-xl max-w-sm w-full p-6 space-y-4">
        <h3 class="text-lg font-bold text-gray-900">⚠️ {{ t('breed_change_confirm') }}</h3>
        <p class="text-sm text-gray-600">{{ t('breed_change_warning') }}</p>
        <div class="flex gap-3 justify-end">
          <button
            @click="showBreedChangeConfirm = false; pendingBreed = null"
            class="px-4 py-2 rounded-xl border border-gray-200 text-gray-600 hover:bg-gray-50 transition-colors text-sm font-medium"
          >
            {{ t('keep_current') }}
          </button>
          <button
            @click="confirmBreedChange"
            class="px-4 py-2 rounded-xl bg-green-600 text-white hover:bg-green-700 transition-colors text-sm font-medium"
          >
            {{ t('regenerate') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Housing Creation Modal -->
    <HousingLocationForm
      v-if="showCreateHousingModal"
      @close="showCreateHousingModal = false"
      @submit="handleCreateHousing"
      :loading="housingLoading"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useI18n } from '@/i18n'
import { usePoultryStore } from '@/stores/poultry.store'
import { useBreedsStore } from '@/stores/breeds.store'
import { useAuthStore } from '@/stores/auth.store'
import HousingLocationForm from './HousingLocationForm.vue'
import type { Flock, CreateFlockRequest, UpdateFlockRequest, BreedSummary, BreedDetail, CreateHousingLocationRequest } from '@/types'

const props = defineProps<{
  initialData?: Flock
  loading?: boolean
}>()

const emit = defineEmits<{
  close: []
  submit: [data: CreateFlockRequest | UpdateFlockRequest]
}>()

const { t } = useI18n()
const poultryStore = usePoultryStore()
const breedsStore = useBreedsStore()
const authStore = useAuthStore()
const error = ref('')

// Housing creation
const showCreateHousingModal = ref(false)
const housingLoading = ref(false)

async function handleCreateHousing(data: CreateHousingLocationRequest) {
  try {
    const newLoc = await poultryStore.createHousingLocation(data)
    form.housingLocationId = newLoc.id
    showCreateHousingModal.value = false
  } catch (e: any) {
    console.error('Failed to create housing', e)
  }
}

// Breed selector state
const breedSearch = ref('')
const showBreedPicker = ref(false)
const selectedBreedId = ref<string | null>(props.initialData?.breedId || null)
const selectedBreedTemplates = ref<BreedDetail | null>(null)
const breedList = ref<BreedSummary[]>([])
const breedsLoading = ref(false)

// Breed change confirmation
const showBreedChangeConfirm = ref(false)
const pendingBreed = ref<BreedSummary | null>(null)
const previousBreedId = ref<string | null>(null)

const selectedBreedName = computed(() => {
  if (!selectedBreedId.value || !breedList.value.length) return ''
  const breed = breedList.value.find(b => b.id === selectedBreedId.value)
  return breed ? getBreedName(breed) : ''
})

function getBreedName(breed: BreedSummary): string {
  const lang = authStore.lang
  if (lang === 'AR' && breed.nameAr) return breed.nameAr
  if (lang === 'EN' && breed.nameEn) return breed.nameEn
  return breed.name
}

async function loadBreeds() {
  breedsLoading.value = true
  try {
    await breedsStore.fetchBreeds(0, 100)
    breedList.value = breedsStore.breeds
  } catch (e) {
    console.error('Failed to load breeds', e)
  } finally {
    breedsLoading.value = false
  }
}

async function loadBreedTemplates(breedId: string) {
  try {
    const templates = await breedsStore.fetchBreedTemplates(breedId)
    selectedBreedTemplates.value = templates
  } catch (e) {
    console.error('Failed to load breed templates', e)
    selectedBreedTemplates.value = null
  }
}

function selectBreed(breed: BreedSummary) {
  // If editing and breed changed, show confirmation
  if (props.initialData && selectedBreedId.value && selectedBreedId.value !== breed.id) {
    previousBreedId.value = selectedBreedId.value
    pendingBreed.value = breed
    showBreedChangeConfirm.value = true
    showBreedPicker.value = false
    return
  }
  
  selectedBreedId.value = breed.id
  breedSearch.value = getBreedName(breed)
  showBreedPicker.value = false
  loadBreedTemplates(breed.id)
}

function confirmBreedChange() {
  if (pendingBreed.value) {
    selectedBreedId.value = pendingBreed.value.id
    breedSearch.value = getBreedName(pendingBreed.value)
    loadBreedTemplates(pendingBreed.value.id)
  }
  showBreedChangeConfirm.value = false
  pendingBreed.value = null
}

function clearBreed() {
  selectedBreedId.value = null
  selectedBreedTemplates.value = null
  breedSearch.value = ''
}

const form = reactive<any>({
  name: props.initialData?.name || '',
  batchCode: props.initialData?.batchCode || '',
  nameAr: props.initialData?.nameAr || '',
  nameEn: props.initialData?.nameEn || '',
  breedId: props.initialData?.breedId || null,
  birdCount: props.initialData?.birdCount || 0,
  purpose: props.initialData?.purpose || 'LAYERS',
  startDate: props.initialData?.startDate || new Date().toISOString().split('T')[0],
  housingLocationId: props.initialData?.housingLocationId,
  supplierId: props.initialData?.supplierId,
  source: props.initialData?.source || '',
  notes: props.initialData?.notes || '',
  status: props.initialData?.status
})

// Load existing breed templates if editing
watch(() => props.initialData, (newVal) => {
  if (newVal?.breedId) {
    selectedBreedId.value = newVal.breedId
    loadBreedTemplates(newVal.breedId)
  }
}, { immediate: true })

async function handleSubmit() {
  if (!form.name || !form.birdCount || form.birdCount <= 0 || !form.purpose) {
    error.value = t('common_error_required_fields')
    return
  }
  if (!props.initialData && !form.batchCode) {
    error.value = t('common_error_required_fields')
    return
  }
  
  error.value = ''
  
  const submitData: any = { ...form }
  submitData.breedId = selectedBreedId.value
  
  emit('submit', submitData)
}

// Close picker when clicking outside
function handleClickOutside(event: MouseEvent) {
  const target = event.target as HTMLElement
  if (!target.closest('.relative')) {
    showBreedPicker.value = false
  }
}

onMounted(() => {
  if (!poultryStore.suppliers.length) {
    poultryStore.fetchSuppliers()
  }
  if (!poultryStore.housingLocations.length) {
    poultryStore.fetchHousingLocations()
  }
  loadBreeds()

  document.addEventListener('click', handleClickOutside)
})
</script>
