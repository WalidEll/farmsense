<template>
  <div class="p-4 lg:p-6 space-y-6">
    <!-- Back button -->
    <RouterLink to="/crops" class="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-700 transition-colors">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      {{ t('common_back') }}
    </RouterLink>

    <!-- Loading -->
    <div v-if="store.loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
    </div>

    <template v-else-if="crop">
      <!-- Header -->
      <div class="space-y-2">
        <div class="flex flex-wrap items-center gap-3">
          <h1 class="text-2xl font-bold text-gray-900">
            {{ localized(crop.name, crop.nameAr, crop.nameEn) }}
          </h1>
          <span :class="['px-2.5 py-0.5 rounded-full text-xs font-medium', categoryBadge]">
            {{ t(`crops_${crop.category.toLowerCase()}`) }}
          </span>
          <span
            v-if="crop.difficulty"
            :class="['px-2.5 py-0.5 rounded-full text-xs font-medium', difficultyBadge]"
          >
            {{ t(`crop_difficulty_${crop.difficulty.toLowerCase()}`) }}
          </span>
        </div>
        <p v-if="crop.scientificName" class="text-sm italic text-gray-400">
          {{ crop.scientificName }}
        </p>
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

      <!-- Overview Tab -->
      <div v-if="activeTab === 'overview'" class="space-y-6">
        <!-- Image -->
        <img
          v-if="crop.imageUrl"
          :src="crop.imageUrl"
          :alt="crop.name"
          class="w-full max-w-md rounded-xl object-cover h-48"
        />

        <!-- Description -->
        <div v-if="localizedDesc" class="prose prose-sm max-w-none">
          <p class="text-gray-700">{{ localizedDesc }}</p>
        </div>

        <!-- Info grid -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div v-if="crop.growingSeason" class="flex items-center gap-3 p-3 bg-green-50 rounded-lg">
            <svg class="w-5 h-5 text-green-600 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <div>
              <p class="text-xs text-green-600 font-medium">{{ t('crops_season') }}</p>
              <p class="text-sm text-gray-900">{{ crop.growingSeason }}</p>
            </div>
          </div>
          <div v-if="crop.daysToHarvest" class="flex items-center gap-3 p-3 bg-green-50 rounded-lg">
            <svg class="w-5 h-5 text-green-600 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <div>
              <p class="text-xs text-green-600 font-medium">{{ t('crops_daysToHarvest') }}</p>
              <p class="text-sm text-gray-900">{{ crop.daysToHarvest }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Requirements Tab -->
      <div v-if="activeTab === 'requirements'" class="space-y-4">
        <div v-if="crop.requirements" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- Soil Moisture -->
          <div class="p-4 bg-white border border-gray-200 rounded-xl space-y-1">
            <div class="flex items-center gap-2 text-green-700">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z" />
              </svg>
              <span class="text-sm font-medium">{{ t('crop_soil') }}</span>
            </div>
            <p class="text-gray-900 text-sm">
              {{ crop.requirements.soilMoistureMin ?? '-' }}% - {{ crop.requirements.soilMoistureMax ?? '-' }}%
            </p>
          </div>

          <!-- Temperature -->
          <div class="p-4 bg-white border border-gray-200 rounded-xl space-y-1">
            <div class="flex items-center gap-2 text-orange-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707" />
              </svg>
              <span class="text-sm font-medium">{{ t('crop_temp') }}</span>
            </div>
            <p class="text-gray-900 text-sm">
              {{ crop.requirements.tempMin ?? '-' }} - {{ crop.requirements.tempMax ?? '-' }} C
            </p>
          </div>

          <!-- Light -->
          <div class="p-4 bg-white border border-gray-200 rounded-xl space-y-1">
            <div class="flex items-center gap-2 text-yellow-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
              <span class="text-sm font-medium">{{ t('crop_light') }}</span>
            </div>
            <p class="text-gray-900 text-sm">
              {{ crop.requirements.lightMin ?? '-' }} - {{ crop.requirements.lightMax ?? '-' }} lux
            </p>
          </div>

          <!-- Humidity -->
          <div class="p-4 bg-white border border-gray-200 rounded-xl space-y-1">
            <div class="flex items-center gap-2 text-blue-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z" />
              </svg>
              <span class="text-sm font-medium">{{ t('crop_humidity') }}</span>
            </div>
            <p class="text-gray-900 text-sm">
              {{ crop.requirements.humidityMin ?? '-' }}% - {{ crop.requirements.humidityMax ?? '-' }}%
            </p>
          </div>

          <!-- pH -->
          <div class="p-4 bg-white border border-gray-200 rounded-xl space-y-1">
            <div class="flex items-center gap-2 text-purple-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
              <span class="text-sm font-medium">{{ t('crop_ph') }}</span>
            </div>
            <p class="text-gray-900 text-sm">
              {{ crop.requirements.phMin ?? '-' }} - {{ crop.requirements.phMax ?? '-' }}
            </p>
          </div>

          <!-- Soil Type -->
          <div v-if="crop.requirements.soilType" class="p-4 bg-white border border-gray-200 rounded-xl space-y-1">
            <div class="flex items-center gap-2 text-amber-700">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6z" />
              </svg>
              <span class="text-sm font-medium">{{ t('crop_soilType') }}</span>
            </div>
            <p class="text-gray-900 text-sm">{{ crop.requirements.soilType }}</p>
          </div>

          <!-- Water Frequency -->
          <div v-if="crop.requirements.waterFrequency" class="p-4 bg-white border border-gray-200 rounded-xl space-y-1">
            <div class="flex items-center gap-2 text-cyan-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
              <span class="text-sm font-medium">{{ t('crop_waterFreq') }}</span>
            </div>
            <p class="text-gray-900 text-sm">{{ crop.requirements.waterFrequency }}</p>
          </div>
        </div>

        <div v-else class="text-center py-8 text-gray-400 text-sm">
          {{ t('crops_empty') }}
        </div>
      </div>

      <!-- Growth Stages Tab -->
      <div v-if="activeTab === 'stages'" class="space-y-1">
        <div v-if="crop.stages.length" class="relative">
          <div
            v-for="(stage, idx) in sortedStages"
            :key="stage.id"
            class="flex gap-4 pb-6 last:pb-0"
          >
            <!-- Timeline line + dot -->
            <div class="flex flex-col items-center">
              <div class="w-8 h-8 rounded-full bg-green-600 text-white flex items-center justify-center text-sm font-bold shrink-0">
                {{ stage.stageOrder }}
              </div>
              <div v-if="idx < sortedStages.length - 1" class="w-0.5 flex-1 bg-green-300 mt-1"></div>
            </div>

            <!-- Content -->
            <div class="pb-2 pt-1">
              <h3 class="font-semibold text-gray-900 text-sm">
                {{ localized(stage.name, stage.nameAr, stage.nameEn) }}
              </h3>
              <p v-if="stage.durationDays" class="text-xs text-green-600 mt-0.5">
                {{ t('crop_stage_duration', { n: stage.durationDays }) }}
              </p>
              <p
                v-if="localized(stage.description, stage.descriptionAr, stage.descriptionEn)"
                class="text-sm text-gray-600 mt-1"
              >
                {{ localized(stage.description, stage.descriptionAr, stage.descriptionEn) }}
              </p>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-8 text-gray-400 text-sm">
          {{ t('crops_empty') }}
        </div>
      </div>

      <!-- Nutrients Tab -->
      <div v-if="activeTab === 'nutrients'" class="space-y-4">
        <div v-if="crop.nutrients" class="space-y-4">
          <!-- NPK cards -->
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div class="p-4 bg-white border border-gray-200 rounded-xl text-center">
              <p class="text-sm font-medium text-gray-700">{{ t('crop_nitrogen') }}</p>
              <span :class="['inline-block mt-2 px-3 py-1 rounded-full text-sm font-medium', nutrientBadge(crop.nutrients.nitrogenNeed)]">
                {{ crop.nutrients.nitrogenNeed || '-' }}
              </span>
            </div>
            <div class="p-4 bg-white border border-gray-200 rounded-xl text-center">
              <p class="text-sm font-medium text-gray-700">{{ t('crop_phosphorus') }}</p>
              <span :class="['inline-block mt-2 px-3 py-1 rounded-full text-sm font-medium', nutrientBadge(crop.nutrients.phosphorusNeed)]">
                {{ crop.nutrients.phosphorusNeed || '-' }}
              </span>
            </div>
            <div class="p-4 bg-white border border-gray-200 rounded-xl text-center">
              <p class="text-sm font-medium text-gray-700">{{ t('crop_potassium') }}</p>
              <span :class="['inline-block mt-2 px-3 py-1 rounded-full text-sm font-medium', nutrientBadge(crop.nutrients.potassiumNeed)]">
                {{ crop.nutrients.potassiumNeed || '-' }}
              </span>
            </div>
          </div>

          <!-- Fertilizer info -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div v-if="localizedFertilizer" class="p-4 bg-green-50 rounded-xl">
              <p class="text-xs text-green-600 font-medium">{{ t('crop_fertilizer') }}</p>
              <p class="text-sm text-gray-900 mt-1">{{ localizedFertilizer }}</p>
            </div>
            <div v-if="crop.nutrients.applicationFrequency" class="p-4 bg-green-50 rounded-xl">
              <p class="text-xs text-green-600 font-medium">{{ t('crop_appFrequency') }}</p>
              <p class="text-sm text-gray-900 mt-1">{{ crop.nutrients.applicationFrequency }}</p>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-8 text-gray-400 text-sm">
          {{ t('crops_empty') }}
        </div>
      </div>

      <!-- Issues Tab -->
      <div v-if="activeTab === 'issues'" class="space-y-6">
        <template v-if="crop.issues.length">
          <!-- Pests section -->
          <div v-if="pestIssues.length">
            <h3 class="text-sm font-semibold text-red-700 mb-3 flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-red-500"></span>
              {{ t('crop_issue_pest') }}
            </h3>
            <div class="space-y-2">
              <IssueCard v-for="issue in pestIssues" :key="issue.id" :issue="issue" />
            </div>
          </div>

          <!-- Diseases section -->
          <div v-if="diseaseIssues.length">
            <h3 class="text-sm font-semibold text-orange-700 mb-3 flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-orange-500"></span>
              {{ t('crop_issue_disease') }}
            </h3>
            <div class="space-y-2">
              <IssueCard v-for="issue in diseaseIssues" :key="issue.id" :issue="issue" />
            </div>
          </div>

          <!-- Deficiencies section -->
          <div v-if="deficiencyIssues.length">
            <h3 class="text-sm font-semibold text-yellow-700 mb-3 flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-yellow-500"></span>
              {{ t('crop_issue_deficiency') }}
            </h3>
            <div class="space-y-2">
              <IssueCard v-for="issue in deficiencyIssues" :key="issue.id" :issue="issue" />
            </div>
          </div>
        </template>

        <div v-else class="text-center py-8 text-gray-400 text-sm">
          {{ t('crops_empty') }}
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, defineComponent, h } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from '@/i18n'
import { useAuthStore } from '@/stores/auth.store'
import { useCropsStore } from '@/stores/crops.store'
import type { CropIssue, NutrientLevel } from '@/types'

