<template>
  <div class="bg-white rounded-2xl border border-gray-100/80 shadow-sm hover:shadow-md transition-all duration-200 overflow-hidden">
    <!-- Color accent top bar -->
    <div :class="[
      transaction.type === 'INCOME' ? 'bg-emerald-500' : 'bg-rose-500',
      'h-1'
    ]"></div>

    <div class="p-4 space-y-3">
      <div class="flex justify-between items-start gap-3">
        <div class="flex items-center gap-3 min-w-0">
          <div :class="[
            transaction.type === 'INCOME' ? 'bg-emerald-100 text-emerald-600' : 'bg-rose-100 text-rose-600',
            'w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0'
          ]">
            <svg v-if="transaction.type === 'INCOME'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 10.5L12 3m0 0l7.5 7.5M12 3v18" />
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 13.5L12 21m0 0l-7.5-7.5M12 21V3" />
            </svg>
          </div>
          <div class="min-w-0">
            <h4 class="font-bold text-gray-900 text-sm truncate">{{ transaction.category }}</h4>
            <p class="text-[11px] text-gray-400 font-medium">{{ new Date(transaction.transactionDate).toLocaleDateString('fr-MA', { day: 'numeric', month: 'long' }) }}</p>
          </div>
        </div>
        <div class="text-right flex-shrink-0">
          <p :class="[transaction.type === 'INCOME' ? 'text-emerald-600' : 'text-rose-600', 'font-extrabold text-base tabular-nums']">
            {{ transaction.type === 'INCOME' ? '+' : '-' }}{{ formatCurrency(transaction.amount) }}
          </p>
        </div>
      </div>

      <p v-if="transaction.description" class="text-xs text-gray-500 line-clamp-2 leading-relaxed">{{ transaction.description }}</p>

      <div class="flex items-center justify-between gap-2">
        <div class="flex flex-wrap gap-1.5 flex-1 min-w-0">
          <TagChip v-for="tag in (transaction.tags || []).slice(0, 3)" :key="tag.id" :tag="tag" />
          <ApprovalBadge v-if="transaction.approvalStatus && transaction.approvalStatus !== 'APPROVED'" :status="transaction.approvalStatus" />
        </div>
        <router-link
          :to="`/accounting/transactions/${transaction.id}/edit`"
          class="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors flex-shrink-0"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931z" />
          </svg>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Transaction } from '@/types'
import ApprovalBadge from './ApprovalBadge.vue'
import TagChip from './TagChip.vue'

defineProps<{
  transaction: Transaction
}>()

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(value)
}
</script>
