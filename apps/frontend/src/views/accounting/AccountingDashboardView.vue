<template>
  <div class="space-y-8">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <div>
        <h1 class="text-2xl font-extrabold text-gray-900 tracking-tight">{{ t('nav_accounting_dashboard') }}</h1>
        <p class="text-sm text-gray-400 mt-1 font-medium">{{ t('dashboard_subtitle') || 'Vue financiere de votre exploitation' }}</p>
      </div>
      <div class="flex items-center gap-2">
        <DateRangeFilter v-model:from="from" v-model:to="to" />
        <button
          @click="refresh"
          :class="[
            'p-2.5 border rounded-xl transition-all duration-200',
            loading ? 'bg-green-50 border-green-200 text-green-600' : 'bg-white border-gray-200 text-gray-500 hover:bg-gray-50 hover:border-gray-300'
          ]"
        >
          <svg xmlns="http://www.w3.org/2000/svg" :class="['h-4.5 w-4.5 transition-transform', loading ? 'animate-spin' : '']" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
        </button>
      </div>
    </div>

    <!-- KPI Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <DashboardKpiCard
        :label="t('dashboard_total_income')"
        :value="dashboardData?.totalIncome || 0"
        isCurrency
        color="green"
        :icon="ArrowTrendingUpIcon"
      />
      <DashboardKpiCard
        :label="t('dashboard_total_expenses')"
        :value="dashboardData?.totalExpenses || 0"
        isCurrency
        color="red"
        :icon="ArrowTrendingDownIcon"
      />
      <DashboardKpiCard
        :label="t('dashboard_net_profit')"
        :value="dashboardData?.netProfit || 0"
        isCurrency
        :color="(dashboardData?.netProfit || 0) >= 0 ? 'blue' : 'red'"
        :icon="BanknotesIcon"
      />
      <DashboardKpiCard
        :label="t('dashboard_pending_approvals')"
        :value="dashboardData?.pendingApprovals || 0"
        color="yellow"
        :icon="ClockIcon"
      />
    </div>

    <!-- Charts Row -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Income vs Expense Chart -->
      <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100/80 hover:shadow-md transition-shadow duration-300">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-base font-bold text-gray-800">{{ t('dashboard_income_vs_expense') }}</h3>
          <span class="text-xs font-medium text-gray-400 bg-gray-50 px-2.5 py-1 rounded-lg">{{ currentPeriodLabel }}</span>
        </div>
        <IncomeExpenseChart
          v-if="dashboardData"
          :income="dashboardData.totalIncome"
          :expenses="dashboardData.totalExpenses"
        />
        <div v-else class="h-80 flex items-center justify-center">
          <div class="animate-pulse flex flex-col items-center gap-3">
            <div class="w-32 h-32 rounded-full bg-gray-100"></div>
            <div class="h-3 w-24 bg-gray-100 rounded"></div>
          </div>
        </div>
      </div>

      <!-- Expenses by Category Chart -->
      <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100/80 hover:shadow-md transition-shadow duration-300">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-base font-bold text-gray-800">{{ t('dashboard_expenses_by_category') }}</h3>
          <span class="text-xs font-medium text-gray-400 bg-gray-50 px-2.5 py-1 rounded-lg">
            {{ dashboardData?.expensesByCategory?.length || 0 }} {{ t('transaction_category') }}
          </span>
        </div>
        <CategoryChart
          v-if="dashboardData"
          :data="dashboardData.expensesByCategory"
          :title="t('dashboard_expenses_by_category')"
        />
        <div v-else class="h-64 flex items-center justify-center">
          <div class="animate-pulse flex flex-col items-center gap-3">
            <div class="w-32 h-32 rounded-full bg-gray-100"></div>
            <div class="h-3 w-24 bg-gray-100 rounded"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Transactions -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100/80 overflow-hidden hover:shadow-md transition-shadow duration-300">
      <div class="flex justify-between items-center px-6 py-5 border-b border-gray-50">
        <div class="flex items-center gap-3">
          <div class="p-2 bg-gray-50 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 class="text-base font-bold text-gray-800">{{ t('dashboard_recent_transactions') }}</h3>
        </div>
        <router-link
          to="/accounting/transactions"
          class="inline-flex items-center gap-1.5 text-sm font-semibold text-green-600 hover:text-green-700 bg-green-50 hover:bg-green-100 px-3 py-1.5 rounded-lg transition-colors"
        >
          {{ t('dashboard_view_all') }}
          <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
          </svg>
        </router-link>
      </div>
      <TransactionTable :transactions="dashboardData?.recentTransactions || []" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useI18n } from '@/i18n'
import { useAccountingStore } from '@/stores/accounting.store'
import { storeToRefs } from 'pinia'
import DateRangeFilter from '@/components/accounting/DateRangeFilter.vue'
import DashboardKpiCard from '@/components/accounting/DashboardKpiCard.vue'
import CategoryChart from '@/components/accounting/CategoryChart.vue'
import IncomeExpenseChart from '@/components/accounting/IncomeExpenseChart.vue'
import TransactionTable from '@/components/accounting/TransactionTable.vue'

const ArrowTrendingUpIcon = {
  template: '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M2.25 18L9 11.25l4.306 4.307L21.75 8.25m-4.5-4.5h4.5V8.25" /></svg>'
}
const ArrowTrendingDownIcon = {
  template: '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M2.25 6L9 12.75l4.306-4.307L21.75 15.75m-4.5 4.5h4.5v-4.5" /></svg>'
}
const BanknotesIcon = {
  template: '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M2.25 18.75a60.07 60.07 0 0115.797 2.101c.727.198 1.453-.342 1.453-1.096V18.75M3.75 4.5v.75m0 10.5v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z" /></svg>'
}
const ClockIcon = {
  template: '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>'
}

const { t } = useI18n()
const store = useAccountingStore()
const { dashboardData, loading } = storeToRefs(store)

const from = ref(new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0])
const to = ref(new Date().toISOString().split('T')[0])

const currentPeriodLabel = computed(() => {
  if (!from.value || !to.value) return ''
  const f = new Date(from.value)
  const t = new Date(to.value)
  return `${f.toLocaleDateString('fr-MA', { month: 'short' })} - ${t.toLocaleDateString('fr-MA', { month: 'short', year: 'numeric' })}`
})

const refresh = () => store.fetchDashboard(from.value, to.value)

onMounted(refresh)
watch([from, to], refresh)
</script>
