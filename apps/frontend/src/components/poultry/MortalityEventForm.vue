<template>
  <div class="fixed inset-0 bg-black/50 z-[60] flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden flex flex-col max-h-[90vh]">
      <!-- Header -->
      <div class="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50/50">
        <h3 class="text-xl font-bold text-gray-900">{{ t('mortality_log_loss') }}</h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 transition-colors">
          <span class="text-2xl">×</span>
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-4 overflow-y-auto flex-1">
        <!-- Type -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('mortality_event_type') }} <span class="text-red-500">*</span>
          </label>
          <select
            v-model="form.type"
            required
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-orange-500 outline-none"
          >
            <option value="NATURAL_DEATH">{{ t('mortality_type_natural_death') }}</option>
            <option value="CULL">{{ t('mortality_type_cull') }}</option>
          </select>
        </div>

        <!-- Count -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('mortality_event_count') }} <span class="text-red-500">*</span>
          </label>
          <input
            v-model.number="form.count"
            type="number"
            min="1"
            :max="maxCount"
            required
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-orange-500 outline-none"
          />
          <p v-if="maxCount" class="text-xs text-gray-400 mt-1">{{ t('flock_current_birds') }}: {{ maxCount }}</p>
        </div>

        <!-- Date -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('mortality_event_date') }} <span class="text-red-500">*</span>
          </label>
          <input
            v-model="form.mortalityDate"
            type="date"
            required
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-orange-500 outline-none"
          />
        </div>

        <!-- Cause -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">{{ t('mortality_event_cause') }}</label>
          <select
            v-model="form.cause"
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-orange-500 outline-none"
          >
            <option value="">—</option>
            <option value="DISEASE">{{ t('mortality_cause_disease') }}</option>
            <option value="INJURY">{{ t('mortality_cause_injury') }}</option>
            <option value="PREDATOR">{{ t('mortality_cause_predator') }}</option>
            <option value="LOW_WEIGHT">{{ t('mortality_cause_low_weight') }}</option>
            <option value="HEAT_STRESS">{{ t('mortality_cause_heat_stress') }}</option>
            <option value="UNKNOWN">{{ t('mortality_cause_unknown') }}</option>
            <option value="OTHER">{{ t('mortality_cause_other') }}</option>
          </select>
        </div>

        <!-- Notes -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">{{ t('plan_description') }}</label>
          <textarea
            v-model="form.notes"
            rows="2"
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-orange-500 outline-none resize-none"
          />
        </div>

        <!-- Error -->
        <p v-if="error" class="text-sm text-red-600 bg-red-50 rounded-xl px-4 py-2">{{ error }}</p>

        <!-- Actions -->
        <div class="flex gap-3 pt-2">
          <button
            type="button"
            @click="$emit('close')"
            class="flex-1 border border-gray-200 rounded-xl px-4 py-2.5 font-bold text-gray-600 hover:bg-gray-50 transition-colors"
          >
            {{ t('common.cancel') }}
          </button>
          <button
            type="submit"
            :disabled="loading"
            class="flex-1 bg-orange-600 text-white rounded-xl px-4 py-2.5 font-bold hover:bg-orange-700 transition-colors disabled:opacity-50"
          >
            {{ loading ? '...' : t('mortality_log_loss') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from '@/i18n'
import type { CreateMortalityEventRequest, MortalityType, MortalityCause } from '@/types'

const props = defineProps<{
  flockId: string
  maxCount?: number
  loading?: boolean
}>()
const emit = defineEmits<{
  close: []
  submit: [data: CreateMortalityEventRequest]
}>()

const { t } = useI18n()

const error = ref('')

const today = new Date().toISOString().slice(0, 10)
const form = ref({
  type: 'NATURAL_DEATH' as MortalityType,
  count: 1,
  mortalityDate: today,
  cause: '' as MortalityCause | '',
  notes: '',
})

function handleSubmit() {
  error.value = ''
  if (props.maxCount !== undefined && form.value.count > props.maxCount) {
    error.value = t('mortality_max_count_error', { max: props.maxCount })
    return
  }
  const payload: CreateMortalityEventRequest = {
    type: form.value.type,
    count: form.value.count,
    mortalityDate: form.value.mortalityDate,
    cause: form.value.cause || undefined,
    notes: form.value.notes || undefined,
  }
  emit('submit', payload)
}
</script>
