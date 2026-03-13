<template>
  <div class="max-w-3xl mx-auto space-y-6">
    <!-- Header -->
    <div class="flex items-center gap-4">
      <button @click="$router.back()" class="p-2 hover:bg-gray-100 rounded-xl transition-colors">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
        </svg>
      </button>
      <div>
        <h1 class="text-xl font-extrabold text-gray-900 tracking-tight">
          {{ isEdit ? t('common_edit') : t('transaction_create') }}
        </h1>
        <p class="text-xs text-gray-400 font-medium mt-0.5">{{ isEdit ? 'Modifier la transaction' : 'Enregistrer une nouvelle transaction' }}</p>
      </div>
    </div>

    <form @submit.prevent="save" class="bg-white rounded-2xl shadow-sm border border-gray-100/80 overflow-hidden">
      <!-- Type Toggle - full width colored header -->
      <div class="p-6 pb-0">
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">{{ t('transaction_type') }}</label>
        <div class="flex gap-3">
          <label class="flex-1 cursor-pointer">
            <input type="radio" v-model="form.type" value="EXPENSE" class="sr-only peer">
            <div class="text-center py-3 px-4 rounded-xl border-2 transition-all duration-200 peer-checked:border-rose-400 peer-checked:bg-rose-50 peer-checked:shadow-sm peer-checked:shadow-rose-100 border-gray-100 hover:border-gray-200">
              <div class="flex items-center justify-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" :class="form.type === 'EXPENSE' ? 'text-rose-500' : 'text-gray-400'" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 13.5L12 21m0 0l-7.5-7.5M12 21V3" />
                </svg>
                <span class="text-sm font-bold" :class="form.type === 'EXPENSE' ? 'text-rose-700' : 'text-gray-600'">{{ t('transaction_expense') }}</span>
              </div>
            </div>
          </label>
          <label class="flex-1 cursor-pointer">
            <input type="radio" v-model="form.type" value="INCOME" class="sr-only peer">
            <div class="text-center py-3 px-4 rounded-xl border-2 transition-all duration-200 peer-checked:border-emerald-400 peer-checked:bg-emerald-50 peer-checked:shadow-sm peer-checked:shadow-emerald-100 border-gray-100 hover:border-gray-200">
              <div class="flex items-center justify-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" :class="form.type === 'INCOME' ? 'text-emerald-500' : 'text-gray-400'" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 10.5L12 3m0 0l7.5 7.5M12 3v18" />
                </svg>
                <span class="text-sm font-bold" :class="form.type === 'INCOME' ? 'text-emerald-700' : 'text-gray-600'">{{ t('transaction_income') }}</span>
              </div>
            </div>
          </label>
        </div>
      </div>

      <div class="p-6 space-y-5">
        <!-- Category + Amount row -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
          <CategoryAutocomplete
            v-model="form.category"
            :categories="categories"
            :label="t('transaction_category') + ' *'"
            :placeholder="t('crops_search')"
          />
          <div>
            <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('transaction_amount') }} *</label>
            <div class="relative">
              <input
                type="number"
                step="0.01"
                v-model="form.amount"
                required
                class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm pr-14 py-2.5 font-semibold text-gray-800 placeholder:text-gray-300"
                placeholder="0.00"
              >
              <div class="absolute inset-y-0 right-0 pr-4 flex items-center pointer-events-none">
                <span class="text-xs font-bold text-gray-400 bg-gray-50 px-2 py-0.5 rounded">DH</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Date + Payment -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div>
            <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('transaction_date') }} *</label>
            <input type="date" v-model="form.transactionDate" required class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5">
          </div>
          <div>
            <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('transaction_payment_method') }}</label>
            <select v-model="form.paymentMethod" class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5">
              <option value="CASH">{{ t('payment_cash') }}</option>
              <option value="BANK_TRANSFER">{{ t('payment_bank_transfer') }}</option>
              <option value="CHECK">{{ t('payment_check') }}</option>
              <option value="MOBILE">{{ t('payment_mobile') }}</option>
              <option value="OTHER">{{ t('payment_other') }}</option>
            </select>
          </div>
        </div>

        <!-- Quantity + Unit Price (collapsed section) -->
        <div>
          <button type="button" @click="showAdvanced = !showAdvanced" class="text-xs font-semibold text-gray-400 hover:text-gray-600 flex items-center gap-1.5 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" :class="['h-3.5 w-3.5 transition-transform', showAdvanced ? 'rotate-90' : '']" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
            </svg>
            {{ t('transaction_advanced') || 'Details supplementaires' }}
          </button>
          <div v-if="showAdvanced" class="grid grid-cols-1 md:grid-cols-2 gap-5 mt-4 pt-4 border-t border-gray-50">
            <div>
              <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('transaction_quantity') }}</label>
              <input type="number" step="0.01" v-model="form.quantity" class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5" placeholder="0">
            </div>
            <div>
              <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('transaction_unit_price') }}</label>
              <input type="number" step="0.01" v-model="form.unitPrice" class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5" placeholder="0.00">
            </div>
          </div>
        </div>

        <!-- Tags -->
        <TagPicker v-model="form.tagIds" :all-tags="tags" />

        <!-- Description -->
        <div>
          <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('transaction_description') }}</label>
          <textarea v-model="form.description" rows="3" class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5 placeholder:text-gray-300 resize-none" :placeholder="t('transaction_description_placeholder') || 'Notes ou details...'"></textarea>
        </div>
      </div>

      <!-- Actions footer -->
      <div class="px-6 py-4 bg-gray-50/50 border-t border-gray-100 flex justify-end gap-3">
        <button type="button" @click="$router.back()" class="px-5 py-2.5 text-sm border border-gray-200 text-gray-600 rounded-xl hover:bg-white transition-colors font-semibold">
          {{ t('cancel') }}
        </button>
        <button
          type="submit"
          :disabled="loading"
          :class="[
            form.type === 'INCOME' ? 'bg-emerald-600 hover:bg-emerald-700 shadow-emerald-200' : 'bg-rose-600 hover:bg-rose-700 shadow-rose-200',
            'px-7 py-2.5 text-sm text-white rounded-xl transition-all duration-200 font-bold disabled:opacity-50 shadow-sm active:scale-[0.98]'
          ]"
        >
          {{ loading ? t('loading') : t('common_save') }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from '@/i18n'
import { useAccountingStore } from '@/stores/accounting.store'
import { useTagsStore } from '@/stores/tags.store'
import { storeToRefs } from 'pinia'
import CategoryAutocomplete from '@/components/accounting/CategoryAutocomplete.vue'
import TagPicker from '@/components/accounting/TagPicker.vue'
import type { CreateTransactionRequest } from '@/types'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const store = useAccountingStore()
const tagsStore = useTagsStore()

const { categories, loading } = storeToRefs(store)
const { tags } = storeToRefs(tagsStore)

const isEdit = computed(() => !!route.params.id)
const showAdvanced = ref(false)

const form = reactive<CreateTransactionRequest>({
  type: 'EXPENSE',
  category: '',
  amount: 0,
  transactionDate: new Date().toISOString().split('T')[0],
  description: '',
  paymentMethod: 'CASH',
  tagIds: [],
  quantity: undefined,
  unitPrice: undefined
})

onMounted(async () => {
  store.fetchCategories()
  tagsStore.fetchTags()

  if (isEdit.value) {
    const transaction = await store.fetchTransactionById(route.params.id as string)
    if (transaction) {
      form.type = transaction.type
      form.category = transaction.category
      form.amount = transaction.amount
      form.transactionDate = transaction.transactionDate
      form.description = transaction.description
      form.paymentMethod = transaction.paymentMethod
      form.tagIds = transaction.tags?.map((t: any) => t.id) || []
      form.quantity = transaction.quantity
      form.unitPrice = transaction.unitPrice
      if (form.quantity || form.unitPrice) showAdvanced.value = true
    }
  }
})

const save = async () => {
  if (isEdit.value) {
    await store.updateTransaction(route.params.id as string, form)
  } else {
    await store.createTransaction(form)
  }
  router.push('/accounting/transactions')
}
</script>
