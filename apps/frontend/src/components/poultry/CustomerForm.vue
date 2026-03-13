<template>
  <div class="fixed inset-0 bg-black/50 z-[60] flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-lg overflow-hidden flex flex-col max-h-[90vh]">
      <!-- Header -->
      <div class="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50/50">
        <h3 class="text-xl font-bold text-gray-900">
          {{ initialData ? t('common.edit') : t('customers_create') }}
        </h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 transition-colors">
          <span class="text-2xl">×</span>
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-4 overflow-y-auto flex-1">
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('customer_name') }} <span class="text-red-500">*</span>
          </label>
          <input
            v-model="form.name"
            type="text"
            required
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
            :placeholder="t('customer_name')"
          />
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">
              {{ t('customer_phone') }}
            </label>
            <input
              v-model="form.phone"
              type="text"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
              placeholder="+212 ..."
            />
          </div>
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-1">
              {{ t('customer_email') }}
            </label>
            <input
              v-model="form.email"
              type="email"
              class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
              placeholder="email@example.com"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-1">
            {{ t('customer_address') }}
          </label>
          <textarea
            v-model="form.address"
            rows="2"
            class="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
          ></textarea>
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
import { ref, reactive } from 'vue'
import { useI18n } from '@/i18n'
import type { Customer, CreateCustomerRequest } from '@/types'

const props = defineProps<{
  initialData?: Customer
  loading?: boolean
}>()

const emit = defineEmits<{
  close: []
  submit: [data: CreateCustomerRequest]
}>()

const { t } = useI18n()
const error = ref('')

const form = reactive<CreateCustomerRequest>({
  name: props.initialData?.name || '',
  phone: props.initialData?.phone || '',
  email: props.initialData?.email || '',
  address: props.initialData?.address || '',
  notes: props.initialData?.notes || ''
})

async function handleSubmit() {
  if (!form.name) return
  error.value = ''
  emit('submit', { ...form })
}
</script>
