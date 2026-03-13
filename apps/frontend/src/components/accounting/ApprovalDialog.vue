<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto">
    <div class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
      <div class="fixed inset-0 transition-opacity" aria-hidden="true">
        <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
      </div>
      <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
      <div class="inline-block align-bottom bg-white rounded-2xl text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
        <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
          <h3 class="text-lg font-bold text-gray-900 mb-4">{{ t('approval_approve') }} / {{ t('approval_reject') }}</h3>
          <div class="space-y-4">
            <p class="text-sm text-gray-500">
              Souhaitez-vous approuver ou rejeter cette transaction de {{ formatCurrency(transaction.amount) }} ?
            </p>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('approval_comment') }}</label>
              <textarea v-model="comment" rows="3" class="block w-full rounded-lg border-gray-300 shadow-sm focus:border-green-500 focus:ring-green-500 sm:text-sm"></textarea>
            </div>
          </div>
        </div>
        <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse gap-2">
          <button
            @click="submit('APPROVED')"
            class="w-full inline-flex justify-center rounded-lg border border-transparent shadow-sm px-4 py-2 bg-green-600 text-base font-medium text-white hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 sm:w-auto sm:text-sm"
          >
            {{ t('approval_approve') }}
          </button>
          <button
            @click="submit('REJECTED')"
            class="w-full inline-flex justify-center rounded-lg border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:w-auto sm:text-sm"
          >
            {{ t('approval_reject') }}
          </button>
          <button
            @click="$emit('close')"
            class="mt-3 w-full inline-flex justify-center rounded-lg border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 sm:mt-0 sm:w-auto sm:text-sm"
          >
            {{ t('cancel') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from '@/i18n'
import type { Transaction, ApprovalAction } from '@/types'

const props = defineProps<{
  isOpen: boolean
  transaction: Transaction
}>()

const { t } = useI18n()
const comment = ref('')

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', action: ApprovalAction, comment: string): void
}>()

const submit = (action: 'APPROVED' | 'REJECTED') => {
  emit('submit', action, comment.value)
  comment.value = ''
}

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('fr-MA', {
    style: 'currency',
    currency: 'MAD'
  }).format(value)
}
</script>
