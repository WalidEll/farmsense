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
          path: 'map',
          name: 'map',
          component: ComingSoonView,
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
