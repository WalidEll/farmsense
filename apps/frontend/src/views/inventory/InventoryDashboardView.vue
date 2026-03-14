<template>
  <div class="space-y-6">
    <!-- ── Top Bar: Title + Search + Actions ── -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-extrabold text-gray-900 tracking-tight">{{ t('inventory.dashboard_title') || 'Inventory Dashboard' }}</h1>
        <p class="text-sm text-gray-400 mt-0.5">{{ t('inventory.dashboard_subtitle') || 'Real-time resource tracking and alerts' }}</p>
      </div>
      <div class="flex items-center gap-3">
        <!-- Search -->
        <div class="relative hidden md:block">
          <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/>
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            :placeholder="t('inventory.search_placeholder') || 'Search items, batches...'"
            class="pl-10 pr-4 py-2.5 w-64 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-green-200 focus:border-green-400 transition-all"
          />
        </div>
        <!-- Log Usage CTA -->
        <button
          @click="showMovementForm = true"
          class="flex items-center gap-2 bg-brand-dark text-white px-5 py-2.5 rounded-xl text-sm font-semibold hover:bg-green-800 transition-colors shadow-sm"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"/></svg>
          {{ t('inventory.log_usage') || 'Log Usage' }}
        </button>
        <!-- Notification Bell -->
        <button class="relative text-gray-400 hover:text-gray-600 transition-colors">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.75">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          <span
            v-if="lowStockAlerts.length > 0"
            class="absolute -top-1 -right-1 bg-red-500 ring-2 ring-white rounded-full w-2.5 h-2.5"
          ></span>
        </button>
      </div>
    </div>

    <!-- ── 4 KPI Cards ── -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <!-- Feed Stock -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
        <div class="flex justify-between items-start mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-100 flex items-center justify-center">
            <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.75">
              <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
            </svg>
          </div>
          <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="kpiTrend(feedKpi.trend)">{{ feedKpi.trendLabel }}</span>
        </div>
        <p class="text-sm text-gray-500 font-medium mb-1">{{ t('inventory.feed_stock') || 'Feed Stock' }}</p>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-extrabold text-gray-900 tracking-tight">{{ feedKpi.value }}</span>
          <span class="text-sm text-gray-400 font-medium">{{ t('inventory.unit_tons') || 'Tons' }}</span>
        </div>
      </div>

      <!-- Fertilizer -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
        <div class="flex justify-between items-start mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-100 flex items-center justify-center">
            <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.75">
              <path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/>
            </svg>
          </div>
          <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="kpiTrend(fertilizerKpi.trend)">{{ fertilizerKpi.trendLabel }}</span>
        </div>
        <p class="text-sm text-gray-500 font-medium mb-1">{{ t('inventory.fertilizer') || 'Fertilizer' }}</p>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-extrabold text-gray-900 tracking-tight">{{ fertilizerKpi.value }}</span>
          <span class="text-sm text-gray-400 font-medium">kg</span>
        </div>
      </div>

      <!-- Seeds -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
        <div class="flex justify-between items-start mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-100 flex items-center justify-center">
            <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.75">
              <path d="M7 20h10M10 20c5.5-2.5.8-6.4 3-10"/><path d="M9.5 9.4c1.1.8 1.8 2.2 2.3 3.7-2 .4-3.5.4-4.8-.3-1.2-.6-2.3-1.9-3-4.2 2.8-.5 4.4 0 5.5.8z"/>
            </svg>
          </div>
          <span class="text-xs font-bold text-gray-500 bg-gray-100 px-2 py-0.5 rounded-full">{{ t('inventory.stable') || 'Stable' }}</span>
        </div>
        <p class="text-sm text-gray-500 font-medium mb-1">{{ t('inventory.seeds') || 'Seeds' }}</p>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-extrabold text-gray-900 tracking-tight">{{ seedsKpi.value }}</span>
          <span class="text-sm text-gray-400 font-medium">{{ t('inventory.unit_bags') || 'Bags' }}</span>
        </div>
      </div>

      <!-- Medication -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
        <div class="flex justify-between items-start mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-100 flex items-center justify-center">
            <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.75">
              <path d="M19 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2z"/><path d="M12 8v8M8 12h8"/>
            </svg>
          </div>
          <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="kpiTrend(medicationKpi.trend)">{{ medicationKpi.trendLabel }}</span>
        </div>
        <p class="text-sm text-gray-500 font-medium mb-1">{{ t('inventory.medication') || 'Medication' }}</p>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-extrabold text-gray-900 tracking-tight">{{ medicationKpi.value }}</span>
          <span class="text-sm text-gray-400 font-medium">{{ t('inventory.unit_units') || 'Units' }}</span>
        </div>
      </div>
    </div>

    <!-- ── Main Content: Items Table (2/3) + Sidebar (1/3) ── -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

      <!-- ── Inventory Items Table ── -->
      <div class="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-gray-50">
          <h2 class="text-lg font-bold text-gray-900">{{ t('inventory.items_title') || 'Inventory Items' }}</h2>
          <!-- Category Filter Tabs -->
          <div class="flex items-center gap-1 bg-gray-50 rounded-xl p-1">
            <button
              v-for="tab in categoryTabs"
              :key="tab.key"
              @click="activeTab = tab.key"
              class="px-3.5 py-1.5 text-xs font-semibold rounded-lg transition-all"
              :class="activeTab === tab.key
                ? 'bg-white text-gray-900 shadow-sm'
                : 'text-gray-500 hover:text-gray-700'"
            >
              {{ tab.label }}
            </button>
          </div>
        </div>

        <!-- Desktop Table -->
        <div class="hidden md:block overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="border-b border-gray-100">
                <th class="text-left px-6 py-3 text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('inventory.col_item_name') || 'Item Name' }}</th>
                <th class="text-left px-6 py-3 text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('inventory.col_category') || 'Category' }}</th>
                <th class="text-left px-6 py-3 text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('inventory.col_stock_level') || 'Stock Level' }}</th>
                <th class="text-left px-6 py-3 text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('inventory.col_status') || 'Status' }}</th>
                <th class="text-left px-6 py-3 text-[11px] font-bold text-gray-400 uppercase tracking-wider">{{ t('inventory.col_action') || 'Action' }}</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr v-for="item in filteredItems" :key="item.id" class="hover:bg-gray-50/50 transition-colors">
                <!-- Item Name + Icon -->
                <td class="px-6 py-4">
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-lg flex items-center justify-center text-sm" :class="categoryIconBg(item.categoryType)">
                      {{ categoryEmoji(item.categoryType) }}
                    </div>
                    <span class="font-semibold text-gray-900 text-sm">{{ item.name }}</span>
                  </div>
                </td>
                <!-- Category -->
                <td class="px-6 py-4 text-sm text-gray-600">{{ item.categoryName || formatCategoryType(item.categoryType) }}</td>
                <!-- Stock Level Bar -->
                <td class="px-6 py-4">
                  <div class="flex items-center gap-3">
                    <div class="flex-1 max-w-[120px] h-2 bg-gray-100 rounded-full overflow-hidden">
                      <div
                        class="h-full rounded-full transition-all duration-500"
                        :class="stockBarColor(item.status)"
                        :style="{ width: Math.min(item.stockPercentage, 100) + '%' }"
                      ></div>
                    </div>
                    <span class="text-sm text-gray-500 font-medium w-10 text-right">{{ item.stockPercentage }}%</span>
                  </div>
                </td>
                <!-- Status Badge -->
                <td class="px-6 py-4">
                  <span
                    class="text-[10px] font-bold uppercase tracking-wide px-2.5 py-1 rounded-full"
                    :class="statusBadge(item.status)"
                  >{{ item.status }}</span>
                </td>
                <!-- Action -->
                <td class="px-6 py-4">
                  <button
                    @click="openRestock(item)"
                    class="text-sm font-medium transition-colors hover:underline"
                    style="color: #1B4235;"
                  >{{ t('inventory.restock') || 'Restock' }}</button>
                </td>
              </tr>
              <!-- Empty state -->
              <tr v-if="filteredItems.length === 0">
                <td colspan="5" class="px-6 py-12 text-center text-gray-400 text-sm">
                  {{ t('inventory.no_items') || 'No inventory items found' }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Mobile Cards -->
        <div class="md:hidden divide-y divide-gray-50">
          <div v-for="item in filteredItems" :key="item.id" class="p-4 space-y-3">
            <div class="flex justify-between items-start">
              <div class="flex items-center gap-2.5">
                <div class="w-8 h-8 rounded-lg flex items-center justify-center text-sm" :class="categoryIconBg(item.categoryType)">
                  {{ categoryEmoji(item.categoryType) }}
                </div>
                <div>
                  <span class="font-semibold text-gray-900 text-sm block">{{ item.name }}</span>
                  <span class="text-xs text-gray-500">{{ item.categoryName || formatCategoryType(item.categoryType) }}</span>
                </div>
              </div>
              <span class="text-[10px] font-bold uppercase tracking-wide px-2 py-0.5 rounded-full" :class="statusBadge(item.status)">
                {{ item.status }}
              </span>
            </div>
            <div class="flex items-center gap-3">
              <div class="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
                <div class="h-full rounded-full" :class="stockBarColor(item.status)" :style="{ width: item.stockPercentage + '%' }"></div>
              </div>
              <span class="text-xs text-gray-500 font-medium">{{ item.stockPercentage }}%</span>
            </div>
            <button @click="openRestock(item)" class="text-xs font-semibold" style="color: #1B4235;">{{ t('inventory.restock') || 'Restock' }}</button>
          </div>
        </div>

        <!-- Pagination -->
        <div class="px-6 py-4 border-t border-gray-50 flex items-center justify-between">
          <span class="text-xs text-gray-400">
            {{ t('inventory.showing') || 'Showing' }} {{ paginationStart }}-{{ paginationEnd }} {{ t('inventory.of') || 'of' }} {{ totalFilteredCount }} {{ t('inventory.items_label') || 'items' }}
          </span>
          <div class="flex items-center gap-2">
            <button
              @click="currentPage > 1 && currentPage--"
              :disabled="currentPage <= 1"
              class="w-8 h-8 rounded-lg border border-gray-200 flex items-center justify-center text-gray-400 hover:text-gray-600 hover:border-gray-300 transition-all disabled:opacity-30"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
            </button>
            <button
              @click="currentPage < totalPages && currentPage++"
              :disabled="currentPage >= totalPages"
              class="w-8 h-8 rounded-lg border border-gray-200 flex items-center justify-center text-gray-400 hover:text-gray-600 hover:border-gray-300 transition-all disabled:opacity-30"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"/></svg>
            </button>
          </div>
        </div>
      </div>

      <!-- ── Right Sidebar ── -->
      <div class="space-y-6">

        <!-- Low Stock Alerts Card -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div class="p-5 flex items-center justify-between border-b border-gray-50">
            <h3 class="text-base font-bold text-gray-900">{{ t('inventory.low_stock_alerts') || 'Low Stock Alerts' }}</h3>
            <span
              v-if="lowStockAlerts.length > 0"
              class="w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center text-[11px] font-bold"
            >{{ lowStockAlerts.length }}</span>
          </div>
          <div class="divide-y divide-gray-50">
            <div v-for="alert in lowStockAlerts.slice(0, 3)" :key="alert.id" class="p-4 flex items-start gap-3">
              <div class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0" :class="alertIconBg(alert.status)">
                <svg v-if="alert.status === 'CRITICAL'" class="w-4 h-4 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                </svg>
                <svg v-else-if="alert.status === 'LOW'" class="w-4 h-4 text-amber-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                </svg>
                <svg v-else class="w-4 h-4 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/>
                </svg>
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-semibold text-gray-900 text-sm">{{ alert.name }}</p>
                <p class="text-xs text-gray-500 mt-0.5">
                  {{ t('inventory.only') || 'Only' }} {{ alert.currentStock }} {{ alert.unit?.toLowerCase() }} {{ t('inventory.remaining') || 'remaining.' }}
                </p>
                <button
                  v-if="alert.status === 'CRITICAL'"
                  @click="openRestock(alert)"
                  class="mt-2 text-[11px] font-bold uppercase tracking-wide bg-red-500 text-white px-3 py-1 rounded-full hover:bg-red-600 transition-colors"
                >{{ t('inventory.order_now') || 'ORDER NOW' }}</button>
              </div>
            </div>
            <!-- Empty -->
            <div v-if="lowStockAlerts.length === 0" class="p-6 text-center text-gray-400 text-sm">
              {{ t('inventory.no_alerts') || 'All stock levels are healthy' }}
            </div>
          </div>
          <div v-if="lowStockAlerts.length > 3" class="px-5 pb-4">
            <RouterLink
              to="/inventory/items?status=LOW"
              class="block text-center py-2.5 border border-gray-200 rounded-xl text-sm text-gray-600 font-medium hover:bg-gray-50 transition-colors"
            >{{ t('inventory.view_all_alerts') || 'View All Alerts' }}</RouterLink>
          </div>
        </div>

        <!-- Quick Restock Card -->
        <div class="rounded-2xl p-5 text-white relative overflow-hidden" style="background-color: #1B4235;">
          <!-- Decorative circles -->
          <div class="absolute -bottom-10 -right-10 w-32 h-32 rounded-full bg-white/5"></div>
          <div class="absolute -bottom-4 -right-4 w-20 h-20 rounded-full bg-white/5"></div>

          <div class="relative z-10">
            <h3 class="text-lg font-bold mb-1">{{ t('inventory.quick_restock') || 'Quick Restock' }}</h3>
            <p class="text-sm text-green-100/70 mb-4">{{ t('inventory.quick_restock_desc') || 'Scan item barcode or select from recently low items.' }}</p>
            <div class="flex items-center gap-3">
              <button
                @click="showMovementForm = true"
                class="flex-1 bg-white text-gray-900 font-semibold text-sm py-3 rounded-xl hover:bg-green-50 transition-colors text-center"
              >{{ t('inventory.scan_code') || 'Scan Code' }}</button>
              <div class="w-11 h-11 bg-white/10 rounded-xl flex items-center justify-center">
                <svg class="w-6 h-6 text-white/70" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                  <path d="M3 7V5a2 2 0 012-2h2M17 3h2a2 2 0 012 2v2M21 17v2a2 2 0 01-2 2h-2M7 21H5a2 2 0 01-2-2v-2"/>
                  <path d="M7 8v8M11 8v8M15 8v4M19 8v8"/>
                </svg>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>

    <!-- ── Stock Movement Modal ── -->
    <Teleport to="body">
      <div v-if="showMovementForm" class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" @click.self="showMovementForm = false">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
          <div class="p-6 border-b border-gray-100">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-bold text-gray-900">{{ t('inventory.log_stock_movement') || 'Log Stock Movement' }}</h3>
              <button @click="showMovementForm = false" class="text-gray-400 hover:text-gray-600">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
              </button>
            </div>
          </div>
          <div class="p-6 space-y-4">
            <!-- Movement Type -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">{{ t('inventory.movement_type') || 'Type' }}</label>
              <select v-model="movementForm.type" class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-200 focus:border-green-400">
                <option value="PURCHASE">{{ t('inventory.type_purchase') || 'Purchase' }}</option>
                <option value="USAGE">{{ t('inventory.type_usage') || 'Usage' }}</option>
                <option value="ADJUSTMENT">{{ t('inventory.type_adjustment') || 'Adjustment' }}</option>
                <option value="LOSS">{{ t('inventory.type_loss') || 'Loss' }}</option>
              </select>
            </div>
            <!-- Item Selection -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">{{ t('inventory.select_item') || 'Item' }}</label>
              <select v-model="movementForm.inventoryItemId" class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-200 focus:border-green-400">
                <option value="">{{ t('inventory.select_item_placeholder') || 'Select an item...' }}</option>
                <option v-for="item in allItems" :key="item.id" :value="item.id">
                  {{ item.name }} ({{ item.currentStock }} {{ item.unit }})
                </option>
              </select>
            </div>
            <!-- Quantity -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">{{ t('inventory.quantity') || 'Quantity' }}</label>
              <input v-model.number="movementForm.quantity" type="number" min="0" step="0.1"
                class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-200 focus:border-green-400"
                :placeholder="t('inventory.quantity_placeholder') || 'Enter quantity...'" />
            </div>
            <!-- Reason / Notes -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">{{ t('inventory.notes') || 'Notes' }}</label>
              <textarea v-model="movementForm.notes" rows="2"
                class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-200 focus:border-green-400 resize-none"
                :placeholder="t('inventory.notes_placeholder') || 'Optional notes...'"></textarea>
            </div>
          </div>
          <div class="p-6 border-t border-gray-100 flex justify-end gap-3">
            <button @click="showMovementForm = false" class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-gray-800 transition-colors">
              {{ t('common.cancel') || 'Cancel' }}
            </button>
            <button
              @click="submitMovement"
              :disabled="!movementForm.inventoryItemId || !movementForm.quantity"
              class="px-5 py-2.5 bg-brand-dark text-white text-sm font-semibold rounded-xl hover:bg-green-800 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
            >{{ t('inventory.submit_movement') || 'Record Movement' }}</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useInventoryStore } from '@/stores/inventory.store'
