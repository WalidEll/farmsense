<template>
  <!-- Backdrop -->
  <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-lg max-h-[90vh] overflow-y-auto">
      <div class="flex items-center justify-between p-6 border-b">
        <h3 class="text-lg font-bold text-gray-800">
          {{ plant ? t('plants.edit') : t('plants.add') }}
        </h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-2xl leading-none">×</button>
      </div>

      <form @submit.prevent="submit" class="p-6 space-y-4">
        <!-- Name -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('plants.name') }} *</label>
          <input v-model="form.name" required class="input" :placeholder="t('plants.namePlaceholder')" />
        </div>

        <!-- Species -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('plants.species') }}</label>
          <input v-model="form.species" class="input" :placeholder="t('plants.speciesPlaceholder')" />
        </div>

        <!-- Location -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('plants.location') }}</label>
          <select v-model="form.location" class="input">
            <option value="">{{ t('common.select') }}</option>
            <option value="INDOOR">{{ t('plants.indoor') }}</option>
            <option value="OUTDOOR">{{ t('plants.outdoor') }}</option>
            <option value="BALCONY">{{ t('plants.balcony') }}</option>
          </select>
        </div>

        <!-- Thresholds -->
        <div class="border-t pt-4">
          <p class="text-sm font-medium text-gray-700 mb-3">{{ t('plants.thresholds') }}</p>
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-xs text-gray-500 mb-1">💧 {{ t('plants.soilMin') }} (%)</label>
              <input v-model.number="form.soilMin" type="number" min="0" max="100" class="input text-sm" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">💧 {{ t('plants.soilMax') }} (%)</label>
              <input v-model.number="form.soilMax" type="number" min="0" max="100" class="input text-sm" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">🌡️ {{ t('plants.tempMin') }} (°C)</label>
              <input v-model.number="form.tempMin" type="number" step="0.5" class="input text-sm" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">🌡️ {{ t('plants.tempMax') }} (°C)</label>
              <input v-model.number="form.tempMax" type="number" step="0.5" class="input text-sm" />
            </div>
            <div class="col-span-2">
              <label class="block text-xs text-gray-500 mb-1">☀️ {{ t('plants.lightMin') }} (lux)</label>
              <input v-model.number="form.lightMin" type="number" min="0" class="input text-sm" />
            </div>
          </div>
        </div>

        <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>

        <div class="flex gap-3 pt-2">
          <button type="button" @click="$emit('close')" class="flex-1 border border-gray-300 text-gray-700 rounded-lg py-2.5 hover:bg-gray-50">
            {{ t('common.cancel') }}
          </button>
          <button type="submit" :disabled="loading" class="flex-1 bg-green-700 hover:bg-green-800 text-white rounded-lg py-2.5 font-medium disabled:opacity-50">
            {{ loading ? '...' : t('common.save') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { usePlantStore } from '@/stores/plants.store'
import { useI18n } from '@/i18n'
import type { Plant } from '@/types'

const props = defineProps<{ plant?: Plant }>()
const emit = defineEmits<{ close: []; saved: [] }>()

const { t } = useI18n()
const plantsStore = usePlantStore()

const form = reactive({
  name: props.plant?.name ?? '',
  species: props.plant?.species ?? '',
  location: props.plant?.location ?? '',
  soilMin: props.plant?.soilMin ?? 30,
  soilMax: props.plant?.soilMax ?? 80,
  tempMin: props.plant?.tempMin ?? 15,
  tempMax: props.plant?.tempMax ?? 30,
  lightMin: props.plant?.lightMin ?? 500,
})

const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    if (props.plant) {
      await plantsStore.update(props.plant.id, form)
    } else {
      await plantsStore.create(form)
    }
    emit('saved')
  } catch (e: any) {
    error.value = e?.response?.data?.error ?? t('common.error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@reference "tailwindcss";

.input {
  @apply w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500 text-gray-800;
}
</style>