const { t } = useI18n()
const route = useRoute()
const auth = useAuthStore()
const store = useCropsStore()

const activeTab = ref<'overview' | 'requirements' | 'stages' | 'nutrients' | 'issues'>('overview')

const tabs = [
  { key: 'overview' as const, label: 'crop_overview' },
  { key: 'requirements' as const, label: 'crop_requirements' },
  { key: 'stages' as const, label: 'crop_stages' },
  { key: 'nutrients' as const, label: 'crop_nutrients' },
  { key: 'issues' as const, label: 'crop_issues' },
]

const crop = computed(() => store.currentCrop)

function localized(base?: string, ar?: string, en?: string) {
  if (auth.user?.lang === 'AR' && ar) return ar
  if (auth.user?.lang === 'EN' && en) return en
  return base || '---'
}

const localizedDesc = computed(() => {
  if (!crop.value) return ''
  return localized(crop.value.description, crop.value.descriptionAr, crop.value.descriptionEn)
})

const localizedFertilizer = computed(() => {
  if (!crop.value?.nutrients) return ''
  return localized(
    crop.value.nutrients.fertilizerType,
    crop.value.nutrients.fertilizerTypeAr,
    crop.value.nutrients.fertilizerTypeEn,
  )
})

const sortedStages = computed(() => {
  if (!crop.value) return []
  return [...crop.value.stages].sort((a, b) => a.stageOrder - b.stageOrder)
})