import { useI18n } from '@/i18n'
import type { InventoryItem, InventoryCategoryType, StockStatus, StockMovementType } from '@/types'

const { t } = useI18n()
const inventoryStore = useInventoryStore()

const searchQuery = ref('')
const activeTab = ref('ALL')
const currentPage = ref(1)
const pageSize = 4
const showMovementForm = ref(false)

const movementForm = reactive({
  type: 'PURCHASE' as StockMovementType,
  inventoryItemId: '',
  quantity: 0,
  notes: '',
})

// ── Category Tabs ──
const categoryTabs = computed(() => [
  { key: 'ALL', label: t('inventory.tab_all') || 'All' },
  { key: 'FEED', label: t('inventory.tab_feed') || 'Feed' },
  { key: 'SEEDS', label: t('inventory.tab_seeds') || 'Seeds' },
  { key: 'FERTILIZER', label: t('inventory.tab_fertilizer') || 'Fertilizer' },
  { key: 'EQUIPMENT', label: t('inventory.tab_tools') || 'Tools' },
  { key: 'VETERINARY', label: t('inventory.tab_medication') || 'Medication' },
])

// ── Static KPIs (will connect to real backend data later) ──
const feedKpi = { value: '12.5', trend: 'down' as const, trendLabel: '-2.4%' }
const fertilizerKpi = { value: '850', trend: 'up' as const, trendLabel: '+5.0%' }
const seedsKpi = { value: '120', trend: 'stable' as const, trendLabel: 'Stable' }
const medicationKpi = { value: '45', trend: 'down' as const, trendLabel: '-1.2%' }

