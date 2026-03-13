<template>
  <div class="overflow-x-auto">
    <table class="min-w-full">
      <thead>
        <tr class="border-b border-gray-100">
          <th class="px-6 py-3.5 text-left text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('transaction_date') }}</th>
          <th class="px-6 py-3.5 text-left text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('transaction_category') }}</th>
          <th class="px-6 py-3.5 text-left text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('transaction_type') }}</th>
          <th class="px-6 py-3.5 text-right text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('transaction_amount') }}</th>
          <th class="px-6 py-3.5 text-right text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('common_actions') }}</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-50">
        <tr
          v-for="transaction in transactions"
          :key="transaction.id"
          class="group hover:bg-gray-50/50 transition-colors duration-150"
        >
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex items-center gap-3">
              <div :class="[
                transaction.type === 'INCOME' ? 'bg-emerald-100 text-emerald-600' : 'bg-rose-100 text-rose-600',
                'w-8 h-8 rounded-lg flex items-center justify-center flex-shrink-0'
              ]">
                <svg v-if="transaction.type === 'INCOME'" xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 10.5L12 3m0 0l7.5 7.5M12 3v18" />
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 13.5L12 21m0 0l-7.5-7.5M12 21V3" />
                </svg>
              </div>
              <div>
                <p class="text-sm font-semibold text-gray-800">{{ formatDate(transaction.transactionDate) }}</p>
                <p class="text-[11px] text-gray-400">{{ formatWeekday(transaction.transactionDate) }}</p>
              </div>
            </div>
          </td>
          <td class="px-6 py-4">
            <div>
              <p class="text-sm font-semibold text-gray-800">{{ transaction.category }}</p>
              <p v-if="transaction.subcategory" class="text-[11px] text-gray-400 mt-0.5">{{ transaction.subcategory }}</p>
              <div v-if="transaction.tags?.length" class="flex flex-wrap gap-1 mt-1.5">
                <span
                  v-for="tag in transaction.tags.slice(0, 3)"
                  :key="tag.id"
                  class="inline-block px-1.5 py-0.5 rounded text-[10px] font-semibold"
                  :style="{ backgroundColor: tag.color + '18', color: tag.color }"
                >{{ tag.name }}</span>
                <span v-if="transaction.tags.length > 3" class="text-[10px] text-gray-400 self-center">+{{ transaction.tags.length - 3 }}</span>
              </div>
            </div>
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
            <ApprovalBadge v-if="transaction.approvalStatus && transaction.approvalStatus !== 'APPROVED'" :status="transaction.approvalStatus" />
            <span v-else :class="[
              transaction.type === 'INCOME' ? 'text-emerald-700 bg-emerald-50 border-emerald-200/50' : 'text-rose-700 bg-rose-50 border-rose-200/50',
              'px-2.5 py-1 rounded-lg text-[11px] font-bold uppercase border'
            ]">
              {{ transaction.type === 'INCOME' ? t('transaction_income') : t('transaction_expense') }}
            </span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-right">
            <span :class="[
              transaction.type === 'INCOME' ? 'text-emerald-600' : 'text-rose-600',
              'text-sm font-bold tabular-nums'
            ]">
              {{ transaction.type === 'INCOME' ? '+' : '-' }}{{ formatCurrency(transaction.amount) }}
            </span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-right">
            <div class="flex items-center justify-end gap-1 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
              <button
                v-if="transaction.approvalStatus === 'PENDING'"
                @click="$emit('approve', transaction)"
                class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors"
                :title="t('approval_approve')"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </button>
              <router-link
                :to="`/accounting/transactions/${transaction.id}/edit`"
                class="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931z" />
                </svg>
              </router-link>
              <button
                @click="$emit('delete', transaction.id)"
                class="p-1.5 text-gray-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                </svg>
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <!-- Empty state -->
    <div v-if="transactions.length === 0" class="py-16 flex flex-col items-center gap-3">
      <div class="w-16 h-16 rounded-2xl bg-gray-50 flex items-center justify-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-7 w-7 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 18.75a60.07 60.07 0 0115.797 2.101c.727.198 1.453-.342 1.453-1.096V18.75M3.75 4.5v.75A.75.75 0 013 6h-.75m0 0v-.375c0-.621.504-1.125 1.125-1.125H20.25M2.25 6v9m18-10.5v.75c0 .414.336.75.75.75h.75m-1.5-1.5h.375c.621 0 1.125.504 1.125 1.125v9.75c0 .621-.504 1.125-1.125 1.125h-.375m1.5-1.5H21a.75.75 0 00-.75.75v.75m0 0H3.75m0 0h-.375a1.125 1.125 0 01-1.125-1.125V15m1.5 1.5v-.75A.75.75 0 003 15h-.75M15 10.5a3 3 0 11-6 0 3 3 0 016 0zm3 0h.008v.008H18V10.5zm-12 0h.008v.008H6V10.5z" />
        </svg>
      </div>
      <p class="text-sm text-gray-400 font-medium">{{ t('transactions_empty') || 'Aucune transaction' }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/i18n'
import type { Transaction } from '@/types'
import ApprovalBadge from './ApprovalBadge.vue'

defineProps<{
  transactions: Transaction[]
}>()

defineEmits<{
  (e: 'delete', id: string): void
  (e: 'approve', transaction: Transaction): void
}>()

const { t } = useI18n()

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(value)
}

const formatDate = (d: string) => new Date(d).toLocaleDateString('fr-MA', { day: 'numeric', month: 'short' })
const formatWeekday = (d: string) => new Date(d).toLocaleDateString('fr-MA', { weekday: 'short' })
</script>
