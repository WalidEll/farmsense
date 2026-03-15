import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'

// ... existing imports
import ComingSoonView from '@/views/ComingSoonView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: () => import('@/components/shared/AppLayout.vue'),
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('@/views/DashboardView.vue'),
        },
        {
          path: 'fields',
          name: 'fields',
          component: () => import('@/views/FieldMapView.vue'),
        },
        {
          path: 'tasks',
          name: 'tasks',
          component: ComingSoonView,
        },
        {
          path: 'nursery',
          name: 'nursery',
          component: ComingSoonView,
        },
        {
          path: 'irrigation',
          name: 'irrigation',
          component: ComingSoonView,
        },
        {
          path: 'harvests',
          name: 'harvests',
          component: ComingSoonView,
        },
        {
          path: 'locations',
          name: 'locations',
          component: () => import('@/views/LocationsView.vue'),
        },
        {
          path: 'sensors',
          name: 'sensors',
          component: () => import('@/views/SensorsView.vue'),
        },
        {
          path: 'plants/:id',
          name: 'plant-detail',
          component: () => import('@/views/PlantDetailView.vue'),
        },
        {
          path: 'alerts',
          name: 'alerts',
          component: () => import('@/views/AlertsView.vue'),
        },
        {
          path: 'crops',
          name: 'crops',
          component: () => import('@/views/CropCatalogView.vue'),
        },
        {
          path: 'crops/admin',
          name: 'crop-admin',
          component: () => import('@/views/CropAdminView.vue'),
        },
        {
          path: 'crops/:id',
          name: 'crop-detail',
          component: () => import('@/views/CropDetailView.vue'),
        },
        {
          path: 'plans',
          name: 'plans',
          component: () => import('@/views/CropPlansView.vue'),
        },
        {
          path: 'plans/:id',
          name: 'plan-detail',
          component: () => import('@/views/CropPlanDetailView.vue'),
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('@/views/SettingsView.vue'),
        },
        // ── Accounting ──
        {
          path: 'accounting',
          name: 'accounting-dashboard',
          component: () => import('@/views/accounting/AccountingDashboardView.vue'),
        },
        {
          path: 'accounting/transactions',
          name: 'transactions',
          component: () => import('@/views/accounting/TransactionsView.vue'),
        },
        {
          path: 'accounting/transactions/new',
          name: 'transaction-new',
          component: () => import('@/views/accounting/TransactionFormView.vue'),
        },
        {
          path: 'accounting/transactions/:id/edit',
          name: 'transaction-edit',
          component: () => import('@/views/accounting/TransactionFormView.vue'),
        },
        {
          path: 'accounting/receipts',
          name: 'receipt-scanner',
          component: () => import('@/views/accounting/ReceiptScannerView.vue'),
        },
        {
          path: 'accounting/labor',
          name: 'labor-tracking',
          component: () => import('@/views/accounting/LaborTrackingView.vue'),
        },
        {
          path: 'accounting/tags',
          name: 'tags-manager',
          component: () => import('@/views/accounting/TagsManagerView.vue'),
        },
        {
          path: 'accounting/team',
          name: 'team-management',
          component: () => import('@/views/accounting/TeamManagementView.vue'),
        },
        // ── Inventory ──
        {
          path: 'inventory',
          name: 'inventory-dashboard',
          component: () => import('@/views/inventory/InventoryDashboardView.vue'),
        },
        {
          path: 'inventory/items',
          name: 'inventory-items',
          component: () => import('@/views/inventory/InventoryDashboardView.vue'),
        },
        {
          path: 'inventory/movements',
          name: 'inventory-movements',
          component: ComingSoonView,
        },
        // ── Poultry ──
        {
          path: 'poultry',
          name: 'poultry-dashboard',
          component: () => import('@/views/poultry/PoultryDashboardView.vue'),
        },
        {
          path: 'poultry/flocks',
          name: 'flocks',
          component: () => import('@/views/poultry/FlocksView.vue'),
        },
        {
          path: 'poultry/flocks/:id',
          name: 'flock-detail',
          component: () => import('@/views/poultry/FlockDetailView.vue'),
        },
        {
          path: 'poultry/breeds',
          name: 'breeds',
          component: () => import('@/views/poultry/BreedsView.vue'),
        },
        {
          path: 'poultry/breeds/:id',
          name: 'breed-detail',
          component: () => import('@/views/poultry/BreedDetailView.vue'),
        },
        {
          path: 'poultry/eggs',
          name: 'poultry-eggs',
          component: ComingSoonView,
        },
        {
          path: 'poultry/weight',
          name: 'poultry-weight',
          component: ComingSoonView,
        },
        {
          path: 'poultry/feed',
          name: 'poultry-feed',
          component: ComingSoonView,
        },
        {
          path: 'poultry/suppliers',
          name: 'suppliers',
          component: () => import('@/views/poultry/SuppliersView.vue'),
        },
        {
          path: 'poultry/customers',
          name: 'customers',
          component: () => import('@/views/poultry/CustomersView.vue'),
        },
      ],
    },
  ],
})
// ... rest of file

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.public && !auth.isLoggedIn) return { name: 'login' }
  if (to.meta.public && auth.isLoggedIn) return { name: 'dashboard' }
})

export default router