// ── Demo data (used when backend hasn't returned items yet) ──
const demoItems: InventoryItem[] = [
  { id: '1', name: 'Premium Broiler Feed', categoryType: 'FEED', categoryName: 'Poultry Feed', unit: 'TONS', currentStock: 9.4, minimumStock: 3, reorderPoint: 5, maximumStock: 12.5, status: 'OPTIMAL', stockPercentage: 75, unitCost: 450, createdAt: '', updatedAt: '' } as InventoryItem,
  { id: '2', name: 'Nitrogen Fertilizer (N-P-K)', categoryType: 'FERTILIZER', categoryName: 'Chemicals', unit: 'KG', currentStock: 200, minimumStock: 100, reorderPoint: 250, maximumStock: 800, status: 'LOW', stockPercentage: 25, unitCost: 12, createdAt: '', updatedAt: '' } as InventoryItem,
  { id: '3', name: 'Maize Seeds (Hybrid)', categoryType: 'SEEDS', categoryName: 'Seeds', unit: 'BAGS', currentStock: 110, minimumStock: 20, reorderPoint: 30, maximumStock: 120, status: 'OPTIMAL', stockPercentage: 92, unitCost: 85, createdAt: '', updatedAt: '' } as InventoryItem,
  { id: '4', name: 'ND-IB Vaccine', categoryType: 'VETERINARY', categoryName: 'Medication', unit: 'UNITS', currentStock: 5, minimumStock: 10, reorderPoint: 15, maximumStock: 45, status: 'CRITICAL', stockPercentage: 12, unitCost: 320, createdAt: '', updatedAt: '' } as InventoryItem,
  { id: '5', name: 'Work Gloves (L)', categoryType: 'EQUIPMENT', categoryName: 'Safety Gear', unit: 'PAIRS', currentStock: 2, minimumStock: 5, reorderPoint: 10, maximumStock: 50, status: 'LOW', stockPercentage: 4, unitCost: 15, createdAt: '', updatedAt: '' } as InventoryItem,
  { id: '6', name: 'Drip Irrigation Hose', categoryType: 'EQUIPMENT', categoryName: 'Equipment', unit: 'METERS', currentStock: 450, minimumStock: 100, reorderPoint: 200, maximumStock: 500, status: 'OPTIMAL', stockPercentage: 90, unitCost: 3, createdAt: '', updatedAt: '' } as InventoryItem,
]