const pestIssues = computed(() => crop.value?.issues.filter(i => i.issueType === 'PEST') || [])
const diseaseIssues = computed(() => crop.value?.issues.filter(i => i.issueType === 'DISEASE') || [])
const deficiencyIssues = computed(() => crop.value?.issues.filter(i => i.issueType === 'DEFICIENCY') || [])

const categoryBadgeMap: Record<string, string> = {
  VEGETABLE: 'bg-green-100 text-green-700',
  FRUIT: 'bg-orange-100 text-orange-700',
  HERB: 'bg-purple-100 text-purple-700',
  GRAIN: 'bg-amber-100 text-amber-700',
  LEGUME: 'bg-teal-100 text-teal-700',
  OTHER: 'bg-gray-100 text-gray-700',
}

const categoryBadge = computed(() => categoryBadgeMap[crop.value?.category || ''] || 'bg-gray-100 text-gray-700')

const difficultyBadge = computed(() => {
  switch (crop.value?.difficulty) {
    case 'EASY': return 'bg-green-100 text-green-700'
    case 'MEDIUM': return 'bg-yellow-100 text-yellow-700'
    case 'HARD': return 'bg-red-100 text-red-700'
    default: return 'bg-gray-100 text-gray-700'
  }
})

function nutrientBadge(level?: NutrientLevel) {
  switch (level) {
    case 'LOW': return 'bg-green-100 text-green-700'
    case 'MEDIUM': return 'bg-yellow-100 text-yellow-700'
    case 'HIGH': return 'bg-red-100 text-red-700'
    default: return 'bg-gray-100 text-gray-500'
  }
}

