<template>
  <div class="flex items-center gap-2">
    <div class="flex-1">
      <label class="block text-xs font-semibold text-gray-500 uppercase mb-1">{{ t('receipt_date') }} (De)</label>
      <input
        type="date"
        :value="from"
        @input="$emit('update:from', ($event.target as HTMLInputElement).value)"
        class="block w-full rounded-lg border-gray-300 shadow-sm focus:border-green-500 focus:ring-green-500 sm:text-sm"
      />
    </div>
    <div class="flex-1">
      <label class="block text-xs font-semibold text-gray-500 uppercase mb-1">(À)</label>
      <input
        type="date"
        :value="to"
        @input="$emit('update:to', ($event.target as HTMLInputElement).value)"
        class="block w-full rounded-lg border-gray-300 shadow-sm focus:border-green-500 focus:ring-green-500 sm:text-sm"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/i18n'

defineProps<{
  from: string
  to: string
}>()

defineEmits<{
  (e: 'update:from', value: string): void
  (e: 'update:to', value: string): void
}>()

const { t } = useI18n()
</script>