const allItems = computed(() =>
  inventoryStore.items.length > 0 ? inventoryStore.items : demoItems
)

const filteredItems = computed(() => {
  let list = allItems.value

  // Filter by category tab
  if (activeTab.value !== 'ALL') {
    list = list.filter(i => i.categoryType === activeTab.value)
  }

  // Filter by search
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(i =>
      i.name.toLowerCase().includes(q) ||
      (i.categoryName?.toLowerCase().includes(q)) ||
      (i.batchNumber?.toLowerCase().includes(q))
    )
  }

  return list
})

const totalFilteredCount = computed(() => filteredItems.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(totalFilteredCount.value / pageSize)))
const paginationStart = computed(() => totalFilteredCount.value === 0 ? 0 : (currentPage.value - 1) * pageSize + 1)
const paginationEnd = computed(() => Math.min(currentPage.value * pageSize, totalFilteredCount.value))

const lowStockAlerts = computed(() =>
  allItems.value.filter(i => i.status === 'LOW' || i.status === 'CRITICAL' || i.status === 'OUT_OF_STOCK')
)

// ── Helpers ──
function kpiTrend(trend: 'up' | 'down' | 'stable') {
  if (trend === 'up') return 'text-green-600 bg-green-50'
  if (trend === 'down') return 'text-red-500 bg-red-50'
  return 'text-gray-500 bg-gray-100'
}

