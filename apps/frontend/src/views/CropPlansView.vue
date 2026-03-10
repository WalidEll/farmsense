<template>
  <div class="p-4 lg:p-6 space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-900">{{ t('plans_title') }}</h1>
      <button
        @click="showCreateModal = true"
        class="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors text-sm font-medium"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        {{ t('plans_create') }}
      </button>
    </div>

    <!-- Season filter pills -->
    <div class="flex gap-2 overflow-x-auto pb-2">
      <button
        v-for="s in seasonFilters"
        :key="s.value ?? 'all'"
        @click="activeSeason = s.value"
        :class="[
          activeSeason === s.value
            ? 'bg-green-600 text-white'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200',
          'px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition-colors',
        ]"
      >
        {{ t(s.label) }}
      </button>
    </div>

    <!-- Status filter pills -->
    <div class="flex gap-2 overflow-x-auto pb-2">
      <button
        v-for="st in statusFilters"
        :key="st.value ?? 'all'"
        @click="activeStatus = st.value"
        :class="[
          activeStatus === st.value
            ? 'bg-green-600 text-white'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200',
          'px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition-colors',
        ]"
      >
        {{ t(st.label) }}
      </button>
    </div>

    <!-- Loading -->
    <div v-if="store.loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
    </div>

    <!-- Grid -->
    <div v-else-if="filteredPlans.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      <PlanCard v-for="plan in filteredPlans" :key="plan.id" :plan="plan" />
    </div>

    <!-- Empty -->
    <div v-else class="text-center py-12 text-gray-500">
      <svg class="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
      </svg>
      <p>{{ t('plans_empty') }}</p>
    </div>

    <!-- Create Plan Modal -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40" @click.self="closeModal">
      <div class="bg-white rounded-xl shadow-xl w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto">
        <div class="p-6 space-y-4">
          <h2 class="text-lg font-bold text-gray-900">
            {{ editingPlan ? t('plans_edit') : t('plans_create') }}
          </h2>

          <!-- Name -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('plan_name') }} *</label>
            <input
              v-model="form.name"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Name AR -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('plan_name') }} (AR) <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model="form.nameAr"
              dir="rtl"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Name EN -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('plan_name') }} (EN) <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model="form.nameEn"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Description -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('plan_description') }} <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <textarea
              v-model="form.description"
              rows="2"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            ></textarea>
          </div>

          <!-- Season -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('plan_season') }} *</label>
            <select
              v-model="form.season"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            >
              <option value="SPRING">{{ t('plan_season_spring') }}</option>
              <option value="SUMMER">{{ t('plan_season_summer') }}</option>
              <option value="AUTUMN">{{ t('plan_season_autumn') }}</option>
              <option value="WINTER">{{ t('plan_season_winter') }}</option>
              <option value="YEAR_ROUND">{{ t('plan_season_year_round') }}</option>
            </select>
          </div>

          <!-- Year -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('plan_year') }} *</label>
            <input
              v-model.number="form.year"
              type="number"
              min="2020"
              max="2050"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Error -->
          <p v-if="error" class="text-sm text-red-600">{{ error }}</p>

          <!-- Buttons -->
          <div class="flex justify-end gap-3 pt-2">
            <button
              @click="closeModal"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
            >
              {{ t('cancel') }}
            </button>
            <button
              @click="submitPlan"
              :disabled="saving"
              class="px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50"
            >
              {{ saving ? t('loading') : t('save') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from '@/i18n'
import { useCropPlansStore } from '@/stores/cropPlans.store'
import PlanCard from '@/components/cropplan/PlanCard.vue'
import type { PlanSeason, PlanStatus, CropPlan } from '@/types'

const { t } = useI18n()
const store = useCropPlansStore()

const activeSeason = ref<PlanSeason | null>(null)
const activeStatus = ref<PlanStatus | null>(null)
const showCreateModal = ref(false)
const editingPlan = ref<CropPlan | null>(null)
const saving = ref(false)
const error = ref('')

const form = ref({
  name: '',
  nameAr: '',
  nameEn: '',
  description: '',
  season: 'SPRING' as PlanSeason,
  year: new Date().getFullYear(),
})

const seasonFilters: { value: PlanSeason | null; label: string }[] = [
  { value: null, label: 'crops_all' },
  { value: 'SPRING', label: 'plan_season_spring' },
  { value: 'SUMMER', label: 'plan_season_summer' },
  { value: 'AUTUMN', label: 'plan_season_autumn' },
  { value: 'WINTER', label: 'plan_season_winter' },
  { value: 'YEAR_ROUND', label: 'plan_season_year_round' },
]

const statusFilters: { value: PlanStatus | null; label: string }[] = [
  { value: null, label: 'crops_all' },
  { value: 'DRAFT', label: 'plan_status_draft' },
  { value: 'ACTIVE', label: 'plan_status_active' },
  { value: 'COMPLETED', label: 'plan_status_completed' },
  { value: 'ARCHIVED', label: 'plan_status_archived' },
]

const filteredPlans = computed(() => {
  let result = store.plans
  if (activeSeason.value) {
    result = result.filter(p => p.season === activeSeason.value)
  }
  if (activeStatus.value) {
    result = result.filter(p => p.status === activeStatus.value)
  }
  return result
})

function closeModal() {
  showCreateModal.value = false
  editingPlan.value = null
  error.value = ''
  form.value = {
    name: '',
    nameAr: '',
    nameEn: '',
    description: '',
    season: 'SPRING',
    year: new Date().getFullYear(),
  }
}

async function submitPlan() {
  if (!form.value.name.trim()) {
    error.value = t('error_generic')
    return
  }
  saving.value = true
  error.value = ''
  try {
    const req = {
      name: form.value.name,
      nameAr: form.value.nameAr || undefined,
      nameEn: form.value.nameEn || undefined,
      description: form.value.description || undefined,
      season: form.value.season,
      year: form.value.year,
    }
    if (editingPlan.value) {
      await store.updatePlan(editingPlan.value.id, req)
    } else {
      await store.createPlan(req)
    }
    closeModal()
  } catch {
    error.value = t('error_generic')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  store.fetchPlans()
})
</script>
