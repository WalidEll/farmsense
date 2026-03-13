<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <div>
        <h1 class="text-2xl font-extrabold text-gray-900 tracking-tight">{{ t('nav_transactions') }}</h1>
        <p class="text-sm text-gray-400 mt-0.5 font-medium">{{ transactions.length }} {{ t('transactions_count') || 'transactions' }}</p>
      </div>
      <router-link
        to="/accounting/transactions/new"
        class="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 active:scale-[0.98] transition-all duration-200 font-semibold text-sm shadow-sm shadow-emerald-200"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
        </svg>
        {{ t('transaction_create') }}
      </router-link>
    </div>

    <TransactionFilters @filter="onFilter" />

    <!-- Loading state -->
    <div v-if="loading" class="flex justify-center py-16">
      <div class="flex flex-col items-center gap-3">
        <div class="animate-spin rounded-full h-10 w-10 border-[3px] border-gray-200 border-t-emerald-500"></div>
        <p class="text-sm text-gray-400 font-medium">{{ t('loading') }}...</p>
      </div>
    </div>

    <div v-else>
      <!-- Desktop Table -->
      <div class="hidden md:block bg-white rounded-2xl shadow-sm border border-gray-100/80 overflow-hidden">
        <TransactionTable :transactions="transactions" @delete="onDelete" @approve="openApprovalDialog" />
      </div>

      <!-- Mobile Cards -->
      <div class="grid grid-cols-1 gap-3 md:hidden">
        <TransactionCard
          v-for="transaction in transactions"
          :key="transaction.id"
          :transaction="transaction"
          @click="transaction.approvalStatus === 'PENDING' ? openApprovalDialog(transaction) : null"
        />
        <!-- Empty mobile state -->
        <div v-if="transactions.length === 0" class="py-16 flex flex-col items-center gap-3">
          <div class="w-16 h-16 rounded-2xl bg-gray-50 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-7 w-7 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 6v12m-3-2.818l.879.659c1.171.879 3.07.879 4.242 0 1.172-.879 1.172-2.303 0-3.182C13.536 12.219 12.768 12 12 12c-.725 0-1.45-.22-2.003-.659-1.106-.879-1.106-2.303 0-3.182s2.9-.879 4.006 0l.415.33M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <p class="text-sm text-gray-400 font-medium">{{ t('transactions_empty') || 'Aucune transaction' }}</p>
          <router-link to="/accounting/transactions/new" class="text-sm text-emerald-600 font-semibold hover:text-emerald-700">
            {{ t('transaction_create') }} &rarr;
          </router-link>
        </div>
      </div>
    </div>

    <ApprovalDialog
      v-if="selectedTransaction"
      :is-open="showApprovalDialog"
      :transaction="selectedTransaction"
      @close="showApprovalDialog = false"
      @submit="onApprove"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from '@/i18n'
import { useAccountingStore } from '@/stores/accounting.store'
import { storeToRefs } from 'pinia'
import TransactionTable from '@/components/accounting/TransactionTable.vue'
import TransactionCard from '@/components/accounting/TransactionCard.vue'
import TransactionFilters from '@/components/accounting/TransactionFilters.vue'
import ApprovalDialog from '@/components/accounting/ApprovalDialog.vue'
import type { Transaction, TransactionFilter, ApprovalAction } from '@/types'

const { t } = useI18n()
const store = useAccountingStore()
const { transactions, loading } = storeToRefs(store)

const showApprovalDialog = ref(false)
const selectedTransaction = ref<Transaction | null>(null)

const onFilter = (filters: TransactionFilter) => {
  store.fetchTransactions(filters)
}

const onDelete = async (id: string) => {
  if (confirm(t('plant_delete_confirm'))) {
    await store.deleteTransaction(id)
  }
}

const openApprovalDialog = (transaction: Transaction) => {
  selectedTransaction.value = transaction
  showApprovalDialog.value = true
}

const onApprove = async (action: ApprovalAction, comment: string) => {
  if (selectedTransaction.value) {
    await store.approveTransaction(selectedTransaction.value.id, { action, comment })
    showApprovalDialog.value = false
    selectedTransaction.value = null
    store.fetchTransactions()
  }
}

onMounted(() => {
  store.fetchTransactions()
})
</script>