function stockBarColor(status: StockStatus | string) {
  switch (status) {
    case 'OPTIMAL': return 'bg-brand-dark'
    case 'LOW': return 'bg-amber-400'
    case 'CRITICAL': return 'bg-red-500'
    case 'OUT_OF_STOCK': return 'bg-gray-300'
    default: return 'bg-gray-300'
  }
}

function statusBadge(status: StockStatus | string) {
  switch (status) {
    case 'OPTIMAL': return 'bg-green-100 text-green-700'
    case 'LOW': return 'bg-amber-100 text-amber-700'
    case 'CRITICAL': return 'bg-red-100 text-red-700'
    case 'OUT_OF_STOCK': return 'bg-gray-100 text-gray-600'
    default: return 'bg-gray-100 text-gray-600'
  }
}

function alertIconBg(status: StockStatus | string) {
  if (status === 'CRITICAL') return 'bg-red-100'
  if (status === 'LOW') return 'bg-amber-100'
  return 'bg-blue-100'
}

function categoryEmoji(type?: InventoryCategoryType) {
  switch (type) {
    case 'FEED': return '🌾'
    case 'SEEDS': return '🌱'
    case 'FERTILIZER': return '🧪'
    case 'PESTICIDE': return '🧴'
    case 'VETERINARY': return '💊'
    case 'EQUIPMENT': return '🔧'
    case 'PACKAGING': return '📦'
    default: return '📋'
  }
}

