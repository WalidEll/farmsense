<template>
  <div class="p-4 lg:p-6 space-y-6">
    <!-- Back button -->
    <RouterLink to="/plans" class="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-700 transition-colors">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      {{ t('common_back') }}
    </RouterLink>

    <!-- Loading -->
    <div v-if="store.loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
    </div>

    <template v-else-if="plan">
      <!-- Header -->
      <div class="space-y-2">
        <div class="flex flex-wrap items-center gap-3">
          <h1 class="text-2xl font-bold text-gray-900">
            {{ localized(plan.name, plan.nameAr, plan.nameEn) }}
          </h1>
          <span :class="['px-2.5 py-0.5 rounded-full text-xs font-medium', seasonBadge]">
            {{ t(`plan_season_${plan.season.toLowerCase()}`) }}
          </span>
          <span :class="['px-2.5 py-0.5 rounded-full text-xs font-medium', statusBadge]">
            {{ t(`plan_status_${plan.status.toLowerCase()}`) }}
          </span>
          <span class="text-sm text-gray-500">{{ plan.year }}</span>
        </div>
        <p v-if="localizedDesc" class="text-sm text-gray-600">{{ localizedDesc }}</p>
      </div>

      <!-- Tabs -->
      <div class="border-b border-gray-200">
        <nav class="flex gap-6 -mb-px overflow-x-auto">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            @click="activeTab = tab.key"
            :class="[
              activeTab === tab.key
                ? 'border-b-2 border-green-600 text-green-700'
                : 'text-gray-500 hover:text-gray-700',
              'pb-3 text-sm font-medium whitespace-nowrap transition-colors',
            ]"
          >
            {{ t(tab.label) }}
          </button>
        </nav>
      </div>

      <!-- Plantings Tab -->
      <div v-if="activeTab === 'plantings'" class="space-y-4">
        <div class="flex items-center justify-between">
          <h2 class="text-lg font-semibold text-gray-900">{{ t('plantings_title') }}</h2>
          <button
            @click="showAddPlanting = true"
            class="flex items-center gap-2 px-3 py-1.5 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors text-sm font-medium"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            {{ t('plantings_add') }}
          </button>
        </div>

        <!-- Plantings list -->
        <div v-if="store.plantings.length" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <PlantingCard
            v-for="p in store.plantings"
            :key="p.id"
            :planting="p"
            @edit="openEditPlanting"
            @delete="handleDeletePlanting"
          />
        </div>
        <div v-else class="text-center py-8 text-gray-400 text-sm">
          <svg class="w-10 h-10 mx-auto mb-2 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
          {{ t('plantings_empty') }}
        </div>
      </div>

      <!-- Tasks Tab -->
      <div v-if="activeTab === 'tasks'" class="space-y-4">
        <h2 class="text-lg font-semibold text-gray-900">{{ t('tasks_title') }}</h2>

        <div v-if="store.tasks.length" class="space-y-2">
          <div
            v-for="task in store.tasks"
            :key="task.id"
            class="flex items-center gap-3 p-3 bg-white border border-gray-200 rounded-xl"
          >
            <!-- Checkbox -->
            <button
              @click="toggleTask(task)"
              :class="[
                'w-5 h-5 rounded border-2 flex items-center justify-center shrink-0 transition-colors',
                task.status === 'DONE'
                  ? 'bg-green-600 border-green-600'
                  : task.status === 'SKIPPED'
                    ? 'bg-gray-400 border-gray-400'
                    : 'border-gray-300 hover:border-green-500',
              ]"
            >
              <svg v-if="task.status === 'DONE'" class="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" />
              </svg>
              <svg v-if="task.status === 'SKIPPED'" class="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>

            <!-- Task info -->
            <div class="flex-1 min-w-0">
              <p :class="['text-sm font-medium', task.status === 'DONE' ? 'text-gray-400 line-through' : 'text-gray-900']">
                {{ localized(task.title, task.titleAr, task.titleEn) }}
              </p>
              <p class="text-xs text-gray-500 mt-0.5">
                {{ formatDate(task.dueDate) }}
              </p>
            </div>

            <!-- Skip button -->
            <button
              v-if="task.status === 'PENDING'"
              @click="skipTask(task)"
              class="text-xs text-gray-400 hover:text-amber-600 transition-colors"
            >
              {{ t('tasks_skip') }}
            </button>
          </div>
        </div>
        <div v-else class="text-center py-8 text-gray-400 text-sm">
          {{ t('plantings_empty') }}
        </div>
      </div>

      <!-- Timeline Tab (Placeholder) -->
      <div v-if="activeTab === 'timeline'" class="text-center py-12 text-gray-400">
        <svg class="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        <p>{{ t('common_coming_soon_title') }}</p>
      </div>
    </template>

    <!-- Add Planting Modal -->
    <div v-if="showAddPlanting" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40" @click.self="closePlantingModal">
      <div class="bg-white rounded-xl shadow-xl w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto">
        <div class="p-6 space-y-4">
          <h2 class="text-lg font-bold text-gray-900">{{ t('plantings_add') }}</h2>

          <!-- Crop selector -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('nav_crops') }} *</label>
            <select
              v-model="plantingForm.cropId"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            >
              <option value="">{{ t('common_select') }}</option>
              <option v-for="c in cropsStore.crops" :key="c.id" :value="c.id">
                {{ c.name }}
              </option>
            </select>
          </div>

          <!-- Location selector -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('nav_locations') }} <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <select
              v-model="plantingForm.farmLocationId"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            >
              <option value="">{{ t('common_select') }}</option>
              <option v-for="l in store.locations" :key="l.id" :value="l.id">
                {{ l.name }}
              </option>
            </select>
          </div>

          <!-- Quantity -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('planting_quantity') }} <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model.number="plantingForm.quantity"
              type="number"
              min="0"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Area -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('planting_area') }} (m2) <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model.number="plantingForm.areaM2"
              type="number"
              min="0"
              step="0.1"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Planned Sow Date -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('planting_sow_date') }} <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model="plantingForm.plannedSowDate"
              type="date"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Planned Transplant Date -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('planting_transplant_date') }} <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model="plantingForm.plannedTransplantDate"
              type="date"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Planned Harvest Date -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('planting_harvest_date') }} <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <input
              v-model="plantingForm.plannedHarvestDate"
              type="date"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>

          <!-- Notes -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Notes <span class="text-gray-400">{{ t('common_optional') }}</span></label>
            <textarea
              v-model="plantingForm.notes"
              rows="2"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
            ></textarea>
          </div>

          <!-- Error -->
          <p v-if="plantingError" class="text-sm text-red-600">{{ plantingError }}</p>

          <!-- Buttons -->
          <div class="flex justify-end gap-3 pt-2">
            <button
              @click="closePlantingModal"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
            >
              {{ t('cancel') }}
            </button>
            <button
              @click="submitPlanting"
              :disabled="plantingSaving"
              class="px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50"
            >
              {{ plantingSaving ? t('loading') : t('save') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from '@/i18n'
import { useAuthStore } from '@/stores/auth.store'
import { useCropPlansStore } from '@/stores/cropPlans.store'
import { useCropsStore } from '@/stores/crops.store'
import PlantingCard from '@/components/cropplan/PlantingCard.vue'
import type { PlantingItem, PlantingTask } from '@/types'

const props = defineProps<{ id: string }>()

const { t } = useI18n()
const route = useRoute()
const auth = useAuthStore()
const store = useCropPlansStore()
const cropsStore = useCropsStore()

const activeTab = ref<'plantings' | 'tasks' | 'timeline'>('plantings')
const showAddPlanting = ref(false)
const plantingSaving = ref(false)
const plantingError = ref('')

const tabs = [
  { key: 'plantings' as const, label: 'plantings_title' },
  { key: 'tasks' as const, label: 'tasks_title' },
  { key: 'timeline' as const, label: 'common_coming_soon_title' },
]

const plan = computed(() => store.currentPlan)

function localized(base?: string, ar?: string, en?: string) {
  if (auth.user?.lang === 'AR' && ar) return ar
  if (auth.user?.lang === 'EN' && en) return en
  return base || '---'
}

const localizedDesc = computed(() => {
  if (!plan.value) return ''
  return localized(plan.value.description, plan.value.descriptionAr, plan.value.descriptionEn)
})

const seasonBadgeMap: Record<string, string> = {
  SPRING: 'bg-green-100 text-green-700',
  SUMMER: 'bg-orange-100 text-orange-700',
  AUTUMN: 'bg-amber-100 text-amber-700',
  WINTER: 'bg-blue-100 text-blue-700',
  YEAR_ROUND: 'bg-purple-100 text-purple-700',
}

const statusBadgeMap: Record<string, string> = {
  DRAFT: 'bg-gray-100 text-gray-600',
  ACTIVE: 'bg-green-100 text-green-700',
  COMPLETED: 'bg-blue-100 text-blue-700',
  ARCHIVED: 'bg-amber-100 text-amber-700',
}

const seasonBadge = computed(() => seasonBadgeMap[plan.value?.season || ''] || 'bg-gray-100 text-gray-700')
const statusBadge = computed(() => statusBadgeMap[plan.value?.status || ''] || 'bg-gray-100 text-gray-600')

const plantingForm = ref({
  cropId: '',
  farmLocationId: '',
  quantity: undefined as number | undefined,
  areaM2: undefined as number | undefined,
  notes: '',
  plannedSowDate: '',
  plannedTransplantDate: '',
  plannedHarvestDate: '',
})

function closePlantingModal() {
  showAddPlanting.value = false
  plantingError.value = ''
  plantingForm.value = {
    cropId: '',
    farmLocationId: '',
    quantity: undefined,
    areaM2: undefined,
    notes: '',
    plannedSowDate: '',
    plannedTransplantDate: '',
    plannedHarvestDate: '',
  }
}

async function submitPlanting() {
  if (!plantingForm.value.cropId) {
    plantingError.value = t('error_generic')
    return
  }
  plantingSaving.value = true
  plantingError.value = ''
  try {
    const req = {
      cropId: plantingForm.value.cropId,
      farmLocationId: plantingForm.value.farmLocationId || undefined,
      quantity: plantingForm.value.quantity || undefined,
      areaM2: plantingForm.value.areaM2 || undefined,
      notes: plantingForm.value.notes || undefined,
      plannedSowDate: plantingForm.value.plannedSowDate || undefined,
      plannedTransplantDate: plantingForm.value.plannedTransplantDate || undefined,
      plannedHarvestDate: plantingForm.value.plannedHarvestDate || undefined,
    }
    await store.createPlanting(planId, req)
    closePlantingModal()
  } catch {
    plantingError.value = t('error_generic')
  } finally {
    plantingSaving.value = false
  }
}

function openEditPlanting(planting: PlantingItem) {
  // For now just open create modal pre-filled
  plantingForm.value = {
    cropId: planting.cropId,
    farmLocationId: planting.farmLocationId || '',
    quantity: planting.quantity,
    areaM2: planting.areaM2,
    notes: planting.notes || '',
    plannedSowDate: planting.plannedSowDate || '',
    plannedTransplantDate: planting.plannedTransplantDate || '',
    plannedHarvestDate: planting.plannedHarvestDate || '',
  }
  showAddPlanting.value = true
}

async function handleDeletePlanting(planting: PlantingItem) {
  try {
    await store.deletePlanting(planId, planting.id)
  } catch {
    // silent
  }
}

async function toggleTask(task: PlantingTask) {
  if (task.status === 'PENDING') {
    await store.updateTask(task.id, { status: 'DONE' })
  } else if (task.status === 'DONE') {
    await store.updateTask(task.id, { status: 'PENDING' })
  }
}

async function skipTask(task: PlantingTask) {
  await store.updateTask(task.id, { status: 'SKIPPED' })
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString()
}

const planId = props.id || (route.params.id as string)

onMounted(async () => {
  await Promise.all([
    store.fetchPlan(planId),
    store.fetchPlantings(planId),
    store.fetchTasks(planId),
    store.fetchLocations(),
    cropsStore.fetchAll(),
  ])
})
</script>
