<template>
  <!-- Backdrop -->
  <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md">
      <div class="flex items-center justify-between p-6 border-b">
        <div>
          <h3 class="text-lg font-bold text-gray-800">{{ t('manual_title') }}</h3>
          <p class="text-xs text-gray-400 mt-1">{{ t('manual_subtitle') }}</p>
        </div>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-2xl leading-none">&times;</button>
      </div>

      <form @submit.prevent="submit" class="p-6 space-y-4">
        <!-- Soil Moisture -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            <span class="mr-1">💧</span>{{ t('manual_soil') }}
            <span class="text-gray-400 text-xs ml-1">({{ t('common_optional') }})</span>
          </label>
          <div class="flex items-center gap-2">
            <input
              v-model.number="form.soilMoisture"
              type="number"
              min="0"
              max="100"
              step="1"
              class="input"
              placeholder="0–100"
            />
            <span class="text-gray-400 text-sm">%</span>
          </div>
        </div>

        <!-- Temperature -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            <span class="mr-1">🌡️</span>{{ t('manual_temp') }}
            <span class="text-gray-400 text-xs ml-1">({{ t('common_optional') }})</span>
          </label>
          <div class="flex items-center gap-2">
            <input
              v-model.number="form.temperature"
              type="number"
              min="-40"
              max="80"
              step="0.1"
              class="input"
              placeholder="-40–80"
            />
            <span class="text-gray-400 text-sm">°C</span>
          </div>
        </div>

        <!-- Humidity -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            <span class="mr-1">💦</span>{{ t('manual_humidity') }}
            <span class="text-gray-400 text-xs ml-1">({{ t('common_optional') }})</span>
          </label>
          <div class="flex items-center gap-2">
            <input
              v-model.number="form.humidity"
              type="number"
              min="0"
              max="100"
              step="1"
              class="input"
              placeholder="0–100"
            />
            <span class="text-gray-400 text-sm">%</span>
          </div>
        </div>

        <!-- Light -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            <span class="mr-1">☀️</span>{{ t('manual_light') }}
            <span class="text-gray-400 text-xs ml-1">({{ t('common_optional') }})</span>
          </label>
          <div class="flex items-center gap-2">
            <input
              v-model.number="form.lightLux"
              type="number"
              min="0"
              max="200000"
              step="1"
              class="input"
              placeholder="0–200000"
            />
            <span class="text-gray-400 text-sm">lux</span>
          </div>
        </div>

        <!-- Success message -->
        <div v-if="success" class="flex items-center gap-2 text-green-600 text-sm bg-green-50 rounded-lg px-3 py-2">
          <span>✓</span> {{ t('manual_success') }}
        </div>

        <!-- Error message -->
        <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>

        <!-- Buttons -->
        <div class="flex gap-3 pt-2">
          <button
            type="button"
            @click="$emit('close')"
            class="flex-1 border border-gray-300 text-gray-700 rounded-lg py-2.5 hover:bg-gray-50"
          >
            {{ t('common_cancel') }}
          </button>
          <button
            type="submit"
            :disabled="loading || !hasAnyValue"
            class="flex-1 bg-green-700 hover:bg-green-800 text-white rounded-lg py-2.5 font-medium disabled:opacity-50 transition-colors"
          >
            {{ loading ? '...' : t('manual_submit') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useReadingsStore } from '@/stores/readings.store'
import { useI18n } from '@/i18n'

const props = defineProps<{ plantId: string }>()
const emit = defineEmits<{ close: []; saved: [] }>()

const { t } = useI18n()
const readingsStore = useReadingsStore()

const form = reactive({
  soilMoisture: undefined as number | undefined,
  temperature: undefined as number | undefined,
  humidity: undefined as number | undefined,
  lightLux: undefined as number | undefined,
})

const error = ref('')
const loading = ref(false)
const success = ref(false)

const hasAnyValue = computed(() =>
  form.soilMoisture != null ||
  form.temperature != null ||
  form.humidity != null ||
  form.lightLux != null
)

async function submit() {
  if (!hasAnyValue.value) return
  error.value = ''
  loading.value = true
  success.value = false

  try {
    const req: Record<string, unknown> = { plantId: props.plantId }
    if (form.soilMoisture != null) req.soilMoisture = form.soilMoisture
    if (form.temperature != null) req.temperature = form.temperature
    if (form.humidity != null) req.humidity = form.humidity
    if (form.lightLux != null) req.lightLux = form.lightLux

    await readingsStore.addManual(req as any)
    success.value = true
    setTimeout(() => emit('saved'), 600)
  } catch (e: any) {
    error.value = e?.response?.data?.error ?? t('manual_error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.input {
  @apply w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500 text-gray-800;
}
</style>