// Inline IssueCard component
const IssueCard = defineComponent({
  props: {
    issue: { type: Object as () => CropIssue, required: true },
  },
  setup(props) {
    const expanded = ref(false)

    return () => h('div', {
      class: 'border border-gray-200 rounded-lg overflow-hidden',
    }, [
      // Header (click to toggle)
      h('button', {
        class: 'w-full flex items-center justify-between px-4 py-3 text-start hover:bg-gray-50 transition-colors',
        onClick: () => { expanded.value = !expanded.value },
      }, [
        h('span', { class: 'font-medium text-sm text-gray-900' },
          localized(props.issue.name, props.issue.nameAr, props.issue.nameEn)
        ),
        h('svg', {
          class: ['w-4 h-4 text-gray-400 transition-transform', expanded.value ? 'rotate-180' : ''].filter(Boolean).join(' '),
          fill: 'none',
          stroke: 'currentColor',
          viewBox: '0 0 24 24',
        }, [
          h('path', {
            'stroke-linecap': 'round',
            'stroke-linejoin': 'round',
            'stroke-width': '2',
            d: 'M19 9l-7 7-7-7',
          }),
        ]),
      ]),
      // Expanded content
      expanded.value ? h('div', { class: 'px-4 pb-4 space-y-3 border-t border-gray-100 pt-3' }, [
        localizedSymptoms(props.issue)
          ? h('div', {}, [
              h('p', { class: 'text-xs font-medium text-gray-500' }, t('crop_symptoms')),
              h('p', { class: 'text-sm text-gray-700 mt-0.5' }, localizedSymptoms(props.issue)),
            ])
          : null,
        localizedTreatment(props.issue)
          ? h('div', {}, [
              h('p', { class: 'text-xs font-medium text-gray-500' }, t('crop_treatment')),
              h('p', { class: 'text-sm text-gray-700 mt-0.5' }, localizedTreatment(props.issue)),
            ])
          : null,
        props.issue.prevention
          ? h('div', {}, [
              h('p', { class: 'text-xs font-medium text-gray-500' }, t('crop_prevention')),
              h('p', { class: 'text-sm text-gray-700 mt-0.5' }, props.issue.prevention),
            ])
          : null,
      ].filter(Boolean)) : null,
    ])
  },
})

function localizedSymptoms(issue: CropIssue) {
  return localized(issue.symptoms, issue.symptomsAr, issue.symptomsEn)
}

function localizedTreatment(issue: CropIssue) {
  return localized(issue.treatment, issue.treatmentAr, issue.treatmentEn)
}

onMounted(() => {
  const id = route.params.id as string
  store.fetchById(id)
})
</script>
