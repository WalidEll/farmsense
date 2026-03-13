<template>
  <div class="fixed inset-0 bg-black/50 z-[60] flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-lg overflow-hidden flex flex-col max-h-[90vh]">
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
            </select>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">
              {{ t('flock_breed') }}
            </label>
            <input
              v-model="form.breed"
              type="text"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
              placeholder="ex. Sasso, Isa Brown"
            />
          </div>
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
        </div>

        <div class="grid grid-cols-2 gap-4">
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
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">
              {{ t('flock_source') }}
            </label>
            <input
              v-model="form.source"
              type="text"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
              :placeholder="t('flock_source')"
            />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from '@/i18n'
import { usePoultryStore } from '@/stores/poultry.store'
import type { Flock, CreateFlockRequest, UpdateFlockRequest } from '@/types'

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
const error = ref('')

const form = reactive<any>({
  name: props.initialData?.name || '',
  nameAr: props.initialData?.nameAr || '',
  nameEn: props.initialData?.nameEn || '',
  breed: props.initialData?.breed || '',
  birdCount: props.initialData?.birdCount || 0,
  purpose: props.initialData?.purpose || 'LAYERS',
  startDate: props.initialData?.startDate || new Date().toISOString().split('T')[0],
  supplierId: props.initialData?.supplierId,
  source: props.initialData?.source || '',
  notes: props.initialData?.notes || '',
  status: props.initialData?.status
})

async function handleSubmit() {
  if (!form.name || !form.birdCount || !form.purpose) return
  error.value = ''
  emit('submit', { ...form })
}

onMounted(() => {
  if (!poultryStore.suppliers.length) {
    poultryStore.fetchSuppliers()
  }
})
</script>
