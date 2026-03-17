<template>
  <div class="fixed inset-0 bg-black/50 z-[60] flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden flex flex-col max-h-[90vh]">
      <!-- Header -->
      <div class="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50/50">
        <h3 class="text-xl font-bold text-gray-900">
          {{ initialData ? t('common.edit') : t('housing_location_create') }}
        </h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 transition-colors">
          <span class="text-2xl">×</span>
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-4 overflow-y-auto flex-1">
        <!-- Name -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('housing_location_name') }} <span class="text-red-500">*</span>
          </label>
          <input
            v-model="form.name"
            type="text"
            required
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-orange-500 outline-none"
          />
        </div>

        <!-- Type -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('housing_location_type') }} <span class="text-red-500">*</span>
          </label>
          <select
            v-model="form.locationType"
            required
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-orange-500 outline-none"
          >
            <option value="COOP">{{ t('housing_location_type_coop') }}</option>
            <option value="PEN">{{ t('housing_location_type_pen') }}</option>
            <option value="FREE_RANGE">{{ t('housing_location_type_free_range') }}</option>
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
            {{ loading ? '...' : t('common.save') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useI18n } from '@/i18n'
import type { HousingLocation, HousingLocationType, CreateHousingLocationRequest } from '@/types'

const props = defineProps<{
  initialData?: HousingLocation
  loading?: boolean
}>()
const emit = defineEmits<{
  close: []
  submit: [data: CreateHousingLocationRequest]
}>()

const { t } = useI18n()

const form = ref({
  name: props.initialData?.name ?? '',
  locationType: (props.initialData?.locationType ?? 'COOP') as HousingLocationType,
  notes: props.initialData?.notes ?? '',
})

watch(() => props.initialData, (val) => {
  if (val) {
    form.value = { name: val.name, locationType: val.locationType, notes: val.notes ?? '' }
  }
})

function handleSubmit() {
  emit('submit', {
    name: form.value.name,
    locationType: form.value.locationType,
    notes: form.value.notes || undefined,
  })
}
</script>
