<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div class="flex flex-col gap-1">
        <h1 class="text-2xl font-bold text-gray-900">{{ t('customers_title') }}</h1>
        <p class="text-sm text-gray-500">{{ poultryStore.customers.length }} {{ t('nav.customers').toLowerCase() }}</p>
      </div>
      <button
        @click="showCreateModal = true"
        class="bg-green-700 text-white font-bold px-6 py-2.5 rounded-xl hover:bg-green-800 transition-all shadow-lg shadow-green-200 flex items-center justify-center gap-2"
      >
        <span>➕</span> {{ t('customers_create') }}
      </button>
    </div>

    <!-- Search & Filters -->
    <div class="bg-white p-4 rounded-2xl border border-gray-100 shadow-sm flex flex-col sm:flex-row gap-4">
      <div class="relative flex-1">
        <span class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">🔍</span>
        <input
          v-model="searchQuery"
          type="text"
          :placeholder="t('crops_search')"
          class="w-full bg-gray-50 border-none rounded-xl pl-11 pr-4 py-2.5 focus:ring-2 focus:ring-green-500 outline-none"
          @input="handleSearch"
        />
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="poultryStore.loading && !poultryStore.customers.length" class="flex justify-center py-12">
      <div class="animate-spin text-4xl">🔄</div>
    </div>

    <!-- Empty State -->
    <div
      v-else-if="!poultryStore.customers.length"
      class="bg-white rounded-2xl border border-gray-100 shadow-sm p-12 text-center space-y-4"
    >
      <div class="text-6xl">👥</div>
      <div class="space-y-2">
        <h3 class="text-xl font-bold text-gray-900">{{ t('customers_empty') }}</h3>
      </div>
      <button
        @click="showCreateModal = true"
        class="text-green-700 font-bold hover:underline"
      >
        {{ t('customers_create') }}
      </button>
    </div>

    <!-- Customers Grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="customer in poultryStore.customers"
        :key="customer.id"
        class="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-all flex flex-col gap-4 group"
      >
        <div class="flex justify-between items-start">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center font-bold text-lg uppercase">
              {{ customer.name.charAt(0) }}
            </div>
            <div>
              <h3 class="font-bold text-gray-900 group-hover:text-green-700 transition-colors">
                {{ customer.name }}
              </h3>
            </div>
          </div>
          <div class="flex gap-1">
            <button
              @click="editCustomer(customer)"
              class="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
            >
              ✏️
            </button>
            <button
              @click="confirmDelete(customer)"
              class="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
            >
              🗑️
            </button>
          </div>
        </div>

        <div class="space-y-2 text-sm text-gray-600">
          <div v-if="customer.phone" class="flex items-center gap-2">
            <span>📞</span> {{ customer.phone }}
          </div>
          <div v-if="customer.email" class="flex items-center gap-2">
            <span>✉️</span> {{ customer.email }}
          </div>
          <div v-if="customer.address" class="flex items-center gap-2">
            <span>📍</span> {{ customer.address }}
          </div>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <CustomerForm
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @submit="handleCreate"
      :loading="poultryStore.loading"
    />

    <CustomerForm
      v-if="editingCustomer"
      :initial-data="editingCustomer"
      @close="editingCustomer = null"
      @submit="handleUpdate"
      :loading="poultryStore.loading"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { usePoultryStore } from '@/stores/poultry.store'
import { useI18n } from '@/i18n'
import CustomerForm from '@/components/poultry/CustomerForm.vue'
import type { Customer, CreateCustomerRequest } from '@/types'

const { t } = useI18n()
const poultryStore = usePoultryStore()

const searchQuery = ref('')
const showCreateModal = ref(false)
const editingCustomer = ref<Customer | null>(null)

let searchTimeout: any = null
function handleSearch() {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    poultryStore.fetchCustomers(searchQuery.value)
  }, 300)
}

async function handleCreate(data: CreateCustomerRequest) {
  try {
    await poultryStore.createCustomer(data)
    showCreateModal.value = false
  } catch (e) {
    console.error(e)
  }
}

async function handleUpdate(data: CreateCustomerRequest) {
  if (!editingCustomer.value) return
  try {
    await poultryStore.updateCustomer(editingCustomer.value.id, data)
    editingCustomer.value = null
  } catch (e) {
    console.error(e)
  }
}

function editCustomer(customer: Customer) {
  editingCustomer.value = customer
}

async function confirmDelete(customer: Customer) {
  if (confirm(t('plants_confirmDelete'))) {
    await poultryStore.deleteCustomer(customer.id)
  }
}

onMounted(() => {
  poultryStore.fetchCustomers()
})
</script>