function categoryIconBg(type?: InventoryCategoryType) {
  switch (type) {
    case 'FEED': return 'bg-amber-50'
    case 'SEEDS': return 'bg-green-50'
    case 'FERTILIZER': return 'bg-emerald-50'
    case 'PESTICIDE': return 'bg-orange-50'
    case 'VETERINARY': return 'bg-blue-50'
    case 'EQUIPMENT': return 'bg-gray-100'
    case 'PACKAGING': return 'bg-purple-50'
    default: return 'bg-gray-100'
  }
}

function formatCategoryType(type?: InventoryCategoryType) {
  if (!type) return '—'
  return type.charAt(0) + type.slice(1).toLowerCase()
}

function openRestock(item: InventoryItem) {
  movementForm.type = 'PURCHASE'
  movementForm.inventoryItemId = item.id
  movementForm.quantity = 0
  movementForm.notes = ''
  showMovementForm.value = true
}

async function submitMovement() {
  if (!movementForm.inventoryItemId || !movementForm.quantity) return
  try {
    await inventoryStore.createMovement({
      inventoryItemId: movementForm.inventoryItemId,
      type: movementForm.type,
      quantity: movementForm.quantity,
      notes: movementForm.notes || undefined,
    })
    showMovementForm.value = false
  } catch {
    // API not yet available — close modal gracefully
    showMovementForm.value = false
  }
}

// ── Data Fetching ──
onMounted(async () => {
  try {
    await Promise.all([
      inventoryStore.fetchItems(),
      inventoryStore.fetchCategories(),
    ])
  } catch {
    // Backend not ready yet — use demo data
  }
})
</script>
