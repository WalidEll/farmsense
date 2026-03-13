<template>
  <span :class="[
    config.bg,
    config.text,
    'px-2 py-0.5 rounded-full text-xs font-semibold uppercase tracking-wider border'
  ]">
    {{ t(config.label) }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from '@/i18n'
import type { ApprovalStatus } from '@/types'

const props = defineProps<{
  status: ApprovalStatus
}>()

const { t } = useI18n()

const config = computed(() => {
  switch (props.status) {
    case 'APPROVED':
      return { bg: 'bg-green-50', text: 'text-green-700', border: 'border-green-200', label: 'approval_approved' }
    case 'PENDING':
      return { bg: 'bg-yellow-50', text: 'text-yellow-700', border: 'border-yellow-200', label: 'approval_pending' }
    case 'REJECTED':
      return { bg: 'bg-red-50', text: 'text-red-700', border: 'border-red-200', label: 'approval_rejected' }
    default:
      return { bg: 'bg-gray-50', text: 'text-gray-700', border: 'border-gray-200', label: 'approval_draft' }
  }
})
</script>
